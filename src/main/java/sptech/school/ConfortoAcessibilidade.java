package sptech.school;

public class ConfortoAcessibilidade {
    private Integer pesquisaID;
    private Integer localizacaoDeslocamento;
    private Integer sinalizacao;
    private Integer disponibilidadePaineisInformacoesVoo;
    private Integer acessibilidadeTerminal;
    private Integer confortoSalaEmbarque;
    private Integer confortoTermico;
    private Integer confortoAcustico;
    private Integer disponibilidadeAssentos;
    private Integer disponibilidadeAssentosReservados;
    private Integer disponibilidadeTomadas;
    private Integer internetDisponibilizadaAeroporto;
    private Integer velocidadeConexao;
    private Integer facilidadeAcessoRede;


    public ConfortoAcessibilidade(){

    }

    public ConfortoAcessibilidade(Integer pesquisaID , Integer localizacaoDeslocamento, Integer sinalizacao, Integer disponibilidadePaineisInformacoesVoo, Integer acessibilidadeTerminal, Integer confortoSalaEmbarque, Integer confortoTermico, Integer confortoAcustico, Integer disponibilidadeAssentos, Integer disponibilidadeAssentosReservados, Integer disponibilidadeTomadas, Integer internetDisponibilizadaAeroporto, Integer velocidadeConexao, Integer facilidadeAcessoRede) {
        this.pesquisaID = pesquisaID;
        this.localizacaoDeslocamento = localizacaoDeslocamento;
        this.sinalizacao = sinalizacao;
        this.disponibilidadePaineisInformacoesVoo = disponibilidadePaineisInformacoesVoo;
        this.acessibilidadeTerminal = acessibilidadeTerminal;
        this.confortoSalaEmbarque = confortoSalaEmbarque;
        this.confortoTermico = confortoTermico;
        this.confortoAcustico = confortoAcustico;
        this.disponibilidadeAssentos = disponibilidadeAssentos;
        this.disponibilidadeAssentosReservados = disponibilidadeAssentosReservados;
        this.disponibilidadeTomadas = disponibilidadeTomadas;
        this.internetDisponibilizadaAeroporto = internetDisponibilizadaAeroporto;
        this.velocidadeConexao = velocidadeConexao;
        this.facilidadeAcessoRede = facilidadeAcessoRede;
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public Integer getLocalizacaoDeslocamento() {
        return localizacaoDeslocamento;
    }

    public void setLocalizacaoDeslocamento(Integer localizacaoDeslocamento) {
        this.localizacaoDeslocamento = localizacaoDeslocamento;
    }

    public Integer getSinalizacao() {
        return sinalizacao;
    }

    public void setSinalizacao(Integer sinalizacao) {
        this.sinalizacao = sinalizacao;
    }

    public Integer getDisponibilidadePaineisInformacoesVoo() {
        return disponibilidadePaineisInformacoesVoo;
    }

    public void setDisponibilidadePaineisInformacoesVoo(Integer disponibilidadePaineisInformacoesVoo) {
        this.disponibilidadePaineisInformacoesVoo = disponibilidadePaineisInformacoesVoo;
    }

    public Integer getAcessibilidadeTerminal() {
        return acessibilidadeTerminal;
    }

    public void setAcessibilidadeTerminal(Integer acessibilidadeTerminal) {
        this.acessibilidadeTerminal = acessibilidadeTerminal;
    }

    public Integer getConfortoSalaEmbarque() {
        return confortoSalaEmbarque;
    }

    public void setConfortoSalaEmbarque(Integer confortoSalaEmbarque) {
        this.confortoSalaEmbarque = confortoSalaEmbarque;
    }

    public Integer getConfortoTermico() {
        return confortoTermico;
    }

    public void setConfortoTermico(Integer confortoTermico) {
        this.confortoTermico = confortoTermico;
    }

    public Integer getConfortoAcustico() {
        return confortoAcustico;
    }

    public void setConfortoAcustico(Integer confortoAcustico) {
        this.confortoAcustico = confortoAcustico;
    }

    public Integer getDisponibilidadeAssentos() {
        return disponibilidadeAssentos;
    }

    public void setDisponibilidadeAssentos(Integer disponibilidadeAssentos) {
        this.disponibilidadeAssentos = disponibilidadeAssentos;
    }

    public Integer getDisponibilidadeAssentosReservados() {
        return disponibilidadeAssentosReservados;
    }

    public void setDisponibilidadeAssentosReservados(Integer disponibilidadeAssentosReservados) {
        this.disponibilidadeAssentosReservados = disponibilidadeAssentosReservados;
    }

    public Integer getDisponibilidadeTomadas() {
        return disponibilidadeTomadas;
    }

    public void setDisponibilidadeTomadas(Integer disponibilidadeTomadas) {
        this.disponibilidadeTomadas = disponibilidadeTomadas;
    }

    public Integer getInternetDisponibilizadaAeroporto() {
        return internetDisponibilizadaAeroporto;
    }

    public void setInternetDisponibilizadaAeroporto(Integer internetDisponibilizadaAeroporto) {
        this.internetDisponibilizadaAeroporto = internetDisponibilizadaAeroporto;
    }

    public Integer getVelocidadeConexao() {
        return velocidadeConexao;
    }

    public void setVelocidadeConexao(Integer velocidadeConexao) {
        this.velocidadeConexao = velocidadeConexao;
    }

    public Integer getFacilidadeAcessoRede() {
        return facilidadeAcessoRede;
    }

    public void setFacilidadeAcessoRede(Integer facilidadeAcessoRede) {
        this.facilidadeAcessoRede = facilidadeAcessoRede;
    }
}
