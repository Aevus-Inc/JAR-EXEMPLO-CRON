package sptech.school;

public class RestituicaoBagagens extends  PesquisaDeSatisfacao{
    private Integer processoRestituicaoBagagens;
    private Integer facilidadeIdentificacaoEsteira;
    private Integer tempoRestituicao;
    private Integer integridadeBagagem;
    private Integer atendimentoCiaAerea;

    public RestituicaoBagagens(){

    }

    public RestituicaoBagagens(Integer pesquisaID, Integer processoRestituicaoBagagens, Integer facilidadeIdentificacaoEsteira, Integer tempoRestituicao, Integer integridadeBagagem, Integer atendimentoCiaAerea) {
        super(pesquisaID);
        this.processoRestituicaoBagagens = processoRestituicaoBagagens;
        this.facilidadeIdentificacaoEsteira = facilidadeIdentificacaoEsteira;
        this.tempoRestituicao = tempoRestituicao;
        this.integridadeBagagem = integridadeBagagem;
        this.atendimentoCiaAerea = atendimentoCiaAerea;
    }

    public Integer getProcessoRestituicaoBagagens() {
        return processoRestituicaoBagagens;
    }

    public void setProcessoRestituicaoBagagens(Integer processoRestituicaoBagagens) {
        this.processoRestituicaoBagagens = processoRestituicaoBagagens;
    }

    public Integer getFacilidadeIdentificacaoEsteira() {
        return facilidadeIdentificacaoEsteira;
    }

    public void setFacilidadeIdentificacaoEsteira(Integer facilidadeIdentificacaoEsteira) {
        this.facilidadeIdentificacaoEsteira = facilidadeIdentificacaoEsteira;
    }

    public Integer getTempoRestituicao() {
        return tempoRestituicao;
    }

    public void setTempoRestituicao(Integer tempoRestituicao) {
        this.tempoRestituicao = tempoRestituicao;
    }

    public Integer getIntegridadeBagagem() {
        return integridadeBagagem;
    }

    public void setIntegridadeBagagem(Integer integridadeBagagem) {
        this.integridadeBagagem = integridadeBagagem;
    }

    public Integer getAtendimentoCiaAerea() {
        return atendimentoCiaAerea;
    }

    public void setAtendimentoCiaAerea(Integer atendimentoCiaAerea) {
        this.atendimentoCiaAerea = atendimentoCiaAerea;
    }
}
