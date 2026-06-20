import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { PieChart } from 'lucide-react';

export const RotationAbc: React.FC = () => {
  const { skus } = useWms();
  const [hoveredZone, setHoveredZone] = useState<{ nombre: string; stock: number; pct: number; color: string } | null>(null);

  // Calcular totales por zona
  const stockA = skus.filter(s => s.zona === 'A').reduce((acc, s) => acc + s.stock, 0);
  const stockB = skus.filter(s => s.zona === 'B').reduce((acc, s) => acc + s.stock, 0);
  const stockC = skus.filter(s => s.zona === 'C').reduce((acc, s) => acc + s.stock, 0);
  const stockTotal = stockA + stockB + stockC;

  const pctA = stockTotal ? (stockA / stockTotal) * 100 : 0;
  const pctB = stockTotal ? (stockB / stockTotal) * 100 : 0;
  const pctC = stockTotal ? (stockC / stockTotal) * 100 : 0;

  // Parámetros del círculo SVG
  const radius = 50;
  const circumference = 2 * Math.PI * radius; // Aprox 314.16

  // Anchos de segmento
  const strokeA = (pctA / 100) * circumference;
  const strokeB = (pctB / 100) * circumference;
  const strokeC = (pctC / 100) * circumference;

  // Offsets (los segmentos se acumulan)
  const offsetA = 0;
  const offsetB = strokeA;
  const offsetC = strokeA + strokeB;

  const zones = [
    { name: 'Zona A', stock: stockA, pct: pctA, color: '#06b6d4', stroke: strokeA, offset: offsetA, class: 'a' },
    { name: 'Zona B', stock: stockB, pct: pctB, color: '#8b5cf6', stroke: strokeB, offset: offsetB, class: 'b' },
    { name: 'Zona C', stock: stockC, pct: pctC, color: '#ef4444', stroke: strokeC, offset: offsetC, class: 'c' },
  ];

  return (
    <div className="wms-card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div className="wms-card-title">
        <PieChart size={18} style={{ color: 'var(--color-primary)' }} />
        <span>Rotación ABC de Inventario</span>
      </div>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        <div className="wms-abc-chart-container">
          <svg width="160" height="160" viewBox="0 0 120 120" className="wms-donut-svg">
            {zones.map((zone) => (
              <circle
                key={zone.name}
                className="wms-donut-segment"
                cx="60"
                cy="60"
                r={radius}
                stroke={zone.color}
                strokeDasharray={`${zone.stroke} ${circumference - zone.stroke}`}
                strokeDashoffset={-zone.offset}
                onMouseEnter={() => setHoveredZone({ nombre: zone.name, stock: zone.stock, pct: zone.pct, color: zone.color })}
                onMouseLeave={() => setHoveredZone(null)}
              />
            ))}
          </svg>

          <div className="wms-donut-center-text">
            <span className="wms-donut-center-val" style={{ color: hoveredZone ? hoveredZone.color : 'var(--text-main)' }}>
              {hoveredZone ? `${hoveredZone.pct.toFixed(0)}%` : `${((stockA / stockTotal) * 100).toFixed(0)}%`}
            </span>
            <span className="wms-donut-center-lbl">
              {hoveredZone ? hoveredZone.nombre : 'Rotación Alta (A)'}
            </span>
          </div>
        </div>

        {/* Leyenda interactiva */}
        <div className="wms-abc-legends">
          {zones.map((zone) => (
            <div 
              key={zone.name} 
              className="wms-legend-item"
              onMouseEnter={() => setHoveredZone({ nombre: zone.name, stock: zone.stock, pct: zone.pct, color: zone.color })}
              onMouseLeave={() => setHoveredZone(null)}
              style={{ 
                cursor: 'pointer',
                opacity: hoveredZone && hoveredZone.nombre !== zone.name ? 0.4 : 1,
                transform: hoveredZone && hoveredZone.nombre === zone.name ? 'scale(1.05)' : 'none',
                transition: 'all 0.2s'
              }}
            >
              <div className={`wms-legend-dot ${zone.class}`} />
              <span style={{ fontWeight: 500 }}>{zone.name}</span>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                ({zone.stock.toLocaleString('es-ES')} und)
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
