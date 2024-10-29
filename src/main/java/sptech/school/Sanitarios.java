package sptech.school;

public class Sanitarios {
    private Integer pesquisaID;
    private Integer sanitariosQt;
    private Integer quantidadeBanheiros;
    private Integer limpezaBanheiros;
    private Integer manutencaoGeralSanitarios;
    private Integer limpezaGeralAeroporto;

    public Sanitarios(){

    }

    public Sanitarios(Integer pesquisaID , Integer sanitariosQt, Integer quantidadeBanheiros, Integer limpezaBanheiros, Integer manutencaoGeralSanitarios, Integer limpezaGeralAeroporto) {
        this.pesquisaID = pesquisaID;
        this.sanitariosQt = sanitariosQt;
        this.quantidadeBanheiros = quantidadeBanheiros;
        this.limpezaBanheiros = limpezaBanheiros;
        this.manutencaoGeralSanitarios = manutencaoGeralSanitarios;
        this.limpezaGeralAeroporto = limpezaGeralAeroporto;
    }

    public Integer getPesquisaID() {
        return pesquisaID;
    }

    public void setPesquisaID(Integer pesquisaID) {
        this.pesquisaID = pesquisaID;
    }
    public Integer getSanitarios() {
        return sanitariosQt;
    }

    public void setSanitarios(Integer sanitariosQt) {
        this.sanitariosQt = sanitariosQt;
    }


    public Integer getQuantidadeBanheiros() {
        return quantidadeBanheiros;
    }

    public void setQuantidadeBanheiros(Integer quantidadeBanheiros) {
        this.quantidadeBanheiros = quantidadeBanheiros;
    }

    public Integer getLimpezaBanheiros() {
        return limpezaBanheiros;
    }

    public void setLimpezaBanheiros(Integer limpezaBanheiros) {
        this.limpezaBanheiros = limpezaBanheiros;
    }

    public Integer getManutencaoGeralSanitarios() {
        return manutencaoGeralSanitarios;
    }

    public void setManutencaoGeralSanitarios(Integer manutencaoGeralSanitarios) {
        this.manutencaoGeralSanitarios = manutencaoGeralSanitarios;
    }

    public Integer getLimpezaGeralAeroporto() {
        return limpezaGeralAeroporto;
    }

    public void setLimpezaGeralAeroporto(Integer limpezaGeralAeroporto) {
        this.limpezaGeralAeroporto = limpezaGeralAeroporto;
    }
}
