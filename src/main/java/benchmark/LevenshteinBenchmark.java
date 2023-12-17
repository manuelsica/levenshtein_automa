package benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class LevenshteinBenchmark {

    private ParametricDFA parametricDfaDistance1NoTranspose;
    private ParametricDFA parametricDfaDistance2NoTranspose;
    private ParametricDFA parametricDfaDistance3NoTranspose;
    private ParametricDFA parametricDfaDistance4NoTranspose;
    private ParametricDFA parametricDfaDistance1WithTranspose;
    private ParametricDFA parametricDfaDistance2WithTranspose;
    private ParametricDFA parametricDfaDistance3WithTranspose;

    private ParametricDFA parametricDfaDistance4WithTranspose;

    @Setup
    public void setup() {
        parametricDfaDistance1NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(1, false));
        parametricDfaDistance2NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(2, false));
        parametricDfaDistance3NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(3, false));
        parametricDfaDistance4NoTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(4, false));
        parametricDfaDistance1WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(1, true));
        parametricDfaDistance2WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(2, true));
        parametricDfaDistance3WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(3, true));
        parametricDfaDistance4WithTranspose = ParametricDFA.fromNFA(new LevenshteinNFA(4, true));
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
        DFA dfa = parametricDfaDistance3WithTranspose.buildDFA("Levenshtein", false);
    }

    // Metodo main per eseguire i benchmark
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(LevenshteinBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
