import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { Reservations } from '../../components/Dashboard/Reservations';
import { GitCompare, Check, X, ArrowRight, Clock } from 'lucide-react';

export const MovementsPanel: React.FC = () => {
  const { transfers, approveTransfer, rejectTransfer } = useWms();
  const [activeTab, setActiveTab] = useState<'transfers' | 'reservations'>('transfers');

  const pendingTransfers = transfers.filter(t => t.estado === 'pendiente');
  const historyTransfers = transfers.filter(t => t.estado !== 'pendiente');

  const getStatusBadge = (estado: 'pendiente' | 'aprobado' | 'rechazado') => {
    switch (estado) {
      case 'pendiente':
        return <span className="wms-badge wms-badge-warning" style={{ animation: 'none' }}>Pendiente</span>;
      case 'aprobado':
        return <span className="wms-badge wms-badge-active">Aprobado</span>;
      case 'rechazado':
        return <span className="wms-badge wms-badge-expired" style={{ animation: 'none' }}>Rechazado</span>;
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Bandeja de Movimientos de Inventario</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
          Gestión y aprobación de transferencias y control de reservas bloqueadas.
        </p>
      </div>

      {/* Selector de sub-vistas */}
      <div className="wms-filter-tabs" style={{ marginBottom: '20px', display: 'inline-flex' }}>
        <button 
          className={`wms-filter-tab ${activeTab === 'transfers' ? 'active' : ''}`}
          onClick={() => setActiveTab('transfers')}
          style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
        >
          <GitCompare size={14} />
          Traslados de Almacén ({pendingTransfers.length})
        </button>
        <button 
          className={`wms-filter-tab ${activeTab === 'reservations' ? 'active' : ''}`}
          onClick={() => setActiveTab('reservations')}
          style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
        >
          <Clock size={14} />
          Reservas FIFO en Espera
        </button>
      </div>

      {activeTab === 'transfers' ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {/* Bandeja de pendientes */}
          <div className="wms-card">
            <div className="wms-card-title" style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Traslados Pendientes de Autorización</span>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Requiere aprobación del Jefe de Almacén</span>
            </div>

            <div className="wms-table-container">
              {pendingTransfers.length === 0 ? (
                <div style={{ padding: '24px', textAlign: 'center', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                  No hay solicitudes de traslados pendientes de aprobación.
                </div>
              ) : (
                <table className="wms-table">
                  <thead>
                    <tr>
                      <th>ID Traslado</th>
                      <th>SKU</th>
                      <th style={{ textAlign: 'right' }}>Cantidad</th>
                      <th>Ruta</th>
                      <th>Fecha</th>
                      <th style={{ textAlign: 'center' }}>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pendingTransfers.map((trans) => (
                      <tr key={trans.id}>
                        <td style={{ fontWeight: 600, color: 'var(--text-main)' }}>{trans.id}</td>
                        <td style={{ fontWeight: 600, color: 'var(--color-primary)' }}>{trans.sku}</td>
                        <td style={{ textAlign: 'right', fontWeight: 700 }}>{trans.cantidad}</td>
                        <td>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }}>
                            <span style={{ color: 'var(--text-muted)' }}>{trans.origen}</span>
                            <ArrowRight size={12} style={{ color: 'var(--color-primary)' }} />
                            <span style={{ color: 'var(--text-main)', fontWeight: 600 }}>{trans.destino}</span>
                          </div>
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>{trans.fecha}</td>
                        <td style={{ textAlign: 'center' }}>
                          <div style={{ display: 'inline-flex', gap: '8px' }}>
                            <button
                              onClick={() => approveTransfer(trans.id)}
                              className="wms-btn wms-btn-success wms-btn-sm"
                              style={{ padding: '4px 8px', borderRadius: '4px' }}
                            >
                              <Check size={12} /> Aprobar
                            </button>
                            <button
                              onClick={() => rejectTransfer(trans.id)}
                              className="wms-btn wms-btn-outline wms-btn-sm"
                              style={{ padding: '4px 8px', borderRadius: '4px', color: 'var(--color-danger)', borderColor: 'rgba(239,68,68,0.2)' }}
                            >
                              <X size={12} /> Rechazar
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>

          {/* Historial de traslados */}
          <div className="wms-card" style={{ opacity: 0.85 }}>
            <div className="wms-card-title">Historial de Traslados Recientes</div>
            <div className="wms-table-container">
              {historyTransfers.length === 0 ? (
                <div style={{ padding: '16px', textAlign: 'center', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                  Ningún traslado procesado recientemente.
                </div>
              ) : (
                <table className="wms-table">
                  <thead>
                    <tr>
                      <th>ID Traslado</th>
                      <th>SKU</th>
                      <th style={{ textAlign: 'right' }}>Cantidad</th>
                      <th>Ruta</th>
                      <th>Fecha</th>
                      <th style={{ textAlign: 'center' }}>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {historyTransfers.map((trans) => (
                      <tr key={trans.id}>
                        <td>{trans.id}</td>
                        <td>{trans.sku}</td>
                        <td style={{ textAlign: 'right' }}>{trans.cantidad}</td>
                        <td>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }}>
                            <span>{trans.origen}</span>
                            <ArrowRight size={10} />
                            <span>{trans.destino}</span>
                          </div>
                        </td>
                        <td>{trans.fecha}</td>
                        <td style={{ textAlign: 'center' }}>
                          {getStatusBadge(trans.estado)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </div>
      ) : (
        <Reservations />
      )}
    </div>
  );
};
