import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { useNavigate } from 'react-router-dom';
import { Shield, User, Lock, ArrowRight, Eye, EyeOff } from 'lucide-react';

export const Login: React.FC = () => {
  const { login } = useWms();
  const navigate = useNavigate();
  const [username, setUsername] = useState('supervisor');
  const [password, setPassword] = useState('password123');
  const [role, setRole] = useState<'admin' | 'manager' | 'operator'>('manager');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Por favor, ingresa tu usuario y contraseña');
      return;
    }

    const success = login(username, role);
    if (success) {
      if (role === 'admin') navigate('/configuracion-espacial');
      else if (role === 'manager') navigate('/dashboard-analitico');
      else navigate('/terminal-escaner');
    } else {
      setError('Credenciales inválidas');
    }
  };

  // Accesos rápidos para calificar/probar el proyecto
  const selectQuickRole = (selectedRole: 'admin' | 'manager' | 'operator', name: string) => {
    setRole(selectedRole);
    setUsername(name);
    setPassword('password123');
  };

  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      width: '100vw',
      background: 'radial-gradient(circle at top left, #0d1527, #06090f)',
      padding: '20px'
    }}>
      <div className="wms-card" style={{
        width: '100%',
        maxWidth: '420px',
        padding: '36px',
        borderRadius: '20px',
        boxShadow: '0 20px 40px rgba(0,0,0,0.5)',
        border: '1px solid rgba(255, 255, 255, 0.08)'
      }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{
            display: 'inline-flex',
            padding: '12px',
            borderRadius: '16px',
            background: 'var(--gradient-primary)',
            color: 'var(--text-inverse)',
            marginBottom: '16px',
            boxShadow: '0 8px 16px rgba(6, 182, 212, 0.3)'
          }}>
            <Shield size={32} />
          </div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>SGA INTELIGENTE</h1>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '4px' }}>
            Ingresa al portal de control logístico
          </p>
        </div>

        {error && (
          <div style={{
            padding: '10px 12px',
            backgroundColor: 'rgba(239, 68, 68, 0.1)',
            border: '1px solid rgba(239, 68, 68, 0.2)',
            borderRadius: '8px',
            color: 'var(--color-danger)',
            fontSize: '0.8rem',
            marginBottom: '16px',
            textAlign: 'center'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {/* Input Usuario */}
          <div>
            <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 500 }}>
              Usuario
            </label>
            <div className="wms-search-bar" style={{ width: '100%' }}>
              <User size={16} style={{ color: 'var(--text-muted)' }} />
              <input
                type="text"
                className="wms-search-input"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Ingresa tu usuario..."
              />
            </div>
          </div>

          {/* Input Contraseña */}
          <div>
            <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 500 }}>
              Contraseña
            </label>
            <div className="wms-search-bar" style={{ width: '100%', justifyContent: 'space-between' }}>
              <div style={{ display: 'flex', alignItems: 'center', flex: 1 }}>
                <Lock size={16} style={{ color: 'var(--text-muted)' }} />
                <input
                  type={showPassword ? 'text' : 'password'}
                  className="wms-search-input"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Ingresa tu contraseña..."
                />
              </div>
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', display: 'flex', alignItems: 'center' }}
              >
                {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          {/* Selector de Rol */}
          <div>
            <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 500 }}>
              Rol Asignado
            </label>
            <select
              className="wms-warehouse-selector"
              style={{ width: '100%', padding: '10px' }}
              value={role}
              onChange={(e) => setRole(e.target.value as any)}
            >
              <option value="admin">🔒 Administrador</option>
              <option value="manager">📊 Jefe de Almacén</option>
              <option value="operator">📱 Operario de Terminal</option>
            </select>
          </div>

          {/* Botón de envío */}
          <button type="submit" className="wms-btn wms-btn-primary" style={{ padding: '12px', fontSize: '0.95rem', marginTop: '8px' }}>
            <span>Iniciar Sesión</span>
            <ArrowRight size={18} />
          </button>
        </form>

        {/* Accesos rápidos de prueba */}
        <div style={{ marginTop: '24px', paddingTop: '20px', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textAlign: 'center', marginBottom: '10px', fontWeight: 600 }}>
            ACCESOS RÁPIDOS PARA PRUEBAS
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
            <button
              onClick={() => selectQuickRole('admin', 'admin_wms')}
              className="wms-btn wms-btn-outline wms-btn-sm"
              style={{ fontSize: '0.7rem', padding: '6px 4px' }}
            >
              Admin
            </button>
            <button
              onClick={() => selectQuickRole('manager', 'jefe_madrid')}
              className="wms-btn wms-btn-outline wms-btn-sm"
              style={{ fontSize: '0.7rem', padding: '6px 4px' }}
            >
              Jefe (WMS)
            </button>
            <button
              onClick={() => selectQuickRole('operator', 'operario_juan')}
              className="wms-btn wms-btn-outline wms-btn-sm"
              style={{ fontSize: '0.7rem', padding: '6px 4px' }}
            >
              Operario
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
