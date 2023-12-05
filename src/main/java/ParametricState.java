// ParametricState.java
public class ParametricState {
    private final int shapeId;
    private final int offset;

    // Costruttore che inizializza uno stato parametrico con uno specifico shapeId e offset
    public ParametricState(int shapeId, int offset) {
        this.shapeId = shapeId;
        this.offset = offset;
    }

    // Metodo statico per ottenere uno stato vuoto (usato come stato di "dead-end")
    public static ParametricState empty() {
        return new ParametricState(0, 0);
    }

    // Metodo che restituisce true se lo stato è un "dead-end" (shapeId == 0)
    public boolean isDeadEnd() {
        return this.shapeId == 0;
    }

    // Metodi getter per ottenere shapeId e offset
    public int getShapeId() {
        return shapeId;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "ParametricState{" +
                "shapeId=" + shapeId +
                ", offset=" + offset +
                '}';
    }
}
