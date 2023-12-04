import java.util.Arrays;

public class LevenshteinNFA {
    private final int maxDistance;
    private final boolean damerau;

    public LevenshteinNFA(int maxDistance, boolean damerau) {
        this.maxDistance = maxDistance;
        this.damerau = damerau;
    }

    public Distance multistateDistance(MultiState multistate, int queryLen) {
        return Arrays.stream(multistate.getStates())
                .map(state -> Distance.exact(state.getDistance() + Math.abs(queryLen - state.getOffset())))
                .filter(d -> d.getDistance() <= maxDistance)
                .min(Distance::compareTo)
                .orElse(Distance.atLeast(maxDistance + 1));
    }

    public int maxDistance() {
        return maxDistance;
    }

    public int multistateDiameter() {
        return 2 * maxDistance + 1;
    }

    public MultiState initialStates() {
        MultiState multistate = MultiState.empty();
        multistate.addState(new NFAState(0, 0, false));
        return multistate;
    }

    public Distance computeDistance(String query, String other) {
        char[] queryChars = query.toCharArray();
        MultiState currentState = initialStates();
        MultiState nextState = MultiState.empty();

        for (char chr : other.toCharArray()) {
            nextState.clear();
            long chi = CharacteristicVector.computeCharacteristicVector(queryChars, chr);
            transition(currentState, nextState, chi);
            MultiState tempState = currentState;
            currentState = nextState;
            nextState = tempState;
        }

        return multistateDistance(currentState, queryChars.length);
    }

    private void simpleTransition(NFAState state, long symbol, MultiState multistate) {
        if (state.getDistance() < maxDistance) {
            multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, false));
            multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance() + 1, false));

            for (int d = 1; d <= maxDistance - state.getDistance(); d++) {
                if (extractBit(symbol, d)) {
                    multistate.addState(new NFAState(state.getOffset() + 1 + d, state.getDistance() + d, false));
                }
            }

            if (damerau && extractBit(symbol, 1)) {
                multistate.addState(new NFAState(state.getOffset(), state.getDistance() + 1, true));
            }
        }

        if (extractBit(symbol, 0)) {
            multistate.addState(new NFAState(state.getOffset() + 1, state.getDistance(), false));
        }

        if (state.isInTranspose() && extractBit(symbol, 0)) {
            multistate.addState(new NFAState(state.getOffset() + 2, state.getDistance(), false));
        }
    }

    void transition(MultiState currentState, MultiState destState, long shiftedChiVector) {
        destState.clear();
        long mask = (1L << multistateDiameter()) - 1L;

        for (NFAState state : currentState.getStates()) {
            long shiftedChiVectorMasked = (shiftedChiVector >> state.getOffset()) & mask;
            simpleTransition(state, shiftedChiVectorMasked, destState);
        }

        Arrays.sort(destState.getStates());
    }

    private boolean extractBit(long bitset, int pos) {
        int shift = (int) (bitset >> pos);
        return (shift & 1) == 1;
    }

    public static void main(String[] args) {
        // Esempi di utilizzo
        LevenshteinNFA levenshteinNFA = new LevenshteinNFA(2, true);
        Distance distance = levenshteinNFA.computeDistance("kitten", "sitting");
        System.out.println(distance);  // Stampa: Exact(3)
    }
}
