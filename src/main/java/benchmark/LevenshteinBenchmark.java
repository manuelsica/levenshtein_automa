package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
    private ParametricDFA parametricDfaDistance1WithTranspose;
    private ParametricDFA parametricDfaDistance2WithTranspose;
    private ParametricDFA parametricDfaDistance3WithTranspose;
    private ParametricDFA parametricDfaDistance4WithTranspose;

    private MemoryMXBean memoryBean;
    private long beforeMemory;
    private long afterMemory;
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

        parametricDfaDistance1WithHamming = ParametricDFA.fromNFA(new LevenshteinNFA(3, false , false, true));

        parametricDfaDistance1WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(1, true, false, false));
        parametricDfaDistance2WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(2, true, false, false));
        parametricDfaDistance3WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(3, true, false, false));
        parametricDfaDistance4WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(4, true, false, false));

        memoryBean = ManagementFactory.getMemoryMXBean();
        beforeMemory = memoryBean.getHeapMemoryUsage().getUsed();

    }

    @Benchmark
    public void benchBuildDfaDistance1NoTranspose() {
        DFA dfa = parametricDfaDistance1NoTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance2NoTranspose() {
        DFA dfa = parametricDfaDistance2NoTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance3NoTranspose() {
        DFA dfa = parametricDfaDistance3NoTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance4NoTranspose() {
        DFA dfa = parametricDfaDistance4NoTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance1WithTouzet() {
        DFA dfa = parametricDfaDistance1WithTouzet.buildDFA("Levenshtein", false);
    }
    @Benchmark
    public void benchBuildDfaDistance2WithTouzet() {
        DFA dfa = parametricDfaDistance2WithTouzet.buildDFA("Levenshtein", false);
    }
    @Benchmark
    public void benchBuildDfaDistance3WithTouzet() {
        DFA dfa = parametricDfaDistance3WithTouzet.buildDFA("Levenshtein", false);
    }
    @Benchmark
    public void benchBuildDfaDistance4WithTouzet() {
        DFA dfa = parametricDfaDistance4WithTouzet.buildDFA("Levenshtein", false);
    }
    @Benchmark
    public void benchBuildDfaDistance1WithHamming() {
        DFA dfa = parametricDfaDistance1WithHamming.buildDFA("Levenshtein", false);
    }
    @Benchmark
    public void benchBuildDfaDistance1WithTranspose() {
        DFA dfa = parametricDfaDistance1WithTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance2WithTranspose() {
        DFA dfa = parametricDfaDistance2WithTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance3WithTranspose() {
        DFA dfa = parametricDfaDistance3WithTranspose.buildDFA("Levenshtein", false);
    }

    @Benchmark
    public void benchBuildDfaDistance4WithTranspose() {
        DFA dfa = parametricDfaDistance4WithTranspose.buildDFA("Levenshtein", false);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        afterMemory = memoryBean.getHeapMemoryUsage().getUsed();
        System.out.println("Memory used: " + (afterMemory - beforeMemory));
    }

    // Metodo main per eseguire i benchmark
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(LevenshteinBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .output("benchmark_results.txt")
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
