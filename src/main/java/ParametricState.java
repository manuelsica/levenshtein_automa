// ParametricState.java
public class ParametricState {
    private final int shapeId;
    private final int offset;

    public ParametricState(int shapeId, int offset) {
        this.shapeId = shapeId;
        this.offset = offset;
    }

    public static ParametricState empty() {
        return new ParametricState(0, 0);
    }

    public boolean isDeadEnd() {
        return this.shapeId == 0;
    }

    // Getters for shapeId and offset
    public int getShapeId() {
        return shapeId;
    }

    public int getOffset() {
        return offset;
    }
}
