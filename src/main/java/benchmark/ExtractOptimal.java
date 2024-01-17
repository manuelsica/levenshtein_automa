package benchmark;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExtractOptimal {
    static String benchmarkName = "bar";
    static int i = 30;
    public static void main(String[] args) {
        for (int count = 1; count <= i; count++) {
            try {
                String filePath = "Testing/Risultati/benchmark_results_" + benchmarkName + "_" + count + "_output.xlsx";
                FileInputStream excelFile = new FileInputStream(new File(filePath));
                Workbook workbook = new XSSFWorkbook(excelFile);
                Sheet datatypeSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = datatypeSheet.iterator();
                double minScoreNorm = Double.MAX_VALUE;
                double maxScoreNorm = Double.MIN_VALUE;
                double minScoreNull = Double.MAX_VALUE;
                double maxScoreNull = Double.MIN_VALUE;
                String bestBenchmarkNorm = "";
                String worstBenchmarkNorm = "";
                String bestBenchmarkNull = "";
                String worstBenchmarkNull = "";
                Row bestRowNorm = null;
                Row worstRowNorm = null;
                Row bestRowNull = null;
                Row worstRowNull = null;
                if (iterator.hasNext()) {
                    iterator.next();
                }
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    Cell benchmarkCell = currentRow.getCell(0); // Benchmark è nella prima colonna
                    Cell gProfileCell = currentRow.getCell(1); // GProfile è nella seconda colonna
                    Cell scoreCell = currentRow.getCell(4); // Score è nella quinta colonna
                    String scoreString = scoreCell.getStringCellValue();
                    scoreString = scoreString.replace(',', '.'); // Sostituisci la virgola con un punto
                    double score = Double.parseDouble(scoreString);
                    if (gProfileCell.getStringCellValue().equals("gc.alloc.rate.norm")) {
                        if (score < minScoreNorm) {
                            minScoreNorm = score;
                            bestBenchmarkNorm = benchmarkCell.getStringCellValue();
                            bestRowNorm = currentRow;
                        }
                        if (score > maxScoreNorm) {
                            maxScoreNorm = score;
                            worstBenchmarkNorm = benchmarkCell.getStringCellValue();
                            worstRowNorm = currentRow;
                        }
                    } else if (gProfileCell.getStringCellValue().equals("")) {
                        if (score < minScoreNull) {
                            minScoreNull = score;
                            bestBenchmarkNull = benchmarkCell.getStringCellValue();
                            bestRowNull = currentRow;
                        }
                        if (score > maxScoreNull) {
                            maxScoreNull = score;
                            worstBenchmarkNull = benchmarkCell.getStringCellValue();
                            worstRowNull = currentRow;
                        }
                    }
                }
                // Crea uno stile di cella con sfondo verde
                CellStyle bestStyle = workbook.createCellStyle();
                bestStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                bestStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                // Crea uno stile di cella con sfondo rosso
                CellStyle worstStyle = workbook.createCellStyle();
                worstStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                worstStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                // Applica lo stile a tutte le celle nelle righe migliori e peggiori
                if (bestRowNorm != null) {
                    for (Cell cell : bestRowNorm) {
                        cell.setCellStyle(bestStyle);
                    }
                }
                if (worstRowNorm != null) {
                    for (Cell cell : worstRowNorm) {
                        cell.setCellStyle(worstStyle);
                    }
                }
                if (bestRowNull != null) {
                    for (Cell cell : bestRowNull) {
                        cell.setCellStyle(bestStyle);
                    }
                }
                if (worstRowNull != null) {
                    for (Cell cell : worstRowNull) {
                        cell.setCellStyle(worstStyle);
                    }
                }
                // Salva il file
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                }
                //System.out.println("Nel file benchmark_results_" + benchmarkName + "_" + count + "_output per 'gc.alloc.rate.norm', il miglior benchmark '" + bestBenchmarkNorm + "' ha un punteggio di: " + minScoreNorm);
                //System.out.println("Nel file benchmark_results_" + benchmarkName + "_" + count + "_output per 'gc.alloc.rate.normPer, il peggiore benchmark '" + worstBenchmarkNorm + "' ha un punteggio di: " + maxScoreNorm);
                //System.out.println("Nel file benchmark_results_" + benchmarkName + "_" + count + "_output il miglior benchmark '" + bestBenchmarkNull + "' ha un punteggio di: " + minScoreNull);
                //System.out.println("Nel file benchmark_results_" + benchmarkName + "_" + count + "_output il peggiore benchmark '" + worstBenchmarkNull + "' ha un punteggio di: " + maxScoreNull);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.err.println("Ottimo estratto");
    }
}
