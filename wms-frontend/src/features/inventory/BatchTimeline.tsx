import React from 'react';
import { useWms } from '../../context/WmsContext';
import { Calendar, AlertCircle, ShieldAlert, Sparkles, MapPin } from 'lucide-react';

export const BatchTimeline: React.FC = () => {
  const { batches, skus } = useWms();

  // Calcular vida útil restante en días y porcentaje
  const calculateShelfLife = (caducidadStr: string, ingresoStr: string) => {
    const today = Date.now();
    const expTime = new Date(caducidadStr).getTime();
    const inTime = new Date(ingresoStr).getTime();
    
    const totalDuration = expTime - inTime;
    const remainingTime = expTime - today;
    
    const daysRemaining = Math.ceil(remainingTime / (1000 * 60 * 60 * 24));
    
    let pct = totalDuration > 0 ? (remainingTime / totalDuration) * 100 : 0;
    pct = Math.max(0, Math.min(100, pct)); // Acotar entre 0 y 100

    return { daysRemaining, pct };
  };

  // Ordenar lotes por fecha de caducidad (FIFO)
  const sortedBatches = [...batches].sort(
    (a, b) => new Date(a.fechaCaducidad).getTime() - new Date(b.fechaCaducidad).getTime()
  );

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto' }}>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Línea de Tiempo FIFO - Control de Lotes</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
          Trazabilidad temporal y orden de salida obligatoria según fechas de vencimiento.
        </p>
      </div>

      <div className="wms-card" style={{ padding: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '20px', color: 'var(--color-primary)' }}>
          <Calendar size={20} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Cronología de Lotes Activos</h3>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', position: 'relative', paddingLeft: '16px' }}>
          {/* Línea vertical del timeline */}
          <div style={{
            position: 'absolute',
            left: '4px',
            top: '8px',
            bottom: '8px',
            width: '2px',
            background: 'linear-gradient(to bottom, var(--color-danger), var(--color-warning), var(--color-success))',
            opacity: 0.5
          }} />

          {sortedBatches.map((batch, idx) => {
            const skuItem = skus.find(s => s.sku === batch.sku);
            const { daysRemaining, pct } = calculateShelfLife(batch.fechaCaducidad, batch.fechaIngreso);
            
            // Determinar urgencia y color
            let statusColor = 'var(--color-success)';
            let statusLabel = 'Seguro (Vida larga)';
            let statusIcon = <Sparkles size={12} />;
            
            if (daysRemaining <= 0) {
              statusColor = 'var(--color-danger)';
              statusLabel = 'VENCIDO / BLOQUEAR';
              statusIcon = <ShieldAlert size={12} />;
            } else if (daysRemaining <= 90) { // Menos de 3 meses
              statusColor = 'var(--color-warning)';
              statusLabel = 'CRÍTICO: Consumo Inmediato';
              statusIcon = <AlertCircle size={12} />;
            }

            return (
              <div key={batch.id} style={{ position: 'relative', paddingLeft: '16px' }}>
                {/* Nodo del timeline */}
                <div style={{
                  position: 'absolute',
                  left: '-17px',
                  top: '6px',
                  width: '10px',
                  height: '10px',
                  borderRadius: '50%',
                  backgroundColor: statusColor,
                  boxShadow: `0 0 10px ${statusColor}`,
                  border: '2px solid #090d16'
                }} />

                <div className="wms-card" style={{ padding: '16px', background: 'rgba(255,255,255,0.015)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '8px', marginBottom: '8px' }}>
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span style={{ fontWeight: 700, color: 'var(--text-main)', fontSize: '1rem' }}>
                          {batch.codigoLote}
                        </span>
                        <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-primary)' }}>
                          {batch.sku}
                        </span>
                        {idx === 0 && (
                          <span className="wms-badge wms-badge-active" style={{ fontSize: '0.65rem', padding: '2px 6px' }}>
                            Prioridad #1 de Extracción
                          </span>
                        )}
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                        {skuItem?.name}
                      </div>
                    </div>

                    <div style={{ textAlign: 'right' }}>
                      <span className="wms-badge" style={{ 
                        backgroundColor: `${statusColor}1A`, 
                        color: statusColor, 
                        border: `1px solid ${statusColor}33`,
                        fontSize: '0.7rem',
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}>
                        {statusIcon}
                        {statusLabel}
                      </span>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                        Caduca: <strong>{batch.fechaCaducidad}</strong>
                      </div>
                    </div>
                  </div>

                  {/* Barra de progreso de frescura */}
                  <div style={{ marginTop: '12px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
                      <span>Ingreso: {batch.fechaIngreso}</span>
                      <span>
                        {daysRemaining > 0 ? `${daysRemaining} días restantes` : 'Lote Expirado'}
                      </span>
                    </div>
                    <div style={{ height: '6px', backgroundColor: 'rgba(255,255,255,0.05)', borderRadius: '3px', overflow: 'hidden' }}>
                      <div style={{ 
                        height: '100%', 
                        width: `${pct}%`, 
                        backgroundColor: statusColor, 
                        transition: 'width 0.5s ease-out'
                      }} />
                    </div>
                  </div>

                  {/* Detalles de ubicación y stock */}
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '12px', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid rgba(255,255,255,0.03)', paddingTop: '8px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <MapPin size={12} />
                      <span>{batch.pasillo} - {batch.rack} - {batch.nivel}</span>
                    </div>
                    <div>
                      Cantidad Disponible: <strong style={{ color: 'var(--text-main)' }}>{batch.cantidad}</strong>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
