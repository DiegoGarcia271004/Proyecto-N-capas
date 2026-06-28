import React, { useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { useNavigate } from 'react-router-dom';
import { Shield, User, Lock, ArrowRight, Eye, EyeOff, Mail, CheckCircle } from 'lucide-react';

export const Register: React.FC = () => {
  const { registerUser } = useWms();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [role, setRole] = useState<'admin' | 'manager' | 'operator'>('manager');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!username.trim() || !password.trim() || !email.trim()) {
      setError('Por favor, completa todos los campos');
      return;
    }

    // Mapear rol a lo que el backend de Spring Security espera
    let backendRole = 'ROLE_OPERATOR';
    if (role === 'admin') backendRole = 'ROLE_ADMIN';
    else if (role === 'manager') backendRole = 'ROLE_WAREHOUSE_MANAGER';

    const registered = await registerUser(username.trim(), email.trim(), backendRole);
    if (registered) {
      setSuccess('¡Usuario registrado con éxito! Redirigiendo al inicio de sesión...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } else {
      setError('Error al registrar usuario en el servidor');
    }
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
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)' }}>REGISTRO DE USUARIO</h1>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '4px' }}>
            Crea una nueva cuenta en el sistema
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

        {success && (
          <div style={{
            padding: '10px 12px',
            backgroundColor: 'rgba(16, 185, 129, 0.1)',
            border: '1px solid rgba(16, 185, 129, 0.2)',
            borderRadius: '8px',
            color: 'var(--color-success)',
            fontSize: '0.8rem',
            marginBottom: '16px',
            textAlign: 'center',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '6px'
          }}>
            <CheckCircle size={16} />
            <span>{success}</span>
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
                placeholder="Crea tu nombre de usuario..."
              />
            </div>
          </div>

          {/* Input Correo */}
          <div>
            <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 500 }}>
              Correo Electrónico
            </label>
            <div className="wms-search-bar" style={{ width: '100%' }}>
              <Mail size={16} style={{ color: 'var(--text-muted)' }} />
              <input
                type="email"
                className="wms-search-input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Ingresa tu correo..."
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
                  placeholder="Crea una contraseña..."
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
            <span>Registrarse</span>
            <ArrowRight size={18} />
          </button>
        </form>

        {/* Enlace para alternar a Login */}
        <button
          type="button"
          onClick={() => navigate('/login')}
          style={{
            background: 'none',
            border: 'none',
            color: 'var(--color-primary)',
            cursor: 'pointer',
            fontSize: '0.85rem',
            textAlign: 'center',
            width: '100%',
            marginTop: '16px',
            textDecoration: 'underline'
          }}
        >
          ¿Ya tienes cuenta? Inicia Sesión
        </button>
      </div>
    </div>
  );
};
