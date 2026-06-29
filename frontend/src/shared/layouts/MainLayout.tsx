import React from 'react';
import { Outlet, useNavigate, useLocation, Navigate } from 'react-router-dom';
import { useWms } from '../../context/WmsContext';
import { 
  LogOut, 
  Bell, 
  Warehouse as WhIcon, 
  Search, 
  Shield, 
  UserCheck, 
  ClipboardCheck, 
  Smartphone,
  ScanLine,
  Sliders
} from 'lucide-react';

export const MainLayout: React.FC = () => {
  const { 
    user, 
    logout, 
    activeWarehouse, 
    warehouses, 
    setActiveWarehouse, 
    skus,
    searchQuery,
    setSearchQuery 
  } = useWms();
  const navigate = useNavigate();
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const lowStockCount = skus.filter(s => s.stock <= s.rop).length;

  const renderSidebarLinks = () => {
    if (user.role === 'ADMIN') {
      return (
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <div 
            onClick={() => navigate('/configuracion-espacial')} 
            className={`wms-nav-item ${location.pathname === '/configuracion-espacial' ? 'active' : ''}`}
          >
            <Sliders size={20} />
            <span>Reglas Espaciales</span>
          </div>
          <div 
            onClick={() => navigate('/auditoria')} 
            className={`wms-nav-item ${location.pathname === '/auditoria' ? 'active' : ''}`}
          >
            <ClipboardCheck size={20} />
            <span>Auditoría de Stock</span>
          </div>
          <div 
            onClick={() => navigate('/dashboard-analitico')} 
            className={`wms-nav-item ${location.pathname === '/dashboard-analitico' ? 'active' : ''}`}
          >
            <span>📊 Dashboard</span>
          </div>
          <div 
            onClick={() => navigate('/catalogo-inventario')} 
            className={`wms-nav-item ${location.pathname === '/catalogo-inventario' ? 'active' : ''}`}
          >
            <span>📦 Catálogo SKUs</span>
          </div>
          <div 
            onClick={() => navigate('/control-lotes')} 
            className={`wms-nav-item ${location.pathname === '/control-lotes' ? 'active' : ''}`}
          >
            <span>⏳ Línea FIFO</span>
          </div>
          <div 
            onClick={() => navigate('/movimientos')} 
            className={`wms-nav-item ${location.pathname === '/movimientos' ? 'active' : ''}`}
          >
            <span>🔀 Movimientos</span>
          </div>
          <div 
            onClick={() => navigate('/terminal-escaner')} 
            className={`wms-nav-item ${location.pathname === '/terminal-escaner' ? 'active' : ''}`}
          >
            <ScanLine size={20} />
            <span>Escaneo Terminal</span>
          </div>
          <div 
            onClick={() => navigate('/tarea-conteo')} 
            className={`wms-nav-item ${location.pathname === '/tarea-conteo' ? 'active' : ''}`}
          >
            <span>📋 Tarea Conteo</span>
          </div>
        </nav>
      );
    }

    if (user.role === 'MANAGER') {
      return (
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <div 
            onClick={() => navigate('/dashboard-analitico')} 
            className={`wms-nav-item ${location.pathname === '/dashboard-analitico' ? 'active' : ''}`}
          >
            <span>📊 Dashboard</span>
          </div>
          <div 
            onClick={() => navigate('/catalogo-inventario')} 
            className={`wms-nav-item ${location.pathname === '/catalogo-inventario' ? 'active' : ''}`}
          >
            <span>📦 Catálogo SKUs</span>
          </div>
          <div 
            onClick={() => navigate('/control-lotes')} 
            className={`wms-nav-item ${location.pathname === '/control-lotes' ? 'active' : ''}`}
          >
            <span>⏳ Línea FIFO</span>
          </div>
          <div 
            onClick={() => navigate('/movimientos')} 
            className={`wms-nav-item ${location.pathname === '/movimientos' ? 'active' : ''}`}
          >
            <span>🔀 Movimientos</span>
          </div>
        </nav>
      );
    }

    return null;
  };

  // LAYOUT PARA OPERARIO (Mobile-First, sin sidebars complejos)
  if (user.role === 'OPERATOR') {
    return (
      <div className="wms-app-layout" style={{ background: '#090d16' }}>
        {/* Encabezado móvil simple */}
        <header className="wms-header" style={{ justifyContent: 'space-between', padding: '0 16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Smartphone size={20} style={{ color: 'var(--color-primary)' }} />
            <span style={{ fontWeight: 700, fontSize: '0.95rem', letterSpacing: '0.05em' }}>
              SGA TERMINAL
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <UserCheck size={14} style={{ color: 'var(--color-success)' }} />
              {user.username}
            </span>
            <button 
              onClick={handleLogout}
              style={{ background: 'none', border: 'none', color: 'var(--color-danger)', cursor: 'pointer', display: 'flex', alignItems: 'center' }}
            >
              <LogOut size={18} />
            </button>
          </div>
        </header>

        {/* Contenido principal */}
        <main className="wms-main-content" style={{ padding: '16px', paddingBottom: '80px', flex: 1, overflowY: 'auto' }}>
          <Outlet />
        </main>

        {/* Barra de navegación inferior táctil */}
        <nav style={{
          position: 'fixed',
          bottom: 0,
          left: 0,
          right: 0,
          height: '64px',
          backgroundColor: '#0f1524',
          borderTop: '1px solid var(--border-color)',
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          zIndex: 100,
          boxShadow: '0 -4px 12px rgba(0,0,0,0.3)'
        }}>
          <button
            onClick={() => navigate('/terminal-escaner')}
            style={{
              background: 'none',
              border: 'none',
              borderRight: '1px solid var(--border-color)',
              color: location.pathname === '/terminal-escaner' ? 'var(--color-primary)' : 'var(--text-muted)',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '4px',
              cursor: 'pointer',
              fontWeight: 600,
              fontSize: '0.8rem'
            }}
          >
            <ScanLine size={20} />
            Escanear
          </button>
          <button
            onClick={() => navigate('/tarea-conteo')}
            style={{
              background: 'none',
              border: 'none',
              color: location.pathname === '/tarea-conteo' ? 'var(--color-success)' : 'var(--text-muted)',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '4px',
              cursor: 'pointer',
              fontWeight: 600,
              fontSize: '0.8rem'
            }}
          >
            <ClipboardCheck size={20} />
            Conteo Cíclico
          </button>
        </nav>
      </div>
    );
  }

  // LAYOUT PARA ADMINISTRADOR Y JEFE DE ALMACÉN
  return (
    <div className="wms-app-layout">
      {/* Header General */}
      <header className="wms-header">
        <div className="wms-header-left">
          <div className="wms-logo" style={{ cursor: 'pointer' }} onClick={() => {
            if (user.role === 'ADMIN') navigate('/configuracion-espacial');
            else navigate('/dashboard-analitico');
          }}>
            <Shield size={20} style={{ color: 'var(--color-primary)' }} />
            <span>SGA PANEL</span>
          </div>

          {user.role === 'MANAGER' && location.pathname === '/catalogo-inventario' && (
            <div className="wms-search-bar">
              <Search size={18} style={{ color: 'var(--text-muted)' }} />
              <input
                type="text"
                placeholder="Buscador aproximado (Fuzzy)..."
                className="wms-search-input"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          )}
        </div>

        <div className="wms-header-right">
          {/* Selector Global de Almacenes (Requerimiento para Admin/Manager) */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <WhIcon size={16} style={{ color: 'var(--color-primary)' }} />
            <select
              className="wms-warehouse-selector"
              value={activeWarehouse.id}
              onChange={(e) => {
                const wh = warehouses.find(w => w.id === e.target.value);
                if (wh) setActiveWarehouse(wh);
              }}
            >
              {warehouses.map(wh => (
                <option key={wh.id} value={wh.id}>
                  {wh.codigo} - {wh.nombre}
                </option>
              ))}
            </select>
          </div>

          {/* Notificaciones de Alertas de Stock Mínimo para el Manager */}
          {user.role === 'MANAGER' && (
            <div style={{ position: 'relative', display: 'flex', alignItems: 'center', cursor: 'pointer' }} title={`${lowStockCount} Alertas de stock crítico`}>
              <Bell size={20} style={{ color: lowStockCount > 0 ? 'var(--color-warning)' : 'var(--text-muted)' }} />
              {lowStockCount > 0 && (
                <span style={{
                  position: 'absolute',
                  top: '-4px',
                  right: '-4px',
                  backgroundColor: 'var(--color-danger)',
                  color: 'white',
                  borderRadius: '50%',
                  fontSize: '0.65rem',
                  fontWeight: 'bold',
                  width: '15px',
                  height: '15px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  animation: 'blink-warning 1s infinite alternate'
                }}>
                  {lowStockCount}
                </span>
              )}
            </div>
          )}

          {/* Perfil de Usuario */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', paddingLeft: '8px', borderLeft: '1px solid var(--border-color)' }}>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
              <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-main)' }}>
                {user.username}
              </span>
              <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>
                {user.role === 'ADMIN' ? 'Administrador' : 'Jefe Almacén'}
              </span>
            </div>
            <button 
              onClick={handleLogout}
              className="wms-btn wms-btn-outline wms-btn-sm"
              style={{ padding: '6px', color: 'var(--color-danger)', borderColor: 'rgba(239,68,68,0.2)' }}
            >
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </header>

      {/* Cuerpo principal (Sidebar + Outlet) */}
      <div className="wms-body">
        <aside className="wms-sidebar">
          {renderSidebarLinks()}
          
          <div style={{ marginTop: 'auto', padding: '12px', background: 'rgba(0,0,0,0.2)', borderRadius: '8px', fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', gap: '4px' }}>
            <div>🔑 Token JWT Cargado</div>
            <div style={{ 
              fontFamily: 'monospace', 
              overflow: 'hidden', 
              textOverflow: 'ellipsis', 
              whiteSpace: 'nowrap',
              color: 'var(--color-success)',
              background: 'rgba(0,0,0,0.3)',
              padding: '2px 4px',
              borderRadius: '4px'
            }}>
              {user.token}
            </div>
          </div>
        </aside>

        <main className="wms-main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
