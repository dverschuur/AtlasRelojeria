<template>
  <div class="app-shell">
    <header class="encabezado">
      <div class="header-content">
        <div class="brand">
          <div class="brand-icon"></div>
          <div class="brand-text">
            <h1>Atlas</h1>
            <span class="brand-sub">Relojería</span>
          </div>
        </div>
      </div>
    </header>

    <nav class="navbar">
      <div class="nav-inner">
        <button v-if="!esAdmin" @click="vista = 'catalogo'" :class="{ active: vista === 'catalogo' }">Catálogo</button>
        <button v-if="!usuarioAutenticado" @click="vista = 'login'" :class="{ active: vista === 'login' }">Iniciar Sesión</button>
        <button v-if="usuarioAutenticado" @click="vista = 'perfil'" :class="{ active: vista === 'perfil' }">Mi Perfil</button>
        <button v-if="esAdmin" @click="vista = 'historial'" :class="{ active: vista === 'historial' }">Historial de Ventas</button>
        <button v-if="esAdmin" @click="vista = 'admin'" :class="{ active: vista === 'admin' }">Inventario</button>
        <button v-if="esAdmin" @click="vista = 'ordenDeCompra'" :class="{ active: vista === 'ordenDeCompra' }">Orden de Compra</button>
        <button v-if="esAdmin" @click="vista = 'historialDeOrdenDeCompra'" :class="{ active: vista === 'historialDeOrdenDeCompra' }">Historial Compras</button>
        <button v-if="esAdmin" @click="vista = 'reporteDeVentas'" :class="{ active: vista === 'reporteDeVentas' }">Reporte de Ventas</button>
      </div>
    </nav>

    <main class="main-content">
      <div v-if="vista === 'ordenDeCompra'" class="view-wrapper">
        <OrdenDeCompraView @cancelar="vista = 'admin'" />
      </div>
      <div v-if="vista === 'catalogo'" class="view-wrapper">
        <CatalogoView
          :logueado="usuarioAutenticado"
          :perfilActual="usuarioActual"
          :mensajeCompra="mensajeCompra"
          @comprar="irAVenta"
        />
      </div>
      <div v-else-if="vista === 'login'" class="view-wrapper">
        <LoginView @login-exitoso="manejarLogin" @crear-perfil="vista = 'crearPerfil'" />
      </div>
      <div v-else-if="vista === 'crearPerfil'" class="view-wrapper">
        <CrearPerfilView />
      </div>
      <div v-else-if="vista === 'admin'" class="view-wrapper">
        <AdminView />
      </div>
      <div v-else-if="vista === 'compra'" class="view-wrapper">
        <CompraView :producto="productoSeleccionado" />
      </div>
      <div v-else-if="vista === 'perfil'" class="view-wrapper">
        <DetalleDePerfilView
          :perfil="usuarioActual"
          :esAdmin="esAdmin"
          @perfil-eliminado="logout"
          @cerrar-sesion="logout"
          @comprar="irAVenta"
          @perfil-actualizado="usuarioActual = $event"
        />
      </div>
      <div v-if="vista === 'venta'" class="view-wrapper">
        <VentasView
          :producto="productoSeleccionado"
          :perfil="usuarioActual"
          @compra-exitosa="manejarCompraExitosa"
        />
      </div>
      <div v-else-if="vista === 'historial'" class="view-wrapper">
        <HistorialView />
      </div>
      <div v-else-if="vista === 'historialDeOrdenDeCompra'" class="view-wrapper">
        <HistorialComprasProveedorView />
      </div>
      <div v-else-if="vista === 'reporteDeVentas'" class="view-wrapper">
        <ReporteDeVentasView />
      </div>
    </main>
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
/* ── Google Fonts ── */
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,600;0,700;1,400&family=Inter:wght@300;400;500;600&display=swap');

/* ── Design Tokens ── */
:root {
  --green-900: #0a3d2b;
  --green-800: #0b5c3f;
  --green-700: #0b7d59;
  --green-600: #0f9a6e;
  --green-100: #e8f5ef;
  --green-50:  #f0faf5;
  --olive:     #3b4a1e;
  --olive-light: #8a9a5b;
  --gold:      #c9a84c;
  --gold-light: #e8c96e;

  --surface-0: #ffffff;
  --surface-1: #f8faf9;
  --surface-2: #f0f4f1;
  --border:    #d6e4dd;
  --text-primary:   #1a2e24;
  --text-secondary: #4a6556;
  --text-muted:     #7a9487;

  --radius-sm:  6px;
  --radius-md:  10px;
  --radius-lg:  16px;
  --radius-xl:  24px;

  --shadow-sm:  0 1px 3px rgba(10, 61, 43, 0.08);
  --shadow-md:  0 4px 16px rgba(10, 61, 43, 0.12);
  --shadow-lg:  0 8px 32px rgba(10, 61, 43, 0.16);

  --transition: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ── Reset ── */
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background-color: var(--surface-1);
  color: var(--text-primary);
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
}

/* ── Shell ── */
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ── Header ── */
.encabezado {
  background: linear-gradient(135deg, var(--green-900) 0%, var(--green-800) 60%, var(--olive) 100%);
  padding: 20px 32px;
  position: relative;
  overflow: hidden;
}

.encabezado::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 80% 50%, rgba(201, 168, 76, 0.15) 0%, transparent 60%);
  pointer-events: none;
}

.encabezado::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--gold), transparent);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-icon {
  width: 44px;
  height: 44px;
  background: rgba(201, 168, 76, 0.18);
  border: 1.5px solid rgba(201, 168, 76, 0.45);
  border-radius: 50%;
  animation: float 3s ease-in-out infinite;
  flex-shrink: 0;
  position: relative;
}

.brand-icon::before {
  content: '';
  position: absolute;
  inset: 6px;
  border: 2px solid rgba(201, 168, 76, 0.7);
  border-radius: 50%;
}

.brand-icon::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 2px;
  height: 10px;
  background: rgba(201, 168, 76, 0.9);
  transform: translate(-50%, -100%);
  transform-origin: bottom center;
  border-radius: 1px;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(-4px); }
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-text h1 {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 2.2rem;
  font-weight: 700;
  color: #ffffff;
  line-height: 1;
  letter-spacing: 0.02em;
  text-shadow: 0 2px 12px rgba(0,0,0,0.3);
}

.brand-sub {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 300;
  color: var(--gold-light);
  letter-spacing: 0.25em;
  text-transform: uppercase;
  margin-top: 2px;
}

/* ── Navbar ── */
.navbar {
  background-color: var(--green-700);
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 12px rgba(10, 61, 43, 0.25);
}

.nav-inner {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 4px;
  align-items: center;
  overflow-x: auto;
  scrollbar-width: none;
}
.nav-inner::-webkit-scrollbar { display: none; }

.navbar button {
  color: rgba(255,255,255,0.85);
  background-color: transparent;
  border: none;
  padding: 14px 14px;
  border-radius: 0;
  cursor: pointer;
  transition: color var(--transition), background-color var(--transition);
  font-family: 'Inter', sans-serif;
  font-size: 0.82rem;
  font-weight: 500;
  letter-spacing: 0.02em;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
  border-bottom: 3px solid transparent;
  position: relative;
  top: 0;
}

.nav-icon {
  font-size: 0.9rem;
}

.navbar button:hover {
  color: #ffffff;
  background-color: rgba(255,255,255,0.08);
}

.navbar button.active {
  color: #ffffff;
  border-bottom-color: var(--gold);
  background-color: rgba(255,255,255,0.06);
}

/* ── Main ── */
.main-content {
  flex: 1;
  padding: 0;
}

.view-wrapper {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
