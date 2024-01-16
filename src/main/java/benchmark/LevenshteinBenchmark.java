package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@State(Scope.Benchmark)
public class LevenshteinBenchmark {

    private ParametricDFA parametricDfaDistance1NoTranspose;
    private ParametricDFA parametricDfaDistance2NoTranspose;
    private ParametricDFA parametricDfaDistance3NoTranspose;
    private ParametricDFA parametricDfaDistance4NoTranspose;
    private ParametricDFA parametricDfaDistance1WithTouzet;
    private ParametricDFA parametricDfaDistance2WithTouzet;
    private ParametricDFA parametricDfaDistance3WithTouzet;
    private ParametricDFA parametricDfaDistance4WithTouzet;

    private ParametricDFA parametricDfaDistance1WithHamming;
    private ParametricDFA parametricDfaDistance2WithHamming;
    private ParametricDFA parametricDfaDistance3WithHamming;
    private ParametricDFA parametricDfaDistance4WithHamming;
    private ParametricDFA parametricDfaDistance1WithTranspose;
    private ParametricDFA parametricDfaDistance2WithTranspose;
    private ParametricDFA parametricDfaDistance3WithTranspose;
    private ParametricDFA parametricDfaDistance4WithTranspose;

    private static int i = 15;
    private static String benchmarkName = "bar";
    private MemoryMXBean memoryBean;
    private long beforeMemory;
    private long afterMemory;
    private String currentBenchmarkMethod;

    private PrintWriter memoryOutputWriter;
    private String memoryOutputFile = "memory_usage_results_" + benchmarkName + "_" + i + ".txt";
    @Setup(Level.Iteration)
    public void setup() {
        parametricDfaDistance1NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(1, false , false, false));
        parametricDfaDistance2NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(2, false, false, false));
        parametricDfaDistance3NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(3, false, false, false));
        parametricDfaDistance4NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(4, false, false, false));

        parametricDfaDistance1WithTouzet = ParametricDFA.fromNFA(new LevenshteinNFA(1, false , true, false));
        parametricDfaDistance2WithTouzet = ParametricDFA.fromNFA(new LevenshteinNFA(2, false , true, false));
        parametricDfaDistance3WithTouzet = ParametricDFA.fromNFA(new LevenshteinNFA(3, false , true, false));
        parametricDfaDistance4WithTouzet = ParametricDFA.fromNFA(new LevenshteinNFA(4, false , true, false));

        parametricDfaDistance1WithHamming = ParametricDFA.fromNFA(new LevenshteinNFA(1, false , false, true));
        parametricDfaDistance2WithHamming = ParametricDFA.fromNFA(new LevenshteinNFA(2, false , false, true));
        parametricDfaDistance3WithHamming = ParametricDFA.fromNFA(new LevenshteinNFA(3, false , false, true));
        parametricDfaDistance4WithHamming = ParametricDFA.fromNFA(new LevenshteinNFA(4, false , false, true));

        parametricDfaDistance1WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(1, true, false, false));
        parametricDfaDistance2WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(2, true, false, false));
        parametricDfaDistance3WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(3, true, false, false));
        parametricDfaDistance4WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(4, true, false, false));

        try {
            // Apri il file in modalità append
            memoryOutputWriter = new PrintWriter(new FileWriter(memoryOutputFile, true));
        } catch (IOException e) {
            throw new RuntimeException("Could not open memory output file", e);
        }
        memoryBean = ManagementFactory.getMemoryMXBean();
        beforeMemory = memoryBean.getHeapMemoryUsage().getUsed();

    }
    private void setCurrentBenchmarkMethod(String methodName) {
        currentBenchmarkMethod = methodName;
    }
    @Benchmark
    public void benchBuildDfaDistance1NoTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance1NoTranspose");
        DFA dfa = parametricDfaDistance1NoTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance2NoTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance2NoTranspose");
        DFA dfa = parametricDfaDistance2NoTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance3NoTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance3NoTranspose");
        DFA dfa = parametricDfaDistance3NoTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance4NoTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance4NoTranspose");
        DFA dfa = parametricDfaDistance4NoTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance1WithTouzet() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance1WithTouzet");
        DFA dfa = parametricDfaDistance1WithTouzet.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance2WithTouzet() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance2WithTouzet");
        DFA dfa = parametricDfaDistance2WithTouzet.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance3WithTouzet() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance3WithTouzet");
        DFA dfa = parametricDfaDistance3WithTouzet.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance4WithTouzet() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance4WithTouzet");
        DFA dfa = parametricDfaDistance4WithTouzet.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance1WithHamming() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance1WithHamming");
        DFA dfa = parametricDfaDistance1WithHamming.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance2WithHamming() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance2WithHamming");
        DFA dfa = parametricDfaDistance2WithHamming.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance3WithHamming() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance3WithHamming");
        DFA dfa = parametricDfaDistance3WithHamming.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance4WithHamming() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance4WithHamming");
        DFA dfa = parametricDfaDistance4WithHamming.buildDFA("bar", false);
    }
    @Benchmark
    public void benchBuildDfaDistance1WithTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance1WithTranspose");
        DFA dfa = parametricDfaDistance1WithTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance2WithTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance2WithTranspose");
        DFA dfa = parametricDfaDistance2WithTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance3WithTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance3WithTranspose");
        DFA dfa = parametricDfaDistance3WithTranspose.buildDFA("bar", false);
    }

    @Benchmark
    public void benchBuildDfaDistance4WithTranspose() {
        setCurrentBenchmarkMethod("benchBuildDfaDistance4WithTranspose");
        DFA dfa = parametricDfaDistance4WithTranspose.buildDFA("bar", false);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        afterMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long memoryUsed = afterMemory - beforeMemory;
        // Scrivi l'output nel file di memoria separato
        memoryOutputWriter.println(currentBenchmarkMethod + " - Memory used: " + memoryUsed + " bytes");
        memoryOutputWriter.flush(); // Assicurati che l'output venga scritto nel file
    }
    @TearDown(Level.Trial)
    public void tearDownTrial() {
        // Chiudi il PrintWriter alla fine del trial
        if (memoryOutputWriter != null) {
            memoryOutputWriter.close();
        }

        System.err.println("Testing concluso di " +  currentBenchmarkMethod);
    }
    // Metodo main per eseguire i benchmark
    public static void main(String[] args) throws Exception {
        System.err.println("Testing Iniziato");
        Options opt = new OptionsBuilder()
                .include(LevenshteinBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .output("Testing/benchmark_results_" + benchmarkName + "_" + i + ".txt")
                .forks(1)
                .build();

        new Runner(opt).run();
        BenchmarkDataToExcel.main(args);
    }
}
