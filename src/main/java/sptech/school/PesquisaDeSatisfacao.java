package sptech.school;

public class PesquisaDeSatisfacao {
    private Integer pesquisaID;
    private Passageiro passageiro;
    private Aeroporto aeroporto;
    private String mes;
    private String DataPesquisa;

    public PesquisaDeSatisfacao(){

    }

    public PesquisaDeSatisfacao(Integer pesquisaID, Passageiro passageiro, Aeroporto aeroporto, String mes, String DataPesquisa) {
        this.pesquisaID = pesquisaID;
        this.passageiro = passageiro;
        this.aeroporto = aeroporto;
        this.mes = mes;
        this.DataPesquisa = DataPesquisa;
    }

    public PesquisaDeSatisfacao(Integer pesquisaID) {
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public Passageiro getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(Passageiro passageiro) {
        this.passageiro = passageiro;
    }

    public Aeroporto getAeroporto() {
        return aeroporto;
    }

    public void setAeroporto(Aeroporto aeroporto) {
        this.aeroporto = aeroporto;
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

}

