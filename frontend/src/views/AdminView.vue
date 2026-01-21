<template>
  <div>
    <h2> Inventario de Relojes</h2>
   


    <button @click="mostrarFormulario = true" v-if="!mostrarFormulario">
      Agregar producto nuevo a inventario
    </button>

    <RegistrarProducto
        v-if="mostrarFormulario"
        @producto-agregado="onProductoAgregado"
        @cancelar="mostrarFormulario = false"
    />
    <br />
    <div class="inventario">
      <div
          v-for="reloj in relojes"
          :key="reloj.nombre"
          :class="['producto', reloj.cantidad < 5 ? 'bajo-stock' : '']"
        >
      <p v-if="reloj.cantidad < 5" class="alerta">
          Stock bajo
        </p>
        <img :src="'http://localhost:8081/img/' + reloj.imagen" :alt="reloj.nombre" />
        <h3>{{ reloj.nombre + " ("+reloj.id+ ")" }}</h3>
        <p>Descripcion: {{ reloj.descripcion }}</p>
        <strong>Precio: ${{ reloj.precio }}</strong>
        <br /><br />
        <strong>Cantidad disponible: {{ reloj.cantidad }}</strong>
        <br />
        <button class="btn-eliminar" @click="confirmarEliminacion(reloj)">Eliminar</button>
        <button class="btn-editar" @click="productoAEditar = reloj">Modificar</button>

        

        <br />
      </div>
    </div>
    <EditarProductoView
  v-if="productoAEditar"
  :producto-original="productoAEditar"
  @actualizado="cerrarYActualizar"
  @cerrar="productoAEditar = null"
/>
  </div>
</template>
<script>
import axios from 'axios'
import RegistrarProducto from './RegistrarProducto.vue'
import EditarProductoView from './EditarProductoView.vue'

export default {
  name: 'InventarioView',
  components: {
    RegistrarProducto,
      EditarProductoView 
  },
  data() {
    return {
      relojes: [],
      mostrarFormulario: false,
      productoAEditar: null
    }
  },
  computed: {
  productosConBajaDisponibilidad() {
    return this.relojes.filter(p => p.cantidad < 5)
  }
},

  mounted() {
    this.cargarInventario()
  },
  methods: {
    cargarInventario() {
      axios.get('http://localhost:8081/api/productos/inventario')
        .then(response => {
          this.relojes = response.data
        })
        .catch(error => {
          console.error('Error al cargar el inventario:', error)
        })
    },
      cerrarYActualizar() {
    this.productoAEditar = null
    this.cargarInventario()
  }, 
    confirmarEliminacion(reloj) {
     
  if (confirm(`¿Estás segura de que deseas eliminar el producto "${reloj.nombre}"?`)) {
    axios.delete(`http://localhost:8081/api/productos/eliminar-id/${encodeURIComponent(reloj.id)}`)
  

      .then(() => {
        alert("Producto eliminado correctamente.");
        this.cargarInventario();
      })
      .catch(error => {
        alert("Error al eliminar el producto.");
        console.error("Error al eliminar:", error);
      });
  }
},

    onProductoAgregado() {
      this.mostrarFormulario = false
      this.cargarInventario()
    }

  },
  

  props: {
    logueado: Boolean
  }
}
</script>

<style scoped>
.inventario {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  padding: 20px;
}
.producto {
  width: 220px;
  border: 1px solid #ccc;
  padding: 10px;
  text-align: center;
  border-radius: 10px;
  background-color:rgb(255, 255, 255);
}
.producto img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

button {
  margin-top: 10px;
  padding: 6px 10px;
  background-color:#0b7d59;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.alerta {
  text-align: center;
  background-color: #ffe0e0;
  color: #d32f2f;
  font-weight: bold;
  border: 2px solid #f44336;
  border-radius: 4px;
  padding:5px;
  margin: 1px auto;
  width: 50%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.bajo-stock {
  border: 3px solid #d32f2f;
  background-color: #fff5f5;
  box-shadow: 0 0 10px rgba(211, 47, 47, 0.5);
  transition: 0.3s ease;
  position: relative;
}

.btn-eliminar {
  background-color:rgb(200, 85, 83);
  color: white;
  border: none;
  padding: 6px 12px;
  margin: 5px 4px 0 4px;
  border-radius: 5px;
  cursor: pointer;
}

.btn-editar {
  background-color:rgb(107, 154, 195);
  color: white;
  border: none;
  padding: 6px 12px;
  margin: 5px 4px 0 4px;
  border-radius: 5px;
  cursor: pointer;
}

</style>