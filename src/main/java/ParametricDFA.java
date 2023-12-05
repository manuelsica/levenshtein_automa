// ParametricDFA.java

import java.util.ArrayList;
import java.util.List;

public class ParametricDFA {
    private final List<Distance> distance;       // Lista delle distanze associate agli stati del DFA
    private final List<Transition> transitions;   // Lista delle transizioni tra gli stati del DFA
    private final byte maxDistance;               // Massima distanza consentita
    private final int transitionStride;           // Dimensione delle transizioni
    private final int diameter;                   // Diametro del DFA

    // Costruttore che inizializza un ParametricDFA con le informazioni necessarie
    public ParametricDFA(List<Distance> distance, List<Transition> transitions, byte maxDistance, int transitionStride, int diameter) {
        this.distance = distance;
        this.transitions = transitions;
        this.maxDistance = maxDistance;
        this.transitionStride = transitionStride;
        this.diameter = diameter;
    }

    // Metodo statico che restituisce uno stato iniziale
    public static ParametricState initialState() {
        return new ParametricState(1, 0);
    }

    // Verifica se uno stato è un "sink" rispetto al prefisso della query
    public boolean isPrefixSink(ParametricState state, int queryLen) {
        if (state.isDeadEnd()) {
            return true;
        }
        int remainingOffset = queryLen - state.getOffset();
        if (remainingOffset < diameter) {
            int stateDistanceIndex = diameter * state.getShapeId() + remainingOffset;
            Distance prefixDistance = distance.get(stateDistanceIndex);
            if (prefixDistance.getDistance() > maxDistance) {
                return false;
            }
            for (int i = 0; i < remainingOffset; i++) {
                if (distance.get(diameter * state.getShapeId() + i).compareTo(prefixDistance) < 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    // Costruisce un DFA standard o personalizzato a seconda del parametro "prefix"
    public DFA buildDFA(String query, boolean prefix) {
        return buildCustomDFA(query, prefix, false);
    }

    // Costruisce un DFA personalizzato a seconda dei parametri specificati
    public DFA buildCustomDFA(String query, boolean prefix, boolean useAppliedDistance) {
        char[] queryChars = query.toCharArray();
        Alphabet alphabet = Alphabet.forQueryChars(queryChars);
        int queryLen = queryChars.length;

        ParametricStateIndex parametricStateIndex = new ParametricStateIndex(queryLen, numStates());
        int maxNumStates = parametricStateIndex.maxNumStates();

        int deadEndStateId = parametricStateIndex.getOrAllocate(ParametricState.empty());
        assert deadEndStateId == 0;
        int initialStateId = parametricStateIndex.getOrAllocate(initialState());

        Utf8DFABuilder dfaBuilder = Utf8DFABuilder.withMaxNumStates(maxNumStates);
        int mask = (1 << diameter) - 1;

        for (int stateId = 0; stateId < parametricStateIndex.numStates(); stateId++) {
            ParametricState state = parametricStateIndex.get(stateId);
            Distance distance = useAppliedDistance ? appliedDistance(state) : distance(state, queryLen);

            if (prefix && isPrefixSink(state, queryLen)) {
                dfaBuilder.addState(stateId, distance, stateId);
            } else {
                Transition defaultTransition = transition(state, 0);
                ParametricState defaultSuccessor = defaultTransition.apply(state);
                int defaultSuccessorId = parametricStateIndex.getOrAllocate(defaultSuccessor);
                Utf8DFAStateBuilder stateBuilder = dfaBuilder.addState(stateId, distance, defaultSuccessorId);
                for (Pair<Character, FullCharacteristicVector> pair : alphabet.getCharset()) {
                    char chr = pair.getFirst();
                    FullCharacteristicVector characteristicVec = pair.getSecond();
                    int chi = (int) characteristicVec.shiftAndMask(state.getOffset(), mask);
                    Transition destTransition = transition(state, chi);
                    ParametricState destState = destTransition.apply(state);
                    int destStateId = parametricStateIndex.getOrAllocate(destState);
                    stateBuilder.addTransition(chr, destStateId);
                }
            }
        }

        dfaBuilder.setInitialState(initialStateId);
        return dfaBuilder.build();
    }

    // Restituisce il numero totale di stati nel DFA
    public int numStates() {
        return transitions.size() / transitionStride;
    }

    // Calcola la distanza per uno stato e una lunghezza di query specificati
    public Distance distance(ParametricState state, int queryLen) {
        int remainingOffset = queryLen - state.getOffset();
        if (state.isDeadEnd() || remainingOffset >= diameter) {
            return Distance.atLeast(maxDistance + 1);
        } else {
            int index = diameter * state.getShapeId() + remainingOffset;
            Distance d = distance.get(index);
            return d.getDistance() > maxDistance ? Distance.atLeast(d.getDistance()) : Distance.exact(d.getDistance());
        }
    }

    // Calcola la distanza applicata per uno stato specificato
    public Distance appliedDistance(ParametricState state) {
        int index = diameter * state.getShapeId();
        Distance d = distance.get(index);
        return d.getDistance() > maxDistance ? Distance.atLeast(d.getDistance()) : Distance.exact(d.getDistance());
    }

    // Restituisce la transizione per uno stato e un valore chi specificati
    public Transition transition(ParametricState state, int chi) {
        assert chi < transitionStride;
        return transitions.get(transitionStride * state.getShapeId() + chi);
    }

    // Costruisce un ParametricDFA da un NFA (Automaton di Levenshtein non deterministico)
    public static ParametricDFA fromNFA(LevenshteinNFA nfa) {
        Index<MultiState> index = new Index<>();
        index.getOrAllocate(MultiState.empty());
        MultiState initialState = nfa.initialStates();
        index.getOrAllocate(initialState);

        byte maxDistance = (byte) nfa.maxDistance();
        int multistateDiameter = nfa.multistateDiameter();
        List<Transition> transitions = new ArrayList<>();

        int numChi = 1 << multistateDiameter;
        List<Integer> chiValues = new ArrayList<>();
        for (int i = 0; i < numChi; i++) {
            chiValues.add(i);
        }

        MultiState destMultistate = MultiState.empty();

        for (int stateId = 0; stateId < index.size(); stateId++) {
            for (int chi : chiValues) {
                MultiState multistate = index.getFromId(stateId);
                nfa.transition(multistate, destMultistate, chi);
                int translation = destMultistate.normalize();
                int destId = index.getOrAllocate(destMultistate);
                transitions.add(new Transition(destId, translation));
            }
        }

        int numStates = index.size();
        List<Distance> distance = new ArrayList<>();
        for (int stateId = 0; stateId < numStates; stateId++) {
            MultiState multistate = index.getFromId(stateId);
            for (int offset = 0; offset < multistateDiameter; offset++) {
                Distance dist = nfa.multistateDistance(multistate, offset);
                distance.add(dist);
            }
        }

        return new ParametricDFA(distance, transitions, maxDistance, numChi, multistateDiameter);
    }

    @Override
    public String toString() {
        return "ParametricDFA{" +
                "distance=" + distance +
                ", transitions=" + transitions +
                ", maxDistance=" + maxDistance +
                ", transitionStride=" + transitionStride +
                ", diameter=" + diameter +
                '}';
    }

    public static void main(String [] args){

        ParametricDFA parametricDFA = ParametricDFA.fromNFA((new LevenshteinNFA(7, true)));

        ParametricState state = new ParametricState(1, 0);
        int queryLen = "kitten".length();

        Distance distance = parametricDFA.distance(state ,queryLen);
        System.out.println(distance);
    }
}
