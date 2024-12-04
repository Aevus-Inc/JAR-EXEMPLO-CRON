package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexBanco {
    private JdbcTemplate conexBanco;

    public ConexBanco() {
        try {
            BasicDataSource configBanco = new BasicDataSource();
            configBanco.setUrl("jdbc:mysql://34.200.9.69/aevus");
            configBanco.setUsername("devaevus");
            configBanco.setPassword("aevus123");
            configBanco.setDriverClassName("com.mysql.cj.jdbc.Driver");

            this.conexBanco = new JdbcTemplate(configBanco);
        } catch (Exception e) {
            Log logger = null;
            logger.error("Erro ao configurar o banco de dados: " + e.getMessage());
            throw new RuntimeException("Erro ao configurar o banco de dados", e);
        }
    }


    // Retorna o JdbcTemplate configurado
    public JdbcTemplate getConexaoBanco() {
        return conexBanco;
    }
}