import React, { useEffect, useRef, useState } from 'react';
import { useWms } from '../../context/WmsContext';
import { SCAN_SIMULATIONS } from '../../mock/mockData';
import { 
  HelpCircle, 
  AlertCircle, 
  CheckCircle, 
  Keyboard, 
  Volume2, 
  Layers, 
  Download,
  Upload
} from 'lucide-react';

export const TerminalEscaner: React.FC = () => {
  const {
    scanMode,
    setScanMode,
    lastScan,
    scanStatus,
    scanHistory,
    isScannerFocused,
    processScan,
    confirmScan,
    setIsScannerFocused,
    resetLastScan
  } = useWms();

  const [inputValue, setInputValue] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);
  const [soundEnabled, setSoundEnabled] = useState(true);

  useEffect(() => {
    const focusInput = () => {
      if (inputRef.current) {
        inputRef.current.focus();
        setIsScannerFocused(true);
      }
    };

    focusInput();
    
    const deviceElement = document.getElementById('wms-handheld-simulator');
    if (deviceElement) {
      deviceElement.addEventListener('click', focusInput);
    }

    return () => {
      if (deviceElement) {
        deviceElement.removeEventListener('click', focusInput);
      }
    };
  }, [setIsScannerFocused, scanStatus]);

  // Escuchar F2 para confirmación
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'F2') {
        e.preventDefault();
        if (lastScan) {
          triggerBeep(880, 150);
          confirmScan();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [lastScan, confirmScan]);

  const triggerBeep = (frequency = 600, duration = 80) => {
    if (!soundEnabled) return;
    try {
      const audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const oscillator = audioCtx.createOscillator();
      const gainNode = audioCtx.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(audioCtx.destination);

      oscillator.type = 'sine';
      oscillator.frequency.value = frequency;
      gainNode.gain.setValueAtTime(0.1, audioCtx.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + duration / 1000);

      oscillator.start(audioCtx.currentTime);
      oscillator.stop(audioCtx.currentTime + duration / 1000);
    } catch (e) {
      console.log('Audio error:', e);
    }
  };

  useEffect(() => {
    if (scanStatus === 'success') {
      triggerBeep(1200, 100); // Exito
    } else if (scanStatus === 'error') {
      triggerBeep(150, 400); // Error
    }
  }, [scanStatus]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim()) {
      // Simula disparar un POST a la API
      processScan(inputValue.trim());
      setInputValue('');
    }
  };

  const handleManualConfirm = () => {
    triggerBeep(880, 150);
    confirmScan();
  };

  const getBannerContent = () => {
    switch (scanStatus) {
      case 'waiting':
        return (
          <div className="wms-status-banner waiting" style={{ fontSize: '0.85rem', padding: '12px' }}>
            <span className="pulse-animation">●</span> ESPERANDO LECTURA RFID/BARRAS...
          </div>
        );
      case 'processing':
        return (
          <div className="wms-status-banner processing" style={{ fontSize: '0.85rem', padding: '12px' }}>
            <span className="pulse-animation">⚡</span> ENVIANDO POST A API...
          </div>
        );
      case 'success':
        return (
          <div className="wms-status-banner success" style={{ fontSize: '0.85rem', padding: '12px' }}>
            <CheckCircle size={14} /> LEÍDO CON ÉXITO
          </div>
        );
      case 'error':
        return (
          <div className="wms-status-banner error" style={{ fontSize: '0.85rem', padding: '12px' }}>
            <AlertCircle size={14} /> CÓDIGO NO VÁLIDO
          </div>
        );
    }
  };

  return (
    <div className="wms-terminal-layout" style={{ gridTemplateColumns: '1fr', maxWidth: '500px' }}>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        
        {/* Toggle de Modo de Escaneo (Entrada vs Salida) */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
          <button
            onClick={() => {
              setScanMode('entrada');
              resetLastScan();
            }}
            className="wms-btn"
            style={{
              padding: '12px',
              borderRadius: '12px',
              backgroundColor: scanMode === 'entrada' ? 'var(--color-primary)' : 'rgba(255,255,255,0.03)',
              color: scanMode === 'entrada' ? 'var(--text-inverse)' : 'var(--text-muted)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '6px',
              fontWeight: 700
            }}
          >
            <Download size={16} />
            Modo Entrada
          </button>
          <button
            onClick={() => {
              setScanMode('salida');
              resetLastScan();
            }}
            className="wms-btn"
            style={{
              padding: '12px',
              borderRadius: '12px',
              backgroundColor: scanMode === 'salida' ? 'var(--color-secondary)' : 'rgba(255,255,255,0.03)',
              color: scanMode === 'salida' ? 'var(--text-inverse)' : 'var(--text-muted)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '6px',
              fontWeight: 700
            }}
          >
            <Upload size={16} />
            Modo Salida
          </button>
        </div>

        {/* Simulador de terminal RF */}
        <div className="wms-scanner-device" id="wms-handheld-simulator" style={{ padding: '16px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)' }}>
              LECTOR RFID / PERIFÉRICO
            </span>
            <button 
              onClick={(e) => {
                e.stopPropagation();
                setSoundEnabled(!soundEnabled);
              }}
              style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
            >
              <Volume2 size={16} style={{ color: soundEnabled ? 'var(--color-success)' : 'var(--text-muted)' }} />
            </button>
          </div>

          {/* Banner de Estado API POST */}
          {getBannerContent()}

          {/* Estado de Enfoque */}
          {isScannerFocused ? (
            <div className="wms-focus-success-panel" style={{ padding: '8px', fontSize: '0.75rem' }}>
              <Keyboard size={12} />
              <span>Pistola enlazada y activa</span>
            </div>
          ) : (
            <div className="wms-focus-warning-panel" style={{ padding: '8px', fontSize: '0.75rem' }} onClick={() => inputRef.current?.focus()}>
              <AlertCircle size={12} />
              <span>Pistola desconectada. Toca aquí para reenfocar.</span>
            </div>
          )}

          {/* Formulario e Input Invisible */}
          <form onSubmit={handleSubmit} className="wms-hidden-input-container">
            <input
              ref={inputRef}
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onBlur={() => setIsScannerFocused(false)}
              onFocus={() => setIsScannerFocused(true)}
            />
          </form>

          {/* Datos del producto escaneado */}
          <div className="wms-card" style={{ padding: '14px', background: 'rgba(255,255,255,0.01)', borderColor: lastScan ? 'var(--color-primary)' : 'var(--border-color)' }}>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '6px' }}>
              {scanMode === 'entrada' ? 'Ingreso de Proveedor:' : 'Despacho de Cliente:'}
            </div>

            {lastScan ? (
              <div>
                <div style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-main)' }}>{lastScan.sku}</div>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '8px' }}>{lastScan.name}</div>
                <div style={{ display: 'inline-flex', gap: '6px', backgroundColor: 'rgba(255,255,255,0.04)', padding: '2px 6px', borderRadius: '4px', fontSize: '0.75rem', fontFamily: 'monospace' }}>
                  <span>Lote:</span>
                  <span style={{ color: 'var(--color-secondary)', fontWeight: 600 }}>{lastScan.lote}</span>
                </div>
              </div>
            ) : (
              <div style={{ color: 'var(--text-muted)', fontStyle: 'italic', fontSize: '0.8rem', padding: '6px 0' }}>
                Dispara el lector sobre el código de barras...
              </div>
            )}
          </div>

          {/* Indicador de Ubicación Inteligente Asignada */}
          <div className="wms-location-card" style={{ padding: '16px' }}>
            <div className="wms-location-label" style={{ fontSize: '0.7rem', marginBottom: '4px' }}>
              {scanMode === 'entrada' ? 'GUARDAR EN UBICACIÓN:' : 'EXTRAER DE UBICACIÓN:'}
            </div>
            {lastScan ? (
              <div style={{ display: 'flex', justifyContent: 'center', gap: '8px', alignItems: 'center', marginTop: '6px' }}>
                <span className="wms-location-badge" style={{ fontSize: '1.1rem', padding: '6px 12px', borderRadius: '8px' }}>
                  {lastScan.ubicacionSugerida.pasillo}
                </span>
                <span>-</span>
                <span className="wms-location-badge" style={{ fontSize: '1.1rem', padding: '6px 12px', borderRadius: '8px', background: 'linear-gradient(135deg, #8b5cf6 0%, #d946ef 100%)' }}>
                  {lastScan.ubicacionSugerida.rack}
                </span>
                <span>-</span>
                <span className="wms-location-badge" style={{ fontSize: '1.1rem', padding: '6px 12px', borderRadius: '8px', background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' }}>
                  {lastScan.ubicacionSugerida.nivel}
                </span>
              </div>
            ) : (
              <div style={{ color: 'var(--text-muted)', fontStyle: 'italic', fontSize: '0.8rem', padding: '4px 0' }}>
                Sin sugerencia de ruta activa
              </div>
            )}
          </div>

          {/* Confirmar acción */}
          <button
            onClick={handleManualConfirm}
            disabled={!lastScan}
            className="wms-btn-giant"
            style={{ 
              padding: '14px', 
              fontSize: '1rem',
              backgroundColor: scanMode === 'entrada' ? 'var(--color-success)' : 'var(--color-secondary)',
              boxShadow: scanMode === 'entrada' ? '0 6px 12px rgba(16, 185, 129, 0.2)' : '0 6px 12px rgba(139, 92, 246, 0.2)'
            }}
          >
            <span>Confirmar Operación</span>
            <span className="wms-btn-subtext" style={{ fontSize: '0.65rem' }}>o presionar tecla F2</span>
          </button>
        </div>

        {/* Códigos de barra simulados para operar con un clic */}
        <div className="wms-card" style={{ padding: '16px' }}>
          <h4 style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '10px', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <HelpCircle size={14} style={{ color: 'var(--color-primary)' }} />
            Simulador Láser (Toca para Escanear)
          </h4>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
            {SCAN_SIMULATIONS.map(sim => (
              <button
                key={sim.sku}
                onClick={() => processScan(sim.barcode)}
                className="wms-btn wms-btn-outline wms-btn-sm"
                style={{
                  fontSize: '0.75rem',
                  padding: '8px',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  borderRadius: '8px',
                  justifyContent: 'center',
                  height: '54px',
                  lineHeight: '1.2'
                }}
              >
                <span style={{ fontWeight: 700 }}>{sim.sku}</span>
                <span style={{ fontSize: '0.65rem', color: 'var(--text-muted)', display: 'block', maxWidth: '140px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {sim.name}
                </span>
              </button>
            ))}
          </div>
        </div>

        {/* Historial de ingresos/egresos del operario */}
        <div style={{ marginTop: '4px' }}>
          <div style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Layers size={12} />
            Movimientos Procesados ({scanHistory.length})
          </div>
          {scanHistory.length === 0 ? (
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
              Ningún registro en esta sesión.
            </p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', maxHeight: '110px', overflowY: 'auto' }}>
              {scanHistory.map((hist, idx) => (
                <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: 'rgba(255,255,255,0.02)', padding: '6px 10px', borderRadius: '6px', fontSize: '0.75rem' }}>
                  <div>
                    <span style={{ 
                      fontWeight: 'bold', 
                      color: hist.tipo === 'Ingreso' ? 'var(--color-success)' : 'var(--color-secondary)',
                      marginRight: '6px'
                    }}>
                      [{hist.tipo}]
                    </span>
                    <span style={{ fontWeight: 600, color: 'var(--text-main)' }}>{hist.sku}</span>
                  </div>
                  <span style={{ color: 'var(--text-muted)' }}>
                    {hist.ubicacionSugerida.pasillo}-{hist.ubicacionSugerida.rack}-{hist.ubicacionSugerida.nivel}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
};
