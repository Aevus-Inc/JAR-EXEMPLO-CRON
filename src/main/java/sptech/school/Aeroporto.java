package sptech.school;

import java.util.Set;

public class Aeroporto {
    private static final Set<String> SIGLAS_VALIDAS = Set.of(
            "SBBE", "SBBR", "SBCF", "SBCT", "SBCY", "SBEG", "SBFL", "SBFZ",
            "SBGL", "SBGO", "SBGR", "SBKP", "SBMO", "SBPA", "SBRF", "SBRJ",
            "SBSG", "SBSP", "SBSV", "SBVT"
    );

    private String siglaAeroporto;
    private Integer classificacao;

    public Aeroporto(String siglaAeroporto, Integer classificacao) {
        this.siglaAeroporto = siglaAeroporto;
        this.classificacao = classificacao;
    }

    public Integer getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Integer classificacao) {
        this.classificacao = classificacao;
    }

    public Aeroporto(String siglaAeroporto) {
        if (!SIGLAS_VALIDAS.contains(siglaAeroporto)) {
            throw new IllegalArgumentException("Sigla de aeroporto inválida: " + siglaAeroporto);
        }
        this.siglaAeroporto = siglaAeroporto;
    }

    public String getSiglaAeroporto() {
        return siglaAeroporto;
    }

    public void setSiglaAeroporto(String siglaAeroporto) {
        if (!SIGLAS_VALIDAS.contains(siglaAeroporto)) {
            throw new IllegalArgumentException("Sigla de aeroporto inválida: " + siglaAeroporto);
        }
        this.siglaAeroporto = siglaAeroporto;
    }
}
