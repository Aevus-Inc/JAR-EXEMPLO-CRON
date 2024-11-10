package sptech.school;

public class InformacoesVoo extends PesquisaDeSatisfacao{
    private String processo; // Enum("Embarque", "Desembarque")
    private String aeroportoVoo;
    private String terminal;
    private String portao; // Altere para String para aceitar números e texto
    private String tipoVoo; // Enum("Doméstico", "Internacional")
    private String ciaAerea; // Enum("AEROLÍNEAS ARGENTINAS", etc.)
    private String voo;
    private String conexao; // Enum("Em conexão", "Embarcado no aeroporto")

    public InformacoesVoo(){

    }

    public InformacoesVoo(Integer pesquisaID, String processo, String aeroportoVoo, String terminal, String portao, String tipoVoo, String ciaAerea, String voo, String conexao) {
        super(pesquisaID);
        this.processo = processo;
        this.aeroportoVoo = aeroportoVoo;
        this.terminal = terminal;
        this.portao = portao; // Agora é um String
        this.tipoVoo = tipoVoo;
        this.ciaAerea = ciaAerea;
        this.voo = voo;
        this.conexao = conexao;
    }


    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getaeroportoVoo() {
        return aeroportoVoo;
    }

    public void setaeroportoVoo(String aeroporto) {
        this.aeroportoVoo = aeroporto;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getPortao() {
        return portao;
    }

    public void setPortao(String portao) {
        this.portao = portao;
    }
    public String getTipoVoo() {
        return tipoVoo;
    }

    public void setTipoVoo(String tipoVoo) {
        this.tipoVoo = tipoVoo;
    }

    public String getCiaAerea() {
        return ciaAerea;
    }

    public void setCiaAerea(String ciaAerea) {
        this.ciaAerea = ciaAerea;
    }

    public String getVoo() {
        return voo;
    }

    public void setVoo(String voo) {
        this.voo = voo;
    }

    public String getConexao() {
        return conexao;
    }

    public void setConexao(String conexao) {
        this.conexao = conexao;
    }


}
