public class CharacteristicVector {
    public static long computeCharacteristicVector(char[] query, char c) {
        long chi = 0L;
        for (int i = 0; i < query.length; i++) {
            if (query[i] == c) {
                chi |= 1L << i;
            }
        }
        return chi;
    }
}
