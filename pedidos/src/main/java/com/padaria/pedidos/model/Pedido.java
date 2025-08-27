package com.padaria.pedidos.model;

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



}
