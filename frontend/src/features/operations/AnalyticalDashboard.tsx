import React from 'react';
import { useWms } from '../../context/WmsContext';
import { KpiCards } from '../../components/Dashboard/KpiCards';
import { RotationAbc } from '../../components/Dashboard/RotationAbc';
import { RopTable } from '../../components/Dashboard/RopTable';
import { BarChart3 } from 'lucide-react';

export const AnalyticalDashboard: React.FC = () => {
  const { activeWarehouse } = useWms();

  return (
    <div>
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Dashboard Analítico</h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
            Consola central de supervisión del {activeWarehouse.nombre} (Costo Promedio Ponderado)
          </p>
        </div>
        
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.8rem', color: 'var(--text-muted)', backgroundColor: 'rgba(255,255,255,0.03)', padding: '6px 12px', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
          <BarChart3 size={14} style={{ color: 'var(--color-success)' }} />
          <span>Frecuencia de actualización: 1s</span>
        </div>
      </div>

      <KpiCards />

      <div className="wms-dashboard-row-2">
        <RotationAbc />
        <RopTable />
      </div>
    </div>
  );
};
