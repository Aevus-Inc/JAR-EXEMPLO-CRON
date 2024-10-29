package sptech.school;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ETL {
    private static final Logger logger = LoggerFactory.getLogger(ETL.class);
    private static final String BUCKET_NAME = "s3-bucket-aevusec2";

    public void processarArquivosS3(List<String> arquivos) {
        S3Client conexS3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        for (String arquivo : arquivos) {
            try {
                byte[] arquivoBytes = conexS3.getObject(GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(arquivo)
                        .build()).readAllBytes();

                if (arquivoBytes.length == 0) {
                    throw new RuntimeException("O arquivo fornecido está vazio (zero bytes). Verifique o bucket S3.");
                }

                List<Consumer<InputStream>> processos = Arrays.asList(

                        stream -> inserirPassageirosNoBanco(extrairDadosPassageiro(arquivo, stream)),
                        stream -> InserirAeroportosNoBanco(ExtrairDadosAeroporto(arquivo, stream)),
                        stream -> inserirPesquisasNoBanco(extrairDadosPesquisaSatisfacao(arquivo, stream)),
                        stream -> inserirInformacoesVooNoBanco(extrairDadosInformacoesVoo(arquivo, stream)),
                        stream -> InserirPassagemNoBanco(extrairDadosAquisicaoPassagem(arquivo, stream)),
                        stream -> inserirNecessidadesEspeciaisNoBanco(extrairDadosNecessidadesEspeciais(arquivo, stream)),
                        stream -> inserirDesembarqueNoBanco(extrairDadosDesembarque(arquivo, stream)),
                        stream -> InserirCheckInBanco(ExtrairDadosCheckIn(arquivo, stream)),
                        stream -> inserirInspecaoSegurancaNoBanco(extrairDadosInspecaoSeguranca(arquivo, stream)),
                        stream -> inserirControleMigratorioAduaneiro(extrairDadosControleMigratorioAduaneiro(arquivo, stream)),
                        stream -> inserirEstabelecimentosNoBanco(extrairDadosEstabelecimentos(arquivo, stream)),
                        stream -> inserirEstacionamentoNoBanco(extrairDadosEstacionamento(arquivo, stream)),
                        stream -> inserirConfortoAcessibilidade(extrairConfortoAcessibilidade(arquivo, stream)),
                        stream -> inserirDadosSanitarios(extrairDadosSanitarios(arquivo, stream)),
                        stream -> inserirDadosBagagens(extrairDadosBagagens(arquivo, stream))

                );

                for (Consumer<InputStream> processo : processos) {
                    try (InputStream stream = new ByteArrayInputStream(arquivoBytes)) {
                        processo.accept(stream);
                    }
                }

                logger.info("Arquivo processado com sucesso: {}", arquivo);

            } catch (IOException | S3Exception e) {
                logger.error("Erro ao processar o arquivo {}: {}", arquivo, e.getMessage());
                logger.info("Continuando para o próximo arquivo...");
            }
        }
    }


    //Método auxiliar para obter valor da célula como String
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BLANK:
                return null; // Retorna null para células em branco
            default:
                return null;
        }
    }

    public String getCellValueAsStringg(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // Converte para String
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    // Método auxiliar para obter o valor numérico da célula
    private Integer getNumericCellValue(Cell cell) {
        if (cell == null) {
            return null; // Retorna null para células nulas
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue(); // Retorna o valor numérico como Integer
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim()); // Tenta converter a string para Integer
                } catch (NumberFormatException e) {
                    return null; // Retorna null se a conversão falhar
                }
            default:
                return null; // Retorna null para outros tipos de células
        }
    }


    // Método auxiliar para obter o valor BOOLEAN da célula
    private String getBooleanValueFromCell(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue()); // Retorna "true" ou "false"
                case STRING:
                    // Lógica para interpretar strings como booleanos
                    String stringValue = cell.getStringCellValue().toLowerCase();
                    if (stringValue.equals("true") || stringValue.equals("1")) {
                        return "true";
                    } else if (stringValue.equals("false") || stringValue.equals("0")) {
                        return "false";
                    }
                    return null; // Retorna null se não puder ser interpretado
                default:
                    return null; // Retorna null para outros tipos
            }
        }
        return null; // Se a célula for nula, retorna null
    }

    private String getVooAsStringOrInteger(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // Converte para String e remove decimais
            default:
                return null;
        }
    }

    public List<Aeroporto> ExtrairDadosAeroporto(String nomeArquivo, InputStream arquivo) {
        logger.info("Extraindo dados de aeroportos do arquivo: {}", nomeArquivo);
        List<Aeroporto> aeroportosExtraidos = new ArrayList<>();
        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0); // Ajuste o índice da planilha conforme necessário
            int limiteDeLinhas = 10;

            // Encontrar a primeira ocorrência de "SBEG" na coluna C
            int startRow = -1;
            for (int i = startRow; i <= sheet.getLastRowNum() && aeroportosExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    Cell cellSigla = linha.getCell(2); // Coluna C (índice 2) para a sigla
                    Cell cellClassificacao = linha.getCell(83); // Coluna D (índice 3) para a classificação
                    if (cellSigla != null && cellSigla.getCellType() == CellType.STRING) {
                        String sigla = cellSigla.getStringCellValue().trim();
                        Integer classificacao = (cellClassificacao != null && cellClassificacao.getCellType() == CellType.NUMERIC)
                                ? (int) cellClassificacao.getNumericCellValue()
                                : null;
                        try {
                            Aeroporto aeroporto = new Aeroporto(sigla, classificacao);
                            aeroportosExtraidos.add(aeroporto);
                        } catch (IllegalArgumentException e) {
                            logger.warn("Sigla ou classificação de aeroporto inválida ignorada: {}", sigla);
                        }
                    }
                }
            }

            // Se "SBEG" foi encontrado, começa a extração a partir de `startRow`
            if (startRow != -1) {
                for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                    Row linha = sheet.getRow(i);
                    if (linha != null) {
                        Cell cellSigla = linha.getCell(2); // Coluna C (índice 2)
                        if (cellSigla != null && cellSigla.getCellType() == CellType.STRING) {
                            String sigla = cellSigla.getStringCellValue().trim();
                            try {
                                Aeroporto aeroporto = new Aeroporto(sigla);
                                aeroportosExtraidos.add(aeroporto);
                            } catch (IllegalArgumentException e) {
                                logger.warn("Sigla de aeroporto inválida ignorada: {}", sigla);
                            }
                        }
                    }
                }
            } else {
                logger.warn("Sigla 'SBEG' não encontrada na coluna C.");
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }
        logger.info("Dados de aeroportos extraídos com sucesso: {}", aeroportosExtraidos.size());
        return aeroportosExtraidos;
    }

    public List<Passageiro> extrairDadosPassageiro(String nomeArquivo, InputStream arquivo) {
        logger.info("Extraindo dados de passageiros do arquivo: {}", nomeArquivo);
        List<Passageiro> passageirosExtraidos = new ArrayList<>();

        try (Workbook workbook = nomeArquivo.endsWith(".xlsx") ?
                new XSSFWorkbook(arquivo) :
                new HSSFWorkbook(arquivo)) {

            Sheet sheet = workbook.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && passageirosExtraidos.size() < limiteDeLinhas; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String nacionalidade = row.getCell(85).getStringCellValue();
                    String genero = row.getCell(86).getStringCellValue();
                    String faixaEtaria = row.getCell(87).getStringCellValue();
                    String escolaridade = row.getCell(88).getStringCellValue();
                    String rendaFamiliar = row.getCell(89).getStringCellValue();

                    String viajandoSozinho = getCellValueAsString(row.getCell(90));
                    String numeroAcompanhantes = getCellValueAsString(row.getCell(91));
                    String motivoViagem = getCellValueAsString(row.getCell(92));
                    String quantidadeViagensUltimos12Meses = getCellValueAsString(row.getCell(93));
                    String jaEmbarcouDesembarcouAntes = getCellValueAsString(row.getCell(94));
                    String antecedencia = getCellValueAsString(row.getCell(95));
                    String tempoEspera = getCellValueAsString(row.getCell(96));

                    String comentariosAdicionais = null;
                    Cell cell = row.getCell(97);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                comentariosAdicionais = cell.getStringCellValue();
                                break;
                            case FORMULA:
                                comentariosAdicionais = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                comentariosAdicionais = String.valueOf((int) cell.getNumericCellValue());
                                break;
                            default:
                                break;
                        }
                    }

                    Passageiro passageiro = new Passageiro(nacionalidade, genero, faixaEtaria, escolaridade, rendaFamiliar,
                            viajandoSozinho, numeroAcompanhantes, motivoViagem, quantidadeViagensUltimos12Meses,
                            jaEmbarcouDesembarcouAntes, antecedencia, tempoEspera, comentariosAdicionais);
                    passageirosExtraidos.add(passageiro);
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao extrair dados do arquivo", e);
            throw new RuntimeException("Erro ao extrair dados do arquivo: " + e.getMessage(), e);
        }
        return passageirosExtraidos;
    }

    public List<PesquisaDeSatisfacao> extrairDadosPesquisaSatisfacao(String nomeArquivo, InputStream arquivo) {
        logger.info("Extraindo dados de pesquisas de satisfação do arquivo: {}", nomeArquivo);
        List<PesquisaDeSatisfacao> pesquisasExtraidas = new ArrayList<>();
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

        try (Workbook workbook = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = workbook.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && pesquisasExtraidas.size() < limiteDeLinhas; i++) {
                Row row = sheet.getRow(i);

                if (row != null) {
                    Integer pesquisaID = (int) row.getCell(0).getNumericCellValue(); // Coluna A
                    String mes = row.getCell(4).getStringCellValue(); // Coluna E
                    String data = formatoData.format(row.getCell(3).getDateCellValue()); // Coluna D

                    PesquisaDeSatisfacao pesquisa = new PesquisaDeSatisfacao(pesquisaID, null, null, mes, data);
                    pesquisasExtraidas.add(pesquisa);
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de pesquisas de satisfação extraídos com sucesso: {}", pesquisasExtraidas.size());
        return pesquisasExtraidas;
    }

    public List<InformacoesVoo> extrairDadosInformacoesVoo(String nomeArquivo, InputStream arquivo) {
        logger.info("Extraindo dados de informações de voo do arquivo: {}", nomeArquivo);
        List<InformacoesVoo> informacoesVoosExtraidas = new ArrayList<>();

        try (Workbook workbook = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = workbook.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && informacoesVoosExtraidas.size() < limiteDeLinhas; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Integer pesquisaID = (int) row.getCell(0).getNumericCellValue();
                    String processo = getCellValueAsString(row.getCell(1));
                    String aeroporto = getCellValueAsString(row.getCell(2));
                    String terminal = getCellValueAsString(row.getCell(7));
                    String portao = getCellValueAsString(row.getCell(8));
                    String tipoVoo = getCellValueAsString(row.getCell(9));
                    String ciaAerea = getCellValueAsString(row.getCell(10));
                    String voo = getCellValueAsString(row.getCell(11));
                    String conexao = getCellValueAsString(row.getCell(12));

                    InformacoesVoo informacoesVoo = new InformacoesVoo(pesquisaID, processo, aeroporto, terminal, portao, tipoVoo, ciaAerea, voo, conexao);
                    informacoesVoosExtraidas.add(informacoesVoo);
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao extrair dados do arquivo", e);
        }
        return informacoesVoosExtraidas;
    }

    public List<AquisicaoPassagem> extrairDadosAquisicaoPassagem(String nomeArquivo, InputStream arquivo) {
        logger.info("Extraindo dados de aquisições de passagem do arquivo: {}", nomeArquivo);
        List<AquisicaoPassagem> aquisicoesExtraidas = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0); // Altere o índice se necessário
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && aquisicoesExtraidas.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    Cell cellPesquisaID = linha.getCell(0); // Supondo que a coluna 0 é Pesquisa_ID
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        String aquisicaoPassagem = getCellValueAsString(linha.getCell(13)); // Coluna para aquisicaoPassagem
                        String meioAquisicaoPassagem = getCellValueAsString(linha.getCell(14)); // Coluna para meioAquisicaoPassagem
                        String meioTransporteAeroporto = getCellValueAsString(linha.getCell(15)); // Coluna para meioTransporteAeroporto

                        // Cria a AquisicaoPassagem independentemente de campos nulos
                        AquisicaoPassagem aquisicao = new AquisicaoPassagem(pesquisaID, aquisicaoPassagem, meioAquisicaoPassagem, meioTransporteAeroporto);
                        aquisicoesExtraidas.add(aquisicao);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de aquisições de passagem extraídos com sucesso: {}", aquisicoesExtraidas.size());
        return aquisicoesExtraidas;
    }

    public List<NecessidadesEspeciais> extrairDadosNecessidadesEspeciais(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de NecessidadesEspeciais do arquivo: {}", nomeArquivo);
        List<NecessidadesEspeciais> necessidadesEspeciaisExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && necessidadesEspeciaisExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        String possuiDeficiencia = getCellValueAsString(linha.getCell(16));
                        String utilizaRecursoAssistivo = getCellValueAsString(linha.getCell(17));
                        String solicitouAssistenciaEspecial = getCellValueAsString(linha.getCell(18));

                        // Cria a CheckIn independentemente de campos nulos
                        NecessidadesEspeciais necessidadesEspeciais = new NecessidadesEspeciais(pesquisaID, possuiDeficiencia, utilizaRecursoAssistivo, solicitouAssistenciaEspecial);
                        necessidadesEspeciaisExtraidos.add(necessidadesEspeciais);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }
        logger.info("Dados de NecessidadesEspeciais extraídos com sucesso: {}", necessidadesEspeciaisExtraidos.size());
        return necessidadesEspeciaisExtraidos;
    }

    public List<Desembarque> extrairDadosDesembarque(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de Desembarque do arquivo: {}", nomeArquivo);
        List<Desembarque> desembarquesExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && desembarquesExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        String formaDesembarque = getCellValueAsString(linha.getCell(19));
                        Integer avaliacaoMetodoDesembarque = getNumericCellValue(linha.getCell(20));
                        String utilizouEstacionamento = getBooleanValueFromCell(linha.getCell(21));
                        Integer facilidadeDesembarqueMeioFio = getNumericCellValue(linha.getCell(22));
                        Integer opcoesTransporteAeroporto = getNumericCellValue(linha.getCell(23));

                        // Cria a CheckIn independentemente de campos nulos
                        Desembarque desembarque = new Desembarque(pesquisaID, formaDesembarque, avaliacaoMetodoDesembarque, utilizouEstacionamento, facilidadeDesembarqueMeioFio, opcoesTransporteAeroporto);
                        desembarquesExtraidos.add(desembarque);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }
        logger.info("Dados de Desembarque extraídos com sucesso: {}", desembarquesExtraidos.size());
        return desembarquesExtraidos;
    }

    public List<CheckIn> ExtrairDadosCheckIn(String nomeArquivo, InputStream arquivo) {
        logger.info("Extraindo dados de check-in do arquivo: {}", nomeArquivo);
        List<CheckIn> checkInsExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && checkInsExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        String formaCheckIn = getCellValueAsString(linha.getCell(24));
                        Integer processoCheckIn = getNumericCellValue(linha.getCell(25));
                        Integer tempoEsperaFila = getNumericCellValue(linha.getCell(26));
                        Integer organizacaoFilas = getNumericCellValue(linha.getCell(27));
                        Integer quantidadeTotensAA = getNumericCellValue(linha.getCell(28));
                        Integer quantidadeBalcoes = getNumericCellValue(linha.getCell(29));
                        Integer cordialidadeFuncionarios = getNumericCellValue(linha.getCell(30));
                        Integer tempoAtendimento = getNumericCellValue(linha.getCell(31));

                        // Cria a CheckIn independentemente de campos nulos
                        CheckIn checkIn = new CheckIn(pesquisaID, formaCheckIn, processoCheckIn, tempoEsperaFila, organizacaoFilas,
                                quantidadeTotensAA, quantidadeBalcoes, cordialidadeFuncionarios, tempoAtendimento);
                        checkInsExtraidos.add(checkIn);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de check-in extraídos com sucesso: {}", checkInsExtraidos.size());
        return checkInsExtraidos;
    }

    public List<InspecaoSeguranca> extrairDadosInspecaoSeguranca(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de InspecaoSeguranca do arquivo: {}", nomeArquivo);
        List<InspecaoSeguranca> inspecaoSegurancasExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && inspecaoSegurancasExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        Integer processoInspecaoSeguranca = getNumericCellValue(linha.getCell(34));
                        Integer tempoEsperaFila = getNumericCellValue(linha.getCell(35));
                        Integer organizacaoFilas = getNumericCellValue(linha.getCell(36));
                        Integer atendimentoFuncionarios = getNumericCellValue(linha.getCell(37));


                        // Cria a CheckIn independentemente de campos nulos
                        InspecaoSeguranca inspecaoSeguranca = new InspecaoSeguranca(pesquisaID, processoInspecaoSeguranca, tempoEsperaFila, organizacaoFilas, atendimentoFuncionarios);
                        inspecaoSegurancasExtraidos.add(inspecaoSeguranca);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de InspecaoSeguranca extraídos com sucesso: {}", inspecaoSegurancasExtraidos.size());
        return inspecaoSegurancasExtraidos;
    }

    public List<ControleMigratorioAduaneiro> extrairDadosControleMigratorioAduaneiro(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de ControleMigratorioAduaneiro do arquivo: {}", nomeArquivo);
        List<ControleMigratorioAduaneiro> controleMigratorioAduaneirosExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && controleMigratorioAduaneirosExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        Integer controleMigratorio = getNumericCellValue(linha.getCell(38));
                        Integer tempoEsperaFila = getNumericCellValue(linha.getCell(39));
                        Integer organizacaoFilas = getNumericCellValue(linha.getCell(40));
                        Integer atendimentoFuncionarios = getNumericCellValue(linha.getCell(41));
                        Integer quantidadeGuiches = getNumericCellValue(linha.getCell(42));
                        String controleAduaneiro = getCellValueAsString(linha.getCell(43));

                        // Cria a CheckIn independentemente de campos nulos
                        ControleMigratorioAduaneiro controleMigratorioAduaneiro = new ControleMigratorioAduaneiro(pesquisaID, controleMigratorio, tempoEsperaFila, organizacaoFilas, atendimentoFuncionarios, quantidadeGuiches, controleAduaneiro);
                        controleMigratorioAduaneirosExtraidos.add(controleMigratorioAduaneiro);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de ControleMigratorioAduaneiro extraídos com sucesso: {}", controleMigratorioAduaneirosExtraidos.size());
        return controleMigratorioAduaneirosExtraidos;
    }

    public List<Estabelecimentos> extrairDadosEstabelecimentos(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de Estabelecimentos do arquivo: {}", nomeArquivo);
        List<Estabelecimentos> estabelecimentosExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && estabelecimentosExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        String estabelecimentosAlimentacao = getCellValueAsString(linha.getCell(47));
                        Integer quantidadeEstabelecimentosAlimentacao = getNumericCellValue(linha.getCell(48));
                        Integer qualidadeVariedadeOpcoesAlimentacao = getNumericCellValue(linha.getCell(49));
                        Integer relacaoPrecoQualidadeAlimentacao = getNumericCellValue(linha.getCell(50));
                        String estabelecimentosComerciais = getCellValueAsStringg(linha.getCell(51));
                        Integer quantidadeEstabelecimentosComerciais = getNumericCellValue(linha.getCell(52));
                        Integer qualidadeVariedadeOpcoesComerciais = getNumericCellValue(linha.getCell(53));


                        // Cria a CheckIn independentemente de campos nulos
                        Estabelecimentos estabelecimentos = new Estabelecimentos(pesquisaID, estabelecimentosAlimentacao, quantidadeEstabelecimentosAlimentacao, qualidadeVariedadeOpcoesAlimentacao, relacaoPrecoQualidadeAlimentacao, estabelecimentosComerciais, quantidadeEstabelecimentosComerciais, qualidadeVariedadeOpcoesComerciais);
                        estabelecimentosExtraidos.add(estabelecimentos);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de Estabelecimentos extraídos com sucesso: {}", estabelecimentosExtraidos.size());
        return estabelecimentosExtraidos;
    }

    public List<Estacionamento> extrairDadosEstacionamento(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de Estacionamento do arquivo: {}", nomeArquivo);
        List<Estacionamento> estacionamentoExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && estacionamentoExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        Integer qualidadeInstalacoesEstacionamento = getNumericCellValue(linha.getCell(56));
                        Integer facilidadeEncontrarVagas = getNumericCellValue(linha.getCell(57));
                        Integer facilidadeAcessoTerminal = getNumericCellValue(linha.getCell(58));
                        Integer relacaoCustoBeneficio = getNumericCellValue(linha.getCell(59));


                        // Cria a CheckIn independentemente de campos nulos
                        Estacionamento estacionamento = new Estacionamento(pesquisaID, qualidadeInstalacoesEstacionamento, facilidadeEncontrarVagas, facilidadeAcessoTerminal, relacaoCustoBeneficio);
                        estacionamentoExtraidos.add(estacionamento);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de Estacionamento extraídos com sucesso: {}", estacionamentoExtraidos.size());
        return estacionamentoExtraidos;
    }

    public List<ConfortoAcessibilidade> extrairConfortoAcessibilidade(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de ConfortoAcessibilidade do arquivo: {}", nomeArquivo);
        List<ConfortoAcessibilidade> confortoAcessibilidadesExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && confortoAcessibilidadesExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        Integer localizacaoDeslocamento = getNumericCellValue(linha.getCell(60));
                        Integer sinalizacao = getNumericCellValue(linha.getCell(61));
                        Integer disponibilidadePaineisInformacoesVoo = getNumericCellValue(linha.getCell(62));
                        Integer acessibilidadeTerminal = getNumericCellValue(linha.getCell(63));
                        Integer confortoSalaEmbarque = getNumericCellValue(linha.getCell(64));
                        Integer confortoTermico = getNumericCellValue(linha.getCell(65));
                        Integer confortoAcustico = getNumericCellValue(linha.getCell(66));
                        Integer disponibilidadeAssentos = getNumericCellValue(linha.getCell(67));
                        Integer disponibilidadeAssentosReservados = getNumericCellValue(linha.getCell(68));
                        Integer disponibilidadeTomadas = getNumericCellValue(linha.getCell(69));
                        Integer internetDisponibilizadaAeroporto = getNumericCellValue(linha.getCell(70));
                        Integer velocidadeConexao = getNumericCellValue(linha.getCell(71));
                        Integer facilidadeAcessoRede = getNumericCellValue(linha.getCell(72));


                        // Cria a CheckIn independentemente de campos nulos
                        ConfortoAcessibilidade confortoAcessibilidade = new ConfortoAcessibilidade(pesquisaID, localizacaoDeslocamento, sinalizacao, disponibilidadePaineisInformacoesVoo, acessibilidadeTerminal, confortoSalaEmbarque, confortoTermico, confortoAcustico,disponibilidadeAssentos, disponibilidadeAssentosReservados, disponibilidadeTomadas, internetDisponibilizadaAeroporto, velocidadeConexao, facilidadeAcessoRede);
                        confortoAcessibilidadesExtraidos.add(confortoAcessibilidade);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de ConfortoAcessibilidade extraídos com sucesso: {}", confortoAcessibilidadesExtraidos.size());
        return confortoAcessibilidadesExtraidos;
    }

    public List<Sanitarios> extrairDadosSanitarios(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de Sanitarios do arquivo: {}", nomeArquivo);
        List<Sanitarios> sanitariosExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && sanitariosExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        Integer sanitariosQt = getNumericCellValue(linha.getCell(73));
                        Integer quantidadeBanheiros = getNumericCellValue(linha.getCell(74));
                        Integer limpezaBanheiros = getNumericCellValue(linha.getCell(75));
                        Integer manutencaoGeralSanitarios = getNumericCellValue(linha.getCell(76));
                        Integer limpezaGeralAeroporto = getNumericCellValue(linha.getCell(77));

                        Sanitarios sanitarios = new Sanitarios(pesquisaID, sanitariosQt, quantidadeBanheiros, limpezaBanheiros, manutencaoGeralSanitarios, limpezaGeralAeroporto);
                        sanitariosExtraidos.add(sanitarios);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de Sanitarios extraídos com sucesso: {}", sanitariosExtraidos.size());
        return sanitariosExtraidos;
    }

    public List<RestituicaoBagagens> extrairDadosBagagens(String nomeArquivo, InputStream arquivo){
        logger.info("Extraindo dados de RestituicaoBagagens do arquivo: {}", nomeArquivo);
        List<RestituicaoBagagens> restituicaoBagagensExtraidos = new ArrayList<>();

        try (Workbook arquivoExcel = nomeArquivo.endsWith(".xlsx") ? new XSSFWorkbook(arquivo) : new HSSFWorkbook(arquivo)) {
            Sheet sheet = arquivoExcel.getSheetAt(0);
            int limiteDeLinhas = 10;

            for (int i = 2; i <= sheet.getLastRowNum() && restituicaoBagagensExtraidos.size() < limiteDeLinhas; i++) {
                Row linha = sheet.getRow(i);
                if (linha != null) {
                    // Supondo que a coluna 0 é onde está o pesquisaID
                    Cell cellPesquisaID = linha.getCell(0);
                    if (cellPesquisaID != null && cellPesquisaID.getCellType() == CellType.NUMERIC) {
                        int pesquisaID = (int) cellPesquisaID.getNumericCellValue();

                        // Verificando campos e tratando como nulos
                        Integer processoRestituicaoBagagens = getNumericCellValue(linha.getCell(78));
                        Integer facilidadeIdentificacaoEsteira = getNumericCellValue(linha.getCell(79));
                        Integer tempoRestituicao = getNumericCellValue(linha.getCell(80));
                        Integer integridadeBagagem = getNumericCellValue(linha.getCell(81));
                        Integer atendimentoCiaAerea = getNumericCellValue(linha.getCell(82));

                        RestituicaoBagagens restituicaoBagagens = new RestituicaoBagagens(pesquisaID, processoRestituicaoBagagens, facilidadeIdentificacaoEsteira, tempoRestituicao, integridadeBagagem, atendimentoCiaAerea);
                        restituicaoBagagensExtraidos.add(restituicaoBagagens);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo Excel: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo Excel: " + e.getMessage());
        }

        logger.info("Dados de RestituicaoBagagens extraídos com sucesso: {}", restituicaoBagagensExtraidos.size());
        return restituicaoBagagensExtraidos;
    }


    //INSERÇÃO NO BANCO DE DADOS
    public void inserirPesquisasNoBanco(List<PesquisaDeSatisfacao> pesquisas) {
        logger.info("Inserindo pesquisas de satisfação no banco de dados. Total: {}", pesquisas.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        // Modificando a consulta SQL para usar STR_TO_DATE
        String sql = "INSERT IGNORE INTO PesquisaDeSatisfacao (Pesquisa_ID, Mes, DataPesquisa) VALUES (?, ?, STR_TO_DATE(?, '%d/%m/%Y'))";

        int batchSize = 2000; // Ajuste o tamanho do lote conforme necessário
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (PesquisaDeSatisfacao pesquisa : pesquisas) {
                Object[] parametros = {
                        pesquisa.getPesquisaID(),
                        pesquisa.getMes(),
                        pesquisa.getData() // Data no formato DD/MM/YYYY
                };

                // Log dos dados que estão sendo inseridos
                logger.info("Inserindo: Pesquisa_ID={}, Mes={}, DataPesquisa={}",
                        pesquisa.getPesquisaID(),
                        pesquisa.getMes(),
                        pesquisa.getData());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch); // Insere lote
                    logger.info("Inserido lote de {} pesquisas de satisfação", batchSize);
                    parametrosBatch.clear(); // Limpa para o próximo lote
                }
            }

            // Insere qualquer restante que não foi inserido no último lote
            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} pesquisas de satisfação", parametrosBatch.size());
            }
        } catch (Exception e) {
            logger.error("Erro ao inserir pesquisas no banco de dados: {}", e.getMessage());
            throw new RuntimeException("Erro ao inserir pesquisas no banco de dados: " + e.getMessage(), e);
        }
    }

    public void InserirAeroportosNoBanco(List<Aeroporto> aeroportos) {
        logger.info("Inserindo aeroportos no banco de dados. Total: {}", aeroportos.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Aeroporto (siglaAeroporto, classificacao) VALUES (?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (Aeroporto aeroporto : aeroportos) {
                Object[] parametros = {
                        aeroporto.getSiglaAeroporto(),
                        aeroporto.getClassificacao()
                };

                // Log dos dados que estão sendo inseridos para cada aeroporto
                logger.info("Inserindo: siglaAeroporto={}, classificacao={}",
                        aeroporto.getSiglaAeroporto(),
                        aeroporto.getClassificacao());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} aeroportos", batchSize);
                }
            }

            // Insere o último lote, se houver registros restantes
            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} aeroportos", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir aeroportos no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirPassageirosNoBanco(List<Passageiro> passageiros) {
        logger.info("Inserindo passageiros no banco de dados. Total: {}", passageiros.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Passageiro (Nacionalidade, Genero, Faixa_Etaria, Escolaridade, Renda_Familiar, Viajando_Sozinho, Numero_Acompanhantes, Motivo_Viagem, Quantidade_Viagens_Ultimos_12_Meses, Ja_Embarcou_Desembarcou_Antes, Antecedencia, Tempo_Espera, Comentarios_Adicionais) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> parametrosBatch = new ArrayList<>();
        for (Passageiro passageiro : passageiros) {
            // Log para verificar os dados do passageiro
            logger.info("Passageiro: Nacionalidade={}, Genero={}, Faixa_Etaria={}, Escolaridade={}, Renda_Familiar={}, Viajando_Sozinho={}, Numero_Acompanhantes={}, Motivo_Viagem={}, Quantidade_Viagens_Ultimos_12_Meses={}, Ja_Embarcou_Desembarcou_Antes={}, Antecedencia={}, Tempo_Espera={}, Comentarios_Adicionais={}",
                    passageiro.getNacionalidade(), passageiro.getGenero(), passageiro.getFaixaEtaria(), passageiro.getEscolaridade(),
                    passageiro.getRendaFamiliar(), passageiro.getViajandoSozinho(), passageiro.getNumeroAcompanhantes(),
                    passageiro.getMotivoViagem(), passageiro.getQuantidadeViagensUltimos12Meses(),
                    passageiro.getJaEmbarcouDesembarcouAntes(), passageiro.getAntecedencia(), passageiro.getTempoEspera(),
                    passageiro.getComentariosAdicionais());

            Object[] parametros = new Object[] {
                    passageiro.getNacionalidade(),
                    passageiro.getGenero(),
                    passageiro.getFaixaEtaria(),
                    passageiro.getEscolaridade(),
                    passageiro.getRendaFamiliar(),
                    passageiro.getViajandoSozinho(),
                    passageiro.getNumeroAcompanhantes(),
                    passageiro.getMotivoViagem(),
                    passageiro.getQuantidadeViagensUltimos12Meses(),
                    passageiro.getJaEmbarcouDesembarcouAntes(),
                    passageiro.getAntecedencia(),
                    passageiro.getTempoEspera(),
                    passageiro.getComentariosAdicionais()
            };
            parametrosBatch.add(parametros);
        }
        conec.batchUpdate(sql, parametrosBatch);
    }

    public void inserirInformacoesVooNoBanco(List<InformacoesVoo> informacoesVoos) {
        logger.info("Inserindo informações de voo no banco de dados. Total: {}", informacoesVoos.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Informacoes_Voo (Pesquisa_ID, Processo, Aeroporto, Terminal, Portao, Tipo_Voo, Cia_Aerea, Voo, Conexao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int batchSize = 2000; // Ajuste o tamanho do lote conforme necessário
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (InformacoesVoo informacaoVoo : informacoesVoos) {
                // Log detalhado com os valores de cada campo
                logger.info("Inserindo: Pesquisa_ID={}, Processo={}, Aeroporto={}, Terminal={}, Portao={}, TipoVoo={}, CiaAerea={}, Voo={}, Conexao={}",
                        informacaoVoo.getPesquisaID(),
                        informacaoVoo.getProcesso(),
                        informacaoVoo.getAeroporto(),
                        informacaoVoo.getTerminal(),
                        informacaoVoo.getPortao(),
                        informacaoVoo.getTipoVoo(),
                        informacaoVoo.getCiaAerea(),
                        informacaoVoo.getVoo(),
                        informacaoVoo.getConexao());

                Object[] parametros = {
                        informacaoVoo.getPesquisaID(),
                        informacaoVoo.getProcesso(),
                        informacaoVoo.getAeroporto(),
                        informacaoVoo.getTerminal(),
                        informacaoVoo.getPortao(),
                        informacaoVoo.getTipoVoo(),
                        informacaoVoo.getCiaAerea(),
                        informacaoVoo.getVoo(),
                        informacaoVoo.getConexao()
                };
                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch); // Insere lote de 500
                    parametrosBatch.clear(); // Limpa para próximo lote
                }
            }
            // Insere qualquer restante que não foi inserido no último lote
            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
            }
        } catch (Exception e) {
            logger.error("Erro ao inserir informações de voo no banco: {}", e.getMessage());
        }
    }

    public void InserirPassagemNoBanco(List<AquisicaoPassagem> aquisicoesPassagem) {
        logger.info("Inserindo aquisições de passagem no banco de dados. Total: {}", aquisicoesPassagem.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Aquisição_Passagem (Pesquisa_ID, Aquisição_Passagem, Meio_Aquisição_Passagem, Meio_Transporte_Aeroporto) VALUES (?, ?, ?, ?)";

        int batchSize = 2000; // Lote de 500 itens (ajuste conforme necessário)
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (AquisicaoPassagem aquisicao : aquisicoesPassagem) {
                Object[] parametros = {
                        aquisicao.getPesquisaID(), // Altere para usar getPesquisaID()
                        aquisicao.getAquisicaoPassagem(),
                        aquisicao.getMeioAquisicaoPassagem(),
                        aquisicao.getMeioTransporteAeroporto()
                };

                logger.info("Inserindo: Pesquisa_ID={}, Aquisicao_Passagem={}, Meio_Aquisicao_Passagem={}, Meio_Transporte_Aeroporto={}",
                        aquisicao.getPesquisaID(),
                        aquisicao.getAquisicaoPassagem(),
                        aquisicao.getMeioAquisicaoPassagem(),
                        aquisicao.getMeioTransporteAeroporto());
                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch); // Insere lote de 500
                    parametrosBatch.clear(); // Limpa para próximo lote
                    logger.info("Inserido lote de {} aquisições de passagem", batchSize);
                }
            }

            // Insere qualquer item restante que não completou o último lote
            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} aquisições de passagem", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir aquisições de passagem no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirNecessidadesEspeciaisNoBanco(List<NecessidadesEspeciais> necessidadesEspeciais) {
        logger.info("Inserindo necessidades especiais no banco de dados. Total: {}", necessidadesEspeciais.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String insertSql = "INSERT INTO Necessidades_Especiais (Pesquisa_ID, Possui_Deficiencia, Utiliza_Recurso_Assistivo, Solicitou_Assistencia_Especial) VALUES (?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (NecessidadesEspeciais necessidades : necessidadesEspeciais) {
                Object[] parametros = {
                        necessidades.getPesquisaID(),
                        necessidades.getPossuiDeficiencia(),
                        necessidades.getUtilizaRecursoAssistivo(),
                        necessidades.getSolicitouAssistenciaEspecial()
                };

                // Log dos dados que estão sendo inseridos
                logger.info("Inserindo: Pesquisa_ID={}, Possui_Deficiencia={}, Utiliza_Recurso_Assistivo={}, Solicitou_Assistencia_Especial={}",
                        necessidades.getPesquisaID(),
                        necessidades.getPossuiDeficiencia(),
                        necessidades.getUtilizaRecursoAssistivo(),
                        necessidades.getSolicitouAssistenciaEspecial());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(insertSql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} necessidades especiais", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(insertSql, parametrosBatch);
                logger.info("Inserido lote final de {} necessidades especiais", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir necessidades especiais no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirDesembarqueNoBanco(List<Desembarque> desembarques) {
        logger.info("Inserindo necessidades especiais no banco de dados. Total: {}", desembarques.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String insertSql = "INSERT INTO Desembarque (Pesquisa_ID, Forma_Desembarque, Avaliacao_Metodo_Desembarque, Utilizou_Estacionamento, Facilidade_Desembarque_Meio_Fio, Opcoes_Transporte_Aeroporto) VALUES (?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (Desembarque desembarque : desembarques) {
                Object[] parametros = {
                        desembarque.getPesquisaID(),
                        desembarque.getFormaDesembarque(),
                        desembarque.getAvaliacaoMetodoDesembarque(),
                        desembarque.getUtilizouEstacionamento(),
                        desembarque.getFacilidadeDesembarqueMeioFio(),
                        desembarque.getOpcoesTransporteAeroporto()
                };

                // Log dos dados que estão sendo inseridos
                logger.info("Inserindo: Pesquisa_ID={}, Forma_Desembarque={}, Avaliacao_Metodo_Desembarque={}, Utilizou_Estacionamento={}, Facilidade_Desembarque_Meio_Fio={}, Opcoes_Transporte_Aeroporto={}",
                        desembarque.getPesquisaID(),
                        desembarque.getFormaDesembarque(),
                        desembarque.getAvaliacaoMetodoDesembarque(),
                        desembarque.getUtilizouEstacionamento(),
                        desembarque.getFacilidadeDesembarqueMeioFio(),
                        desembarque.getOpcoesTransporteAeroporto());
                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(insertSql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} necessidades especiais", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(insertSql, parametrosBatch);
                logger.info("Inserido lote final de {} necessidades especiais", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir necessidades especiais no banco de dados: " + e.getMessage(), e);
        }
    }

    public void InserirCheckInBanco(List<CheckIn> checkIns) {
        logger.info("Inserindo check-ins no banco de dados. Total: {}", checkIns.size());
        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Check_in (Pesquisa_ID, Forma_Check_in, Processo_Check_in, Tempo_Espera_Fila, Organizacao_Filas, Quantidade_Totens_AA, Quantidade_Balcoes, Cordialidade_Funcionarios, Tempo_Atendimento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (CheckIn checkIn : checkIns) {
                Object[] parametros = {
                        checkIn.getPesquisaID(),
                        checkIn.getFormaCheckIn(),
                        checkIn.getProcessoCheckIn(),
                        checkIn.getTempoEsperaFila(),
                        checkIn.getOrganizacaoFilas(),
                        checkIn.getQuantidadeTotensAA(),
                        checkIn.getQuantidadeBalcoes(),
                        checkIn.getCordialidadeFuncionarios(),
                        checkIn.getTempoAtendimento()
                };

                // Log dos dados que estão sendo inseridos
                logger.info("Inserindo: Pesquisa_ID={}, Forma_Check_in={}, Processo_Check_in={}, Tempo_Espera_Fila={}, Organizacao_Filas={}, Quantidade_Totens_AA={}, Quantidade_Balcoes={}, Cordialidade_Funcionarios={}, Tempo_Atendimento={}",
                        checkIn.getPesquisaID(),
                        checkIn.getFormaCheckIn(),
                        checkIn.getProcessoCheckIn(),
                        checkIn.getTempoEsperaFila(),
                        checkIn.getOrganizacaoFilas(),
                        checkIn.getQuantidadeTotensAA(),
                        checkIn.getQuantidadeBalcoes(),
                        checkIn.getCordialidadeFuncionarios(),
                        checkIn.getTempoAtendimento());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de check-in", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de check-in", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir check-ins no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirInspecaoSegurancaNoBanco(List<InspecaoSeguranca> inspecaoSegurancas) {
        logger.info("Inserindo registros de inspeção de segurança no banco de dados. Total: {}", inspecaoSegurancas.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Inspecao_Seguranca (Pesquisa_ID, Processo_Inspecao_Seguranca, Tempo_Espera_Fila, Organizacao_Filas, Atendimento_Funcionarios) VALUES (?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (InspecaoSeguranca inspecao : inspecaoSegurancas) {
                Object[] parametros = {
                        inspecao.getPesquisaID(),
                        inspecao.getProcessoInspecaoSeguranca(),
                        inspecao.getTempoEsperaFila(),
                        inspecao.getOrganizacaoFilas(),
                        inspecao.getAtendimentoFuncionarios()
                };

                // Log dos dados que estão sendo inseridos
                logger.info("Inserindo: Pesquisa_ID={}, Processo_Inspecao_Seguranca={}, Tempo_Espera_Fila={}, Organizacao_Filas={}, Atendimento_Funcionarios={}",
                        inspecao.getPesquisaID(),
                        inspecao.getProcessoInspecaoSeguranca(),
                        inspecao.getTempoEsperaFila(),
                        inspecao.getOrganizacaoFilas(),
                        inspecao.getAtendimentoFuncionarios());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de inspeção de segurança", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de inspeção de segurança", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir inspeções de segurança no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirControleMigratorioAduaneiro(List<ControleMigratorioAduaneiro> controleMigratorioAduaneiros) {
        logger.info("Inserindo registros de controle migratório aduaneiro no banco de dados. Total: {}", controleMigratorioAduaneiros.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Controle_Migratorio_Aduaneiro (Pesquisa_ID, Controle_Migratorio, Tempo_Espera_Fila, Organizacao_Filas, Atendimento_Funcionarios, Quantidade_Guiches, Controle_Aduaneiro) VALUES (?, ?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (ControleMigratorioAduaneiro controle : controleMigratorioAduaneiros) {
                // Verifica se ControleAduaneiro é "NS/NR" e substitui por null
                Object controleAduaneiroValue = "NS/NR".equals(controle.getControleAduaneiro()) ? null : controle.getControleAduaneiro();

                Object[] parametros = {
                        controle.getPesquisaID(),
                        controle.getControleMigratorio(),
                        controle.getTempoEsperaFila(),
                        controle.getOrganizacaoFilas(),
                        controle.getAtendimentoFuncionarios(),
                        controle.getQuantidadeGuiches(),
                        controleAduaneiroValue
                };

                // Log detalhado de cada registro
                logger.info("Inserindo: Pesquisa_ID={}, Controle_Migratorio={}, Tempo_Espera_Fila={}, Organizacao_Filas={}, Atendimento_Funcionarios={}, Quantidade_Guiches={}, Controle_Aduaneiro={}",
                        controle.getPesquisaID(),
                        controle.getControleMigratorio(),
                        controle.getTempoEsperaFila(),
                        controle.getOrganizacaoFilas(),
                        controle.getAtendimentoFuncionarios(),
                        controle.getQuantidadeGuiches(),
                        controleAduaneiroValue);

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de controle migratório aduaneiro", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de controle migratório aduaneiro", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir controle migratório aduaneiro no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirEstabelecimentosNoBanco(List<Estabelecimentos> estabelecimentos) {
        logger.info("Inserindo registros de estabelecimentos no banco de dados. Total: {}", estabelecimentos.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Estabelecimentos (Pesquisa_ID, Estabelecimentos_Alimentacao, Quantidade_Estabelecimentos_Alimentacao, Qualidade_Variedade_Opcoes_Alimentacao, Relacao_Preco_Qualidade_Alimentacao, Estabelecimentos_Comerciais, Quantidade_Estabelecimentos_Comerciais, Qualidade_Variedade_Opcoes_Comerciais) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (Estabelecimentos estabelecimento : estabelecimentos) {
                Object[] parametros = {
                        estabelecimento.getPesquisaID(),
                        estabelecimento.getEstabelecimentosAlimentacao(),
                        estabelecimento.getQuantidadeEstabelecimentosAlimentacao(),
                        estabelecimento.getQualidadeVariedadeOpcoesAlimentacao(),
                        estabelecimento.getRelacaoPrecoQualidadeAlimentacao(),
                        estabelecimento.getEstabelecimentosComerciais(),
                        estabelecimento.getQuantidadeEstabelecimentosComerciais(),
                        estabelecimento.getQualidadeVariedadeOpcoesComerciais()
                };

                // Log detalhado de cada registro
                logger.info("Inserindo: Pesquisa_ID={}, Estabelecimentos_Alimentacao={}, Quantidade_Estabelecimentos_Alimentacao={}, Qualidade_Variedade_Opcoes_Alimentacao={}, Relacao_Preco_Qualidade_Alimentacao={}, Estabelecimentos_Comerciais={}, Quantidade_Estabelecimentos_Comerciais={}, Qualidade_Variedade_Opcoes_Comerciais={} ",
                        estabelecimento.getPesquisaID(),
                        estabelecimento.getEstabelecimentosAlimentacao(),
                        estabelecimento.getQuantidadeEstabelecimentosAlimentacao(),
                        estabelecimento.getQualidadeVariedadeOpcoesAlimentacao(),
                        estabelecimento.getRelacaoPrecoQualidadeAlimentacao(),
                        estabelecimento.getEstabelecimentosComerciais(),
                        estabelecimento.getQuantidadeEstabelecimentosComerciais(),
                        estabelecimento.getQualidadeVariedadeOpcoesComerciais());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de estabelecimentos", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de estabelecimentos", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir estabelecimentos no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirEstacionamentoNoBanco(List<Estacionamento> estacionamentos) {
        logger.info("Inserindo registros de estacionamento no banco de dados. Total: {}", estacionamentos.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Estacionamento (Pesquisa_ID, Qualidade_Instalacoes_Estacionamento, Facilidade_Encontrar_Vagas, Facilidade_Acesso_Terminal, Relacao_Preco_Qualidade) VALUES (?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (Estacionamento estacionamento : estacionamentos) {
                Object[] parametros = {
                        estacionamento.getPesquisaID(),
                        estacionamento.getQualidadeInstalacoesEstacionamento(),
                        estacionamento.getFacilidadeEncontrarVagas(),
                        estacionamento.getFacilidadeAcessoTerminal(),
                        estacionamento.getRelacaoCustoBeneficio()
                };

                // Log detalhado de cada registro
                logger.info("Inserindo: Pesquisa_ID={}, Qualidade_Instalacoes_Estacionamento={}, Facilidade_Encontrar_Vagas={}, Facilidade_Acesso_Terminal={}, Relacao_Custo_Beneficio={} ",
                        estacionamento.getPesquisaID(),
                        estacionamento.getQualidadeInstalacoesEstacionamento(),
                        estacionamento.getFacilidadeEncontrarVagas(),
                        estacionamento.getFacilidadeAcessoTerminal(),
                        estacionamento.getRelacaoCustoBeneficio());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de estacionamento", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de estacionamento", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir estacionamento no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirConfortoAcessibilidade(List<ConfortoAcessibilidade> confortoAcessibilidades) {
        logger.info("Inserindo registros de conforto e acessibilidade no banco de dados. Total: {}", confortoAcessibilidades.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Conforto_Acessibilidade (Pesquisa_ID, Localizacao_Deslocamento, Sinalizacao, Disponibilidade_Paineis_Informacoes_Voo, Acessibilidade_Terminal, Conforto_Sala_Embarque, Conforto_Termico, Conforto_Acustico, Disponibilidade_Assentos, Disponibilidade_Assentos_Reservados, Disponibilidade_Tomadas, Internet_Disponibilizada_Aeroporto, Velocidade_Conexao, Facilidade_Acesso_Rede) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (ConfortoAcessibilidade conforto : confortoAcessibilidades) {
                Object[] parametros = {
                        conforto.getPesquisaID(),
                        conforto.getLocalizacaoDeslocamento(),
                        conforto.getSinalizacao(),
                        conforto.getDisponibilidadePaineisInformacoesVoo(),
                        conforto.getAcessibilidadeTerminal(),
                        conforto.getConfortoSalaEmbarque(),
                        conforto.getConfortoTermico(),
                        conforto.getConfortoAcustico(),
                        conforto.getDisponibilidadeAssentos(),
                        conforto.getDisponibilidadeAssentosReservados(),
                        conforto.getDisponibilidadeTomadas(),
                        conforto.getInternetDisponibilizadaAeroporto(),
                        conforto.getVelocidadeConexao(),
                        conforto.getFacilidadeAcessoRede()
                };

                // Log detalhado de cada registro
                logger.info("Inserindo: Pesquisa_ID={}, Localizacao_Deslocamento={}, Sinalizacao={}, Disponibilidade_Paineis_Informacoes_Voo={}, Acessibilidade_Terminal={}, Conforto_Sala_Embarque={}, Conforto_Termico={}, Conforto_Acustico={}, Disponibilidade_Assentos={}, Disponibilidade_Assentos_Reservados={}, Disponibilidade_Tomadas={}, Internet_Disponibilizada_Aeroporto={}, Velocidade_Conexao={}, Facilidade_Acesso_Rede={}",
                        conforto.getPesquisaID(),
                        conforto.getLocalizacaoDeslocamento(),
                        conforto.getSinalizacao(),
                        conforto.getDisponibilidadePaineisInformacoesVoo(),
                        conforto.getAcessibilidadeTerminal(),
                        conforto.getConfortoSalaEmbarque(),
                        conforto.getConfortoTermico(),
                        conforto.getConfortoAcustico(),
                        conforto.getDisponibilidadeAssentos(),
                        conforto.getDisponibilidadeAssentosReservados(),
                        conforto.getDisponibilidadeTomadas(),
                        conforto.getInternetDisponibilizadaAeroporto(),
                        conforto.getVelocidadeConexao(),
                        conforto.getFacilidadeAcessoRede());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de conforto e acessibilidade", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de conforto e acessibilidade", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir conforto e acessibilidade no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirDadosSanitarios(List<Sanitarios> sanitarios) {
        logger.info("Inserindo registros de sanitários no banco de dados. Total: {}", sanitarios.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Sanitarios (Pesquisa_ID, sanitariosQt, Quantidade_Banheiros, Limpeza_Banheiros, Manutencao_Geral_Sanitarios, Limpeza_Geral_Aeroporto) VALUES (?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (Sanitarios sanitario : sanitarios) {
                Object[] parametros = {
                        sanitario.getPesquisaID(),
                        sanitario.getSanitarios(),
                        sanitario.getQuantidadeBanheiros(),
                        sanitario.getLimpezaBanheiros(),
                        sanitario.getManutencaoGeralSanitarios(),
                        sanitario.getLimpezaGeralAeroporto()
                };

                // Log detalhado de cada registro
                logger.info("Inserindo: Pesquisa_ID={}, sanitariosQt={}, Quantidade_Banheiros={}, Limpeza_Banheiros={}, Manutencao_Geral_Sanitarios={}, Limpeza_Geral_Aeroporto={}",
                        sanitario.getPesquisaID(),
                        sanitario.getSanitarios(),
                        sanitario.getQuantidadeBanheiros(),
                        sanitario.getLimpezaBanheiros(),
                        sanitario.getManutencaoGeralSanitarios(),
                        sanitario.getLimpezaGeralAeroporto());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de sanitários", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de sanitários", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir dados de sanitários no banco de dados: " + e.getMessage(), e);
        }
    }

    public void inserirDadosBagagens(List<RestituicaoBagagens> restituicaoBagagens) {
        logger.info("Inserindo registros de restituição de bagagens no banco de dados. Total: {}", restituicaoBagagens.size());

        ConexBanco conectar = new ConexBanco();
        JdbcTemplate conec = conectar.getConexaoBanco();

        String sql = "INSERT INTO Restituicao_Bagagens (Pesquisa_ID, Processo_Restituicao_Bagagens, Facilidade_Identificacao_Esteira, Tempo_Restituicao, Integridade_Bagagem, Atendimento_Cia_Aerea) VALUES (?, ?, ?, ?, ?, ?)";

        int batchSize = 2000;
        List<Object[]> parametrosBatch = new ArrayList<>();
        int count = 0;

        try {
            for (RestituicaoBagagens bagagem : restituicaoBagagens) {
                Object[] parametros = {
                        bagagem.getPesquisaID(),
                        bagagem.getProcessoRestituicaoBagagens(),
                        bagagem.getFacilidadeIdentificacaoEsteira(),
                        bagagem.getTempoRestituicao(),
                        bagagem.getIntegridadeBagagem(),
                        bagagem.getAtendimentoCiaAerea()
                };

                // Log detalhado de cada registro
                logger.info("Inserindo: Pesquisa_ID={}, Processo_Restituicao_Bagagens={}, Facilidade_Identificacao_Esteira={}, Tempo_Restituicao={}, Integridade_Bagagem={}, Atendimento_Cia_Aerea={}",
                        bagagem.getPesquisaID(),
                        bagagem.getProcessoRestituicaoBagagens(),
                        bagagem.getFacilidadeIdentificacaoEsteira(),
                        bagagem.getTempoRestituicao(),
                        bagagem.getIntegridadeBagagem(),
                        bagagem.getAtendimentoCiaAerea());

                parametrosBatch.add(parametros);

                if (++count % batchSize == 0) {
                    conec.batchUpdate(sql, parametrosBatch);
                    parametrosBatch.clear();
                    logger.info("Inserido lote de {} registros de restituição de bagagens", batchSize);
                }
            }

            if (!parametrosBatch.isEmpty()) {
                conec.batchUpdate(sql, parametrosBatch);
                logger.info("Inserido lote final de {} registros de restituição de bagagens", parametrosBatch.size());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir dados de restituição de bagagens no banco de dados: " + e.getMessage(), e);
        }
    }
}