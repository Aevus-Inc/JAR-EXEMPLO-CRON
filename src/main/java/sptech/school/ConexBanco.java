package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConexBanco {
    private JdbcTemplate conexBanco;
    private static final Logger logger = LoggerFactory.getLogger(ConexBanco.class);

    public ConexBanco() {
        try {
            BasicDataSource configBanco = new BasicDataSource();

            // Configuração básica do banco de dados
            configBanco.setUrl("jdbc:mysql://34.200.9.69/aevus"); // Substitua pelo seu IP/nome do banco
            configBanco.setUsername("devaevus");                 // Substitua pelo seu usuário
            configBanco.setPassword("aevus123");                 // Substitua pela sua senha
            configBanco.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Configuração do pool de conexões
            configBanco.setInitialSize(5);       // Conexões iniciadas no pool
            configBanco.setMaxTotal(20);         // Máximo de conexões
            configBanco.setMaxIdle(10);          // Conexões ociosas máximas
            configBanco.setMinIdle(5);           // Conexões ociosas mínimas
            configBanco.setMaxWaitMillis(10000); // Tempo máximo para aguardar uma conexão (10s)

            // Configuração de validação
            configBanco.setTestOnBorrow(true);                       // Valida conexão antes de usar
            configBanco.setValidationQuery("SELECT 1");              // Query para validação
            configBanco.setTestWhileIdle(true);                      // Valida conexões ociosas
            configBanco.setTimeBetweenEvictionRunsMillis(5000);      // Verifica conexões a cada 5s
            configBanco.setMinEvictableIdleTimeMillis(60000);        // Tempo máximo ocioso (1 min)
            configBanco.setLogExpiredConnections(true);              // Loga conexões expiradas
            configBanco.setRemoveAbandonedOnBorrow(true);            // Remove conexões abandonadas
            configBanco.setRemoveAbandonedTimeout(60);               // Remove após 60s inativa

            // Associa o DataSource ao JdbcTemplate
            this.conexBanco = new JdbcTemplate(configBanco);
            logger.info("Configuração do banco de dados concluída com sucesso.");
        } catch (Exception e) {
            logger.error("Erro ao configurar o banco de dados: {}", e.getMessage());
            throw new RuntimeException("Erro ao configurar o banco de dados", e);
        }
    }

    // Retorna o JdbcTemplate configurado
    public JdbcTemplate getConexaoBanco() {
        return conexBanco;
    }
}
