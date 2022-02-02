import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Stack;

public class Board
{
    protected enum Transform {
        None, Rotate90CW, FlipVert
    }

    private Symbol[] spaces, preT;
    private Stack<Transform> trans;

    public Board() {
        trans = new Stack<>();
        spaces = new Symbol[] {
                Symbol.Empty, Symbol.Empty, Symbol.Empty,
                Symbol.Empty, Symbol.Empty, Symbol.Empty,
                Symbol.Empty, Symbol.Empty, Symbol.Empty
        };
    }
    public Board(Board b) {
        copy(b);
    }
    public void copy(Board b) {
        spaces = Arrays.copyOf(b.spaces, b.spaces.length);
        if (b.preT != null)
            preT = Arrays.copyOf(b.preT, b.preT.length);
        trans = (Stack<Transform>) b.trans.clone();
    }

    public boolean move(int i, Symbol s) {
        if (spaces[i] != Symbol.Empty || s == Symbol.Empty)
            return false;
        spaces[i] = s;
        return true;
    }
    public Symbol symbolAt(int i) { return spaces[i]; }
    public int numEmpty(){
        int n = 0;
        for (int i = 0; i < spaces.length; i++)
            if (spaces[i] == Symbol.Empty) n++;
        return n;
    }
    public void clear() { Arrays.fill(spaces, Symbol.Empty); }
    //win conditions
    public boolean isFull() { return numEmpty() == 0; }
    public Symbol whoWon() {
        //diagonals
        if (spaces[4] != Symbol.Empty && (spaces[0] == spaces[4] && spaces[4] == spaces[8]
                || spaces[2] == spaces[4] && spaces[4] == spaces[6])) return spaces[4];
        for (int i = 0; i < 3; i++) {
            //columns
            if (spaces[i] != Symbol.Empty && spaces[i] == spaces[i + 3] && spaces[i] == spaces[i + 6])
                return spaces[i];
            //rows
            if (spaces[i*3] != Symbol.Empty && spaces[i*3] == spaces[i*3 + 1] && spaces[i*3] == spaces[i*3 + 2])
                return spaces[i];
        }
        return Symbol.Empty;
    }
    //transformations
    public void rotate90CW(){
        if (trans.isEmpty()) preT = spaces;
        Symbol[] rotated = new Symbol[spaces.length];
        for (int i = 0; i < spaces.length; i++)
            rotated[i] = spaces[3*(i%3) + 2 - i/3];
        spaces = rotated;
        trans.push(Transform.Rotate90CW);
    }
    public void flipVertical(){
        if (trans.isEmpty()) preT = spaces;
        Symbol[] flipped = new Symbol[spaces.length];
        for (int i = 0; i < spaces.length; i++)
            flipped[i] = spaces[i + 6*(1 - i/3)];
        spaces = flipped;
        trans.push(Transform.FlipVert);
    }
    public int transformPos(int i) {
        while (!trans.isEmpty())
            if (trans.pop() == Transform.Rotate90CW)
                i = 3*(i%3) + 2 - i/3;
            else i = i + 6*(1 - i/3);
        return i;
    }
    public void resetTrans() {
        if (!trans.isEmpty()) {
            trans.clear();
            spaces = preT;
            preT = null;
        }
    }
    //IO
    public void draw() {
        System.out.println("   |   |");
        System.out.println(" " + spaces[6] + " | " + spaces[7] + " | " + spaces[8]);
        System.out.println("___|___|___");
        System.out.println("   |   |");
        System.out.println(" " + spaces[3] + " | " + spaces[4] + " | " + spaces[5]);
        System.out.println("___|___|___");
        System.out.println("   |   |");
        System.out.println(" " + spaces[0] + " | " + spaces[1] + " | " + spaces[2]);
        System.out.println("   |   |\n");
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces.length; i++)
            sb.append(spaces[i]);
        return sb.toString().replace(' ', 'e');
    }
}
