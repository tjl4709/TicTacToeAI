import java.io.Serializable;
public class MoveChance implements Serializable
{
    private int sum;
    private byte[] chance;

    public MoveChance() {
        sum = 9;         //0, 1, 2, 3, 4, 5, 6, 7, 8
        chance = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
    }
    public MoveChance(Board b) {
        chance = new byte[9];
        reset(b);
    }

    public int size() { return sum; }
    public int getRand() {
        if (sum == 0) return -1;
        double c = Math.random() * sum;
        int s = 0;
        for (int i = 0; i < chance.length - 1; i++) {
            s += (short)chance[i] & 0xFF;
            if (c < s) return i;
        }
        return chance.length - 1;
    }
    public boolean add(int i) { return add(i, 255 * 9); }
    public boolean add(int i, int cap) { return add(i, cap, 1); }
    public boolean add(int i, int cap, int inc) {
        if (sum >= cap || ((short)chance[i] & 0xFF) == 255) return false;
        sum += inc;
        chance[i] += inc;
        return true;
    }
    public boolean remove(int i) { return remove(i, 0); }
    public boolean remove(int i, int lim) { return remove(i, lim, 1); }
    public boolean remove(int i, int lim, int inc) {
        if (sum <= lim || chance[i] == 0) return false;
        sum -= inc;
        chance[i] -= inc;
        return true;
    }
    public void reset(Board b) {
        sum = 0;
        for (int i = 0; i < chance.length; i++)
            if (b.symbolAt(i) == Symbol.Empty) {
                sum++;
                chance[i] = 1;
            } else
                chance[i] = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append((short)chance[0] & 0xFF);
        for (int i = 1; i < chance.length; i++)
            sb.append(',').append((short)chance[i] & 0xFF);
        sb.append(']');
        return sb.toString();
    }
}
