package sptech.school;

import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import sptech.school.client.S3Provider;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Iniciando o processo de ETL...");
        ETL etlAevus = new ETL();

        S3Client conexS3 = new S3Provider().getS3Client();

        // Lista dos arquivos a serem processados
        List<String> arquivos = Arrays.asList("2023-01tri.xlsx", "2023-02tri.xlsx", "2023-03tri.xlsx", "2023-04tri.xlsx", "2024-01tri.xlsx", "2024-02tri.xlsx");

        // Processa todos os arquivos na lista
        etlAevus.processarArquivosS3(arquivos, conexS3);

        logger.info("Processo de ETL finalizado.");
    }
}
