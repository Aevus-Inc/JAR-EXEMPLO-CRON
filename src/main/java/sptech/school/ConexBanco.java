package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexBanco {
    private JdbcTemplate conexBanco;

    public ConexBanco() {
        BasicDataSource configBanco = new BasicDataSource();

        // Configurações da conexão com MySQL
        configBanco.setUrl("jdbc:mysql://ContainerBDAevus:3306/aevus2"); // Altere para o seu IP e nome do banco
        configBanco.setUsername("devaevus"); // Altere para o seu nome de usuário
        configBanco.setPassword("aevus123"); // Altere para a sua senha

        // Configura o driver do MySQL
        configBanco.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Adicione esta linha

        // Associa o DataSource ao JdbcTemplate
        this.conexBanco = new JdbcTemplate(configBanco);
    }

    // Retorna o JdbcTemplate configurado
    public JdbcTemplate getConexaoBanco() {
        return conexBanco;
    }
}