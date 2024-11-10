package sptech.school;

public class CheckIn extends PesquisaDeSatisfacao{
    private String formaCheckIn; // Enum("Balcão", "Totem AA", etc.)
    private Integer processoCheckIn;
    private Integer tempoEsperaFila;
    private Integer organizacaoFilas;
    private Integer quantidadeTotensAA;
    private Integer quantidadeBalcoes;
    private Integer cordialidadeFuncionarios;
    private Integer tempoAtendimento;

    public CheckIn(){

    }

    public CheckIn(Integer pesquisaID, String formaCheckIn, Integer processoCheckIn, Integer tempoEsperaFila, Integer organizacaoFilas, Integer quantidadeTotensAA, Integer quantidadeBalcoes, Integer cordialidadeFuncionarios, Integer tempoAtendimento) {
        super(pesquisaID);
        this.formaCheckIn = formaCheckIn;
        this.processoCheckIn = processoCheckIn;
        this.tempoEsperaFila = tempoEsperaFila;
        this.organizacaoFilas = organizacaoFilas;
        this.quantidadeTotensAA = quantidadeTotensAA;
        this.quantidadeBalcoes = quantidadeBalcoes;
        this.cordialidadeFuncionarios = cordialidadeFuncionarios;
        this.tempoAtendimento = tempoAtendimento;
    }

    public CheckIn(PesquisaDeSatisfacao pesquisa, String stringCellValue, int numericCellValue, int numericCellValue1, int numericCellValue2, int numericCellValue3, int numericCellValue4) {
    }

    public String getFormaCheckIn() {
        return formaCheckIn;
    }

    public void setFormaCheckIn(String formaCheckIn) {
        this.formaCheckIn = formaCheckIn;
    }

    public Integer getProcessoCheckIn() {
        return processoCheckIn;
    }

    public void setProcessoCheckIn(Integer processoCheckIn) {
        this.processoCheckIn = processoCheckIn;
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

    public Integer getQuantidadeTotensAA() {
        return quantidadeTotensAA;
    }

    public void setQuantidadeTotensAA(Integer quantidadeTotensAA) {
        this.quantidadeTotensAA = quantidadeTotensAA;
    }

    public Integer getQuantidadeBalcoes() {
        return quantidadeBalcoes;
    }

    public void setQuantidadeBalcoes(Integer quantidadeBalcoes) {
        this.quantidadeBalcoes = quantidadeBalcoes;
    }

    public Integer getCordialidadeFuncionarios() {
        return cordialidadeFuncionarios;
    }

    public void setCordialidadeFuncionarios(Integer cordialidadeFuncionarios) {
        this.cordialidadeFuncionarios = cordialidadeFuncionarios;
    }

    public Integer getTempoAtendimento() {
        return tempoAtendimento;
    }

    public void setTempoAtendimento(Integer tempoAtendimento) {
        this.tempoAtendimento = tempoAtendimento;
    }
}
