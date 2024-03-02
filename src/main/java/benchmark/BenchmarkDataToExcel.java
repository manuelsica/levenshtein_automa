package benchmark;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class BenchmarkDataToExcel {
    public static void main(String[] args) {
        String benchmarkName = "levenshtein";
        int numFile = 30;
        Map<String, List<Double>> memoryUsageByBenchmark = new HashMap<>();
        for (int i = 1; i <= numFile; i++) {
            String inputFilePath = "benchmark_results_" + benchmarkName + "_" + i + ".txt";
            String memoryUsageFilePath = "memory_usage_results_" + benchmarkName + "_" + i + ".txt";
            String outputFilePath = "benchmark_results_" + benchmarkName + "_" + i + "_output.xlsx";
            File inputFile = new File(inputFilePath);
            if (!inputFile.exists()) {
                inputFile = new File("Testing/" + inputFilePath);
            }
            File memoryFile = new File(memoryUsageFilePath);
            if (!memoryFile.exists()) {
                memoryFile = new File("Testing/" + memoryUsageFilePath);
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedReader memoryReader = new BufferedReader(new FileReader(memoryFile));
                 XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
                // Crea il foglio di lavoro per i dati di benchmark
                Sheet benchmarkSheet = workbook.createSheet("Benchmark Data");
                int rowNum = 0;
                int shiftAmount = 1; // Modifica a seconda del numero di colonne prefissate da escludere
                // Crea l'intestazione della tabella
                String[] headers = {"Benchmark", "GProfile", "Mode", "Cnt", "Score", "Error", "Units"};
                Row headerRow = benchmarkSheet.createRow(rowNum++);
                for (int j = 0; j < headers.length; j++) {
                    headerRow.createCell(j).setCellValue(headers[j]);
                }
                // Pattern per estrarre i dati di benchmark
                Pattern benchmarkPattern = Pattern.compile("^(.*?)(:·gc.*?)?\\s+(\\w+)\\s+(\\d+)\\s+([\\d,.]+)\\s±\\s+([\\d,.]+)\\s+(\\w+/s)$");
                Pattern newBenchmarkPattern = Pattern.compile("^(.*?):·(\\S+)\\s+(\\w+)\\s+(\\d+)\\s+([\\d,.]+)\\s±\\s+([\\d,.]+)\\s+(\\w+/\\w+\\s*\\w*)$");

                String line;
                while ((line = reader.readLine()) != null) {
                    //System.out.println("Line read: " + line); // Stampa la riga letta
                    Matcher oldMatcher = benchmarkPattern.matcher(line);
                    Matcher newMatcher = newBenchmarkPattern.matcher(line);
                    if (oldMatcher.matches()) {
                        Row row = benchmarkSheet.createRow(rowNum++);
                        String benchmark = oldMatcher.group(1); // Nome del benchmark dalla prima cattura
                        row.createCell(0).setCellValue(benchmark); // Inserisci il nome del benchmark nella colonna "Benchmark"
                        row.createCell(1).setCellValue(""); // Colonna "GProfile" vuota

                        // Aggiungi i dati nelle colonne successive (shiftati a sinistra)
                        for (int j = 2; j <= oldMatcher.groupCount(); j++) {
                            String group = oldMatcher.group(j);
                            //System.out.println("Group captured: " + group); // Stampa il gruppo catturato
                            row.createCell(j - shiftAmount).setCellValue(group);
                        }
                    } else if (newMatcher.matches()) {
                        // Gestisci i nuovi dati di benchmark
                        Row row = benchmarkSheet.createRow(rowNum++);
                        String benchmark = newMatcher.group(1); // Nome del benchmark dalla prima cattura
                        row.createCell(0).setCellValue(benchmark); // Inserisci il nome del benchmark nella colonna "Benchmark"
                        row.createCell(1).setCellValue(""); // Colonna "GProfile" vuota
                        // Aggiungi i dati nelle colonne successive (shiftati a sinistra)
                        for (int j = 2; j <= newMatcher.groupCount(); j++) {
                            String group = newMatcher.group(j);
                            //System.out.println("Group captured: " + group); // Stampa il gruppo catturato
                            row.createCell(j - shiftAmount).setCellValue(group);
                        }
                    }
                }

                // Crea il foglio di lavoro per i dati di utilizzo della memoria
                Sheet memorySheet = workbook.createSheet("Memory Usage Data");
                rowNum = 0;
                // Crea l'intestazione della tabella
                headers = new String[]{"Benchmark", "Memory Used (bytes)"};
                headerRow = memorySheet.createRow(rowNum++);
                for (int j = 0; j < headers.length; j++) {
                    headerRow.createCell(j).setCellValue(headers[j]);
                }
                // Pattern per estrarre i dati di utilizzo della memoria
                Pattern memoryPattern = Pattern.compile("^(.*) - Memory used: (-?\\d+) bytes$"); // Modifica qui
                while ((line = memoryReader.readLine()) != null) {
                    //System.out.println("Line read: " + line); // Stampa la riga letta
                    Matcher matcher = memoryPattern.matcher(line);
                    if (matcher.matches()) {
                        Row row = memorySheet.createRow(rowNum++);
                        for (int j = 0; j < matcher.groupCount(); j++) {
                            String group = matcher.group(j + 1);
                            //System.out.println("Group captured: " + group); // Stampa il gruppo catturato
                            row.createCell(j).setCellValue(group);
                        }
                        String benchmark = matcher.group(1);
                        double memoryUsed = Double.parseDouble(matcher.group(2));
                        if (!memoryUsageByBenchmark.containsKey(benchmark)) {
                            memoryUsageByBenchmark.put(benchmark, new ArrayList<>());
                        }
                        memoryUsageByBenchmark.get(benchmark).add(memoryUsed);
                    }
                }
                // Rimuovi il vecchio foglio "Average Memory Usage" se esiste
                int index = workbook.getSheetIndex("Memory Usage Data");
                if (index != -1) {
                    workbook.removeSheetAt(index);
                }
                // Crea un nuovo foglio "Average Memory Usage"
                Sheet averageSheet = workbook.createSheet("Average Memory Usage");
                headerRow = averageSheet.createRow(0);
                headerRow.createCell(0).setCellValue("Benchmark");
                headerRow.createCell(1).setCellValue("Average Memory Used (bytes)");
                rowNum = 1;
                for (Map.Entry<String, List<Double>> entry : memoryUsageByBenchmark.entrySet()) {
                    String benchmark = entry.getKey();
                    List<Double> memoryUsages = entry.getValue();
                    double sum = 0;
                    for (double memoryUsed : memoryUsages) {
                        sum += memoryUsed;
                    }
                    double average = sum / memoryUsages.size();

                    Row row = averageSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(benchmark);
                    row.createCell(1).setCellValue(average);
                }
                // Calcola la media per ogni categoria di benchmark
                Map<String, Double> averageMemoryUsageByBenchmark = new HashMap<>();
                for (Map.Entry<String, List<Double>> entry : memoryUsageByBenchmark.entrySet()) {
                    String benchmark = entry.getKey();
                    List<Double> memoryUsages = entry.getValue();

                    double sum = 0;
                    for (double memoryUsed : memoryUsages) {
                        sum += memoryUsed;
                    }
                    double average = sum / memoryUsages.size();

                    averageMemoryUsageByBenchmark.put(benchmark, average);
                }
                // Crea una TreeMap per ordinare i dati
                TreeMap<String, Double> sortedMemoryUsageByBenchmark = new TreeMap<>(new Comparator<String>() {
                    @Override
                    public int compare(String benchmark1, String benchmark2) {
                        double average1 = averageMemoryUsageByBenchmark.get(benchmark1);
                        double average2 = averageMemoryUsageByBenchmark.get(benchmark2);
                        return Double.compare(average1, average2);
                    }
                });
                // Aggiungi i dati al TreeMap
                sortedMemoryUsageByBenchmark.putAll(averageMemoryUsageByBenchmark);
                // Rimuovi il vecchio foglio "Average Memory Usage" se esiste
                int sheetIndex = workbook.getSheetIndex("Average Memory Usage");
                if (sheetIndex != -1) {
                    workbook.removeSheetAt(sheetIndex);
                }
                // Crea un nuovo foglio "Average Memory Usage"
                Sheet newAverageSheet = workbook.createSheet("Average Memory Usage");
                Row newHeaderRow = newAverageSheet.createRow(0);
                newHeaderRow.createCell(0).setCellValue("Benchmark");
                newHeaderRow.createCell(1).setCellValue("Average Memory Used (bytes)");
                int newRowNum = 1;
                for (Map.Entry<String, Double> entry : sortedMemoryUsageByBenchmark.entrySet()) {
                    String benchmark = entry.getKey();
                    double average = entry.getValue();
                    Row row = newAverageSheet.createRow(newRowNum++);
                    row.createCell(0).setCellValue(benchmark);
                    row.createCell(1).setCellValue(average);
                }
                //Set larghezza colonne
                for (int j = 0; j < headers.length; j++) {
                    benchmarkSheet.autoSizeColumn(j);
                    newAverageSheet.autoSizeColumn(j);
                }
                for (int j = 0; j < headers.length; j++) {
                    memorySheet.autoSizeColumn(j);
                }

                for (int j = 0; j < 2; j++) {
                    averageSheet.autoSizeColumn(j);
                }
                // Scrivi il file Excel
                workbook.write(fileOut);
                System.err.println("Dati esportati con successo in " + outputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Sposta i file di input e output nelle rispettive cartelle
            try {
                // Sposta il file di input nella cartella "Testing"
                Path sourcePath = Paths.get(inputFile.getPath());
                Path targetPath = Paths.get("Testing/" + sourcePath.getFileName());
                Files.createDirectories(targetPath.getParent()); // Crea la directory se non esiste
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                //System.out.println("File di input spostato con successo in " + targetPath);
                // Sposta il file di output nella cartella "Testing/Risultati"
                sourcePath = Paths.get(outputFilePath);
                targetPath = Paths.get("Testing/Risultati/" + sourcePath.getFileName());
                Files.createDirectories(targetPath.getParent()); // Crea la directory se non esiste
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                //System.out.println("File di output spostato con successo in " + targetPath);
                // Sposta il file di utilizzo della memoria nella cartella "Testing"
                sourcePath = Paths.get(memoryFile.getPath());
                targetPath = Paths.get("Testing/" + sourcePath.getFileName());
                Files.createDirectories(targetPath.getParent()); // Crea la directory se non esiste
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                //System.out.println("File di utilizzo della memoria spostato con successo in " + targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ExtractOptimal.main(args);
    }
}



