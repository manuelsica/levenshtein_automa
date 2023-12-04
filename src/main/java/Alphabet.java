import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Alphabet {
    private final List<Pair<Character, FullCharacteristicVector>> charset;

    public Alphabet(List<Pair<Character, FullCharacteristicVector>> charset) {
        this.charset = new ArrayList<>(charset);
    }

    public List<Pair<Character, FullCharacteristicVector>> getCharset() {
        return charset;
    }

    public static Alphabet forQueryChars(char[] queryChars) {
        List<Character> charList = new ArrayList<>();
        for (char c : queryChars) {
            charList.add(c);
        }
        charList.sort(null);
        charList = new ArrayList<>(new HashSet<>(charList));

        List<Pair<Character, FullCharacteristicVector>> charset = new ArrayList<>();
        for (char c : charList) {
            List<Long> bits = new ArrayList<>();
            for (char chr : queryChars) {
                long chunkBits = 0L;
                long bit = 1L;
                if (chr == c) {
                    chunkBits |= bit;
                }
                bit <<= 1;
                bits.add(chunkBits);
            }
            bits.add(0L);
            charset.add(new Pair<>(c, new FullCharacteristicVector(bits)));
        }

        return new Alphabet(charset);
    }

}
