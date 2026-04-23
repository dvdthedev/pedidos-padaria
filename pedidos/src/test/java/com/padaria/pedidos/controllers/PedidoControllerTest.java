package com.padaria.pedidos.controllers;

import com.padaria.pedidos.controller.PedidoController;
import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @InjectMocks
    private PedidoController pedidoController;

    @Mock
    private PedidoService pedidoService;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido(1L, "Bolo de Cenoura", 1.0, 45.0, LocalDateTime.now(), "Com cobertura", "Maria", "31988888888", 20.0);
    }

    @Test
    void pedidosFuturos_DeveRetornarListaDePedidosEStatusOk() {
        when(pedidoService.pedidosFuturos()).thenReturn(List.of(pedido));

        ResponseEntity<List<Pedido>> response = pedidoController.pedidosFuturos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(pedidoService, times(1)).pedidosFuturos();
    }

    @Test
    void pedidosPassado_DeveRetornarListaDePedidosEStatusOk() {
        when(pedidoService.pedidosPassado()).thenReturn(List.of(pedido));

        ResponseEntity<List<Pedido>> response = pedidoController.pedidosPassado();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(pedidoService, times(1)).pedidosPassado();
    }

    @Test
    void findById_DeveRetornarPedidoEStatusOk() {
        when(pedidoService.findById(1L)).thenReturn(pedido);

        ResponseEntity<Pedido> response = pedidoController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(pedido.getId(), response.getBody().getId());
        verify(pedidoService, times(1)).findById(1L);
    }

    @Test
    void criarPedido_DeveCriarPedidoERetornarStatusOk() throws IOException {
        when(pedidoService.postPedido(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<Pedido> response = pedidoController.criarPedido(pedido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(pedido.getId(), response.getBody().getId());
        verify(pedidoService, times(1)).postPedido(pedido);
    }

    @Test
    void atualizarPedido_QuandoPedidoExiste_DeveRetornarStatusOkEPedido() {
        when(pedidoService.atualizarPedido(eq(1L), any(Pedido.class))).thenReturn(Optional.of(pedido));

        ResponseEntity<Pedido> response = pedidoController.atualizarPedido(1L, pedido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(pedido.getId(), response.getBody().getId());
        verify(pedidoService, times(1)).atualizarPedido(1L, pedido);
    }

    @Test
    void atualizarPedido_QuandoPedidoNaoExiste_DeveRetornarStatusNotFound() {
        when(pedidoService.atualizarPedido(eq(2L), any(Pedido.class))).thenReturn(Optional.empty());

        ResponseEntity<Pedido> response = pedidoController.atualizarPedido(2L, pedido);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoService, times(1)).atualizarPedido(2L, pedido);
    }

    @Test
    void deletarPedido_DeveDeletarERetornarStatusNoContent() {
        doNothing().when(pedidoService).deleteById(1L);

        ResponseEntity<Void> response = pedidoController.deletarPedido(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoService, times(1)).deleteById(1L);
    }
}
