
/**
 * Costruttore di stato per DFA che opera su stringhe codificate in UTF-8.
 */
public class Utf8DFAStateBuilder {
    private Utf8DFABuilder dfaBuilder;
    private int stateId;
    public Utf8DFAStateBuilder(Utf8DFABuilder dfaBuilder, int stateId, int[] defaultSuccessor) {
        this.dfaBuilder = dfaBuilder;
        this.stateId = stateId;
    }

    public void addTransition(char chr, int toStateId) {
        // In Java, i caratteri sono già codificati in UTF-16, quindi la gestione
        // delle transizioni sarà diversa da quella in Rust.
        // Questo è un esempio semplificato che non gestisce la codifica UTF-8.
        int codePoint = Character.codePointAt(new char[]{chr}, 0);
        dfaBuilder.addTransition(stateId, codePoint, toStateId);
    }
}
