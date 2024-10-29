package sptech.school;

public class AquisicaoPassagem {
    private Integer pesquisaID;
    private String aquisicaoPassagem; // Enum("Pelo passageiro", "Por terceiros")
    private String meioAquisicaoPassagem; // Enum("Diretamente com a cia. aérea", etc.)
    private String meioTransporteAeroporto; // Enum("Aplicativos", "Carona", etc.)

    public AquisicaoPassagem() {
    }

    public AquisicaoPassagem(Integer pesquisaID, String aquisicaoPassagem, String meioAquisicaoPassagem, String meioTransporteAeroporto) {
        this.pesquisaID = pesquisaID;
        this.aquisicaoPassagem = aquisicaoPassagem;
        this.meioAquisicaoPassagem = meioAquisicaoPassagem;
        this.meioTransporteAeroporto = meioTransporteAeroporto;
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public String getAquisicaoPassagem() {
        return aquisicaoPassagem;
    }

    public void setAquisicaoPassagem(String aquisicaoPassagem) {
        this.aquisicaoPassagem = aquisicaoPassagem;
    }

    public String getMeioAquisicaoPassagem() {
        return meioAquisicaoPassagem;
    }

    public void setMeioAquisicaoPassagem(String meioAquisicaoPassagem) {
        this.meioAquisicaoPassagem = meioAquisicaoPassagem;
    }

    public String getMeioTransporteAeroporto() {
        return meioTransporteAeroporto;
    }

    public void setMeioTransporteAeroporto(String meioTransporteAeroporto) {
        this.meioTransporteAeroporto = meioTransporteAeroporto;
    }
}
