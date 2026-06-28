import React from 'react';
import { useWms } from '../../context/WmsContext';
import { Package, AlertTriangle, DollarSign, Clock, ArrowUpRight, ArrowDownRight } from 'lucide-react';

export const KpiCards: React.FC = () => {

  const { skus, reservations } = useWms();

  const stockTotal = skus.reduce((acc, item) => acc + item.stock, 0);
  
  const alertasStock = skus.filter(item => item.stock <= item.rop).length;
  
  const valorInventario = skus.reduce((acc, item) => acc + (item.stock * item.costo), 0);
  
  const pendientes = reservations.filter(r => r.estado !== 'expired').length;

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  return (
    <div className="wms-kpi-grid">
      {/* Tarjeta 1: Stock Total */}
      <div className="wms-card wms-card-cyan">
        <div className="wms-kpi-header">
          <span>Stock Total</span>
          <Package size={20} className="wms-kpi-icon" style={{ color: 'var(--color-primary)' }} />
        </div>
        <div className="wms-kpi-value">
          {stockTotal.toLocaleString('es-ES')}
        </div>
        <div className="wms-kpi-change up">
          <ArrowUpRight size={14} />
          <span>+4.2% esta semana</span>
        </div>
      </div>

      {/* Tarjeta 2: Alertas de Stock */}
      <div className="wms-card wms-card-danger">
        <div className="wms-kpi-header">
          <span>Alertas de Stock</span>
          <AlertTriangle size={20} className="wms-kpi-icon" style={{ color: 'var(--color-danger)' }} />
        </div>
        <div className="wms-kpi-value" style={{ color: alertasStock > 0 ? 'var(--color-danger)' : 'var(--color-success)' }}>
          {alertasStock}
        </div>
        <div className={`wms-kpi-change ${alertasStock > 3 ? 'down' : 'up'}`}>
          {alertasStock > 3 ? <ArrowDownRight size={14} /> : <ArrowUpRight size={14} />}
          <span>{alertasStock > 0 ? `${alertasStock} SKUs bajo nivel mínimo` : 'Todo en orden'}</span>
        </div>
      </div>

      {/* Tarjeta 3: Valor del Inventario */}
      <div className="wms-card wms-card-success">
        <div className="wms-kpi-header">
          <span>Valor del Inventario</span>
          <DollarSign size={20} className="wms-kpi-icon" style={{ color: 'var(--color-success)' }} />
        </div>
        <div className="wms-kpi-value" style={{ color: 'var(--color-success)' }}>
          {formatCurrency(valorInventario)}
        </div>
        <div className="wms-kpi-change up">
          <ArrowUpRight size={14} />
          <span>+1.8% vs mes anterior</span>
        </div>
      </div>

      {/* Tarjeta 4: Reservas Pendientes */}
      <div className="wms-card wms-card-warning">
        <div className="wms-kpi-header">
          <span>Reservas Activas</span>
          <Clock size={20} className="wms-kpi-icon" style={{ color: 'var(--color-warning)' }} />
        </div>
        <div className="wms-kpi-value" style={{ color: 'var(--color-warning)' }}>
          {pendientes}
        </div>
        <div className="wms-kpi-change neutral">
          <span>Simulación de timeouts activos</span>
        </div>
      </div>
    </div>
  );
};
