import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { WmsProvider, useWms } from './context/WmsContext';
import { Login } from './features/auth/Login';
import { MainLayout } from './shared/layouts/MainLayout';
import { ProtectedRoute } from './core/routes/ProtectedRoute';

// Módulo Administrador
import { SpatialConfig } from './features/inventory/SpatialConfig';
import { AuditTable } from './features/inventory/AuditTable';

// Módulo Jefe de Almacén (Manager)
import { AnalyticalDashboard } from './features/operations/AnalyticalDashboard';
import { InventoryCatalog } from './features/catalog/InventoryCatalog';
import { BatchTimeline } from './features/inventory/BatchTimeline';
import { MovementsPanel } from './features/operations/MovementsPanel';

// Módulo Operario
import { TerminalEscaner } from './features/operations/TerminalEscaner';
import { TareaConteo } from './features/operations/TareaConteo';

// Redirección del landing page `/`
const HomeRedirect: React.FC = () => {
  const { user } = useWms();
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  // Redirigir según el rol activo
  if (user.role === 'admin') {
    return <Navigate to="/configuracion-espacial" replace />;
  } else if (user.role === 'manager') {
    return <Navigate to="/dashboard-analitico" replace />;
  } else {
    return <Navigate to="/terminal-escaner" replace />;
  }
};

const AppContent: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Ruta pública de Login */}
        <Route path="/login" element={<Login />} />

        {/* Rutas Privadas / Protegidas */}
        <Route path="/" element={<MainLayout />}>
          {/* Landing / Redireccionamiento según rol */}
          <Route index element={<HomeRedirect />} />

          {/* 1. Módulo del Administrador */}
          <Route 
            path="configuracion-espacial" 
            element={
              <ProtectedRoute allowedRoles={['admin']}>
                <SpatialConfig />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="auditoria" 
            element={
              <ProtectedRoute allowedRoles={['admin']}>
                <AuditTable />
              </ProtectedRoute>
            } 
          />

          {/* 2. Módulo del Jefe de Almacén */}
          <Route 
            path="dashboard-analitico" 
            element={
              <ProtectedRoute allowedRoles={['manager']}>
                <AnalyticalDashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="catalogo-inventario" 
            element={
              <ProtectedRoute allowedRoles={['manager']}>
                <InventoryCatalog />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="control-lotes" 
            element={
              <ProtectedRoute allowedRoles={['manager']}>
                <BatchTimeline />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="movimientos" 
            element={
              <ProtectedRoute allowedRoles={['manager']}>
                <MovementsPanel />
              </ProtectedRoute>
            } 
          />

          {/* 3. Módulo del Operario */}
          <Route 
            path="terminal-escaner" 
            element={
              <ProtectedRoute allowedRoles={['operator']}>
                <TerminalEscaner />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="tarea-conteo" 
            element={
              <ProtectedRoute allowedRoles={['operator']}>
                <TareaConteo />
              </ProtectedRoute>
            } 
          />

          {/* Fallback de redirección */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

const App: React.FC = () => {
  return (
    <WmsProvider>
      <AppContent />
    </WmsProvider>
  );
};

export default App;
