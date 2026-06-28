import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { ChevronDown, ChevronUp, Calendar, MapPin, Layers, Weight, RefreshCw } from 'lucide-react';

const fuzzyMatch = (str: string, query: string): boolean => {
  if (!query) return true;
  const cleanStr = str.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  const cleanQuery = query.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  
  let queryIdx = 0;
  for (let i = 0; i < cleanStr.length && queryIdx < cleanQuery.length; i++) {
    if (cleanStr[i] === cleanQuery[queryIdx]) {
      queryIdx++;
    }
  }
  return queryIdx === cleanQuery.length;
};

export const InventoryCatalog: React.FC = () => {
  const { skus, batches, searchQuery } = useWms();
  const [expandedSku, setExpandedSku] = useState<string | null>(null);
  const [selectedFamily, setSelectedFamily] = useState<string>('all');

  const toggleExpand = (sku: string) => {
    setExpandedSku(expandedSku === sku ? null : sku);
  };

  const filteredSkus = skus.filter(sku => {
    const matchesFuzzy = fuzzyMatch(sku.sku + ' ' + sku.name, searchQuery);
    const matchesFamily = selectedFamily === 'all' ? true : sku.familia === selectedFamily;
    return matchesFuzzy && matchesFamily;
  });

  return (
    <div>
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Catálogo y Trazabilidad FIFO</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
            Auditoría de lotes activos ordenados por caducidad (Método FIFO)
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <select 
            className="wms-warehouse-selector"
            value={selectedFamily}
            onChange={(e) => setSelectedFamily(e.target.value)}
          >
            <option value="all">Todas las Familias</option>
            <option value="Ferretería">Ferretería</option>
            <option value="Eléctricos">Eléctricos</option>
            <option value="Químicos">Químicos</option>
            <option value="Logística">Logística</option>
          </select>
        </div>
      </div>

      <div className="wms-card" style={{ padding: '0px', overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }} className="wms-table-container">
          <table className="wms-table" style={{ margin: 0 }}>
            <thead>
              <tr style={{ backgroundColor: 'rgba(255,255,255,0.01)' }}>
                <th style={{ width: '48px' }}></th>
                <th>SKU</th>
                <th>Nombre del Artículo</th>
                <th>Familia</th>
                <th style={{ textAlign: 'right' }}>Lotes Activos</th>
                <th style={{ textAlign: 'right' }}>Stock Total</th>
                <th style={{ textAlign: 'right' }}>Costo Promedio (WAC)</th>
              </tr>
            </thead>
            <tbody>
              {filteredSkus.map((sku) => {
                const skuBatches = batches
                  .filter(b => b.sku === sku.sku)
                  // Regla FIFO: los más viejos de caducidad van arriba
                  .sort((a, b) => new Date(a.fechaCaducidad).getTime() - new Date(b.fechaCaducidad).getTime());

                const isExpanded = expandedSku === sku.sku;
                const isUnderStock = sku.stock <= sku.rop;

                return (
                  <React.Fragment key={sku.id}>
                    {/* Fila principal */}
                    <tr 
                      onClick={() => toggleExpand(sku.sku)}
                      style={{ 
                        cursor: 'pointer',
                        borderLeft: isUnderStock ? '3px solid var(--color-danger)' : 'none',
                        backgroundColor: isExpanded ? 'rgba(255,255,255,0.02)' : 'transparent'
                      }}
                    >
                      <td style={{ textAlign: 'center' }}>
                        {isExpanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                      </td>
                      <td>
                        <span style={{ fontWeight: 700, color: 'var(--color-primary)' }}>{sku.sku}</span>
                      </td>
                      <td>
                        <div style={{ fontWeight: 600, color: 'var(--text-main)' }}>{sku.name}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', gap: '8px', marginTop: '2px' }}>
                          <span style={{ display: 'flex', alignItems: 'center', gap: '2px' }}><Weight size={10} /> {sku.peso} kg</span>
                          <span>|</span>
                          <span>Dim: {sku.dimensiones}</span>
                        </div>
                      </td>
                      <td>
                        <span style={{ 
                          fontSize: '0.75rem', 
                          padding: '2px 6px', 
                          borderRadius: '4px',
                          backgroundColor: 'rgba(255,255,255,0.04)',
                          color: 'var(--text-muted)'
                        }}>
                          {sku.familia}
                        </span>
                      </td>
                      <td style={{ textAlign: 'right', fontWeight: 600 }}>
                        {skuBatches.length} lotes
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <span style={{ 
                          fontWeight: 700,
                          color: isUnderStock ? 'var(--color-danger)' : 'var(--text-main)'
                        }}>
                          {sku.stock.toLocaleString('es-ES')}
                        </span>
                        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginLeft: '4px' }}>{sku.unidad}</span>
                      </td>
                      <td style={{ textAlign: 'right', fontWeight: 600, color: 'var(--color-success)' }}>
                        {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(sku.costoPromedio)}
                      </td>
                    </tr>

                    {/* Fila Expandida (Accordion de lotes FIFO) */}
                    {isExpanded && (
                      <tr>
                        <td colSpan={7} style={{ padding: '0px', backgroundColor: '#0d1322' }}>
                          <div style={{ padding: '16px 24px 20px 48px' }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '12px', fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                              <Layers size={12} /> Desglose de lotes activos (Prioridad FIFO)
                            </div>
                            
                            {skuBatches.length === 0 ? (
                              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontStyle: 'italic', padding: '12px 0' }}>
                                ⚠️ No hay lotes activos asignados para este SKU en el almacén.
                              </div>
                            ) : (
                              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                {skuBatches.map((batch, idx) => {
                                  const isExpired = new Date(batch.fechaCaducidad).getTime() < Date.now();
                                  return (
                                    <div 
                                      key={batch.id}
                                      style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'space-between',
                                        padding: '10px 16px',
                                        backgroundColor: 'rgba(255,255,255,0.02)',
                                        border: '1px solid rgba(255,255,255,0.05)',
                                        borderRadius: '8px',
                                        fontSize: '0.85rem'
                                      }}
                                    >
                                      <div>
                                        <span style={{ fontWeight: 700, color: 'var(--text-main)' }}>{batch.codigoLote}</span>
                                        <span style={{ 
                                          fontSize: '0.7rem', 
                                          marginLeft: '8px', 
                                          backgroundColor: idx === 0 ? 'rgba(6, 182, 212, 0.1)' : 'rgba(255,255,255,0.03)',
                                          color: idx === 0 ? 'var(--color-primary)' : 'var(--text-muted)',
                                          padding: '2px 6px',
                                          borderRadius: '4px',
                                          fontWeight: idx === 0 ? 'bold' : 'normal'
                                        }}>
                                          {idx === 0 ? 'Siguiente Salida (FIFO)' : `Lote #${idx + 1}`}
                                        </span>
                                      </div>

                                      <div style={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--text-muted)' }}>
                                          <MapPin size={14} />
                                          <span>{batch.pasillo} - {batch.rack} ({batch.nivel})</span>
                                        </div>

                                        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: isExpired ? 'var(--color-danger)' : 'var(--text-muted)' }}>
                                          <Calendar size={14} />
                                          <span>Exp: {batch.fechaCaducidad}</span>
                                        </div>

                                        <div style={{ fontWeight: 700, color: 'var(--text-main)', minWidth: '80px', textAlign: 'right' }}>
                                          {batch.cantidad} uds
                                        </div>
                                      </div>
                                    </div>
                                  );
                                })}
                              </div>
                            )}
                          </div>
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                );
              })}
            </tbody>
          </table>
        </div>

        {/* Paginador visual de simulación de millones de SKUs */}
        <div style={{ 
          padding: '16px 20px', 
          backgroundColor: 'rgba(255,255,255,0.01)', 
          borderTop: '1px solid var(--border-color)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          fontSize: '0.8rem',
          color: 'var(--text-muted)'
        }}>
          <div>
            Mostrando <strong>{filteredSkus.length}</strong> de <strong>1,500,000</strong> productos (Simulación de Virtualización)
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <RefreshCw size={12} className="pulse-animation" />
            <span>Carga diferida activada</span>
          </div>
        </div>
      </div>
    </div>
  );
};
