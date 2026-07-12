package com.ve.edu.ucab.AtlasRelojeria;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CicloInventarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; 

    private final String productoPruebaId = "RELOJ-INTEGRACION-99";

    @Test
    @Order(1)
    public void test01_DebeSincronizarAbastecimientoHU1ConVentaHU2() throws Exception {
        Map<String, Object> ordenCompra = new HashMap<>();
        ordenCompra.put("productoId", productoPruebaId);
        ordenCompra.put("proveedorId", "PROV-001");
        ordenCompra.put("cantidad", 50);          
        ordenCompra.put("precioCosto", 120.00);    

        long startTimeHU1 = System.currentTimeMillis();

        mockMvc.perform(post("/api/ordenes-compra")
                .header("Authorization", "Bearer token_valido_admin") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ordenCompra)))
                .andExpect(status().isCreated());

        long durationHU1 = System.currentTimeMillis() - startTimeHU1;
        assertTrue(durationHU1 < 5000, "La HU 1 excedió el límite de rendimiento de 5 segundos");

        Map<String, Object> venta = new HashMap<>();
        venta.put("productoId", productoPruebaId);
        venta.put("cantidad", 15);                 
        venta.put("precioVenta", 250.00);
        venta.put("cliente", "Valeria Leon");
        venta.put("metodoPago", "Visa");           

        long startTimeHU2 = System.currentTimeMillis();

        mockMvc.perform(post("/api/ventas")
                .header("Authorization", "Bearer token_valido_usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated());

        long durationHU2 = System.currentTimeMillis() - startTimeHU2;
        assertTrue(durationHU2 < 5000, "La HU 2 excedió el límite de rendimiento de 5 segundos");
    }

    @Test
    @Order(2)
    public void test02_DebeRechazarVentaEnHU2SiSuperaElStockDisponible() throws Exception {
        Map<String, Object> ventaInvalida = new HashMap<>();
        ventaInvalida.put("productoId", productoPruebaId);
        ventaInvalida.put("cantidad", 100);        
        ventaInvalida.put("precioVenta", 250.00);
        ventaInvalida.put("cliente", "Daniel Verschuur");
        ventaInvalida.put("metodoPago", "Mastercard");

        mockMvc.perform(post("/api/ventas")
                .header("Authorization", "Bearer token_valido_usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ventaInvalida)))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.error", containsString("Stock insuficiente"))); 
    }

    @Test
    @Order(3)
    public void test03_DebeGarantizarConsistenciaDeMonedaUnicaEnAmbasEstructuras() throws Exception {
        Map<String, Object> ordenVerificacion = new HashMap<>();
        ordenVerificacion.put("productoId", productoPruebaId);
        ordenVerificacion.put("cantidad", 1);
        ordenVerificacion.put("precioCosto", 10.00);

        mockMvc.perform(post("/api/ordenes-compra")
                .header("Authorization", "Bearer token_valido_admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ordenVerificacion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.moneda").value("USD")); 
    }
}
