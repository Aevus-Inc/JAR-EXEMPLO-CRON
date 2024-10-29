package sptech.school;

import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Iniciando o processo de ETL...");

        ETL etlAevus = new ETL();

        // Lista dos arquivos a serem processados
        List<String> arquivos = Arrays.asList("2023-02tri.xlsx");

        // Processa todos os arquivos na lista
        etlAevus.processarArquivosS3(arquivos);

        logger.info("Processo de ETL finalizado.");
    }
}
