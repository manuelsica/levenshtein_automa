package benchmark;


public class Transition {
    private final int destShapeId;
    private final int deltaOffset;

    // Costruttore che inizializza una transizione con il destinatario e il delta offset
    public Transition(int destShapeId, int deltaOffset) {
        this.destShapeId = destShapeId;
        this.deltaOffset = deltaOffset;
    }

    // Applica la transizione a uno stato parametrico restituendo il nuovo stato risultante
    public ParametricState apply(ParametricState state) {
        // Calcola il nuovo offset in base al destinatario e al delta offset
        int newOffset = (destShapeId == 0) ? 0 : state.getOffset() + deltaOffset;

        // Restituisci il nuovo stato parametrico
        return new ParametricState(destShapeId, newOffset);
    }

    @Override
    public String toString() {
        return " Transition{" +
                "destShapeId=" + destShapeId +
                ", deltaOffset=" + deltaOffset +
                '}';
    }
}
