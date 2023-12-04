// Transition.java
public class Transition {
    private final int destShapeId;
    private final int deltaOffset;

    public Transition(int destShapeId, int deltaOffset) {
        this.destShapeId = destShapeId;
        this.deltaOffset = deltaOffset;
    }

    public ParametricState apply(ParametricState state) {
        int newOffset = (destShapeId == 0) ? 0 : state.getOffset() + deltaOffset;
        return new ParametricState(destShapeId, newOffset);
    }
}
