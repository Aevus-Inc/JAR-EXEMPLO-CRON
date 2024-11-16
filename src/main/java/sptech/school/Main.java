package sptech.school;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import sptech.school.client.S3Provider;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (FileWriter writer = new FileWriter("log_erro_etl.txt", true)) {
            logger.info("Iniciando o processo de ETL...");
            try {
                writer.write("[" + timestamp + "] [INFO] Iniciando o processo de ETL...\n");
            } catch (IOException e) {
                logger.error("Erro ao escrever no arquivo de log: {}", e.getMessage());
            }

            ETL etlAevus = new ETL();
            S3Client conexS3 = new S3Provider().getS3Client();

            List<String> arquivos = Arrays.asList("2023-01tri.xlsx");

            try {
                etlAevus.processarArquivosS3(arquivos, conexS3);
                logger.info("Processo de ETL finalizado.");
                writer.write("[" + timestamp + "] [INFO] Processo de ETL finalizado.\n");
            } catch (Exception e) {
                logger.error("Erro durante o processo de ETL: {}", e.getMessage());
                try {
                    writer.write("[" + timestamp + "] [ERROR] Erro durante o processo de ETL: " + e.getMessage() + "\n");
                } catch (IOException ex) {
                    logger.error("Erro ao tentar escrever no arquivo de log: {}", ex.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao abrir o arquivo de log: {}", e.getMessage());
        }
    }
}
