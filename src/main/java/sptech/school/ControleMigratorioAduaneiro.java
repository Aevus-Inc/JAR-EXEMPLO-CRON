package sptech.school;

public class ControleMigratorioAduaneiro extends PesquisaDeSatisfacao{
    private Integer controleMigratorio;
    private Integer tempoEsperaFila;
    private Integer organizacaoFilas;
    private Integer atendimentoFuncionarios;
    private Integer quantidadeGuiches;
    private String controleAduaneiro;

    public ControleMigratorioAduaneiro(){

    }

    public ControleMigratorioAduaneiro(Integer pesquisaID, Integer controleMigratorio, Integer tempoEsperaFila, Integer organizacaoFilas, Integer atendimentoFuncionarios, Integer quantidadeGuiches, String controleAduaneiro) {
        super(pesquisaID);
        this.controleMigratorio = controleMigratorio;
        this.tempoEsperaFila = tempoEsperaFila;
        this.organizacaoFilas = organizacaoFilas;
        this.atendimentoFuncionarios = atendimentoFuncionarios;
        this.quantidadeGuiches = quantidadeGuiches;
        this.controleAduaneiro = controleAduaneiro;
    }

    public Integer getControleMigratorio() {
        return controleMigratorio;
    }

    public void setControleMigratorio(Integer controleMigratorio) {
        this.controleMigratorio = controleMigratorio;
    }

    public Integer getTempoEsperaFila() {
        return tempoEsperaFila;
    }

    public void setTempoEsperaFila(Integer tempoEsperaFila) {
        this.tempoEsperaFila = tempoEsperaFila;
    }

    public Integer getOrganizacaoFilas() {
        return organizacaoFilas;
    }

    public void setOrganizacaoFilas(Integer organizacaoFilas) {
        this.organizacaoFilas = organizacaoFilas;
    }

    public Integer getAtendimentoFuncionarios() {
        return atendimentoFuncionarios;
    }

    public void setAtendimentoFuncionarios(Integer atendimentoFuncionarios) {
        this.atendimentoFuncionarios = atendimentoFuncionarios;
    }

    public Integer getQuantidadeGuiches() {
        return quantidadeGuiches;
    }

    public void setQuantidadeGuiches(Integer quantidadeGuiches) {
        this.quantidadeGuiches = quantidadeGuiches;
    }

    public String getControleAduaneiro() {
        return controleAduaneiro;
    }

    public void setControleAduaneiro(String controleAduaneiro) {
        this.controleAduaneiro = controleAduaneiro;
    }
}
