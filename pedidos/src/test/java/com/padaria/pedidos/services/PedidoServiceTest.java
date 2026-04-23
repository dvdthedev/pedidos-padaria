package com.padaria.pedidos.services;

import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.repositories.PedidoRepository;
import com.padaria.pedidos.service.PedidoService;
import com.padaria.pedidos.service.PrintServicePos;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PrintServicePos printService;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido(1L, "Cachorro Quente", 2.0, 30.0, LocalDateTime.now(), "Sem mostarda", "João", "31999999999", 0.0);
    }

    @Test
    void findById_QuandoPedidoExiste_DeveRetornarPedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João", resultado.getNomeCliente());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void findById_QuandoPedidoNaoExiste_DeveLancarException() {
        when(pedidoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.findById(2L));
        verify(pedidoRepository, times(1)).findById(2L);
    }

    @Test
    void postPedido_DeveSalvarEImprimirPedido() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido resultado = pedidoService.postPedido(pedido);

        assertNotNull(resultado);
        assertEquals(pedido.getId(), resultado.getId());
        verify(pedidoRepository, times(1)).save(pedido);
        
        try {
            verify(printService, times(1)).imprimirRecibo(pedido);
            verify(printService, times(1)).imprimirViaProducao(pedido);
        } catch (Exception e) {
            fail("Não deveria lançar exceção na verificação do mock");
        }
    }

    @Test
    void pedidosFuturos_DeveRetornarListaDePedidos() {
        when(pedidoRepository.pedidosFuturo()).thenReturn(List.of(pedido));

        List<Pedido> resultados = pedidoService.pedidosFuturos();

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        verify(pedidoRepository, times(1)).pedidosFuturo();
    }

    @Test
    void deleteById_QuandoPedidoExiste_DeveDeletar() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoRepository).delete(pedido);

        pedidoService.deleteById(1L);

        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    @Test
    void deleteById_QuandoPedidoNaoExiste_DeveLancarException() {
        when(pedidoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> pedidoService.deleteById(2L));
        verify(pedidoRepository, times(1)).findById(2L);
        verify(pedidoRepository, never()).delete(any());
    }

    @Test
    void atualizarPedido_QuandoPedidoExiste_DeveAtualizarERetornar() {
        Pedido pedidoAtualizado = new Pedido();
        pedidoAtualizado.setNomeCliente("Maria");
        pedidoAtualizado.setProduto("Hamburguer");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Optional<Pedido> resultado = pedidoService.atualizarPedido(1L, pedidoAtualizado);

        assertTrue(resultado.isPresent());
        assertEquals("Maria", pedido.getNomeCliente());
        assertEquals("Hamburguer", pedido.getProduto());
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido);
    }
}
