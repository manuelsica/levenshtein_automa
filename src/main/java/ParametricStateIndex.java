// ParametricStateIndex.java
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class ParametricStateIndex {
    private final List<OptionalInt> stateIndex;
    private final List<ParametricState> stateQueue;
    private final int numOffsets;

    public ParametricStateIndex(int queryLen, int numParamStates) {
        this.numOffsets = queryLen + 1;
        int maxNumStates = numParamStates * numOffsets;
        this.stateIndex = new ArrayList<>(maxNumStates);
        for (int i = 0; i < maxNumStates; i++) {
            this.stateIndex.add(OptionalInt.empty());
        }
        this.stateQueue = new ArrayList<>(100);
    }

    public int numStates() {
        return stateQueue.size();
    }

    public int maxNumStates() {
        return stateIndex.size();
    }

    public int getOrAllocate(ParametricState parametricState) {
        int bucket = parametricState.getShapeId() * numOffsets + parametricState.getOffset();
        OptionalInt stateIdOpt = stateIndex.get(bucket);
        if (stateIdOpt.isPresent()) {
            return stateIdOpt.getAsInt();
        }
        int newState = stateQueue.size();
        stateQueue.add(parametricState);
        stateIndex.set(bucket, OptionalInt.of(newState));
        return newState;
    }

    public ParametricState get(int stateId) {
        return stateQueue.get(stateId);
    }
}
