import React, { useState, useEffect } from 'react';
import { useWms } from '../../context/WmsContext';
import { PieChart } from 'lucide-react';
import apiClient from '../../core/api/apiClient';

export const RotationAbc: React.FC = () => {
  const { skus } = useWms();
  const [hoveredZone, setHoveredZone] = useState<{ nombre: string; stock: number; pct: number; color: string } | null>(null);
  const [abcReport, setAbcReport] = useState<any>(null);

  useEffect(() => {
    const fetchAbcReport = async () => {
      try {
        const today = new Date().toISOString().split('T')[0];
        const sixMonthsAgo = new Date(Date.now() - 180 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
        const res = await apiClient.get(`/reporting/abc?from=${sixMonthsAgo}&to=${today}`);
        if (res.data && res.data.data) {
          setAbcReport(res.data.data);
        }
      } catch (error) {
        console.error("Failed to fetch ABC report:", error);
      }
    };
    fetchAbcReport();
  }, []);

  // Calcular totales por zona (usando datos del backend si están disponibles, o fallando al cálculo por stock)
  let stockA = skus.filter(s => s.zona === 'A').reduce((acc, s) => acc + s.stock, 0);
  let stockB = skus.filter(s => s.zona === 'B').reduce((acc, s) => acc + s.stock, 0);
  let stockC = skus.filter(s => s.zona === 'C').reduce((acc, s) => acc + s.stock, 0);

  if (abcReport && abcReport.classifiedItems && abcReport.classifiedItems.length > 0) {
    const items = abcReport.classifiedItems;
    stockA = items.filter((i: any) => i.category === 'A').reduce((acc: number, i: any) => acc + i.totalExitQuantity, 0);
    stockB = items.filter((i: any) => i.category === 'B').reduce((acc: number, i: any) => acc + i.totalExitQuantity, 0);
    stockC = items.filter((i: any) => i.category === 'C').reduce((acc: number, i: any) => acc + i.totalExitQuantity, 0);
  }

  const stockTotal = stockA + stockB + stockC || 1;

  const pctA = (stockA / stockTotal) * 100;
  const pctB = (stockB / stockTotal) * 100;
  const pctC = (stockC / stockTotal) * 100;

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
