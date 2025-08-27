package com.padaria.pedidos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String produto;
    private Double quantidade;
    private Double valorTotal;
    private LocalDateTime dataHora;
    private String nomeCliente;
    private String contato;
    private Double valorSinal;


}
