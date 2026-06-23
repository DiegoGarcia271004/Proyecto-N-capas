import React, { createContext, useContext, useState, useEffect } from 'react';
import type { 
  SkuItem, 
  BatchItem, 
  Reservation, 
  Warehouse, 
  SpatialPolicy,
  TransferOrder,
  AuditRecord,
  ScanSimulation
} from '../mock/mockData';
import { 
  INITIAL_SKUS, 
  INITIAL_RESERVATIONS, 
  INITIAL_WAREHOUSES,
  INITIAL_BATCHES,
  INITIAL_POLICIES,
  INITIAL_TRANSFERS,
  INITIAL_AUDITS,
  SCAN_SIMULATIONS
} from '../mock/mockData';

interface UserSession {
  username: string;
  role: 'admin' | 'manager' | 'operator';
  token: string;
}

interface WmsContextType {
  // Session State
  user: UserSession | null;
  login: (username: string, role: 'admin' | 'manager' | 'operator') => boolean;
  logout: () => void;

  // General WMS State
  activeWarehouse: Warehouse;
  warehouses: Warehouse[];
  skus: SkuItem[];
  batches: BatchItem[];
  reservations: Reservation[];
  policies: SpatialPolicy[];
  transfers: TransferOrder[];
  audits: AuditRecord[];
  
  // Controls
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  setActiveWarehouse: (wh: Warehouse) => void;
  
  // Actions
  updatePolicy: (policyId: string) => void; // Simula PUT de Admin
  approveTransfer: (id: string) => void;
  rejectTransfer: (id: string) => void;
  registerCycleCount: (skuCode: string, pasillo: string, rack: string, nivel: string, contado: number) => void; // Operario a ciegas
  approveRopReplenish: (skuId: string) => void;
  
  // Terminal states & actions
  scanMode: 'entrada' | 'salida';
  setScanMode: (mode: 'entrada' | 'salida') => void;
  lastScan: ScanSimulation | null;
  scanStatus: 'waiting' | 'processing' | 'success' | 'error';
  scanHistory: (ScanSimulation & { fecha: Date; tipo: string })[] ;
  isScannerFocused: boolean;
  setIsScannerFocused: (focused: boolean) => void;
  processScan: (barcode: string) => void;
  confirmScan: () => void;
  resetLastScan: () => void;
}

const WmsContext = createContext<WmsContextType | undefined>(undefined);

export const WmsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // 1. Session state initialized from LocalStorage
  const [user, setUser] = useState<UserSession | null>(() => {
    const saved = localStorage.getItem('wms_session');
    return saved ? JSON.parse(saved) : null;
  });

  // 2. WMS general states
  const [warehouses] = useState<Warehouse[]>(INITIAL_WAREHOUSES);
  const [activeWarehouse, setActiveWarehouseState] = useState<Warehouse>(INITIAL_WAREHOUSES[0]);
  const [skus, setSkus] = useState<SkuItem[]>(INITIAL_SKUS);
  const [batches, setBatches] = useState<BatchItem[]>(INITIAL_BATCHES);
  const [reservations, setReservations] = useState<Reservation[]>(INITIAL_RESERVATIONS);
  const [policies, setPolicies] = useState<SpatialPolicy[]>(INITIAL_POLICIES);
  const [transfers, setTransfers] = useState<TransferOrder[]>(INITIAL_TRANSFERS);
  const [audits, setAudits] = useState<AuditRecord[]>(INITIAL_AUDITS);
  
  const [searchQuery, setSearchQuery] = useState<string>('');
  
  // 3. Scanner terminal states
  const [scanMode, setScanMode] = useState<'entrada' | 'salida'>('entrada');
  const [lastScan, setLastScan] = useState<ScanSimulation | null>(null);
  const [scanStatus, setScanStatus] = useState<'waiting' | 'processing' | 'success' | 'error'>('waiting');
  const [scanHistory, setScanHistory] = useState<(ScanSimulation & { fecha: Date; tipo: string })[]>([]);
  const [isScannerFocused, setIsScannerFocused] = useState<boolean>(true);

  // Auth Functions
  const login = (username: string, role: 'admin' | 'manager' | 'operator'): boolean => {
    if (!username.trim()) return false;
    
    // Generar un token JWT ficticio
    const header = btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }));
    const payload = btoa(JSON.stringify({ sub: username, role: role, exp: Math.floor(Date.now() / 1000) + 3600 }));
    const signature = "signature_xyz";
    const token = `${header}.${payload}.${signature}`;

    const session: UserSession = { username, role, token };
    setUser(session);
    localStorage.setItem('wms_session', JSON.stringify(session));
    return true;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('wms_session');
  };

  // Selector de almacén
  const setActiveWarehouse = (wh: Warehouse) => {
    setActiveWarehouseState(wh);
    const multiplier = wh.id === '1' ? 1.0 : wh.id === '2' ? 0.75 : 0.5;
    
    // Actualizar stocks en base al almacén
    setSkus(INITIAL_SKUS.map(sku => ({
      ...sku,
      stock: Math.round(sku.stock * multiplier),
    })));
  };

  // Simulación del PUT del Administrador para cambiar reglas
  const updatePolicy = (policyId: string) => {
    setPolicies(prev => 
      prev.map(p => ({
        ...p,
        activa: p.id === policyId
      }))
    );
  };

  // Aprobación de reabastecimiento ROP
  const approveRopReplenish = (skuId: string) => {
    setSkus(prevSkus => 
      prevSkus.map(sku => {
        if (sku.id === skuId) {
          const newStock = sku.stock + sku.sugerido;
          // Recalcular WAC (Costo Promedio Ponderado)
          // Fórmula: WAC = (Q1 * C1 + Q_added * C_original) / (Q1 + Q_added)
          const newWac = ((sku.stock * sku.costoPromedio) + (sku.sugerido * sku.costo)) / newStock;
          
          // Crear un lote correspondiente
          const newBatch: BatchItem = {
            id: `b-rop-${Date.now()}`,
            sku: sku.sku,
            codigoLote: `LOTE-ROP-${new Date().getFullYear()}`,
            cantidad: sku.sugerido,
            fechaCaducidad: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000 * 12).toISOString().substring(0, 10), // 12 meses
            pasillo: 'PASILLO 1',
            rack: 'RACK A',
            nivel: 'NIVEL 1',
            fechaIngreso: new Date().toISOString().substring(0, 10)
          };
          setBatches(prev => [...prev, newBatch]);
          
          return {
            ...sku,
            stock: newStock,
            costoPromedio: parseFloat(newWac.toFixed(2))
          };
        }
        return sku;
      })
    );
  };

  // Aprobación de traslados (Manager)
  const approveTransfer = (id: string) => {
    setTransfers(prev => 
      prev.map(t => {
        if (t.id === id) {
          // Si se aprueba, aumentamos o disminuimos el stock localmente en base a las reglas
          return { ...t, estado: 'aprobado' };
        }
        return t;
      })
    );
  };

  const rejectTransfer = (id: string) => {
    setTransfers(prev => 
      prev.map(t => (t.id === id ? { ...t, estado: 'rechazado' } : t))
    );
  };

  // Registro de conteo a ciegas (Operario)
  const registerCycleCount = (skuCode: string, pasillo: string, rack: string, nivel: string, contado: number) => {
    const skuItem = skus.find(s => s.sku.toLowerCase() === skuCode.toLowerCase() || s.name.toLowerCase().includes(skuCode.toLowerCase()));
    const sku = skuItem ? skuItem.sku : 'SKU-DESCONOCIDO';
    
    // Calcular stock teórico actual sumando lotes en esa posición, o el total si no hay desglose
    const matchingBatches = batches.filter(b => b.sku === sku && b.pasillo === pasillo && b.rack === rack);
    const stockTeorico = matchingBatches.reduce((acc, b) => acc + b.cantidad, 0) || (skuItem ? skuItem.stock : 0);
    
    const discrepancia = contado - stockTeorico;

    const newAudit: AuditRecord = {
      id: `aud-${Date.now()}`,
      sku,
      pasillo,
      rack,
      nivel,
      stockTeorico,
      stockContado: contado,
      discrepancia,
      operario: user ? user.username : 'Operario Anonimo',
      fecha: new Date().toISOString().substring(0, 10),
      estado: 'pendiente'
    };

    setAudits(prev => [newAudit, ...prev]);
  };

  // Temporizadores FIFO de timeouts para reservas activas
  useEffect(() => {
    const timer = setInterval(() => {
      setReservations(prevRes => 
        prevRes.map(res => {
          if (res.tiempoRestante <= 0) {
            return { ...res, tiempoRestante: 0, estado: 'expired' };
          }
          const nextTime = res.tiempoRestante - 1;
          const nextStatus = nextTime < 60 ? 'warning' : 'active';
          return {
            ...res,
            tiempoRestante: nextTime,
            estado: nextStatus as 'active' | 'warning'
          };
        })
      );
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  // Procesar escaneo en la terminal
  const processScan = (barcode: string) => {
    if (!barcode.trim()) return;
    
    setScanStatus('processing');
    
    setTimeout(() => {
      const match = SCAN_SIMULATIONS.find(s => s.barcode === barcode || s.sku.toLowerCase() === barcode.toLowerCase());
      
      if (match) {
        // Asignar ubicación según la regla espacial activa
        const activePolicy = policies.find(p => p.activa);
        let customLocation = { ...match.ubicacionSugerida };

        if (activePolicy?.id === 'pol-2') { // Optimización de espacio (Aleatorio)
          const pasillos = ['PASILLO 1', 'PASILLO 2', 'PASILLO 3', 'PASILLO 4', 'PASILLO 5'];
          const racks = ['RACK A', 'RACK B', 'RACK C', 'RACK D'];
          const niveles = ['NIVEL 1', 'NIVEL 2', 'NIVEL 3'];
          customLocation = {
            pasillo: pasillos[Math.floor(Math.random() * pasillos.length)],
            rack: racks[Math.floor(Math.random() * racks.length)],
            nivel: niveles[Math.floor(Math.random() * niveles.length)],
          };
        } else if (activePolicy?.id === 'pol-3') { // Ubicación fija por Familia
          const skuItem = skus.find(s => s.sku === match.sku);
          if (skuItem) {
            if (skuItem.familia === 'Eléctricos') {
              customLocation = { pasillo: 'PASILLO E-1', rack: 'RACK EL', nivel: 'NIVEL 1' };
            } else if (skuItem.familia === 'Químicos') {
              customLocation = { pasillo: 'PASILLO Q-5', rack: 'RACK QU', nivel: 'NIVEL 3' };
            } else {
              customLocation = { pasillo: 'PASILLO G-2', rack: 'RACK GE', nivel: 'NIVEL 2' };
            }
          }
        }

        setLastScan({
          ...match,
          ubicacionSugerida: customLocation
        });
        setScanStatus('success');
      } else {
        setLastScan(null);
        setScanStatus('error');
        setTimeout(() => {
          setScanStatus('waiting');
        }, 3000);
      }
    }, 800);
  };

  // Confirmar lectura
  const confirmScan = () => {
    if (!lastScan) return;

    // Agregar al historial de escaneo
    setScanHistory(prev => [
      { ...lastScan, fecha: new Date(), tipo: scanMode === 'entrada' ? 'Ingreso' : 'Egreso' },
      ...prev
    ]);

    // Modificar stock y crear lote en caso de entrada
    setSkus(prevSkus => 
      prevSkus.map(sku => {
        if (sku.sku === lastScan.sku) {
          const qtyDiff = scanMode === 'entrada' ? 100 : -100; // Incremento o extracción estándar por lote
          const nextStock = Math.max(0, sku.stock + qtyDiff);
          
          // Calcular nuevo costo promedio ponderado en entrada
          let nextWac = sku.costoPromedio;
          if (scanMode === 'entrada') {
            nextWac = ((sku.stock * sku.costoPromedio) + (100 * sku.costo)) / nextStock;
          }

          return { 
            ...sku, 
            stock: nextStock,
            costoPromedio: parseFloat(nextWac.toFixed(2))
          };
        }
        return sku;
      })
    );

    if (scanMode === 'entrada') {
      // Registrar nuevo lote
      const newBatch: BatchItem = {
        id: `b-scan-${Date.now()}`,
        sku: lastScan.sku,
        codigoLote: lastScan.lote,
        cantidad: 100,
        fechaCaducidad: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000 * 6).toISOString().substring(0, 10), // 6 meses
        pasillo: lastScan.ubicacionSugerida.pasillo,
        rack: lastScan.ubicacionSugerida.rack,
        nivel: lastScan.ubicacionSugerida.nivel,
        fechaIngreso: new Date().toISOString().substring(0, 10)
      };
      setBatches(prev => [...prev, newBatch]);
    } else {
      // Extracción FIFO: restamos del lote más antiguo (FIFO)
      const matchingBatches = [...batches]
        .filter(b => b.sku === lastScan.sku)
        .sort((a, b) => new Date(a.fechaCaducidad).getTime() - new Date(b.fechaCaducidad).getTime());
      
      let extractQty = 100;
      const updatedBatches = batches.map(b => {
        if (matchingBatches.length > 0 && b.id === matchingBatches[0].id) {
          const nextQty = Math.max(0, b.cantidad - extractQty);
          extractQty = Math.max(0, extractQty - b.cantidad);
          return { ...b, cantidad: nextQty };
        }
        return b;
      }).filter(b => b.cantidad > 0); // eliminar lotes agotados
      
      setBatches(updatedBatches);
    }

    setLastScan(null);
    setScanStatus('waiting');
  };

  const resetLastScan = () => {
    setLastScan(null);
    setScanStatus('waiting');
  };

  return (
    <WmsContext.Provider value={{
      user,
      login,
      logout,
      activeWarehouse,
      warehouses,
      skus,
      batches,
      reservations,
      policies,
      transfers,
      audits,
      searchQuery,
      setSearchQuery,
      setActiveWarehouse,
      updatePolicy,
      approveTransfer,
      rejectTransfer,
      registerCycleCount,
      approveRopReplenish,
      
      // Terminal
      scanMode,
      setScanMode,
      lastScan,
      scanStatus,
      scanHistory,
      isScannerFocused,
      setIsScannerFocused,
      processScan,
      confirmScan,
      resetLastScan
    }}>
      {children}
    </WmsContext.Provider>
  );
};

export const useWms = () => {
  const context = useContext(WmsContext);
  if (!context) {
    throw new Error('useWms debe ser utilizado dentro de un WmsProvider');
  }
  return context;
};
