import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { ClipboardCheck, CheckCircle2, UserCheck, AlertTriangle } from 'lucide-react';

export const TareaConteo: React.FC = () => {
  const { registerCycleCount } = useWms();

  // Form states
  const [skuCode, setSkuCode] = useState('');
  const [pasillo, setPasillo] = useState('PASILLO 1');
  const [rack, setRack] = useState('RACK A');
  const [nivel, setNivel] = useState('NIVEL 1');
  const [contado, setContado] = useState('');
  
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');
    setSuccessMsg('');

    if (!skuCode.trim()) {
      setErrorMsg('Debes ingresar el SKU o Nombre del artículo');
      return;
    }

    const countNum = parseInt(contado);
    if (isNaN(countNum) || countNum < 0) {
      setErrorMsg('Ingresa una cantidad contada válida (mayor o igual a 0)');
      return;
    }

    // Registrar en auditoría a ciegas
    registerCycleCount(skuCode.trim(), pasillo, rack, nivel, countNum);

    // Resetear form
    setSkuCode('');
    setContado('');
    setSuccessMsg('¡Conteo registrado con éxito! Se ha enviado al Administrador para calcular discrepancias.');
    
    // Ocultar mensaje después de 4 segundos
    setTimeout(() => {
      setSuccessMsg('');
    }, 4000);
  };

  return (
    <div style={{ maxWidth: '500px', margin: '0 auto' }}>
      <div style={{ marginBottom: '20px' }}>
        <h2 style={{ fontSize: '1.35rem', fontWeight: 700, color: 'var(--text-main)' }}>Conteo Cíclico a Ciegas</h2>
        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '2px' }}>
          Ingresa las existencias físicas reales observadas en la ubicación.
        </p>
      </div>

      {successMsg && (
        <div style={{ 
          padding: '12px 16px', 
          backgroundColor: 'rgba(16, 185, 129, 0.1)', 
          border: '1px solid rgba(16, 185, 129, 0.2)',
          color: 'var(--color-success)',
          borderRadius: '10px',
          fontSize: '0.85rem',
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          marginBottom: '16px'
        }}>
          <CheckCircle2 size={18} style={{ flexShrink: 0 }} />
          <span>{successMsg}</span>
        </div>
      )}

      {errorMsg && (
        <div style={{ 
          padding: '12px 16px', 
          backgroundColor: 'rgba(239, 68, 68, 0.1)', 
          border: '1px solid rgba(239, 68, 68, 0.2)',
          color: 'var(--color-danger)',
          borderRadius: '10px',
          fontSize: '0.85rem',
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          marginBottom: '16px'
        }}>
          <AlertTriangle size={18} style={{ flexShrink: 0 }} />
          <span>{errorMsg}</span>
        </div>
      )}

      <div className="wms-card" style={{ padding: '20px' }}>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          
          {/* Campo SKU */}
          <div>
            <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
              SKU del Producto
            </label>
            <input 
              type="text"
              placeholder="Ej. SKU-99812 o tornillo..."
              className="wms-search-input"
              style={{
                width: '100%',
                padding: '12px',
                borderRadius: '8px',
                border: '1px solid var(--border-color)',
                backgroundColor: 'rgba(255,255,255,0.02)'
              }}
              value={skuCode}
              onChange={(e) => setSkuCode(e.target.value)}
            />
          </div>

          {/* Fila de Ubicación */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
                Pasillo
              </label>
              <select
                className="wms-warehouse-selector"
                style={{ width: '100%', padding: '12px', borderRadius: '8px' }}
                value={pasillo}
                onChange={(e) => setPasillo(e.target.value)}
              >
                <option value="PASILLO 1">Pasillo 1</option>
                <option value="PASILLO 2">Pasillo 2</option>
                <option value="PASILLO 3">Pasillo 3</option>
                <option value="PASILLO 4">Pasillo 4</option>
                <option value="PASILLO 5">Pasillo 5</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
                Estantería (Rack)
              </label>
              <select
                className="wms-warehouse-selector"
                style={{ width: '100%', padding: '12px', borderRadius: '8px' }}
                value={rack}
                onChange={(e) => setRack(e.target.value)}
              >
                <option value="RACK A">Rack A</option>
                <option value="RACK B">Rack B</option>
                <option value="RACK C">Rack C</option>
                <option value="RACK D">Rack D</option>
              </select>
            </div>
          </div>

          {/* Fila Nivel / Cantidad */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
                Altura (Nivel)
              </label>
              <select
                className="wms-warehouse-selector"
                style={{ width: '100%', padding: '12px', borderRadius: '8px' }}
                value={nivel}
                onChange={(e) => setNivel(e.target.value)}
              >
                <option value="NIVEL 1">Nivel 1</option>
                <option value="NIVEL 2">Nivel 2</option>
                <option value="NIVEL 3">Nivel 3</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
                Cantidad Contada
              </label>
              <input 
                type="number"
                inputMode="numeric"
                pattern="[0-9]*"
                placeholder="¿Cuánto ves?"
                className="wms-search-input"
                style={{
                  width: '100%',
                  padding: '12px',
                  borderRadius: '8px',
                  border: '1px solid var(--border-color)',
                  backgroundColor: 'rgba(255,255,255,0.02)'
                }}
                value={contado}
                onChange={(e) => setContado(e.target.value)}
              />
            </div>
          </div>

          {/* Botón enviar */}
          <button 
            type="submit" 
            className="wms-btn wms-btn-primary" 
            style={{ 
              padding: '14px', 
              fontSize: '1rem', 
              fontWeight: 700, 
              borderRadius: '12px',
              marginTop: '10px',
              backgroundColor: 'var(--color-success)',
              backgroundImage: 'none'
            }}
          >
            <ClipboardCheck size={18} />
            GUARDAR CONTEO A CIEGAS
          </button>
        </form>
      </div>

      <div style={{ 
        marginTop: '20px', 
        padding: '12px', 
        background: 'rgba(255,255,255,0.01)', 
        border: '1px solid var(--border-color)', 
        borderRadius: '8px', 
        fontSize: '0.75rem', 
        color: 'var(--text-muted)',
        display: 'flex',
        alignItems: 'center',
        gap: '8px'
      }}>
        <UserCheck size={16} style={{ color: 'var(--color-success)' }} />
        <span>Se registrará bajo el operario activo de la sesión.</span>
      </div>
    </div>
  );
};
