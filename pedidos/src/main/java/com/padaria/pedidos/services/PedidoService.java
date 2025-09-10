package com.padaria.pedidos.services;

import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PrintServicePos printService;

    public List<Pedido> pedidosFuturos(){
        return pedidoRepository.pedidosFuturo();
    }

    public List<Pedido> pedidosPassado(){
        return pedidoRepository.pedidosPassado();
    }

    public Pedido findById(Long id){
        Optional<Pedido> obj = pedidoRepository.findById(id);
        return obj.orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));
    }

    @Transactional
    public Pedido postPedido(Pedido pedido) throws IOException {
        var obj = pedidoRepository.save(pedido);
        imprimirAsync(pedido);
        return obj;
    }

    @Transactional
    public void deleteById(Long id){
        Optional<Pedido> pedidoOptional = this.pedidoRepository.findById(id);
        if (pedidoOptional.isPresent()){
            pedidoRepository.delete(pedidoOptional.get());
        }else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id não encontrado: " + id);
    }

    public Optional<Pedido> atualizarPedido(Long id, Pedido pedidoAtualizado){
        Optional<Pedido> pedidoOptional = pedidoRepository.findById(id)
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

        pedidoOptional.ifPresent(this::imprimirAsync);
    return pedidoOptional;
    }

    @Async
    public void imprimirAsync(Pedido pedido){
        try{
            printService.imprimirRecibo(pedido);
            printService.imprimirViaProducao(pedido);
        } catch (IOException e){
            System.err.println("Erro ao imprimir pedido " + pedido.getId() + ": " + e.getMessage());
        }
    }
}
