package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

public class ConexBanco {

    private static ConexBanco instance;

    private JdbcTemplate conexBanco;

    private ConexBanco() {
        BasicDataSource configBanco = new BasicDataSource();

        // Configurações da conexão com MySQL
        configBanco.setUrl("jdbc:mysql://44.209.193.217/aevus?autoReconnect=true&useSSL=true");// Altere para o seu IP e nome do banco
        configBanco.setUsername("devaevus"); // Altere para o seu nome de usuário
        configBanco.setPassword("aevus123"); // Altere para a sua senha

        // Configura o driver do MySQL
        configBanco.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Adicione esta linha

        // Configurações adicionais para o DataSource
        configBanco.setMaxTotal(50); // Número máximo de conexões no pool
        configBanco.setMaxIdle(25); // Número máximo de conexões inativas
        configBanco.setMinIdle(5);  // Número mínimo de conexões inativas
        configBanco.setMaxWaitMillis(30000); // Tempo máximo para obter uma conexão (em ms)

        // Verificar conexões antes de usá-las
        configBanco.setTestOnBorrow(true);
        configBanco.setValidationQuery("SELECT 1");
        configBanco.setValidationQueryTimeout(5);


        // Associa o DataSource ao JdbcTemplate
        this.conexBanco = new JdbcTemplate(configBanco);
    }

    public static ConexBanco getInstance() {
        if (instance == null) {
            instance = new ConexBanco();
        }

        return instance;
    }

    // Retorna o JdbcTemplate configurado
    public JdbcTemplate getConexaoBanco() {
        return this.conexBanco;
    }

    public Boolean arquivoJaProcessado(String nomeArquivo) {
        String sql = "SELECT COUNT(*) FROM ArquivoProcessado WHERE nome_arquivo = ?";
        Integer count = getConexaoBanco().queryForObject(sql, new Object[]{nomeArquivo}, Integer.class);
        return count != null && count > 0;
    }

    public void registrarArquivoProcessado(String nomeArquivo) {
        String sql = "INSERT INTO ArquivoProcessado (nome_arquivo, data_processamento) VALUES (?, ?)";
        getConexaoBanco().update(sql, nomeArquivo, new Date());
    }

}