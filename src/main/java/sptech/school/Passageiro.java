package sptech.school;

public class Passageiro {
    private String nacionalidade; // Enum("Brasileiro", "Estrangeiro")
    private String genero; // Enum("Feminino", "Masculino")
    private String faixaEtaria; // Enum("18 a 25 anos", etc.)
    private String escolaridade; // Enum("Analfabeto", "Ensino fundamental", etc.)
    private String rendaFamiliar; // Enum("Até 1 salário mínimo", etc.)
    private String viajandoSozinho; // Atualizado para String
    private String numeroAcompanhantes; // Enum("1 pessoa", "2 pessoas", etc.)
    private String motivoViagem; // Enum("Lazer", "Trabalho", etc.)
    private String quantidadeViagensUltimos12Meses; // Enum("1 (Primeira viagem)", etc.)
    private String jaEmbarcouDesembarcouAntes; // Atualizado para String
    private String antecedencia; // Enum("30min a 1h", etc.)
    private String tempoEspera; // Enum("30min a 1h", etc.)
    private String comentariosAdicionais;

    public Passageiro(String nacionalidade, String genero, String faixaEtaria, String escolaridade, String rendaFamiliar, String viajandoSozinho, String numeroAcompanhantes, String motivoViagem, String quantidadeViagensUltimos12Meses, String jaEmbarcouDesembarcouAntes, String antecedencia, String tempoEspera, String comentariosAdicionais) {
        this.nacionalidade = nacionalidade;
        this.genero = genero;
        this.faixaEtaria = faixaEtaria;
        this.escolaridade = escolaridade;
        this.rendaFamiliar = rendaFamiliar;
        this.viajandoSozinho = viajandoSozinho; // String agora
        this.numeroAcompanhantes = numeroAcompanhantes;
        this.motivoViagem = motivoViagem;
        this.quantidadeViagensUltimos12Meses = quantidadeViagensUltimos12Meses;
        this.jaEmbarcouDesembarcouAntes = jaEmbarcouDesembarcouAntes; // String agora
        this.antecedencia = antecedencia;
        this.tempoEspera = tempoEspera;
        this.comentariosAdicionais = comentariosAdicionais;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFaixaEtaria() {
        return faixaEtaria;
    }

    public void setFaixaEtaria(String faixaEtaria) {
        this.faixaEtaria = faixaEtaria;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getRendaFamiliar() {
        return rendaFamiliar;
    }

    public void setRendaFamiliar(String rendaFamiliar) {
        this.rendaFamiliar = rendaFamiliar;
    }

    public String getViajandoSozinho() {
        return viajandoSozinho;
    }

    public void setViajandoSozinho(String viajandoSozinho) {
        this.viajandoSozinho = viajandoSozinho; // String agora
    }

    public String getNumeroAcompanhantes() {
        return numeroAcompanhantes;
    }

    public void setNumeroAcompanhantes(String numeroAcompanhantes) {
        this.numeroAcompanhantes = numeroAcompanhantes;
    }

    public String getMotivoViagem() {
        return motivoViagem;
    }

    public void setMotivoViagem(String motivoViagem) {
        this.motivoViagem = motivoViagem;
    }

    public String getQuantidadeViagensUltimos12Meses() {
        return quantidadeViagensUltimos12Meses;
    }

    public void setQuantidadeViagensUltimos12Meses(String quantidadeViagensUltimos12Meses) {
        this.quantidadeViagensUltimos12Meses = quantidadeViagensUltimos12Meses;
    }

    public String getJaEmbarcouDesembarcouAntes() {
        return jaEmbarcouDesembarcouAntes; // String agora
    }

    public void setJaEmbarcouDesembarcouAntes(String jaEmbarcouDesembarcouAntes) {
        this.jaEmbarcouDesembarcouAntes = jaEmbarcouDesembarcouAntes; // String agora
    }

    public String getAntecedencia() {
        return antecedencia;
    }

    public void setAntecedencia(String antecedencia) {
        this.antecedencia = antecedencia;
    }

    public String getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(String tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public String getComentariosAdicionais() {
        return comentariosAdicionais;
    }

    public void setComentariosAdicionais(String comentariosAdicionais) {
        this.comentariosAdicionais = comentariosAdicionais;
    }
}
