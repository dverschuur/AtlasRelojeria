<template>
  <div class="historial-container">
    <h2>Historial de Ventas</h2>
    
    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="Buscar por ID, cliente, producto..."
        @input="filterVentas"
      >
      <span class="search-icon">🔍</span>
    </div>

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
          <tr v-for="venta in ventasFiltradas" :key="venta.idVenta">
            <td v-html="resaltarCoincidencia(venta.idVenta)"></td>
            <td v-html="resaltarCoincidencia(venta.idUsuario)"></td>
            <td v-html="resaltarCoincidencia(venta.idProducto)"></td>
            <td v-html="resaltarCoincidencia(venta.direccion)"></td>
            <td>{{ formatearFecha(venta.fecha) }}</td>
            <td v-html="resaltarCoincidencia(venta.monto.toString())"></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="ventasFiltradas.length === 0" class="no-results">
      No se encontraron ventas que coincidan con la búsqueda
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
        const searchableFields = {
          idVenta: String(venta.idVenta || ''),
          idUsuario: String(venta.idUsuario || ''),
          idProducto: String(venta.idProducto || ''),
          direccion: String(venta.direccion || ''),
          monto: String(venta.monto || '')
        };

        return Object.values(searchableFields).some(value => 
          value.toLowerCase().includes(query)
        );
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
      
      return `${antes}<span class="highlight">${coincidencia}</span>${despues}`;
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

.search-bar {
  position: relative;
  margin-bottom: 20px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.search-bar input {
  width: 100%;
  padding: 12px 40px 12px 15px;
  border: 2px solid #0b7d59;
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.search-bar input:focus {
  border-color: #435505;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
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

.highlight {
  background-color: #ffeb3b;
  padding: 2px;
  border-radius: 2px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .tabla-ventas {
    font-size: 14px;
  }
  
  th, td {
    padding: 8px;
  }
  
  .search-bar input {
    font-size: 14px;
    padding: 10px 35px 10px 12px;
  }
}
</style> 