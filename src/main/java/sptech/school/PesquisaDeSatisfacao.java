package sptech.school;

public class PesquisaDeSatisfacao {
    private Integer pesquisaID;
    private Integer passageiroID;
    private Integer aeroportoId;
    private String mes;
    private String DataPesquisa;
    private Integer classificacao;

    public PesquisaDeSatisfacao(){

    }

    public PesquisaDeSatisfacao(Integer pesquisaID, Integer passageiroID, Integer aeroportoId, String mes, String DataPesquisa, Integer classificacao) {
        this.pesquisaID = pesquisaID;
        this.passageiroID = passageiroID;
        this.aeroportoId = aeroportoId;
        this.mes = mes;
        this.DataPesquisa = DataPesquisa;
        this.classificacao = classificacao;
    }

    public PesquisaDeSatisfacao(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public Integer getpassageiroID() {
        return passageiroID;
    }

    public void setpassageiroID() {
        this.passageiroID = passageiroID;
    }

    public Integer getaeroportoId() {
        return aeroportoId;
    }

    public void setaeroportoId(Aeroporto aeroporto) {
        this.aeroportoId = aeroportoId;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getData() {
        return DataPesquisa;
    }

    public void setData(String data) {
        this.DataPesquisa = DataPesquisa;
    }

    public Integer getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Integer classificacao) {
        this.classificacao = classificacao;
    }

}

