package com.padaria.pedidos.services;

import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.repositories.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> findAll(){
        return pedidoRepository.findAll();
    }

    public Pedido findById(Long id){
        Optional<Pedido> obj = pedidoRepository.findById(id);
        return obj.get();
    }

    @Transactional
    public Pedido postPedido(Pedido pedido){
        var obj = pedidoRepository.save(pedido);
        return obj;
    }

    public Optional<Pedido> atualizarPedido(Long id, Pedido pedidoAtualizado){
        return pedidoRepository.findById(id)
                .map(pedidoExistente -> {
                    Optional.ofNullable(pedidoAtualizado.getNomeCliente())
                            .filter(StringUtils::hasText)
                            .ifPresent(pedidoExistente::setNomeCliente);

                    Optional.ofNullable(pedidoAtualizado.getContato())
                            .filter(StringUtils::hasText)
                            .ifPresent(pedidoExistente::setContato);

                    Optional.ofNullable(pedidoAtualizado.getDataHora())
                            .ifPresent(pedidoExistente::setDataHora);

                    Optional.ofNullable(pedidoAtualizado.getProduto())
                            .ifPresent(pedidoExistente::setProduto);

                    Optional.ofNullable(pedidoAtualizado.getQuantidade())
                            .ifPresent(pedidoExistente::setQuantidade);

                    Optional.ofNullable(pedidoAtualizado.getValorSinal())
                            .ifPresent(pedidoExistente::setValorSinal);

                    Optional.ofNullable(pedidoAtualizado.getValorTotal())
                            .ifPresent(pedidoExistente::setValorTotal);

                    Optional.ofNullable(pedidoAtualizado.getDescricao())
                            .filter(StringUtils::hasText)
                            .ifPresent(pedidoExistente::setDescricao);
                    return pedidoRepository.save(pedidoExistente);
                        }
                );

    }

}
