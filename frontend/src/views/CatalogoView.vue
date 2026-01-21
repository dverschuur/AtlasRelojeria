<template>

  <div style="text-align: center;">
    <div v-if="!logueado" class="mensaje">
      <p><strong>Debes iniciar sesión</strong> para poder realizar una compra.</p>
    </div>
  </div>

  <div v-if="mensajeCompra" class="mensaje-exito">
    <p><strong>{{ mensajeCompra }}</strong></p>
  </div>

  <div class="catalogo">
    <div v-for="reloj in relojes" :key="reloj.nombre" class="producto">
      <div class="producto-header">
        <button v-if="logueado" 
                @click="toggleListaDeseos(reloj)" 
                class="btn-lista-deseos"
                :class="{ 'en-lista': estaEnListaDeseos(reloj) }">
          ♥
        </button>
      </div>
      <img :src="'http://localhost:8081/img/' + reloj.imagen" alt="Imagen del reloj" />
      <h3>{{ reloj.nombre }}</h3>
      <p>{{ reloj.descripcion }}</p>
      <strong>${{ reloj.precio }}</strong>
      <br />
      <button v-if="logueado" @click="$emit('comprar', reloj)">Comprar</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CatalogoView',
  data() {
    return {
      relojes: [],
      listaDeseos: [],
      perfilId: null
    }
  },
  props: {
    logueado: Boolean,
    mensajeCompra: String,
    perfilActual: {
      type: Object,
      default: null
    }
  },
  watch: {
    perfilActual: {
      immediate: true,
      handler(nuevoPerfil) {
        if (nuevoPerfil) {
          this.perfilId = nuevoPerfil.id;
          if (this.logueado) {
            this.cargarListaDeseos();
          }
        }
      }
    },
    logueado: {
      immediate: true,
      handler(estaLogueado) {
        if (estaLogueado && this.perfilId) {
          this.cargarListaDeseos();
        }
      }
    }
},
    
  mounted() {
    this.cargarProductos();
  },
  methods: {
    async cargarProductos() {
      try {
        const response = await axios.get('http://localhost:8081/api/productos/vista-cliente');
        this.relojes = response.data.filter(p => p.cantidad > 0);
      } catch (error) {
        console.error("Error al cargar productos:", error);
      }
    },
    async cargarListaDeseos() {
      if (!this.perfilId) {
        console.error('No hay ID de perfil disponible');
        return;
      }
      
      try {
        console.log('Cargando lista de deseos para ID:', this.perfilId);
        const response = await axios.get(`http://localhost:8081/api/perfiles/lista-deseos/${this.perfilId}`);
        this.listaDeseos = response.data || [];
        console.log('Lista de deseos cargada:', this.listaDeseos);
      } catch (error) {
        console.error("Error al cargar lista de deseos:", error);
        this.listaDeseos = [];
      }
    },
    async toggleListaDeseos(reloj) {
      if (!this.perfilId) {
        console.error('No hay ID de perfil disponible');
        return;
      }

      try {
        console.log('Intentando toggle para producto:', reloj.id);
        const response = await axios.post(
          `http://localhost:8081/api/perfiles/toggle-lista-deseos/${this.perfilId}`,
          { productoId: reloj.id }
        );
        this.listaDeseos = response.data || [];
        console.log('Lista de deseos actualizada:', this.listaDeseos);
      } catch (error) {
        console.error("Error al actualizar lista de deseos:", error);
      }
    },
    estaEnListaDeseos(reloj) {
      return this.listaDeseos.some(item => item.id === reloj.id);
    }
  }
}
</script>

<style scoped>
.catalogo {
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
  position: relative;
}
.producto img {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  object-fit: cover;
}

.mensaje {
  display: inline-block;
  background-color: #fff3cd;
  color: #856404;
  border: 3px solid #ffeeba;
  border-radius: 8px;
  padding: 1px 1px;
  margin: 20px auto;
  text-align: center;
  line-height: 1.2;
  font-size: 15px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
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

.producto-header {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1;
}

.btn-lista-deseos {
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  margin: 0;
}

.btn-lista-deseos.en-lista {
  background-color: #ff4081;
  color: white;
  border-color: #ff4081;
}

.mensaje-exito {
  background-color: #d4edda;
  color: #155724;
  border: 3px solid #c3e6cb;
  border-radius: 8px;
  padding: 10px;
  margin: 20px auto;
  width: fit-content;
  font-size: 16px;
  text-align: center;
}

</style>
