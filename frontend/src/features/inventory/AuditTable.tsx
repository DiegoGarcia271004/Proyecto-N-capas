import React from 'react';
import { useWms } from '../../context/WmsContext';
import { ClipboardCheck, CheckCircle2, AlertTriangle, User } from 'lucide-react';

export const AuditTable: React.FC = () => {
  const { audits } = useWms();

  const getDiscrepancyStyle = (disc: number) => {
    if (disc === 0) return { color: 'var(--color-success)', fontWeight: 700 };
    if (disc < 0) return { color: 'var(--color-danger)', fontWeight: 700 };
    return { color: 'var(--color-warning)', fontWeight: 700 };
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Registro Global de Auditorías y Conteos</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
          Módulo de control del Administrador. Vista de solo lectura de discrepancias físicas reportadas por los operarios.
        </p>
      </div>

      <div className="wms-card" style={{ padding: '20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px', color: 'var(--color-primary)' }}>
          <ClipboardCheck size={20} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Discrepancias de Stock Registradas</h3>
        </div>

        <div className="wms-table-container">
          <table className="wms-table">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>SKU</th>
                <th>Ubicación</th>
                <th style={{ textAlign: 'right' }}>Teórico</th>
                <th style={{ textAlign: 'right' }}>Contado</th>
                <th style={{ textAlign: 'right' }}>Diferencia</th>
                <th>Operario</th>
                <th style={{ textAlign: 'center' }}>Estado</th>
              </tr>
            </thead>
            <tbody>
              {audits.map((aud) => (
                <tr key={aud.id}>
                  <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>{aud.fecha}</td>
                  <td style={{ fontWeight: 600, color: 'var(--text-main)' }}>{aud.sku}</td>
                  <td>
                    <span style={{ 
                      fontSize: '0.75rem', 
                      backgroundColor: 'rgba(255,255,255,0.04)', 
                      padding: '3px 6px', 
                      borderRadius: '4px',
                      fontFamily: 'monospace'
                    }}>
                      {aud.pasillo} - {aud.rack} ({aud.nivel})
                    </span>
                  </td>
                  <td style={{ textAlign: 'right', color: 'var(--text-muted)' }}>{aud.stockTeorico}</td>
                  <td style={{ textAlign: 'right', fontWeight: 500, color: 'var(--text-main)' }}>{aud.stockContado}</td>
                  <td style={{ textAlign: 'right', ...getDiscrepancyStyle(aud.discrepancia) }}>
                    {aud.discrepancia > 0 ? `+${aud.discrepancia}` : aud.discrepancia}
                  </td>
                  <td style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }}>
                    <User size={12} style={{ color: 'var(--text-muted)' }} />
                    <span>{aud.operario}</span>
                  </td>
                  <td style={{ textAlign: 'center' }}>
                    {aud.discrepancia === 0 ? (
                      <span className="wms-badge wms-badge-active" style={{ fontSize: '0.7rem' }}>
                        <CheckCircle2 size={10} style={{ marginRight: '3px' }} /> Cuadrado
                      </span>
                    ) : (
                      <span className="wms-badge wms-badge-warning" style={{ fontSize: '0.7rem', animation: 'none' }}>
                        <AlertTriangle size={10} style={{ marginRight: '3px' }} /> Discrepancia
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
