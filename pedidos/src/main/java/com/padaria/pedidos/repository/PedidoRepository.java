package com.padaria.pedidos.repositories;

import com.padaria.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query(value = "SELECT * FROM pedido WHERE data_hora > NOW() ORDER BY data_hora ASC", nativeQuery = true)
    List<Pedido> pedidosFuturo();

    @Query(value = "SELECT * FROM pedido WHERE data_hora < NOW() ORDER BY data_hora DESC", nativeQuery = true)
    List<Pedido> pedidosPassado();
}
