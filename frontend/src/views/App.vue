<template>
  <div>
    <header class="encabezado">
      <h1>Atlas</h1>
      <h3>Relojería</h3>
    </header>
    <nav class="navbar">
      <button v-if="!esAdmin" @click="vista = 'catalogo'">Catálogo</button>
      <button v-if="!usuarioAutenticado" @click="vista = 'login'">Iniciar Sesión</button>
      <button v-if="usuarioAutenticado" @click="vista = 'perfil'">Detalle de Perfil</button>
      <button v-if="esAdmin" @click="vista = 'historial'">Historial de Ventas</button>
      <button v-if="esAdmin" @click="vista = 'admin'">Inventario</button>
      <button v-if="esAdmin" @click="vista = 'ordenDeCompra'">Orden de Compra</button>
      <button v-if="esAdmin" @click="vista = 'historialDeOrdenDeCompra'">Historial de Ordenes de Compra</button>
      <button v-if="esAdmin" @click="vista = 'reporteDeVentas'">Reporte De Ventas</button>
    </nav>

    <div v-if="vista === 'ordenDeCompra'">
      <OrdenDeCompraView @cancelar="vista = 'admin'" />
    </div>
    <div v-if="vista === 'catalogo'">
      <CatalogoView 
        :logueado="usuarioAutenticado" 
        :perfilActual="usuarioActual"
     
        :mensajeCompra="mensajeCompra"
        @comprar="irAVenta"
        
      />
    </div>
    <div v-else-if="vista === 'login'">
      <LoginView @login-exitoso="manejarLogin" @crear-perfil="vista = 'crearPerfil'" />
    </div>
    <div v-else-if="vista === 'crearPerfil'">
      <CrearPerfilView />
    </div>
    <div v-else-if="vista === 'admin'">
      <AdminView />
    </div>
    <div v-else-if="vista === 'compra'">
      <CompraView :producto="productoSeleccionado" />
    </div>
    <div v-else-if="vista === 'perfil'">
      <DetalleDePerfilView 
        :perfil="usuarioActual" 
        :esAdmin="esAdmin" 
        @perfil-eliminado="logout" 
        @cerrar-sesion="logout"
        @comprar="irAVenta"
         @perfil-actualizado="usuarioActual = $event"
      />
    </div>
    <div v-if="vista === 'venta'">
      <VentasView 
    :producto="productoSeleccionado" 
    :perfil="usuarioActual"
    @compra-exitosa="manejarCompraExitosa"
  />
    </div>
    <div v-else-if="vista === 'historial'">
      <HistorialView />
    </div>
    <div v-else-if="vista === 'historialDeOrdenDeCompra'">
      <HistorialComprasProveedorView />
    </div>
    <div v-else-if="vista === 'reporteDeVentas'">
      <ReporteDeVentasView />
    </div>
  </div>
</template>

<script>
import CatalogoView from './CatalogoView.vue'
import LoginView from './LoginView.vue'
import AdminView from './AdminView.vue'
import CrearPerfilView from './CrearPerfilView.vue'
import DetalleDePerfilView from './DetalleDePerfilView.vue'
import VentasView from './VentasView.vue'
import HistorialView from './HistorialView.vue'
import OrdenDeCompraView from './OrdenDeCompraView.vue'
import HistorialComprasProveedorView from "@/views/HistorialComprasProveedorView.vue";
import ReporteDeVentasView from "@/views/ReporteDeVentasView.vue";

export default {
  name: 'App',
  data() {
    return {
      vista: 'catalogo',
      productoSeleccionado: null,
      usuarioAutenticado: false,
      usuarioActual: null,
      esAdmin: false,
      mensajeCompra: ''

    }
  },
  components: {
    OrdenDeCompraView,
    CatalogoView,
    LoginView,
    AdminView,
    CrearPerfilView,
    DetalleDePerfilView,
    VentasView,
    HistorialView,
    HistorialComprasProveedorView,
    ReporteDeVentasView,
  },
  methods: {
    irACompra(producto) {
      this.productoSeleccionado = producto
      this.vista = 'compra'
    },
    manejarCompraExitosa(mensaje) {
  this.mensajeCompra = mensaje;
  this.vista = 'catalogo';

  // Este timeout borra el mensaje a los 3 segundos
  setTimeout(() => {
    this.mensajeCompra = '';
  }, 2000);
},

    manejarLogin(esAdmin, perfil) {
      this.usuarioAutenticado = true
      this.usuarioActual = perfil
      this.esAdmin = esAdmin
      this.vista = esAdmin ? 'admin' : 'catalogo'
    },
    logout() {
      this.usuarioAutenticado = false
      this.usuarioActual = null
      this.esAdmin = false
      this.vista = 'catalogo'
    },
    irAVenta(producto) {
      this.productoSeleccionado = producto
      this.vista = 'venta'
    }
  }
}
</script>

<style>
.navbar {
  background-color:#435505;
  padding: 10px;
  display: flex;
  gap: 10px;
  justify-content: center;
}
.navbar button {
  color: white;
  background-color: transparent;
  border: 2px solid white;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.navbar button:hover {
  background-color: white;
  color: #435505;
}
.encabezado {
  background-color:rgba(193, 201, 169, 0.88);
  padding: 15px;
  text-align: center;
  border-bottom: 2px solid #ddd;
}

.encabezado h1 {
  margin: 0;
  font-size: 2.5em;
  color: #0b7d59;
  font-weight: bold;
}

.encabezado h3 {
  margin: 0;
  font-size: 1.2em;
  color: #435505;
  font-style: italic;
}

.encabezado h4 {
  margin: 0;
  font-size: 1.2em;
  color: #435505;
  font-style: italic;
}
</style>
