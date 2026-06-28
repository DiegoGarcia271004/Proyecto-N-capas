import React from 'react';
import { Navigate } from 'react-router-dom';
import { useWms } from '../../context/WmsContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: ('admin' | 'manager' | 'operator')[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedRoles }) => {
  const { user } = useWms();

  if (!user) {

    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    if (user.role === 'admin') {
      return <Navigate to="/configuracion-espacial" replace />;
    } else if (user.role === 'manager') {
      return <Navigate to="/dashboard-analitico" replace />;
    } else {
      return <Navigate to="/terminal-escaner" replace />;
    }
  }

  return <>{children}</>;
};
