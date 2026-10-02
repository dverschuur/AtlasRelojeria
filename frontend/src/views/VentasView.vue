<template>
  <div class="venta-page">
    <div class="venta-inner">

      <!-- Panel producto -->
      <div class="panel-producto">
        <div class="prod-img-wrap">
          <img :src="'http://localhost:8081/img/' + producto.imagen" :alt="producto.nombre" />
        </div>
        <div class="prod-info">
          <h2 class="prod-nombre">{{ producto.nombre }}</h2>
          <p class="prod-desc">{{ producto.descripcion }}</p>
          <div class="prod-precio">${{ producto.precio }}</div>
        </div>
      </div>

      <!-- Panel formulario -->
      <div class="panel-formulario">
        <div class="comprador-info">
          <h3 class="section-title">Información del comprador</h3>
          <div class="info-row">
            <span class="info-label">Nombre</span>
            <span class="info-value">{{ perfil.nombre }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Cédula</span>
            <span class="info-value">{{ perfil.id }}</span>
          </div>
        </div>

        <form @submit.prevent="procesarCompra" class="checkout-form">
          <h3 class="section-title">Datos de envío y pago</h3>

          <div class="field-group">
            <label for="direccion">Dirección de envío</label>
            <input
              type="text"
              id="direccion"
              v-model="direccion"
              placeholder="Av. Principal, Edificio..."
              required
            />
          </div>

          <div class="field-group">
            <label for="tarjeta">Número de tarjeta</label>
            <input
              type="text"
              id="tarjeta"
              v-model="tarjeta"
              inputmode="numeric"
              maxlength="16"
              placeholder="0000 0000 0000 0000"
              required
            />
          </div>

          <div class="field-row">
            <div class="field-group">
              <label for="vencimiento">Vencimiento (MM/YY)</label>
              <input
                type="text"
                id="vencimiento"
                v-model="vencimiento"
                placeholder="MM/YY"
                required
              />
            </div>
            <div class="field-group">
              <label for="codigo">CVV</label>
              <input
                type="text"
                id="codigo"
                v-model="codigo"
                maxlength="3"
                placeholder="•••"
                required
              />
            </div>
          </div>

          <transition name="fade-slide">
            <div v-if="mensaje" class="alert" :class="mensajeEsError ? 'alert-error' : 'alert-success'">
              {{ mensaje }}
            </div>
          </transition>

          <button type="submit" class="btn-confirmar">
            Confirmar compra — ${{ producto.precio }}
          </button>
        </form>
      </div>

    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VentasView',
  props: {
    producto: Object,
    perfil: Object
  },
  data() {
    return {
      direccion: '',
      tarjeta: '',
      codigo: '',
      vencimiento: '',
      mensaje: '',
      mensajeEsError: false
    };
  },
  methods: {
    async procesarCompra() {
      this.mensaje = '';

      if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(this.direccion.trim())) {
        this.mensaje = 'La dirección solo debe contener letras y espacios.';
        this.mensajeEsError = true;
        return;
      }

      const tarjetaLimpia = this.tarjeta.replace(/\s/g, '');
      if (!/^\d{16}$/.test(tarjetaLimpia)) {
        this.mensaje = 'El número de tarjeta debe tener exactamente 16 dígitos.';
        this.mensajeEsError = true;
        return;
      }

      if (!/^\d{3}$/.test(this.codigo)) {
        this.mensaje = 'El código de seguridad debe tener exactamente 3 dígitos.';
        this.mensajeEsError = true;
        return;
      }

      const match = this.vencimiento.match(/^(\d{2})\/(\d{2})$/);
      if (!match) {
        this.mensaje = 'La fecha de vencimiento debe tener el formato MM/YY.';
        this.mensajeEsError = true;
        return;
      }

      const mes = parseInt(match[1]);
      const año = parseInt(match[2]);

      if (mes < 1 || mes > 12) {
        this.mensaje = 'El mes de la fecha de vencimiento debe estar entre 01 y 12.';
        this.mensajeEsError = true;
        return;
      }

      const fechaActual = new Date();
      const mesActual = fechaActual.getMonth() + 1;
      const añoActual = parseInt(fechaActual.getFullYear().toString().slice(-2));

      if (año < añoActual || (año === añoActual && mes < mesActual)) {
        this.mensaje = 'La tarjeta está vencida.';
        this.mensajeEsError = true;
        return;
      }

      const nuevaVenta = {
        nombreCliente: this.perfil.nombre,
        idUsuario: this.perfil.id,
        idProducto: this.producto.id,
        direccion: this.direccion,
        fecha: new Date().toISOString(),
        monto: this.producto.precio
      };

      try {
        await axios.post('http://localhost:8081/api/ventas', nuevaVenta);
        this.$emit('compra-exitosa', '¡Compra realizada con éxito!');
        this.direccion = this.tarjeta = this.codigo = this.vencimiento = '';
      } catch (error) {
        console.error('Error al registrar la venta:', error);
        this.mensaje = 'Hubo un error al procesar la compra.';
        this.mensajeEsError = true;
      }
    }
  }
};
</script>

<style scoped>
.venta-page {
  padding: 40px 24px;
  min-height: 80vh;
  background: linear-gradient(160deg, var(--green-50) 0%, var(--surface-1) 100%);
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.venta-inner {
  display: flex;
  gap: 32px;
  max-width: 960px;
  width: 100%;
  align-items: flex-start;
  flex-wrap: wrap;
}

/* ── Panel producto ── */
.panel-producto {
  flex: 1;
  min-width: 260px;
  max-width: 380px;
  background: var(--surface-0);
  border-radius: var(--radius-xl);
  overflow: hidden;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-md);
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.prod-img-wrap {
  width: 100%;
  height: 240px;
  overflow: hidden;
  background: var(--surface-2);
}

.prod-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.prod-info {
  padding: 20px 22px 24px;
}

.prod-nombre {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 8px;
}

.prod-desc {
  font-size: 0.85rem;
  color: var(--text-muted);
  line-height: 1.5;
  margin-bottom: 14px;
}

.prod-precio {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--green-700);
  font-variant-numeric: tabular-nums;
}

/* ── Panel formulario ── */
.panel-formulario {
  flex: 1;
  min-width: 300px;
  background: var(--surface-0);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-md);
  padding: 28px 28px 32px;
  animation: slideUp 0.4s 0.1s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.comprador-info {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border);
}

.section-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--green-900);
  margin-bottom: 14px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--surface-2);
}

.info-label {
  font-size: 0.8rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  font-weight: 500;
}

.info-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-primary);
}

.checkout-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.field-group label {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.field-group input {
  width: 100%;
  padding: 11px 13px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.92rem;
  color: var(--text-primary);
  background: var(--surface-1);
  transition: border-color var(--transition), box-shadow var(--transition), background var(--transition);
  outline: none;
}

.field-group input:focus {
  border-color: var(--green-700);
  background: var(--surface-0);
  box-shadow: 0 0 0 3px rgba(11, 125, 89, 0.12);
}

.alert {
  padding: 11px 14px;
  border-radius: var(--radius-md);
  font-size: 0.85rem;
}

.alert-error {
  background: #fff0f0;
  color: #c0392b;
  border: 1px solid #fcc;
}

.alert-success {
  background: var(--green-100);
  color: var(--green-800);
  border: 1px solid #b2dfcf;
}

.btn-confirmar {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, var(--green-700), var(--green-800));
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition);
  letter-spacing: 0.01em;
  margin-top: 4px;
}

.btn-confirmar:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(11, 125, 89, 0.35);
}

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.25s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(-6px); }
</style>
