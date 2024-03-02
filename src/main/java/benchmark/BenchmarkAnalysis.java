package benchmark;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BenchmarkAnalysis {

    public static void main(String[] args) {
        String zipFilePath = "Testing/Risultati/Stress Test/Risultati_stress_test_levenshtein.zip";
        Map<String, Map<String, List<Double>>> benchmarkScores = new HashMap<>();
        try {
            processZipFile(zipFilePath, benchmarkScores);
            printAverages(benchmarkScores);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("Media calcolata");
    }

    private static void processZipFile(String zipFilePath, Map<String, Map<String, List<Double>>> benchmarkScores) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".xlsx")) {
                    processExcelFile(zis, benchmarkScores);
                    zis.closeEntry();
                }
            }
        }
    }

    private static void processExcelFile(ZipInputStream zis, Map<String, Map<String, List<Double>>> benchmarkScores) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zis.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))){

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell benchmarkCell = row.getCell(0);
                Cell gProfileCell = row.getCell(1);
                Cell scoreCell = row.getCell(4);

                if (benchmarkCell != null && scoreCell != null && benchmarkCell.getCellType() == CellType.STRING) {
                    String benchmark = benchmarkCell.getStringCellValue();
                    String gProfile = (gProfileCell != null && gProfileCell.getCellType() == CellType.STRING) ? gProfileCell.getStringCellValue() : "NULL";
                    Double score = null;

                    if (scoreCell.getCellType() == CellType.NUMERIC) {
                        score = scoreCell.getNumericCellValue();
                    } else if (scoreCell.getCellType() == CellType.STRING) {
                        try {
                            score = Double.parseDouble(scoreCell.getStringCellValue().replace(",", "."));
                        } catch (NumberFormatException e) {
                            System.err.println("Cannot parse score as number: " + scoreCell.getStringCellValue());
                        }
                    }

                    if (score != null) {
                        benchmarkScores
                                .computeIfAbsent(benchmark, k -> new HashMap<>())
                                .computeIfAbsent(gProfile, k -> new ArrayList<>())
                                .add(score);
                    }
                }
            }
        }
    }

    private static void printAverages(Map<String, Map<String, List<Double>>> benchmarkScores) throws IOException {
        String outputFilePath = "Testing/Risultati/Stress Test/averages.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            benchmarkScores.forEach((benchmark, gProfileScores) -> {
                gProfileScores.forEach((gProfile, scores) -> {
                    double average = scores.stream().mapToDouble(d -> d).average().orElse(Double.NaN);
                    String outputLine = "Benchmark: " + benchmark + ", GProfile: " + gProfile + ", Average Score: " + average + "\n";
                    try {
                        writer.write(outputLine);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }
}
