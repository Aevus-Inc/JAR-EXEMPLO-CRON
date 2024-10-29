package sptech.school;

public class Estacionamento {
    private Integer pesquisaID;
    private Integer qualidadeInstalacoesEstacionamento;
    private Integer facilidadeEncontrarVagas;
    private Integer facilidadeAcessoTerminal;
    private Integer relacaoCustoBeneficio;

    // Getters e Setters

    public Estacionamento(){
        
    }

    public Estacionamento(Integer pesquisaID, Integer qualidadeInstalacoesEstacionamento, Integer facilidadeEncontrarVagas, Integer facilidadeAcessoTerminal, Integer relacaoCustoBeneficio) {
        this.pesquisaID = pesquisaID;
        this.qualidadeInstalacoesEstacionamento = qualidadeInstalacoesEstacionamento;
        this.facilidadeEncontrarVagas = facilidadeEncontrarVagas;
        this.facilidadeAcessoTerminal = facilidadeAcessoTerminal;
        this.relacaoCustoBeneficio = relacaoCustoBeneficio;
    }

    public Estacionamento(Integer qualidadeInstalacoesEstacionamento, Integer facilidadeEncontrarVagas, Integer facilidadeAcessoTerminal, Integer relacaoCustoBeneficio) {
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }

    public Integer getQualidadeInstalacoesEstacionamento() {
        return qualidadeInstalacoesEstacionamento;
    }

    public Integer getFacilidadeEncontrarVagas() {
        return facilidadeEncontrarVagas;
    }


    public Integer getFacilidadeAcessoTerminal() {
        return facilidadeAcessoTerminal;
    }


    public Integer getRelacaoCustoBeneficio() {
        return relacaoCustoBeneficio;
    }

}

