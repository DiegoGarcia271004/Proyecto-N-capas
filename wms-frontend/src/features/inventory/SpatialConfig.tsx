import React from 'react';
import { useWms } from '../../context/WmsContext';
import { Sliders, Check, HelpCircle, Save } from 'lucide-react';

export const SpatialConfig: React.FC = () => {
  const { policies, updatePolicy } = useWms();

  const handleSavePolicy = (id: string) => {
    updatePolicy(id);
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>Configuración Espacial del Almacén</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
          Módulo de control del Administrador. Gestiona las reglas de distribución de mercancías.
        </p>
      </div>

      <div className="wms-card" style={{ padding: '24px', marginBottom: '20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px', color: 'var(--color-primary)' }}>
          <Sliders size={20} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Políticas de Asignación y Ubicación</h3>
        </div>

        <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '20px' }}>
          Define cómo el sistema calculará y sugerirá las ubicaciones de almacenamiento (pasillo, rack, nivel) para las mercancías entrantes durante el escaneo en la terminal de operarios.
        </p>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {policies.map((policy) => (
            <div 
              key={policy.id} 
              className="wms-card"
              style={{ 
                borderColor: policy.activa ? 'var(--color-primary)' : 'var(--border-color)',
                backgroundColor: policy.activa ? 'rgba(6, 182, 212, 0.03)' : 'rgba(255,255,255,0.01)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '20px',
                transition: 'all 0.3s ease'
              }}
            >
              <div style={{ flex: 1, paddingRight: '16px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontWeight: 700, fontSize: '1.05rem', color: policy.activa ? 'var(--color-primary)' : 'var(--text-main)' }}>
                    {policy.nombre}
                  </span>
                  {policy.activa && (
                    <span className="wms-badge wms-badge-active" style={{ fontSize: '0.65rem', padding: '2px 6px' }}>
                      <Check size={10} style={{ marginRight: '2px' }} /> Activa
                    </span>
                  )}
                </div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '6px' }}>
                  {policy.descripcion}
                </p>
              </div>

              <div>
                <button
                  onClick={() => handleSavePolicy(policy.id)}
                  disabled={policy.activa}
                  className={`wms-btn ${policy.activa ? 'wms-btn-outline' : 'wms-btn-primary'} wms-btn-sm`}
                  style={{ display: 'flex', alignItems: 'center', gap: '6px', minWidth: '120px' }}
                >
                  <Save size={12} />
                  {policy.activa ? 'Activa (PUT)' : 'Activar (PUT)'}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div style={{ display: 'flex', gap: '12px', padding: '16px', background: 'rgba(255,255,255,0.02)', borderRadius: '12px', border: '1px solid var(--border-color)', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
        <HelpCircle size={32} style={{ color: 'var(--color-primary)', flexShrink: 0 }} />
        <div>
          <span style={{ fontWeight: 600, color: 'var(--text-main)' }}>Nota del Administrador (Permiso PUT):</span>
          <p style={{ marginTop: '2px' }}>
            Las modificaciones de políticas espaciales utilizan métodos `PUT` simulados en el cliente que actualizan de inmediato el algoritmo en el WmsContext. Las terminales móviles de los operarios adaptarán automáticamente sus cálculos de ubicación sugerida al instante del cambio.
          </p>
        </div>
      </div>
    </div>
  );
};
