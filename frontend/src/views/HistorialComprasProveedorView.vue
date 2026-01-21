<template>
  <div class="historial-container">
    <h2>Historial de Compras a Proveedores</h2>

    <div class="search-bar">
      <input
          type="text"
          v-model="searchQuery"
          placeholder="Buscar por numero de compra, proveedor, producto..."
          @input="filterCompras"
      >
      <span class="search-icon">🔍</span>
    </div>

    <div class="tabla-compras">
      <table>
        <thead>
        <tr>
          <th>Numero De Compra</th>
          <th>Proveedor</th>
          <th>Monto</th>
          <th>Producto</th>
          <th>Cantidad De Producto</th>
          <th>Fecha De Compra</th>
          <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="compra in comprasFiltradas" :key="compra.numeroDeCompra">
          <td v-html="resaltarCoincidencia(compra.numeroDeCompra)"></td>
          <td v-html="resaltarCoincidencia(compra.proveedor)"></td>
          <td v-html="resaltarCoincidencia(compra.monto)"></td>
          <td v-html="resaltarCoincidencia(compra.producto)"></td>
          <td v-html="resaltarCoincidencia(compra.cantidadDeProducto)"></td>
          <td>{{ formatearFecha(compra.fechaCompra) }}</td>
          <td>
            <button @click="cancelarOrden(compra.numeroDeCompra)" class="btn-cancelar">
              Cancelar
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="comprasFiltradas.length === 0" class="no-results">
      No se encontraron ventas que coincidan con la búsqueda
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'HistorialComprasProveedorView',
  data() {
    return {
      compras: [],
      comprasFiltradas: [],
      searchQuery: ''
    };
  },
  mounted() {
    this.cargarCompras();
  },
  methods: {
    async cargarCompras() {
      try {
        const response = await axios.get('http://localhost:8081/api/ordenes');
        this.compra = response.data;
        this.comprasFiltradas = response.data;
      } catch (error) {
        console.error('Error al cargar las compras a proveedores:', error);
      }
    },
    async cancelarOrden(numeroDeCompra) {
      try {
        const response = await axios.delete(`http://localhost:8081/api/ordenes/${numeroDeCompra}`);
        if (response.data.error) {
          alert(response.data.error);
        } else {
          alert('Orden cancelada exitosamente');
          await this.cargarCompras();
        }
      } catch (error) {
        if (error.response && error.response.data.error) {
          alert(error.response.data.error);
        } else {
          alert('Error al cancelar la orden');
        }
      }
    },
    formatearFecha(fecha) {
      const date = new Date(fecha);
      date.setMinutes(date.getMinutes() + date.getTimezoneOffset());
      return date.toLocaleDateString('es-ES');
    },
    filterCompras() {
      if (!this.searchQuery.trim()) {
        this.comprasFiltradas = this.compra;
        return;
      }

      const query = this.searchQuery.toLowerCase().trim();
      this.comprasFiltradas = this.compra.filter(compra => {
        const searchableFields = {
          numeroDeCompra: String(compra.numeroDeCompra|| ''),
          proveedor: String(compra.proveedor || ''),
          monto: String(compra.monto || ''),
          producto: String(compra.producto || ''),
          cantidadDeProducto: String(compra.cantidadDeProducto || ''),
          fechaCompra: String(compra.fechaCompra || '')
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

.tabla-compras {
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

.btn-cancelar {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-cancelar:hover {
  background-color: #c82333;
}

@media (max-width: 768px) {
  .tabla-compras {
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