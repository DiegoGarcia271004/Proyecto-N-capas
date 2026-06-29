export interface SkuItem {
  id: string;
  sku: string;
  name: string;
  zona: 'A' | 'B' | 'C';
  stock: number;
  rop: number; // Reorder Point
  sugerido: number;
  costo: number; // Costo original
  costoPromedio: number; // Weighted Average Cost (WAC)
  unidad: string;
  peso: number; // en kg
  dimensiones: string; // "Alto x Ancho x Profundidad" en cm
  familia: 'Ferretería' | 'Eléctricos' | 'Químicos' | 'Logística';
}

export interface BatchItem {
  id: string;
  sku: string;
  codigoLote: string;
  cantidad: number;
  fechaCaducidad: string; // YYYY-MM-DD
  pasillo: string;
  rack: string;
  nivel: string;
  fechaIngreso: string; // YYYY-MM-DD
}

export interface Reservation {
  id: string;
  orden: string;
  cliente: string;
  almacenDestino: string;
  tiempoRestante: number; // en segundos
  estado: 'active' | 'warning' | 'expired';
  sku: string;
  cantidad: number;
}

export interface Warehouse {
  id: string;
  nombre: string;
  codigo: string;
}

export interface SpatialPolicy {
  id: string;
  nombre: string;
  descripcion: string;
  activa: boolean;
}

export interface TransferOrder {
  id: string;
  sku: string;
  cantidad: number;
  origen: string;
  destino: string;
  estado: 'pendiente' | 'aprobado' | 'rechazado';
  fecha: string;
}

export interface AuditRecord {
  id: string;
  sku: string;
  pasillo: string;
  rack: string;
  nivel: string;
  stockTeorico: number;
  stockContado: number;
  discrepancia: number;
  operario: string;
  fecha: string;
  estado: 'revisado' | 'pendiente';
}

export const INITIAL_WAREHOUSES: Warehouse[] = [
  { id: '1', nombre: 'Almacén Central Madrid', codigo: 'ALM-1' },
  { id: '2', nombre: 'Almacén Satélite Barcelona', codigo: 'ALM-2' },
  { id: '3', nombre: 'Almacén Pulmón Valencia', codigo: 'ALM-3' },
];

export const INITIAL_SKUS: SkuItem[] = [
  { id: '1', sku: 'SKU-99812', name: 'Tornillo Acero Inoxidable 1/2', zona: 'A', stock: 1250, rop: 1500, sugerido: 2000, costo: 0.12, costoPromedio: 0.15, unidad: 'unidades', peso: 0.02, dimensiones: '2x1x1 cm', familia: 'Ferretería' },
  { id: '2', sku: 'SKU-88291', name: 'Cable Eléctrico Cobre 10m', zona: 'A', stock: 45, rop: 100, sugerido: 250, costo: 11.20, costoPromedio: 12.50, unidad: 'rollos', peso: 1.5, dimensiones: '30x30x10 cm', familia: 'Eléctricos' },
  { id: '3', sku: 'SKU-77382', name: 'Interruptor Térmico 16A', zona: 'B', stock: 320, rop: 200, sugerido: 400, costo: 8.10, costoPromedio: 8.90, unidad: 'unidades', peso: 0.18, dimensiones: '8x5x6 cm', familia: 'Eléctricos' },
  { id: '4', sku: 'SKU-66291', name: 'Pintura Epóxica Industrial Gris 5G', zona: 'C', stock: 12, rop: 15, sugerido: 30, costo: 80.00, costoPromedio: 85.00, unidad: 'baldes', peso: 20.0, dimensiones: '40x35x35 cm', familia: 'Químicos' },
  { id: '5', sku: 'SKU-55481', name: 'Cinta Aislante Negra 20m', zona: 'C', stock: 680, rop: 500, sugerido: 800, costo: 0.95, costoPromedio: 1.20, unidad: 'rollos', peso: 0.08, dimensiones: '7x7x2 cm', familia: 'Eléctricos' },
  { id: '6', sku: 'SKU-44392', name: 'Fusible Cerámico Rápido 10A', zona: 'B', stock: 110, rop: 300, sugerido: 1000, costo: 0.38, costoPromedio: 0.45, unidad: 'unidades', peso: 0.005, dimensiones: '3x1x1 cm', familia: 'Eléctricos' },
  { id: '7', sku: 'SKU-33291', name: 'Tubo Termocontraíble 1m', zona: 'B', stock: 415, rop: 300, sugerido: 500, costo: 1.80, costoPromedio: 2.10, unidad: 'metros', peso: 0.05, dimensiones: '100x2x2 cm', familia: 'Logística' },
  { id: '8', sku: 'SKU-22198', name: 'Aceite Lubricante Multiuso 1L', zona: 'A', stock: 85, rop: 50, sugerido: 100, costo: 14.20, costoPromedio: 15.60, unidad: 'botellas', peso: 0.95, dimensiones: '22x8x8 cm', familia: 'Químicos' },
];

export const INITIAL_BATCHES: BatchItem[] = [
  // Lotes para SKU-99812 (Tornillo 1/2) - Ordenado por caducidad (FIFO)
  { id: 'b-1-1', sku: 'SKU-99812', codigoLote: 'LOTE-2026-A1', cantidad: 450, fechaCaducidad: '2026-08-15', pasillo: 'PASILLO 4', rack: 'RACK B', nivel: 'NIVEL 1', fechaIngreso: '2026-02-15' },
  { id: 'b-1-2', sku: 'SKU-99812', codigoLote: 'LOTE-2026-A2', cantidad: 500, fechaCaducidad: '2026-12-20', pasillo: 'PASILLO 4', rack: 'RACK B', nivel: 'NIVEL 2', fechaIngreso: '2026-04-10' },
  { id: 'b-1-3', sku: 'SKU-99812', codigoLote: 'LOTE-2026-A3', cantidad: 300, fechaCaducidad: '2027-04-05', pasillo: 'PASILLO 1', rack: 'RACK A', nivel: 'NIVEL 3', fechaIngreso: '2026-05-01' },

  // Lotes para SKU-88291 (Cable Cobre)
  { id: 'b-2-1', sku: 'SKU-88291', codigoLote: 'LOTE-2026-B1', cantidad: 15, fechaCaducidad: '2026-07-01', pasillo: 'PASILLO 1', rack: 'RACK A', nivel: 'NIVEL 1', fechaIngreso: '2026-01-10' },
  { id: 'b-2-2', sku: 'SKU-88291', codigoLote: 'LOTE-2026-B2', cantidad: 30, fechaCaducidad: '2027-02-18', pasillo: 'PASILLO 1', rack: 'RACK A', nivel: 'NIVEL 2', fechaIngreso: '2026-03-22' },

  // Lotes para SKU-77382 (Interruptor Térmico)
  { id: 'b-3-1', sku: 'SKU-77382', codigoLote: 'LOTE-2026-C1', cantidad: 120, fechaCaducidad: '2026-09-30', pasillo: 'PASILLO 3', rack: 'RACK C', nivel: 'NIVEL 1', fechaIngreso: '2026-02-28' },
  { id: 'b-3-2', sku: 'SKU-77382', codigoLote: 'LOTE-2026-C2', cantidad: 200, fechaCaducidad: '2027-06-15', pasillo: 'PASILLO 3', rack: 'RACK C', nivel: 'NIVEL 2', fechaIngreso: '2026-05-12' },

  // Lotes para SKU-66291 (Pintura Epóxica)
  { id: 'b-4-1', sku: 'SKU-66291', codigoLote: 'LOTE-2026-D1', cantidad: 4, fechaCaducidad: '2026-06-12', pasillo: 'PASILLO 5', rack: 'RACK A', nivel: 'NIVEL 1', fechaIngreso: '2026-03-01' },
  { id: 'b-4-2', sku: 'SKU-66291', codigoLote: 'LOTE-2026-D2', cantidad: 8, fechaCaducidad: '2026-08-25', pasillo: 'PASILLO 2', rack: 'RACK A', nivel: 'NIVEL 1', fechaIngreso: '2026-04-15' },

  // Lotes para SKU-55481 (Cinta Aislante)
  { id: 'b-5-1', sku: 'SKU-55481', codigoLote: 'LOTE-2026-E1', cantidad: 680, fechaCaducidad: '2027-10-01', pasillo: 'PASILLO 2', rack: 'RACK D', nivel: 'NIVEL 3', fechaIngreso: '2026-04-20' },
];

export const INITIAL_RESERVATIONS: Reservation[] = [
  { id: 'res-1', orden: 'ORD-2026-101', cliente: 'Logística Express S.A.', almacenDestino: 'Sevilla Hub', tiempoRestante: 180, estado: 'active', sku: 'SKU-99812', cantidad: 350 },
  { id: 'res-2', orden: 'ORD-2026-102', cliente: 'Suministros Industriales Sur', almacenDestino: 'Málaga Delegación', tiempoRestante: 45, estado: 'warning', sku: 'SKU-77382', cantidad: 50 },
  { id: 'res-3', orden: 'ORD-2026-103', cliente: 'Construcciones Norte S.L.', almacenDestino: 'Bilbao Almacén', tiempoRestante: 0, estado: 'expired', sku: 'SKU-66291', cantidad: 8 },
  { id: 'res-4', orden: 'ORD-2026-104', cliente: 'Redes y Comunicaciones Levante', almacenDestino: 'Alicante Sucursal', tiempoRestante: 420, estado: 'active', sku: 'SKU-88291', cantidad: 20 },
];

export const INITIAL_POLICIES: SpatialPolicy[] = [
  { id: 'pol-1', nombre: 'FIFO Estricto', descripcion: 'Ubica y extrae respetando rigurosamente las fechas de caducidad más antiguas.', activa: true },
  { id: 'pol-2', nombre: 'Optimización de Espacio', descripcion: 'Busca huecos vacíos de forma aleatoria para maximizar el uso espacial del almacén.', activa: false },
  { id: 'pol-3', nombre: 'Ubicación Fija por SKU', descripcion: 'Mantiene pasillos dedicados fijos para cada familia de productos.', activa: false },
];

export const INITIAL_TRANSFERS: TransferOrder[] = [
  { id: 'trans-101', sku: 'SKU-99812', cantidad: 200, origen: 'ALM-1', destino: 'ALM-2', estado: 'pendiente', fecha: '2026-06-08' },
  { id: 'trans-102', sku: 'SKU-77382', cantidad: 80, origen: 'ALM-1', destino: 'ALM-3', estado: 'pendiente', fecha: '2026-06-09' },
  { id: 'trans-103', sku: 'SKU-88291', cantidad: 10, origen: 'ALM-2', destino: 'ALM-1', estado: 'aprobado', fecha: '2026-06-05' },
];

export const INITIAL_AUDITS: AuditRecord[] = [
  { id: 'aud-101', sku: 'SKU-99812', pasillo: 'PASILLO 4', rack: 'RACK B', nivel: 'NIVEL 1', stockTeorico: 450, stockContado: 450, discrepancia: 0, operario: 'M. Gomez', fecha: '2026-06-02', estado: 'revisado' },
  { id: 'aud-102', sku: 'SKU-88291', pasillo: 'PASILLO 1', rack: 'RACK A', nivel: 'NIVEL 1', stockTeorico: 15, stockContado: 12, discrepancia: -3, operario: 'J. Perez', fecha: '2026-06-07', estado: 'pendiente' },
  { id: 'aud-103', sku: 'SKU-55481', pasillo: 'PASILLO 2', rack: 'RACK D', nivel: 'NIVEL 3', stockTeorico: 680, stockContado: 685, discrepancia: 5, operario: 'J. Perez', fecha: '2026-06-08', estado: 'pendiente' },
];
export interface ScanSimulation {
  barcode: string;
  sku: string;
  name: string;
  lote: string;
  ubicacionSugerida: {
    pasillo: string;
    rack: string;
    nivel: string;
  };
}

export const SCAN_SIMULATIONS: ScanSimulation[] = [
  { barcode: '742819280918', sku: 'SKU-99812', name: 'Tornillo Acero Inoxidable 1/2', lote: 'LOTE-2026-A4', ubicacionSugerida: { pasillo: 'PASILLO 4', rack: 'RACK B', nivel: 'NIVEL 3' } },
  { barcode: '742819280925', sku: 'SKU-88291', name: 'Cable Eléctrico Cobre 10m', lote: 'LOTE-2026-B3', ubicacionSugerida: { pasillo: 'PASILLO 1', rack: 'RACK A', nivel: 'NIVEL 3' } },
  { barcode: '742819280932', sku: 'SKU-77382', name: 'Interruptor Térmico 16A', lote: 'LOTE-2026-C3', ubicacionSugerida: { pasillo: 'PASILLO 3', rack: 'RACK C', nivel: 'NIVEL 3' } },
  { barcode: '742819280949', sku: 'SKU-66291', name: 'Pintura Epóxica Industrial Gris 5G', lote: 'LOTE-2026-D3', ubicacionSugerida: { pasillo: 'PASILLO 5', rack: 'RACK A', nivel: 'NIVEL 2' } },
  { barcode: '742819280956', sku: 'SKU-55481', name: 'Cinta Aislante Negra 20m', lote: 'LOTE-2026-E2', ubicacionSugerida: { pasillo: 'PASILLO 2', rack: 'RACK D', nivel: 'NIVEL 1' } }
];
