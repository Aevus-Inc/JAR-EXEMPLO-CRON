package sptech.school;

public class Estabelecimentos extends PesquisaDeSatisfacao{
    private String estabelecimentosAlimentacao;
    private Integer quantidadeEstabelecimentosAlimentacao;
    private Integer qualidadeVariedadeOpcoesAlimentacao;
    private Integer relacaoPrecoQualidadeAlimentacao;
    private String estabelecimentosComerciais;
    private Integer quantidadeEstabelecimentosComerciais;
    private Integer qualidadeVariedadeOpcoesComerciais;


    public Estabelecimentos(){

    }

    public Estabelecimentos(Integer pesquisaID, String estabelecimentosAlimentacao, Integer quantidadeEstabelecimentosAlimentacao, Integer qualidadeVariedadeOpcoesAlimentacao, Integer relacaoPrecoQualidadeAlimentacao, String estabelecimentosComerciais, Integer quantidadeEstabelecimentosComerciais, Integer qualidadeVariedadeOpcoesComerciais) {
        super(pesquisaID);
        this.estabelecimentosAlimentacao = estabelecimentosAlimentacao;
        this.quantidadeEstabelecimentosAlimentacao = quantidadeEstabelecimentosAlimentacao;
        this.qualidadeVariedadeOpcoesAlimentacao = qualidadeVariedadeOpcoesAlimentacao;
        this.relacaoPrecoQualidadeAlimentacao = relacaoPrecoQualidadeAlimentacao;
        this.estabelecimentosComerciais = estabelecimentosComerciais;
        this.quantidadeEstabelecimentosComerciais = quantidadeEstabelecimentosComerciais;
        this.qualidadeVariedadeOpcoesComerciais = qualidadeVariedadeOpcoesComerciais;
    }

    public String getEstabelecimentosAlimentacao() {
        return estabelecimentosAlimentacao;
    }

    public void setEstabelecimentosAlimentacao(String estabelecimentosAlimentacao) {
        this.estabelecimentosAlimentacao = estabelecimentosAlimentacao;
    }

    public Integer getQuantidadeEstabelecimentosAlimentacao() {
        return quantidadeEstabelecimentosAlimentacao;
    }

    public void setQuantidadeEstabelecimentosAlimentacao(Integer quantidadeEstabelecimentosAlimentacao) {
        this.quantidadeEstabelecimentosAlimentacao = quantidadeEstabelecimentosAlimentacao;
    }

    public Integer getQualidadeVariedadeOpcoesAlimentacao() {
        return qualidadeVariedadeOpcoesAlimentacao;
    }

    public void setQualidadeVariedadeOpcoesAlimentacao(Integer qualidadeVariedadeOpcoesAlimentacao) {
        this.qualidadeVariedadeOpcoesAlimentacao = qualidadeVariedadeOpcoesAlimentacao;
    }

    public Integer getRelacaoPrecoQualidadeAlimentacao() {
        return relacaoPrecoQualidadeAlimentacao;
    }

    public void setRelacaoPrecoQualidadeAlimentacao(Integer relacaoPrecoQualidadeAlimentacao) {
        this.relacaoPrecoQualidadeAlimentacao = relacaoPrecoQualidadeAlimentacao;
    }

    public String getEstabelecimentosComerciais() {
        return estabelecimentosComerciais;
    }

    public void setEstabelecimentosComerciais(String estabelecimentosComerciais) {
        this.estabelecimentosComerciais = estabelecimentosComerciais;
    }

    public Integer getQuantidadeEstabelecimentosComerciais() {
        return quantidadeEstabelecimentosComerciais;
    }

    public void setQuantidadeEstabelecimentosComerciais(Integer quantidadeEstabelecimentosComerciais) {
        this.quantidadeEstabelecimentosComerciais = quantidadeEstabelecimentosComerciais;
    }

    public Integer getQualidadeVariedadeOpcoesComerciais() {
        return qualidadeVariedadeOpcoesComerciais;
    }

    public void setQualidadeVariedadeOpcoesComerciais(Integer qualidadeVariedadeOpcoesComerciais) {
        this.qualidadeVariedadeOpcoesComerciais = qualidadeVariedadeOpcoesComerciais;
    }
}
