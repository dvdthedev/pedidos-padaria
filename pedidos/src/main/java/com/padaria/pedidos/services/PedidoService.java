package com.padaria.pedidos.services;

import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
