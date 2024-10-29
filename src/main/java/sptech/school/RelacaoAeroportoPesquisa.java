package sptech.school;

public class RelacaoAeroportoPesquisa {
    private PesquisaDeSatisfacao pesquisa;
    private Aeroporto aeroporto;
    private String data;
    private String relatorio;

    public PesquisaDeSatisfacao getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(PesquisaDeSatisfacao pesquisa) {
        this.pesquisa = pesquisa;
    }

    public Aeroporto getAeroporto() {
        return aeroporto;
    }

    public void setAeroporto(Aeroporto aeroporto) {
        this.aeroporto = aeroporto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }
}
