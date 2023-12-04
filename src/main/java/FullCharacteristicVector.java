import java.util.ArrayList;
import java.util.List;

public class FullCharacteristicVector {
    private final List<Long> vector;

    public FullCharacteristicVector(List<Long> vector) {
        this.vector = new ArrayList<>(vector);
    }

    public long shiftAndMask(int offset, long mask) {
        int bucketId = offset / 32;
        int align = offset - bucketId * 32;
        if (align == 0) {
            return this.vector.get(bucketId) & mask;
        } else {
            long left = this.vector.get(bucketId) >> align;
            long right = this.vector.get(bucketId + 1) << (32 - align);
            return (left | right) & mask;
        }
    }
}
