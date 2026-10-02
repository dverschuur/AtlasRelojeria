<template>
  <div class="historial-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">Historial de Ventas</h2>
        <p class="page-subtitle">Consulta todas las transacciones registradas</p>
      </div>
    </div>

    <!-- Búsqueda -->
    <div class="search-wrapper">
      <div class="search-box">
        <span class="search-icon"></span>
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Buscar por ID, cliente, producto..."
          @input="filterVentas"
        />
        <button v-if="searchQuery" @click="searchQuery = ''; filterVentas()" class="btn-clear">✕</button>
      </div>
      <div class="results-count" v-if="searchQuery">
        {{ ventasFiltradas.length }} resultado(s)
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
          <tr v-for="venta in ventasFiltradas" :key="venta.idVenta" class="tabla-row">
            <td v-html="resaltarCoincidencia(venta.idVenta)" class="td-id"></td>
            <td v-html="resaltarCoincidencia(venta.idUsuario)"></td>
            <td v-html="resaltarCoincidencia(venta.idProducto)"></td>
            <td v-html="resaltarCoincidencia(venta.direccion)"></td>
            <td class="td-fecha">{{ formatearFecha(venta.fecha) }}</td>
            <td v-html="resaltarCoincidencia(venta.monto.toString())" class="td-monto"></td>
          </tr>
        </tbody>
      </table>
    </div>

    <transition name="fade-slide">
      <div v-if="ventasFiltradas.length === 0" class="empty-state">
        <div class="empty-icon"></div>
        <p>No se encontraron ventas que coincidan con la búsqueda.</p>
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
      searchQuery: ''
    };
  },
  mounted() {
    this.cargarVentas();
  },
  methods: {
    async cargarVentas() {
      try {
        const response = await axios.get('http://localhost:8081/api/ventas');
        this.ventas = response.data;
        this.ventasFiltradas = response.data;
      } catch (error) {
        console.error('Error al cargar las ventas:', error);
      }
    },
    formatearFecha(fecha) {
      const date = new Date(fecha);
      date.setMinutes(date.getMinutes() + date.getTimezoneOffset());
      return date.toLocaleDateString('es-ES');
    },
    filterVentas() {
      if (!this.searchQuery.trim()) {
        this.ventasFiltradas = this.ventas;
        return;
      }
      const query = this.searchQuery.toLowerCase().trim();
      this.ventasFiltradas = this.ventas.filter(venta => {
        const fields = {
          idVenta: String(venta.idVenta || ''),
          idUsuario: String(venta.idUsuario || ''),
          idProducto: String(venta.idProducto || ''),
          direccion: String(venta.direccion || ''),
          monto: String(venta.monto || '')
        };
        return Object.values(fields).some(v => v.toLowerCase().includes(query));
      });
    },
    resaltarCoincidencia(texto) {
      if (!texto || !this.searchQuery.trim()) return texto;
      const query = this.searchQuery.toLowerCase().trim();
      const textoStr = String(texto);
      const index = textoStr.toLowerCase().indexOf(query);
      if (index === -1) return textoStr;
      const antes = textoStr.substring(0, index);
      const coincidencia = textoStr.substring(index, index + query.length);
      const despues = textoStr.substring(index + query.length);
      return `${antes}<mark class="highlight">${coincidencia}</mark>${despues}`;
    }
  }
};
</script>

<style scoped>
.historial-page {
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

/* ── Search ── */
.search-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 500px;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 14px;
  width: 16px;
  height: 16px;
  pointer-events: none;
  opacity: 0.5;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%234a6556' stroke-width='2'%3E%3Ccircle cx='11' cy='11' r='8'/%3E%3Cpath d='m21 21-4.35-4.35'/%3E%3C/svg%3E") center/contain no-repeat;
}

.search-box input {
  width: 100%;
  padding: 12px 40px 12px 40px;
  border: 1.5px solid var(--border);
  border-radius: 50px;
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  color: var(--text-primary);
  background: var(--surface-0);
  outline: none;
  transition: border-color var(--transition), box-shadow var(--transition);
}

.search-box input:focus {
  border-color: var(--green-700);
  box-shadow: 0 0 0 3px rgba(11, 125, 89, 0.1);
}

.btn-clear {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.85rem;
  padding: 4px;
  line-height: 1;
}

.results-count {
  font-size: 0.82rem;
  color: var(--text-muted);
  white-space: nowrap;
}

/* ── Tabla ── */
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
  font-family: 'Inter', sans-serif;
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

.tabla-premium tbody tr:hover {
  background: var(--green-50);
}

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

/* ── Highlight ── */
:deep(.highlight) {
  background: #ffeb3b;
  color: #333;
  padding: 1px 3px;
  border-radius: 3px;
  font-weight: 700;
}

/* ── Empty ── */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.empty-icon {
  width: 48px;
  height: 48px;
  border: 2px solid var(--border);
  border-radius: 50%;
  margin: 0 auto 12px;
  opacity: 0.35;
}

/* ── Transitions ── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(8px); }
</style>