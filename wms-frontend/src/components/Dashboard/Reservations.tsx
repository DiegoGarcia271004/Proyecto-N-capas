import React from 'react';
import { useWms } from '../../context/WmsContext';
import { Clock, Hourglass, AlertCircle } from 'lucide-react';

export const Reservations: React.FC = () => {

  const { reservations } = useWms();

  // Formatear segundos a MM:SS
  const formatTime = (secs: number) => {
    if (secs <= 0) return '00:00';
    const minutes = Math.floor(secs / 60);
    const remainingSeconds = secs % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const getStatusBadge = (estado: 'active' | 'warning' | 'expired') => {
    switch (estado) {
      case 'active':
        return <span className="wms-badge wms-badge-active">Reservado</span>;
      case 'warning':
        return <span className="wms-badge wms-badge-warning">Por Expirar</span>;
      case 'expired':
        return <span className="wms-badge wms-badge-expired">Timeout / Liberado</span>;
    }
  };

  const getTimeColor = (estado: 'active' | 'warning' | 'expired') => {
    switch (estado) {
      case 'active': return 'var(--color-success)';
      case 'warning': return 'var(--color-warning)';
      case 'expired': return 'var(--color-danger)';
    }
  };

  return (
    <div className="wms-card wms-dashboard-row-3">
      <div className="wms-card-title">
        <Clock size={18} style={{ color: 'var(--color-primary)' }} />
        <span>Reservas en Tiempo Real y Control de Timeouts (FIFO)</span>
      </div>

      <div className="wms-table-container">
        <table className="wms-table">
          <thead>
            <tr>
              <th>Código Orden</th>
              <th>Cliente</th>
              <th>Almacén Destino</th>
              <th>Artículo (Cant.)</th>
              <th style={{ textAlign: 'center' }}>Tiempo Restante</th>
              <th style={{ textAlign: 'center' }}>Estado</th>
            </tr>
          </thead>
          <tbody>
            {reservations.map((res) => (
              <tr key={res.id} style={{ opacity: res.estado === 'expired' ? 0.6 : 1 }}>
                <td style={{ fontWeight: 600, color: 'var(--text-main)' }}>{res.orden}</td>
                <td>{res.cliente}</td>
                <td>{res.almacenDestino}</td>
                <td>
                  <span style={{ fontWeight: 500 }}>{res.sku}</span>
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}> ({res.cantidad} und)</span>
                </td>
                <td style={{ textAlign: 'center' }}>
                  <div style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center', 
                    gap: '6px',
                    fontWeight: 700,
                    fontFamily: 'monospace',
                    fontSize: '1.05rem',
                    color: getTimeColor(res.estado)
                  }}>
                    {res.estado === 'expired' ? (
                      <AlertCircle size={16} />
                    ) : (
                      <Hourglass size={16} className={res.estado === 'warning' ? 'pulse-animation' : ''} />
                    )}
                    {formatTime(res.tiempoRestante)}
                  </div>
                </td>
                <td style={{ textAlign: 'center' }}>
                  {getStatusBadge(res.estado)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
