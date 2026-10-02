<template>
  <div class="reporte-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">Reporte de Ventas</h2>
        <p class="page-subtitle">Analiza las ventas por fecha</p>
      </div>
    </div>

    <!-- Selector de fecha -->
    <div class="filtro-fecha">
      <div class="fecha-field">
        <label for="fechaSeleccionada">📅 Seleccionar fecha</label>
        <input
          type="date"
          id="fechaSeleccionada"
          v-model="fechaSeleccionada"
          @change="cambiarFecha"
        />
      </div>
      <button @click="irADiaActual" class="btn-buscar">Buscar</button>
    </div>

    <!-- Resumen KPIs -->
    <div class="kpi-grid">
      <div class="kpi-card">
        <div class="kpi-icon kpi-icon--date"></div>
        <div class="kpi-body">
          <div class="kpi-label">Fecha seleccionada</div>
          <div class="kpi-value">{{ fechaSeleccionadaFormateada || '—' }}</div>
        </div>
      </div>
      <div class="kpi-card accent">
        <div class="kpi-icon kpi-icon--sales"></div>
        <div class="kpi-body">
          <div class="kpi-label">Ventas del día</div>
          <div class="kpi-value">{{ ventasFiltradas.length }}</div>
        </div>
      </div>
      <div class="kpi-card gold">
        <div class="kpi-icon kpi-icon--money"></div>
        <div class="kpi-body">
          <div class="kpi-label">Monto total</div>
          <div class="kpi-value">${{ formatoMoneda(montoTotalDelDia) }}</div>
        </div>
      </div>
    </div>

    <!-- Tabla -->
    <div class="tabla-wrapper">
      <table class="tabla-premium">
        <thead>
          <tr>
            <th>ID Venta</th>
            <th>ID Usuario</th>
            <th>ID Producto</th>
            <th>Dirección</th>
            <th>Fecha</th>
            <th>Monto</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="venta in ventasFiltradas" :key="venta.fecha">
            <td class="td-id">{{ venta.idVenta }}</td>
            <td>{{ venta.idUsuario }}</td>
            <td>{{ venta.idProducto }}</td>
            <td>{{ venta.direccion }}</td>
            <td class="td-fecha">{{ formatearFecha(venta.fecha) }}</td>
            <td class="td-monto">${{ venta.monto }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <transition name="fade-slide">
      <div v-if="ventasFiltradas.length === 0" class="empty-state">
        <div class="empty-icon"></div>
        <p>No hubo ventas el <strong>{{ fechaSeleccionadaFormateada || 'día seleccionado' }}</strong>.</p>
      </div>
    </transition>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'HistorialView',
  data() {
    return {
      ventas: [],
      ventasFiltradas: [],
      fechaHoy: '',
      fechaHoyFormateada: '',
      fechaSeleccionada: '',
      fechaSeleccionadaFormateada: '',
      montoTotalDelDia: 0
    };
  },
  mounted() {
    this.cargarVentas();
    this.setearFechaHoy();
    this.fechaSeleccionada = this.fechaHoy;
    this.fechaSeleccionadaFormateada = this.fechaHoyFormateada;
  },
  methods: {
    setearFechaHoy() {
      const hoy = new Date();
      const yyyy = hoy.getFullYear();
      const mm = String(hoy.getMonth() + 1).padStart(2, '0');
      const dd = String(hoy.getDate()).padStart(2, '0');
      this.fechaHoy = `${yyyy}-${mm}-${dd}`;
      this.fechaHoyFormateada = `${dd}/${mm}/${yyyy}`;
    },
    cambiarFecha() {
      const [yyyy, mm, dd] = this.fechaSeleccionada.split('-');
      this.fechaSeleccionadaFormateada = `${dd}/${mm}/${yyyy}`;
      this.ventasFiltradas = this.filtrarVentasPorFecha(this.ventas, this.fechaSeleccionada);
      this.montoTotalDelDia = this.calcularMontoTotal(this.ventasFiltradas);
    },
    irADiaActual() {
      this.fechaSeleccionada = this.fechaHoy;
      this.fechaSeleccionadaFormateada = this.fechaHoyFormateada;
      this.ventasFiltradas = this.filtrarVentasPorFecha(this.ventas, this.fechaHoy);
      this.montoTotalDelDia = this.calcularMontoTotal(this.ventasFiltradas);
    },
    async cargarVentas() {
      try {
        const response = await axios.get('http://localhost:8081/api/ventas');
        this.ventas = response.data;
        this.ventasFiltradas = this.filtrarVentasPorFecha(response.data, this.fechaSeleccionada);
        this.montoTotalDelDia = this.calcularMontoTotal(this.ventasFiltradas);
      } catch (error) {
        console.error('Error al cargar las ventas:', error);
      }
    },
    filtrarVentasPorFecha(ventas, fecha) {
      return ventas.filter(venta => venta.fecha === fecha);
    },
    calcularMontoTotal(ventas) {
      return ventas.reduce((total, venta) => {
        const monto = typeof venta.monto === 'string' ? parseFloat(venta.monto) : venta.monto;
        return total + (isNaN(monto) ? 0 : monto);
      }, 0);
    },
    formatearFecha(fecha) {
      if (typeof fecha === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(fecha)) {
        const [anio, mes, dia] = fecha.split('-');
        return `${dia}/${mes}/${anio}`;
      }
      if (Array.isArray(fecha) && fecha.length === 3) {
        const [anio, mes, dia] = fecha;
        return `${String(dia).padStart(2,'0')}/${String(mes).padStart(2,'0')}/${anio}`;
      }
      return new Date(fecha).toLocaleDateString('es-ES');
    },
    formatoMoneda(valor) {
      if (typeof valor !== 'number') return valor;
      return valor.toFixed(2);
    }
  },
  watch: {
    ventasFiltradas(newVal) {
      this.montoTotalDelDia = this.calcularMontoTotal(newVal);
    }
  }
};
</script>

<style scoped>
.reporte-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 48px;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 4px;
}

.page-subtitle {
  font-size: 0.88rem;
  color: var(--text-muted);
}

/* ── Filtro fecha ── */
.filtro-fecha {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: var(--surface-0);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
  flex-wrap: wrap;
}

.fecha-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.fecha-field label {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.fecha-field input[type="date"] {
  padding: 10px 14px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  color: var(--text-primary);
  background: var(--surface-1);
  outline: none;
  transition: border-color var(--transition), box-shadow var(--transition);
}

.fecha-field input[type="date"]:focus {
  border-color: var(--green-700);
  box-shadow: 0 0 0 3px rgba(11, 125, 89, 0.1);
}

.btn-buscar {
  padding: 10px 24px;
  background: linear-gradient(135deg, var(--green-700), var(--green-800));
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.88rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition);
}

.btn-buscar:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(11, 125, 89, 0.3);
}

/* ── KPIs ── */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 28px;
}

.kpi-card {
  background: var(--surface-0);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition);
}

.kpi-card:hover { transform: translateY(-2px); }

.kpi-card.accent {
  background: linear-gradient(135deg, var(--green-50), var(--green-100));
  border-color: #b2dfcf;
}

.kpi-card.gold {
  background: linear-gradient(135deg, #fffbea, #fff8e1);
  border-color: #f5e090;
}

.kpi-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--surface-2);
  border: 1px solid var(--border);
  flex-shrink: 0;
  position: relative;
}

.kpi-icon--date {
  background: rgba(11, 125, 89, 0.08);
  border-color: rgba(11, 125, 89, 0.2);
}
.kpi-icon--date::before {
  content: '';
  position: absolute;
  inset: 8px;
  border: 1.5px solid var(--green-700);
  border-radius: 3px;
}

.kpi-icon--sales {
  background: rgba(11, 125, 89, 0.12);
  border-color: rgba(11, 125, 89, 0.25);
}
.kpi-icon--sales::before {
  content: '';
  position: absolute;
  bottom: 8px;
  left: 8px;
  right: 8px;
  height: 2px;
  background: var(--green-700);
  box-shadow: 0 -6px 0 var(--green-700), 0 -12px 0 rgba(11,125,89,0.4);
}

.kpi-icon--money {
  background: rgba(201, 168, 76, 0.15);
  border-color: rgba(201, 168, 76, 0.35);
}
.kpi-icon--money::before {
  content: '$';
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--gold);
  display: block;
  text-align: center;
  line-height: 40px;
}

.kpi-label {
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--text-muted);
  font-weight: 600;
  margin-bottom: 4px;
}

.kpi-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--green-900);
  font-variant-numeric: tabular-nums;
}

/* ── Tabla (mismo estilo compartido) ── */
.tabla-wrapper {
  overflow-x: auto;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
}

.tabla-premium {
  width: 100%;
  border-collapse: collapse;
  background: var(--surface-0);
  font-size: 0.88rem;
}

.tabla-premium thead th {
  background: linear-gradient(135deg, var(--green-800), var(--green-700));
  color: rgba(255,255,255,0.95);
  font-size: 0.78rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  padding: 14px 16px;
  text-align: left;
  white-space: nowrap;
}

.tabla-premium tbody tr {
  border-bottom: 1px solid var(--border);
  transition: background var(--transition);
}

.tabla-premium tbody tr:last-child { border-bottom: none; }
.tabla-premium tbody tr:hover { background: var(--green-50); }

.tabla-premium tbody td {
  padding: 13px 16px;
  color: var(--text-primary);
  vertical-align: middle;
}

.td-id {
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 0.8rem;
}

.td-fecha {
  color: var(--text-muted);
  font-size: 0.83rem;
}

.td-monto {
  font-weight: 700;
  color: var(--green-700);
}

/* ── Empty ── */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.empty-icon { font-size: 2.5rem; margin-bottom: 12px; opacity: 0.5; }

/* ── Transitions ── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(8px); }

@media (max-width: 768px) {
  .filtro-fecha { flex-direction: column; align-items: stretch; }
  .btn-buscar { width: 100%; }
}
</style>