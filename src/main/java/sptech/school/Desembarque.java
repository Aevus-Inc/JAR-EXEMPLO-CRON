package sptech.school;

public class Desembarque {
    private Integer pesquisaID;
    private String formaDesembarque; // Enum("Ponte", "Ambulift", etc.)
    private Integer avaliacaoMetodoDesembarque;
    private String utilizouEstacionamento;
    private Integer facilidadeDesembarqueMeioFio;
    private Integer opcoesTransporteAeroporto;

    public Desembarque(){

    }

    public Desembarque(Integer pesquisaID, String formaDesembarque, Integer avaliacaoMetodoDesembarque, String utilizouEstacionamento, Integer facilidadeDesembarqueMeioFio, Integer opcoesTransporteAeroporto) {
        this.pesquisaID = pesquisaID;
        this.formaDesembarque = formaDesembarque;
        this.avaliacaoMetodoDesembarque = avaliacaoMetodoDesembarque;
        this.utilizouEstacionamento = utilizouEstacionamento;
        this.facilidadeDesembarqueMeioFio = facilidadeDesembarqueMeioFio;
        this.opcoesTransporteAeroporto = opcoesTransporteAeroporto;
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public String getUtilizouEstacionamento() {
        return utilizouEstacionamento;
    }

    public String getFormaDesembarque() {
        return formaDesembarque;
    }

    public void setFormaDesembarque(String formaDesembarque) {
        this.formaDesembarque = formaDesembarque;
    }

    public Integer getAvaliacaoMetodoDesembarque() {
        return avaliacaoMetodoDesembarque;
    }

    public void setAvaliacaoMetodoDesembarque(Integer avaliacaoMetodoDesembarque) {
        this.avaliacaoMetodoDesembarque = avaliacaoMetodoDesembarque;
    }

    public String isUtilizouEstacionamento() {
        return utilizouEstacionamento;
    }

    public void setUtilizouEstacionamento(String utilizouEstacionamento) {
        this.utilizouEstacionamento = utilizouEstacionamento;
    }

    public Integer getFacilidadeDesembarqueMeioFio() {
        return facilidadeDesembarqueMeioFio;
    }

    public void setFacilidadeDesembarqueMeioFio(Integer facilidadeDesembarqueMeioFio) {
        this.facilidadeDesembarqueMeioFio = facilidadeDesembarqueMeioFio;
    }

    public Integer getOpcoesTransporteAeroporto() {
        return opcoesTransporteAeroporto;
    }

    public void setOpcoesTransporteAeroporto(Integer opcoesTransporteAeroporto) {
        this.opcoesTransporteAeroporto = opcoesTransporteAeroporto;
    }
}
