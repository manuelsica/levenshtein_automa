public class NFAState implements Comparable<NFAState> {
    int offset;
    private final int distance;
    private final boolean inTranspose;

    public NFAState(int offset, int distance, boolean inTranspose) {
        this.offset = offset;
        this.distance = distance;
        this.inTranspose = inTranspose;
    }


    public boolean imply(NFAState other) {
        boolean transposeImplies = this.inTranspose || !other.inTranspose;
        int deltaOffset = Math.abs(this.offset - other.offset);

        if (transposeImplies) {
            return other.distance >= this.distance + deltaOffset;
        } else {
            return other.distance > this.distance + deltaOffset;
        }
    }

    @Override
    public int compareTo(NFAState other) {
        // Implement Comparable interface for sorting
        if (this.offset == other.offset) {
            if (this.distance == other.distance) {
                return Boolean.compare(this.inTranspose, other.inTranspose);
            }
            return Integer.compare(this.distance, other.distance);
        }
        return Integer.compare(this.offset, other.offset);
    }

    public int getOffset() {
        return offset;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isInTranspose() {
        return inTranspose;
    }


}
