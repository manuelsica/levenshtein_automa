// ParametricDFA.java
import java.util.ArrayList;
import java.util.List;

public class ParametricDFA {
    private final List<Distance> distance;
    private final List<Transition> transitions;
    private final byte maxDistance;
    private final int transitionStride;
    private final int diameter;

    public ParametricDFA(List<Distance> distance, List<Transition> transitions, byte maxDistance, int transitionStride, int diameter) {
        this.distance = distance;
        this.transitions = transitions;
        this.maxDistance = maxDistance;
        this.transitionStride = transitionStride;
        this.diameter = diameter;
    }

    public static ParametricState initialState() {
        return new ParametricState(1, 0);
    }

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

    public DFA buildDFA(String query, boolean prefix) {
        return buildCustomDFA(query, prefix, false);
    }

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

    public int numStates() {
        return transitions.size() / transitionStride;
    }

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

    public Distance appliedDistance(ParametricState state) {
        int index = diameter * state.getShapeId();
        Distance d = distance.get(index);
        return d.getDistance() > maxDistance ? Distance.atLeast(d.getDistance()) : Distance.exact(d.getDistance());
    }

    public Transition transition(ParametricState state, int chi) {
        assert chi < transitionStride;
        return transitions.get(transitionStride * state.getShapeId() + chi);
    }

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
}
