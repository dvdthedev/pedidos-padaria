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

    public Pedido(Long id, String produto, Double quantidade, Double valorTotal,
                  LocalDateTime dataHora, String nomeCliente, String contato, Double valorSinal) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.dataHora = dataHora;
        this.nomeCliente = nomeCliente;
        this.contato = contato;
        this.valorSinal = valorSinal;
    }

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Double getValorSinal() {
        return valorSinal;
    }

    public void setValorSinal(Double valorSinal) {
        this.valorSinal = valorSinal;
    }
}
