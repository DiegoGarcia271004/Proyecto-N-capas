import React from 'react';
import { useWms } from '../../context/WmsContext';
import { RefreshCw, Play, CheckCircle2 } from 'lucide-react';

export const RopTable: React.FC = () => {

  const { skus, approveRopReplenish } = useWms();

  const reorderSkus = skus.filter(sku => sku.stock <= sku.rop);

  return (
    <div className="wms-card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div className="wms-card-title" style={{ justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <RefreshCw size={18} style={{ color: 'var(--color-warning)' }} />
          <span>Propuestas de Reabastecimiento (ROP)</span>
        </div>
        <span style={{ 
          fontSize: '0.75rem', 
          backgroundColor: reorderSkus.length > 0 ? 'rgba(239, 68, 68, 0.1)' : 'rgba(16, 185, 129, 0.1)', 
          color: reorderSkus.length > 0 ? 'var(--color-danger)' : 'var(--color-success)',
          padding: '2px 8px',
          borderRadius: '4px',
          fontWeight: 600
        }}>
          {reorderSkus.length} pendientes
        </span>
      </div>

      <div style={{ flex: 1, overflowY: 'auto' }} className="wms-table-container">
        {reorderSkus.length === 0 ? (
          <div style={{ 
            height: '100%', 
            display: 'flex', 
            flexDirection: 'column', 
            alignItems: 'center', 
            justifyContent: 'center', 
            color: 'var(--text-muted)',
            textAlign: 'center',
            padding: '40px 20px',
            gap: '12px'
          }}>
            <CheckCircle2 size={40} style={{ color: 'var(--color-success)' }} />
            <div>
              <p style={{ fontWeight: 600, color: 'var(--text-main)' }}>Stock Óptimo</p>
              <p style={{ fontSize: '0.85rem' }}>No hay SKUs por debajo del Punto de Reorden (ROP).</p>
            </div>
          </div>
        ) : (
          <table className="wms-table">
            <thead>
              <tr>
                <th>SKU / Artículo</th>
                <th style={{ textAlign: 'right' }}>Stock / ROP</th>
                <th style={{ textAlign: 'right' }}>Sugerido</th>
                <th style={{ textAlign: 'center' }}>Acción</th>
              </tr>
            </thead>
            <tbody>
              {reorderSkus.map((sku) => (
                <tr key={sku.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--text-main)' }}>{sku.sku}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', maxWidth: '180px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {sku.name}
                    </div>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <span style={{ color: 'var(--color-danger)', fontWeight: 600 }}>{sku.stock}</span>
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}> / {sku.rop}</span>
                  </td>
                  <td style={{ textAlign: 'right', fontWeight: 500, color: 'var(--color-primary)' }}>
                    +{sku.sugerido} {sku.unidad}
                  </td>
                  <td style={{ textAlign: 'center' }}>
                    <button 
                      className="wms-btn wms-btn-primary wms-btn-sm" 
                      onClick={() => approveRopReplenish(sku.id)}
                      style={{ padding: '6px 12px' }}
                    >
                      <Play size={12} fill="currentColor" />
                      Aprobar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};
