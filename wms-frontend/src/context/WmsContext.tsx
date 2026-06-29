import React, { createContext, useContext, useState, useEffect } from 'react';
import apiClient from '../core/api/apiClient';
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

export interface UserSession {
  username: string;
  role: 'ADMIN' | 'MANAGER' | 'OPERATOR';
  token: string;
}

export interface ProductPayload {
  sku: string;
  name: string;
  category: 'FOOD_PERISHABLE' | 'FOOD_NON_PERISHABLE' | 'BEVERAGES' | 'FROZEN' | 'PHARMACEUTICAL' | 'MEDICAL_DEVICES' | 'SUPPLEMENTS' | 'PERSONAL_CARE' | 'CLEANING_SUPPLIES' | 'HOME_GOODS' | 'ELECTRONICS' | 'COMPONENTS' | 'ACCESSORIES' | 'APPAREL' | 'FOOTWEAR' | 'TEXTILES' | 'RAW_MATERIALS' | 'MACHINERY_PARTS' | 'TOOLS' | 'CHEMICALS' | 'OFFICE_SUPPLIES' | 'SEASONAL' | 'PROMOTIONAL' | 'OTHER';
  minStockLevel: number;
  reorderPoint: number;
  weight: {
    value: number;
    unit: 'KG' | 'LBS' | 'OZ' | 'G';
  };
  dimensions: {
    height: number;
    width: number;
    depth: number;
    unit: 'CM' | 'M' | 'IN' | 'FT';
  };
  requirements: 'AMBIENT' | 'REFRIGERATED' | 'FROZEN' | 'CONTROLLED_TEMP' | 'DRY' | 'HAZARDOUS' | 'HIGH_SECURITY' | 'FLAMMABLE' | 'FRAGILE' | 'OVERSIZED';
  leadTimeDays: number;
  safetyStock: number;
}

interface WmsContextType {
  user: UserSession | null;
  login: (username: string, password: string) => Promise<string | null>;
  logout: () => Promise<void>;
  activeWarehouse: Warehouse;
  warehouses: Warehouse[];
  skus: SkuItem[];
  batches: BatchItem[];
  reservations: Reservation[];
  policies: SpatialPolicy[];
  transfers: TransferOrder[];
  audits: AuditRecord[];
  notifications: any[];
  fetchNotifications: () => Promise<void>;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  setActiveWarehouse: (wh: Warehouse) => void;
  updatePolicy: (policyId: string) => void;
  approveTransfer: (id: string) => void;
  rejectTransfer: (id: string) => void;
  registerCycleCount: (skuCode: string, pasillo: string, rack: string, nivel: string, contado: number) => void;
  approveRopReplenish: (skuId: string) => void;
  createReservation: (sku: string, quantity: number) => Promise<boolean>;
  confirmReservation: (id: string) => Promise<boolean>;
  releaseReservation: (id: string) => Promise<boolean>;
  registerUser: (username: string, role: string, password: string) => Promise<boolean>;
  createProduct: (productData: ProductPayload) => Promise<boolean>;
  createWarehouse: (name: string, address: string) => Promise<boolean>;
  updateWarehouse: (id: string, name: string, address: string) => Promise<boolean>;
  deleteWarehouse: (id: string) => Promise<boolean>;
  activateWarehouse: (id: string) => Promise<boolean>;
  createStorageLocation: (warehouseId: string, pasillo: string, rack: string, nivel: string) => Promise<boolean>;
  getStorageLocationDetails: (id: string) => Promise<any>;
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
  const [user, setUser] = useState<UserSession | null>(() => {
    const saved = localStorage.getItem('wms_session');
    return saved ? JSON.parse(saved) : null;
  });

  const [warehouses] = useState<Warehouse[]>(INITIAL_WAREHOUSES);
  const [activeWarehouse, setActiveWarehouseState] = useState<Warehouse>(INITIAL_WAREHOUSES[0]);
  const [skus, setSkus] = useState<SkuItem[]>(INITIAL_SKUS);
  const [batches, setBatches] = useState<BatchItem[]>(INITIAL_BATCHES);
  const [reservations, setReservations] = useState<Reservation[]>(INITIAL_RESERVATIONS);
  const [policies, setPolicies] = useState<SpatialPolicy[]>(INITIAL_POLICIES);
  const [transfers, setTransfers] = useState<TransferOrder[]>(INITIAL_TRANSFERS);
  const [audits, setAudits] = useState<AuditRecord[]>(INITIAL_AUDITS);
  const [notifications, setNotifications] = useState<any[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const [scanMode, setScanMode] = useState<'entrada' | 'salida'>('entrada');
  const [lastScan, setLastScan] = useState<ScanSimulation | null>(null);
  const [scanStatus, setScanStatus] = useState<'waiting' | 'processing' | 'success' | 'error'>('waiting');
  const [scanHistory, setScanHistory] = useState<(ScanSimulation & { fecha: Date; tipo: string })[]>([]);
  const [isScannerFocused, setIsScannerFocused] = useState<boolean>(true);

  const login = async (username: string, password: string): Promise<string | null> => {
    if (!username.trim()) return null;
    try {
      const response = await apiClient.post('/auth/login', { username, password });

      const rawRole = response.data.role

      const session: UserSession = { username, role: rawRole, token: 'session_cookie' };
      setUser(session);
      localStorage.setItem('wms_session', JSON.stringify(session));

      return rawRole;
    } catch (error) {
      console.error("Login failed:", error);
      return null;
    }
  };

  const logout = async () => {
    try {
      await apiClient.post('/auth/logout');
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      setUser(null);
      localStorage.removeItem('wms_session');
    }
  };

  const registerUser = async (username: string, role: string, password: string): Promise<boolean> => {
    try {
      await apiClient.post('/auth/register', { username, role, password });
      return true;
    } catch (error) {
      console.error("Failed to register user:", error);
      return false;
    }
  };

  const fetchInfrastructure = async () => {
    try {
      const whResponse = await apiClient.get('/warehouse');

      if (whResponse.data && whResponse.data.data && whResponse.data.data.length > 0) {
        const firstWhId = whResponse.data.data[0].id;

        await apiClient.get(`/warehouse/${firstWhId}`);

        await apiClient.get(`/storage-location/warehouse/${firstWhId}`);
      }
    } catch (error: any) {

      console.warn("El backend falló al traer los almacenes (Error 500). Usando almacenes de prueba (Mock Data).");

    }
  };

  const setActiveWarehouse = (wh: Warehouse) => {
    setActiveWarehouseState(wh);
    fetchSkus();
  };

  const createProduct = async (productData: ProductPayload): Promise<boolean> => {
    try {
      const response = await apiClient.post('/product', productData);
      if (response.status === 200 || response.status === 201) return true;
      return false;
    } catch (error: any) {
      console.error("Error al crear producto:", error.response?.data || error.message);
      return false;
    }
  };

  const updatePolicy = async (policyId: string) => {
    setPolicies(prev => prev.map(p => ({ ...p, activa: p.id === policyId })));
    try {
      const warehouseUuid = activeWarehouse.id === '2' ? "22222222-2222-2222-2222-222222222222" : activeWarehouse.id === '3' ? "33333333-3333-3333-3333-333333333333" : "11111111-1111-1111-1111-111111111111";
      await apiClient.put(`/warehouse-policy/${warehouseUuid}`, { strategy: policyId });
      await apiClient.get(`/warehouse-policy/${warehouseUuid}`);
    } catch (error) {
      console.error("Failed to sync warehouse policy:", error);
    }
  };

  const fetchSkus = async () => {
    try {
      const response = await apiClient.get('/product/inactive');
      const data = response.data.data;
      if (Array.isArray(data)) {
        const mappedSkus: SkuItem[] = data.map((item: any) => {
          let familia: 'Ferretería' | 'Eléctricos' | 'Químicos' | 'Logística' = 'Logística';
          let zona: 'A' | 'B' | 'C' = 'A';
          const cat = item.category || '';
          if (cat === 'TOOLS' || cat === 'MACHINERY_PARTS' || cat === 'HOME_GOODS' || cat === 'COMPONENTS') { familia = 'Ferretería'; zona = 'A'; }
          else if (cat === 'ELECTRONICS' || cat === 'ACCESSORIES') { familia = 'Eléctricos'; zona = 'B'; }
          else if (cat === 'CHEMICALS' || cat === 'PHARMACEUTICAL' || cat === 'CLEANING_SUPPLIES') { familia = 'Químicos'; zona = 'C'; }

          let dimsStr = '10x10x10 cm';
          if (item.dimensions) dimsStr = `${item.dimensions.height || 10}x${item.dimensions.width || 10}x${item.dimensions.depth || 10} cm`;

          const skuBatches = batches.filter((b: any) => b.sku === item.sku);
          const stock = skuBatches.length > 0 ? skuBatches.reduce((acc: number, b: any) => acc + b.cantidad, 0) : 50;

          return {
            id: item.id || '', sku: item.sku || '', name: item.name || '', zona, stock,
            rop: item.minStockLevel || 100, sugerido: item.reorderPoint || 200, costo: 0.12,
            costoPromedio: 0.15, unidad: 'unidades', peso: item.weight?.value || 0.1,
            dimensiones: dimsStr, familia
          };
        });
        setSkus(mappedSkus);
      }
    } catch (error) {
      console.error("Failed to fetch products:", error);
    }
  };

  const fetchNotifications = async () => {
    try {
      const response = await apiClient.get('/notifications/unread');
      if (response.data && Array.isArray(response.data.data)) {
        setNotifications(response.data.data);
      }
    } catch (error) {
      console.error("Failed to fetch notifications:", error);
    }
  };


  useEffect(() => {
    if (user) {
      fetchSkus();
      fetchNotifications();
      fetchInfrastructure();
    } else {
      setSkus([]);
      setNotifications([]);
    }
  }, [user]);

  useEffect(() => {
    setSkus(prevSkus =>
        prevSkus.map(sku => {
          const skuBatches = batches.filter(b => b.sku === sku.sku);
          const stock = skuBatches.length > 0 ? skuBatches.reduce((acc, b) => acc + b.cantidad, 0) : sku.stock;
          return { ...sku, stock };
        })
    );
  }, [batches]);

  const approveRopReplenish = async (skuId: string) => {
    const skuItem = skus.find(s => s.id === skuId);
    if (!skuItem) return;
    const lotCode = `LOTE-ROP-${new Date().getFullYear()}-${Math.floor(Math.random() * 1000)}`;
    const quantity = skuItem.sugerido;
    const expirationDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000 * 12).toISOString().substring(0, 10);
    let warehouseUuid = activeWarehouse.id === '2' ? "22222222-2222-2222-2222-222222222222" : activeWarehouse.id === '3' ? "33333333-3333-3333-3333-333333333333" : "11111111-1111-1111-1111-111111111111";

    try {
      await apiClient.post('/inventory/entry', { product: skuItem.id, warehouse: warehouseUuid, lotNumber: lotCode, quantity: quantity, unitCost: skuItem.costo, expirationDate });
      await apiClient.patch(`/reorder-suggestions/11111111-1111-1111-4444-111111111111/attended`);
      const newBatch: BatchItem = { id: `b-rop-${Date.now()}`, sku: skuItem.sku, codigoLote: lotCode, cantidad: quantity, fechaCaducidad: expirationDate, pasillo: 'PASILLO 1', rack: 'RACK A', nivel: 'NIVEL 1', fechaIngreso: new Date().toISOString().substring(0, 10) };
      setBatches(prev => [...prev, newBatch]);
    } catch (error) { console.error("ROP Replenish failed:", error); }
  };

  const approveTransfer = (id: string) => setTransfers(prev => prev.map(t => t.id === id ? { ...t, estado: 'aprobado' } : t));
  const rejectTransfer = (id: string) => setTransfers(prev => prev.map(t => t.id === id ? { ...t, estado: 'rechazado' } : t));

  const createReservation = async (sku: string, quantity: number): Promise<boolean> => {
    const skuItem = skus.find(s => s.sku === sku);
    if (!skuItem) return false;
    try {
      await apiClient.post('/reservation', { product: skuItem.id, quantity, expirationMinutes: 10 });
      const newRes: Reservation = { id: `res-${Date.now()}`, orden: `ORD-${Math.floor(Math.random() * 100000)}`, cliente: 'Cliente General', almacenDestino: activeWarehouse.nombre, sku, cantidad: quantity, tiempoRestante: 600, estado: 'active' };
      setReservations(prev => [...prev, newRes]);
      return true;
    } catch (error) { return false; }
  };

  const confirmReservation = async (id: string): Promise<boolean> => {
    try { await apiClient.put(`/reservation/confirm/11111111-1111-1111-3333-111111111111`); setReservations(prev => prev.filter(r => r.id !== id)); return true; } catch (error) { return false; }
  };

  const releaseReservation = async (id: string): Promise<boolean> => {
    try { await apiClient.put(`/reservation/release/11111111-1111-1111-3333-111111111111`); setReservations(prev => prev.map(r => r.id === id ? { ...r, tiempoRestante: 0, estado: 'expired' } : r)); return true; } catch (error) { return false; }
  };

  const registerCycleCount = async (skuCode: string, pasillo: string, rack: string, nivel: string, contado: number) => {
    const skuItem = skus.find(s => s.sku.toLowerCase() === skuCode.toLowerCase() || s.name.toLowerCase().includes(skuCode.toLowerCase()));
    const sku = skuItem ? skuItem.sku : 'SKU-DESCONOCIDO';
    const matchingBatches = batches.filter(b => b.sku === sku && b.pasillo === pasillo && b.rack === rack);
    const stockTeorico = matchingBatches.reduce((acc, b) => acc + b.cantidad, 0) || (skuItem ? skuItem.stock : 0);
    const newAudit: AuditRecord = { id: `aud-${Date.now()}`, sku, pasillo, rack, nivel, stockTeorico, stockContado: contado, discrepancia: contado - stockTeorico, operario: user ? user.username : 'Operario Anonimo', fecha: new Date().toISOString().substring(0, 10), estado: 'pendiente' };
    setAudits(prev => [newAudit, ...prev]);
    try {
      const createRes = await apiClient.post('/cyclic-counts', { lot: "11111111-1111-1111-2222-111111111111" });
      const cyclicCountId = createRes.data?.data?.id || `cyclic-${Date.now()}`;
      await apiClient.patch(`/cyclic-counts/${cyclicCountId}/start`);
      await apiClient.patch(`/cyclic-counts/${cyclicCountId}/submit`, { physicalQuantity: contado });
      await apiClient.get('/cyclic-counts');
    } catch (error) { console.error("Sync cyclic count failed:", error); }
  };

  const createWarehouse = async (name: string, address: string): Promise<boolean> => { try { await apiClient.post('/warehouse', { name, address }); return true; } catch (error) { return false; } };
  const updateWarehouse = async (id: string, name: string, address: string): Promise<boolean> => { try { await apiClient.put(`/warehouse/${id}`, { name, address }); return true; } catch (error) { return false; } };
  const deleteWarehouse = async (id: string): Promise<boolean> => { try { await apiClient.delete(`/warehouse/${id}`); return true; } catch (error) { return false; } };
  const activateWarehouse = async (id: string): Promise<boolean> => { try { await apiClient.patch(`/warehouse/${id}/activate`); return true; } catch (error) { return false; } };
  const createStorageLocation = async (warehouseId: string, pasillo: string, rack: string, nivel: string): Promise<boolean> => { try { await apiClient.post('/storage-location', { warehouseId, pasillo, rack, nivel }); return true; } catch (error) { return false; } };
  const getStorageLocationDetails = async (id: string) => { try { const res = await apiClient.get(`/storage-location/${id}`); return res.data?.data; } catch (error) { return null; } };

  useEffect(() => {
    const timer = setInterval(() => {
      setReservations(prevRes => prevRes.map(res => {
            if (res.tiempoRestante <= 0) return { ...res, tiempoRestante: 0, estado: 'expired' };
            const nextTime = res.tiempoRestante - 1;
            return { ...res, tiempoRestante: nextTime, estado: nextTime < 60 ? 'warning' : 'active' };
          })
      );
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const processScan = (barcode: string) => {
    if (!barcode.trim()) return;
    setScanStatus('processing');
    setTimeout(() => {
      const match = SCAN_SIMULATIONS.find(s => s.barcode === barcode || s.sku.toLowerCase() === barcode.toLowerCase());
      if (match) {
        const activePolicy = policies.find(p => p.activa);
        let customLocation = { ...match.ubicacionSugerida };
        if (activePolicy?.id === 'pol-2') {
          customLocation = { pasillo: ['PASILLO 1', 'PASILLO 2'][Math.floor(Math.random()*2)], rack: 'RACK A', nivel: 'NIVEL 1' };
        } else if (activePolicy?.id === 'pol-3') {
          const skuItem = skus.find(s => s.sku === match.sku);
          if (skuItem?.familia === 'Eléctricos') customLocation = { pasillo: 'PASILLO E-1', rack: 'RACK EL', nivel: 'NIVEL 1' };
        }
        setLastScan({ ...match, ubicacionSugerida: customLocation });
        setScanStatus('success');
      } else {
        setLastScan(null); setScanStatus('error');
        setTimeout(() => setScanStatus('waiting'), 3000);
      }
    }, 800);
  };

  const confirmScan = async () => {
    if (!lastScan) return;
    const skuItem = skus.find(s => s.sku === lastScan.sku);
    if (!skuItem) return;
    let warehouseUuid = activeWarehouse.id === '2' ? "22222222-2222-2222-2222-222222222222" : activeWarehouse.id === '3' ? "33333333-3333-3333-3333-333333333333" : "11111111-1111-1111-1111-111111111111";

    if (scanMode === 'entrada') {
      const expirationDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000 * 6).toISOString().substring(0, 10);
      try {
        await apiClient.post('/inventory/entry', { product: skuItem.id, warehouse: warehouseUuid, lotNumber: lastScan.lote, quantity: 100, unitCost: skuItem.costo, expirationDate });
        setBatches(prev => [...prev, { id: `b-scan-${Date.now()}`, sku: lastScan.sku, codigoLote: lastScan.lote, cantidad: 100, fechaCaducidad: expirationDate, pasillo: lastScan.ubicacionSugerida.pasillo, rack: lastScan.ubicacionSugerida.rack, nivel: lastScan.ubicacionSugerida.nivel, fechaIngreso: new Date().toISOString().substring(0, 10) }]);
        setScanHistory(prev => [{ ...lastScan, fecha: new Date(), tipo: 'Ingreso' }, ...prev]);
      } catch (error) { console.error("Scan entry failed:", error); }
    } else {
      try {
        await apiClient.post('/inventory/consume', { product: skuItem.id, warehouse: warehouseUuid, quantity: 100 });
        const matchingBatches = [...batches].filter(b => b.sku === lastScan.sku).sort((a, b) => new Date(a.fechaCaducidad).getTime() - new Date(b.fechaCaducidad).getTime());
        let extractQty = 100;
        const updatedBatches = batches.map(b => {
          if (matchingBatches.length > 0 && b.id === matchingBatches[0].id) {
            const nextQty = Math.max(0, b.cantidad - extractQty);
            extractQty = Math.max(0, extractQty - b.cantidad);
            return { ...b, cantidad: nextQty };
          }
          return b;
        }).filter(b => b.cantidad > 0);
        setBatches(updatedBatches);
        setScanHistory(prev => [{ ...lastScan, fecha: new Date(), tipo: 'Egreso' }, ...prev]);
      } catch (error) { console.error("Scan consume failed:", error); }
    }
    setLastScan(null); setScanStatus('waiting');
  };

  const resetLastScan = () => { setLastScan(null); setScanStatus('waiting'); };

  return (
      <WmsContext.Provider value={{
        user, login, logout, registerUser, activeWarehouse, warehouses, skus, batches, reservations, policies, transfers, audits, notifications,
        fetchNotifications, searchQuery, setSearchQuery, setActiveWarehouse, updatePolicy, approveTransfer, rejectTransfer, registerCycleCount,
        approveRopReplenish, createReservation, confirmReservation, releaseReservation, createWarehouse, updateWarehouse, deleteWarehouse,
        activateWarehouse, createStorageLocation, getStorageLocationDetails, createProduct,
        scanMode, setScanMode, lastScan, scanStatus, scanHistory, isScannerFocused, setIsScannerFocused, processScan, confirmScan, resetLastScan
      }}>
        {children}
      </WmsContext.Provider>
  );
};

export const useWms = () => {
  const context = useContext(WmsContext);
  if (!context) throw new Error('useWms debe ser utilizado dentro de un WmsProvider');
  return context;
};