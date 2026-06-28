import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { ChevronDown, ChevronUp, Calendar, MapPin, Layers, Weight, RefreshCw, Plus, X } from 'lucide-react';

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
  const { skus, batches, searchQuery, createProduct, fetchSkus } = useWms();
  const [expandedSku, setExpandedSku] = useState<string | null>(null);
  const [selectedFamily, setSelectedFamily] = useState<string>('all');
  
  // Modal states
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Form states
  const [skuCode, setSkuCode] = useState('');
  const [name, setName] = useState('');
  const [category, setCategory] = useState('ELECTRONICS');
  const [minStockLevel, setMinStockLevel] = useState(10);
  const [reorderPoint, setReorderPoint] = useState(30);
  const [weightValue, setWeightValue] = useState(1.0);
  const [weightUnit, setWeightUnit] = useState<'KG' | 'LBS' | 'OZ' | 'G'>('KG');
  const [dimHeight, setDimHeight] = useState(10);
  const [dimWidth, setDimWidth] = useState(10);
  const [dimDepth, setDimDepth] = useState(10);
  const [dimUnit, setDimUnit] = useState<'CM' | 'M' | 'IN' | 'FT'>('CM');
  const [requirements, setRequirements] = useState<'AMBIENT' | 'REFRIGERATED' | 'FROZEN' | 'CONTROLLED_TEMP' | 'DRY' | 'HAZARDOUS' | 'HIGH_SECURITY' | 'FLAMMABLE' | 'FRAGILE' | 'OVERSIZED'>('AMBIENT');
  const [leadTimeDays, setLeadTimeDays] = useState(5);
  const [safetyStock, setSafetyStock] = useState(15);

  const toggleExpand = (sku: string) => {
    setExpandedSku(expandedSku === sku ? null : sku);
  };

  const filteredSkus = skus.filter(sku => {
    const matchesFuzzy = fuzzyMatch(sku.sku + ' ' + sku.name, searchQuery);
    const matchesFamily = selectedFamily === 'all' ? true : sku.familia === selectedFamily;
    return matchesFuzzy && matchesFamily;
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);
    setSuccessMsg(null);

    if (!skuCode.trim() || !name.trim()) {
      setErrorMsg('El código SKU y el Nombre son obligatorios.');
      return;
    }

    const payload = {
      sku: skuCode.trim(),
      name: name.trim(),
      category: category as any,
      minStockLevel: Number(minStockLevel),
      reorderPoint: Number(reorderPoint),
      weight: {
        value: Number(weightValue),
        unit: weightUnit
      },
      dimensions: {
        height: Number(dimHeight),
        width: Number(dimWidth),
        depth: Number(dimDepth),
        unit: dimUnit
      },
      requirements: requirements as any,
      leadTimeDays: Number(leadTimeDays),
      safetyStock: Number(safetyStock)
    };

    const success = await createProduct(payload);
    if (success) {
      setSuccessMsg('¡Producto registrado con éxito en el servidor!');
      fetchSkus();
      setTimeout(() => {
        setIsModalOpen(false);
        setSuccessMsg(null);
        // Reset
        setSkuCode('');
        setName('');
        setMinStockLevel(10);
        setReorderPoint(30);
        setWeightValue(1.0);
        setDimHeight(10);
        setDimWidth(10);
        setDimDepth(10);
        setLeadTimeDays(5);
        setSafetyStock(15);
      }, 1500);
    } else {
      setErrorMsg('Error al registrar el producto en el servidor. Revisa los datos.');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Catálogo y Trazabilidad FIFO</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
            Auditoría de lotes activos ordenados por caducidad (Método FIFO)
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
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

          <button
            onClick={() => setIsModalOpen(true)}
            className="wms-btn wms-btn-primary"
            style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 16px', fontSize: '0.85rem' }}
          >
            <Plus size={16} />
            <span>Nuevo Producto</span>
          </button>
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
      {/* Modal de Registro de Producto */}
      {isModalOpen && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(9, 13, 22, 0.85)',
          backdropFilter: 'blur(8px)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000,
          padding: '20px'
        }}>
          <div style={{
            backgroundColor: '#0d1322',
            border: '1px solid var(--border-color)',
            borderRadius: '16px',
            padding: '24px',
            width: '100%',
            maxWidth: '650px',
            maxHeight: '90vh',
            overflowY: 'auto',
            boxShadow: '0 20px 40px rgba(0,0,0,0.5)',
            position: 'relative'
          }}>
            <button
              onClick={() => setIsModalOpen(false)}
              style={{
                position: 'absolute',
                top: '16px',
                right: '16px',
                background: 'none',
                border: 'none',
                color: 'var(--text-muted)',
                cursor: 'pointer'
              }}
            >
              <X size={20} />
            </button>

            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '4px' }}>
              Registrar Nuevo Producto
            </h3>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '20px' }}>
              Mapeado con el DTO `CreateProductRequest` del backend Spring Boot.
            </p>

            {errorMsg && (
              <div className="wms-alert wms-alert-danger" style={{ marginBottom: '16px', padding: '10px 14px', fontSize: '0.85rem' }}>
                {errorMsg}
              </div>
            )}

            {successMsg && (
              <div className="wms-alert wms-alert-success" style={{ marginBottom: '16px', padding: '10px 14px', fontSize: '0.85rem' }}>
                {successMsg}
              </div>
            )}

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>SKU Código *</label>
                  <input
                    type="text"
                    value={skuCode}
                    onChange={(e) => setSkuCode(e.target.value)}
                    className="wms-search-input"
                    placeholder="Ej: SKU-NUEVO-1"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    required
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Nombre *</label>
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="wms-search-input"
                    placeholder="Ej: Cable de Cobre"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    required
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Categoría (ProductCategory)</label>
                  <select
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    className="wms-warehouse-selector"
                    style={{ width: '100%', height: '40px', backgroundColor: '#0d1322' }}
                  >
                    <option value="ELECTRONICS">ELECTRONICS (Electrónica)</option>
                    <option value="CHEMICALS">CHEMICALS (Químicos)</option>
                    <option value="FOOD_PERISHABLE">FOOD_PERISHABLE (Comida Perecedera)</option>
                    <option value="FOOD_NON_PERISHABLE">FOOD_NON_PERISHABLE (No Perecedera)</option>
                    <option value="BEVERAGES">BEVERAGES (Bebidas)</option>
                    <option value="FROZEN">FROZEN (Congelados)</option>
                    <option value="PHARMACEUTICAL">PHARMACEUTICAL (Farmacéutica)</option>
                    <option value="TOOLS">TOOLS (Herramientas)</option>
                    <option value="OTHER">OTHER (Otros)</option>
                  </select>
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Requisito Almacenamiento (StorageRequirement)</label>
                  <select
                    value={requirements}
                    onChange={(e) => setRequirements(e.target.value as any)}
                    className="wms-warehouse-selector"
                    style={{ width: '100%', height: '40px', backgroundColor: '#0d1322' }}
                  >
                    <option value="AMBIENT">AMBIENT (Ambiente)</option>
                    <option value="REFRIGERATED">REFRIGERATED (Refrigeración)</option>
                    <option value="FROZEN">FROZEN (Congelación)</option>
                    <option value="DRY">DRY (Seco)</option>
                    <option value="HAZARDOUS">HAZARDOUS (Peligroso)</option>
                    <option value="FLAMMABLE">FLAMMABLE (Inflamable)</option>
                    <option value="FRAGILE">FRAGILE (Frágil)</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Stock Mínimo (minStockLevel)</label>
                  <input
                    type="number"
                    value={minStockLevel}
                    onChange={(e) => setMinStockLevel(Number(e.target.value))}
                    className="wms-search-input"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    min="1"
                    required
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Punto de Reorden (reorderPoint)</label>
                  <input
                    type="number"
                    value={reorderPoint}
                    onChange={(e) => setReorderPoint(Number(e.target.value))}
                    className="wms-search-input"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    min="1"
                    required
                  />
                </div>
              </div>

              <div style={{ border: '1px solid rgba(255,255,255,0.05)', padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.01)' }}>
                <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-main)', display: 'block', marginBottom: '8px' }}>Peso del Producto (Weight DTO)</span>
                <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '12px' }}>
                  <input
                    type="number"
                    step="0.01"
                    value={weightValue}
                    onChange={(e) => setWeightValue(Number(e.target.value))}
                    className="wms-search-input"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    min="0.01"
                    required
                  />
                  <select
                    value={weightUnit}
                    onChange={(e) => setWeightUnit(e.target.value as any)}
                    className="wms-warehouse-selector"
                    style={{ width: '100%', height: '40px', backgroundColor: '#0d1322' }}
                  >
                    <option value="KG">KG</option>
                    <option value="LBS">LBS</option>
                    <option value="G">G</option>
                    <option value="OZ">OZ</option>
                  </select>
                </div>
              </div>

              <div style={{ border: '1px solid rgba(255,255,255,0.05)', padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.01)' }}>
                <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-main)', display: 'block', marginBottom: '8px' }}>Dimensiones del Producto (Dimensions DTO)</span>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr', gap: '8px' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.65rem', color: 'var(--text-muted)', marginBottom: '2px' }}>Alto</label>
                    <input
                      type="number"
                      value={dimHeight}
                      onChange={(e) => setDimHeight(Number(e.target.value))}
                      className="wms-search-input"
                      style={{ width: '100%', height: '36px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '6px', color: 'var(--text-main)', padding: '0 8px' }}
                      min="1"
                      required
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.65rem', color: 'var(--text-muted)', marginBottom: '2px' }}>Ancho</label>
                    <input
                      type="number"
                      value={dimWidth}
                      onChange={(e) => setDimWidth(Number(e.target.value))}
                      className="wms-search-input"
                      style={{ width: '100%', height: '36px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '6px', color: 'var(--text-main)', padding: '0 8px' }}
                      min="1"
                      required
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.65rem', color: 'var(--text-muted)', marginBottom: '2px' }}>Profundidad</label>
                    <input
                      type="number"
                      value={dimDepth}
                      onChange={(e) => setDimDepth(Number(e.target.value))}
                      className="wms-search-input"
                      style={{ width: '100%', height: '36px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '6px', color: 'var(--text-main)', padding: '0 8px' }}
                      min="1"
                      required
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.65rem', color: 'var(--text-muted)', marginBottom: '2px' }}>Unidad</label>
                    <select
                      value={dimUnit}
                      onChange={(e) => setDimUnit(e.target.value as any)}
                      className="wms-warehouse-selector"
                      style={{ width: '100%', height: '36px', backgroundColor: '#0d1322', padding: '0' }}
                    >
                      <option value="CM">CM</option>
                      <option value="M">M</option>
                      <option value="IN">IN</option>
                      <option value="FT">FT</option>
                    </select>
                  </div>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Lead Time (Días)</label>
                  <input
                    type="number"
                    value={leadTimeDays}
                    onChange={(e) => setLeadTimeDays(Number(e.target.value))}
                    className="wms-search-input"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    min="1"
                    required
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Safety Stock (Stock Seguridad)</label>
                  <input
                    type="number"
                    value={safetyStock}
                    onChange={(e) => setSafetyStock(Number(e.target.value))}
                    className="wms-search-input"
                    style={{ width: '100%', height: '40px', backgroundColor: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)', borderRadius: '8px', color: 'var(--text-main)', padding: '0 12px' }}
                    min="1"
                    required
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '12px' }}>
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="wms-btn wms-btn-outline"
                  style={{ padding: '10px 20px', fontSize: '0.85rem' }}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="wms-btn wms-btn-primary"
                  style={{ padding: '10px 20px', fontSize: '0.85rem' }}
                >
                  Guardar Producto
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
