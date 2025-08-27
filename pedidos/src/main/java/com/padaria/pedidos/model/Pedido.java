package com.padaria.pedidos.model;

<<<<<<< HEAD
public class Pedido {
=======
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class Pedido {
    private String produto;
    private Double quantidade;
    private Double valorTotal;
    private LocalDateTime dataHora;
    private String nomeCliente;
    private String contato;
    private Double valorSinal;



>>>>>>> 5a0e4cafab892a7158f015a6d3f31a3cae905b2e
}
