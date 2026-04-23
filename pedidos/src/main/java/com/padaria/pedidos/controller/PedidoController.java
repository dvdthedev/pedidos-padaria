package com.padaria.pedidos.controller;

import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.service.PedidoService;
import com.padaria.pedidos.service.PrintServicePos;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> pedidosFuturos(){
        List<Pedido> list = service.pedidosFuturos();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/passado")
    public ResponseEntity<List<Pedido>> pedidosPassado(){
        List<Pedido> list = service.pedidosPassado();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id){
        Pedido obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) throws IOException {
        var obj = service.postPedido(pedido);
        return ResponseEntity.ok().body(obj);
    }

    @PutMapping(value = "/{id}")
    @Transactional
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Long id, @RequestBody Pedido pedidoAtualizado){
        return service.atualizarPedido(id, pedidoAtualizado).map(
                pedido -> ResponseEntity.ok(pedido)
        ).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
