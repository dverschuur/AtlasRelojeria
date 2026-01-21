<template>
  <div class="historial-container">
    <!-- Selector de fecha -->
    <div class="fecha-selector">
      <label for="fechaSeleccionada">Seleccionar fecha:</label>
      <input 
        type="date" 
        id="fechaSeleccionada" 
        v-model="fechaSeleccionada" 
        @change="cambiarFecha"
        class="date-input"
      />
      <button @click="irADiaActual" class="btn-hoy">Buscar</button>
    </div>

    <!-- Resumen de ventas de la fecha seleccionada -->
    <div class="resumen-ventas">
      <span><strong>Fecha seleccionada:</strong> {{ fechaSeleccionadaFormateada }}</span>
      <span><strong>Ventas totales:</strong> {{ ventasFiltradas.length }}</span>
      <span><strong>Monto total:</strong> {{ formatoMoneda(montoTotalDelDia) }}</span>
    </div>
    <h2>Reporte de Ventas</h2>
    <div class="tabla-ventas">
      <table>
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
          <td>{{ venta.idVenta }}</td>
          <td>{{ venta.idUsuario }}</td>
          <td>{{ venta.idProducto }}</td>
          <td>{{ venta.direccion }}</td>
          <td>{{ formatearFecha(venta.fecha) }}</td>
          <td>{{ venta.monto }}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="ventasFiltradas.length === 0" class="no-results">
      No hubo ventas el {{ fechaSeleccionadaFormateada }}
    </div>
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
    this.fechaSeleccionada = this.fechaHoy; // Inicialmente muestra el día de hoy
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
      // Formatear la fecha seleccionada para mostrar
      // Usar el string de fecha directamente para evitar problemas de zona horaria
      const [yyyy, mm, dd] = this.fechaSeleccionada.split('-');
      this.fechaSeleccionadaFormateada = `${dd}/${mm}/${yyyy}`;
      
      // Filtrar ventas para la fecha seleccionada
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
        console.log('Ventas recibidas:', response.data);
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
    filtrarVentasDeHoy(ventas) {
      const hoy = new Date();
      const yyyy = hoy.getFullYear();
      const mm = String(hoy.getMonth() + 1).padStart(2, '0');
      const dd = String(hoy.getDate()).padStart(2, '0');
      const fechaHoy = `${yyyy}-${mm}-${dd}`;
      return ventas.filter(venta => venta.fecha === fechaHoy);
    },
    calcularMontoTotal(ventas) {
      return ventas.reduce((total, venta) => {
        // Si el monto viene como string, conviértelo a número
        const monto = typeof venta.monto === 'string' ? parseFloat(venta.monto) : venta.monto;
        return total + (isNaN(monto) ? 0 : monto);
      }, 0);
    },
    formatearFecha(fecha) {
      // Si la fecha es un string tipo 'YYYY-MM-DD'
      if (typeof fecha === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(fecha)) {
        const [anio, mes, dia] = fecha.split('-');
        return `${dia}/${mes}/${anio}`;
      }
      // Si la fecha es un array [año, mes, día]
      if (Array.isArray(fecha) && fecha.length === 3) {
        const [anio, mes, dia] = fecha;
        // Asegura dos dígitos para día y mes
        return `${String(dia).padStart(2, '0')}/${String(mes).padStart(2, '0')}/${anio}`;
      }
      // Fallback para otros formatos de fecha
      const date = new Date(fecha);
      return date.toLocaleDateString('es-ES');
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
.historial-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.fecha-selector {
  display: flex;
  align-items: center;
  gap: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.fecha-selector label {
  font-weight: bold;
  color: #333;
  margin: 0;
}

.date-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
}

.btn-hoy {
  padding: 8px 16px;
  background-color: #0b7d59;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-hoy:hover {
  background-color: #095a42;
}

.resumen-ventas {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #e6f4ef;
  border-radius: 8px;
  padding: 16px 24px;
  margin-bottom: 24px;
  font-size: 1.1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.resumen-ventas span {
  margin-right: 20px;
}

.tabla-ventas {
  overflow-x: auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #0b7d59;
  color: white;
  font-weight: bold;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}

tr:hover {
  background-color: #f5f5f5;
}

.no-results {
  text-align: center;
  padding: 20px;
  color: #666;
  font-style: italic;
}

@media (max-width: 768px) {
  .fecha-selector {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .fecha-selector label {
    text-align: center;
  }
  
  .resumen-ventas {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .resumen-ventas span {
    margin-right: 0;
  }
  
  .tabla-ventas {
    font-size: 14px;
  }

  th, td {
    padding: 8px;
  }
}
</style>