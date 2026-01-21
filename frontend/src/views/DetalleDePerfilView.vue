<template>
  <div class="detalle-container">
    <h2> Detalle del Perfil</h2>
    <ul>
     <ul  v-if="perfilEditable">
  <li><strong>Nombre:</strong> {{ perfilEditable.nombre }}</li>
  <li><strong>Cédula (ID):</strong> {{ perfilEditable.id }}</li>
  <li><strong>Edad:</strong> {{ perfilEditable.edad }}</li>
  
</ul>

    </ul>

    <div class="botones-horizontales">
      <button v-if="!esAdmin" @click="eliminarPerfil" class="eliminar">Eliminar Perfil</button>
      <button @click="$emit('cerrar-sesion')" class="cerrar-sesion">Cerrar Sesión</button>
      <button v-if="!esAdmin" @click="mostrarEditor = true" class="editar">Editar Perfil</button>
      <button v-if="!esAdmin" @click="toggleListaDeseos" class="lista-deseos">
        {{ mostrarListaDeseos ? 'Ocultar Lista de Deseos' : 'Ver Lista de Deseos' }}
      </button>
    </div>

    <!-- Vista de Lista de Deseos -->
    <div v-if="mostrarListaDeseos" class="lista-deseos-container">
      <h3>Mi Lista de Deseos</h3>
      <div v-if="productosDeseados.length === 0" class="mensaje-vacio">
        No tienes productos en tu lista de deseos.
      </div>
      <div v-else class="productos-grid">
        <div v-for="producto in productosDeseados" :key="producto.id" class="producto-card">
          <img :src="'http://localhost:8081/api/productos/img/' + producto.imagen" :alt="producto.nombre">
          <div class="producto-info">
            <h4>{{ producto.nombre }}</h4>
            <p>{{ producto.descripcion }}</p>
            <strong>${{ producto.precio }}</strong>
          </div>
          <div class="producto-acciones">
            <button @click="quitarDeListaDeseos(producto)" class="quitar">
              Quitar de la lista
            </button>
            <button @click="$emit('comprar', producto)" class="comprar">
              Comprar
            </button>
          </div>
        </div>
      </div>
    </div>

<EditarPerfilView
  v-if="mostrarEditor"
  :perfil-original="perfilEditable"
  @actualizado="actualizarPerfil"
  @cerrar="mostrarEditor = false"
/>


    <p v-if="mensaje" :class="['mensaje-status', { error: mensajeEsError }]">{{ mensaje }}</p>
  </div>
</template>

<script>
import axios from 'axios'
import EditarPerfilView from './EditarPerfilView.vue'

export default {
  name: 'DetalleDePerfilView',
  components: {
    EditarPerfilView
  },
  props: {
    perfil: Object,
    esAdmin: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      perfilEditable: null,
      mensaje: '',
      mensajeEsError: false,
      mostrarEditor: false,
      mostrarListaDeseos: false,
      productosDeseados: []
    }
  },
  mounted() {
    this.perfilEditable = { ...this.perfil };
  },

  methods: {
    async eliminarPerfil() {
      const confirmar = confirm("¿Estás seguro de que deseas eliminar este perfil?");
      if (!confirmar) return;

      try {
        await axios.delete(`http://localhost:8081/api/perfiles/eliminar/${this.perfil.nombre}`);
        this.mensaje = "Perfil eliminado exitosamente.";
        this.mensajeEsError = false;
        setTimeout(() => this.$emit('perfil-eliminado'), 1000);
      } catch (err) {
        this.mensaje = "Error al eliminar el perfil.";
        this.mensajeEsError = true;
      }
    },

    actualizarPerfil(perfilEditado) {
      this.perfilEditable = { ...perfilEditado };
      this.$emit('perfil-actualizado', perfilEditado); // 
      this.mensaje = "Perfil actualizado correctamente.";
      this.mensajeEsError = false;
    },
    async toggleListaDeseos() {
      this.mostrarListaDeseos = !this.mostrarListaDeseos;
      if (this.mostrarListaDeseos) {
        await this.cargarListaDeseos();
      }
    },
    async cargarListaDeseos() {
      try {
        console.log('Cargando lista de deseos para ID:', this.perfil.id);
        const response = await axios.get(`http://localhost:8081/api/perfiles/lista-deseos/${this.perfil.id}`);
        this.productosDeseados = response.data;
        console.log('Productos en lista de deseos:', this.productosDeseados);
      } catch (error) {
        console.error('Error al cargar la lista de deseos:', error);
        this.mensaje = 'Error al cargar la lista de deseos.';
        this.mensajeEsError = true;
      }
    },
    async quitarDeListaDeseos(producto) {
      try {
        console.log('Quitando producto de lista de deseos:', producto.id);
        const response = await axios.post(`http://localhost:8081/api/perfiles/toggle-lista-deseos/${this.perfil.id}`, {
          productoId: producto.id
        });
        this.productosDeseados = response.data;
        this.mensaje = 'Producto eliminado de la lista de deseos.';
        this.mensajeEsError = false;
      } catch (error) {
        console.error('Error al quitar de la lista de deseos:', error);
        this.mensaje = 'Error al quitar el producto de la lista de deseos.';
        this.mensajeEsError = true;
      }
    }
  }
}
</script>

<style scoped>
.detalle-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
  border: 1px solid #aaa;
  border-radius: 10px;
  background-color: #fefefe;
}

ul {
  list-style: none;
  padding: 0;
}
li {
  margin: 10px 0;
}

.mensaje-status {
  padding: 10px;
  border-radius: 5px;
  margin-top: 15px;
  text-align: center;
}

.mensaje-status.error {
  background-color: #ffebee;
  color: #c62828;
  border: 1px solid #ffcdd2;
}

.mensaje-status:not(.error) {
  background-color: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
}

.botones-horizontales {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.botones-horizontales button {
  padding: 10px 15px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
}

.eliminar {
  background-color:#9c1c1c;
  color: white;
}

.cerrar-sesion {
  background-color: #9c1c1c;
  color: white;
}

.editar {
  background-color: rgb(107, 154, 195);
  color: white;
}

.lista-deseos {
  background-color: rgb(107, 154, 195);
  color: white;
}

.lista-deseos-container {
  margin-top: 30px;
  padding: 20px;
  border-top: 1px solid #ddd;
}

.mensaje-vacio {
  text-align: center;
  color: #666;
  padding: 20px;
}

.productos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.producto-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.producto-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.producto-info {
  padding: 15px;
}

.producto-info h4 {
  margin: 0 0 10px 0;
}

.producto-acciones {
  padding: 15px;
  display: flex;
  gap: 10px;
  justify-content: space-between;
}

.quitar {
  background-color: #ff4081;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.comprar {
  background-color: #0b7d59;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}
</style>
