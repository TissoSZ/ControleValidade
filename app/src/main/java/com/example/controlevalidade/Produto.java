package com.example.controlevalidade;

public class Produto {

    private String nome;
    private String quantidade;
    private String unidade;
    private String categoria;
    private String validade;
    private String localArmazenamento;
    private boolean aberto;

    public Produto(String nome, String quantidade, String unidade, String categoria, String validade,
                   String localArmazenamento, boolean aberto) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.categoria = categoria;
        this.validade = validade;
        this.localArmazenamento = localArmazenamento;
        this.aberto = aberto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getLocalArmazenamento() {
        return localArmazenamento;
    }

    public void setLocalArmazenamento(String localArmazenamento) {
        this.localArmazenamento = localArmazenamento;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }
}
