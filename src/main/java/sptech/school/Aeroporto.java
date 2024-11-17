package sptech.school;

import java.util.Set;

public class Aeroporto {
    private static final Set<String> SIGLAS_VALIDAS = Set.of(
            "SBBE", "SBBR", "SBCF", "SBCT", "SBCY", "SBEG", "SBFL", "SBFZ",
            "SBGL", "SBGO", "SBGR", "SBKP", "SBMO", "SBPA", "SBRF", "SBRJ",
            "SBSG", "SBSP", "SBSV", "SBVT"
    );

    private String siglaAeroporto;

    public Aeroporto(String siglaAeroporto, Integer classificacao) {
        this.siglaAeroporto = siglaAeroporto;

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
