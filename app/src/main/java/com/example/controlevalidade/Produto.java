package com.example.controlevalidade;

public class Produto {

    private String nome;
    private String categoria;
    private String validade;
    private String localArmazenamento;

    public Produto(String nome, String categoria, String validade, String localArmazenamento) {
        this.nome = nome;
        this.categoria = categoria;
        this.validade = validade;
        this.localArmazenamento = localArmazenamento;
    }
    public String getNome() {
        return nome;
    }
    public String getCategoria() {
        return categoria;
    }
    public String getValidade() {
        return validade;
    }
    public String getLocalArmazenamento() {
        return localArmazenamento;
    }
}