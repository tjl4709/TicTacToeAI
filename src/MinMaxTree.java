import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MinMaxTree implements Serializable
{
    private String board;
    private short score, move;
    private Symbol symbol;
    private MinMaxTree[] children;

    private MinMaxTree(Board b, int numChildren, boolean myTurn, int moveMade)
    {
        Board next;
        board = b.toString();
        symbol = myTurn ? Symbol.O : Symbol.X;
        score = (short) (myTurn ? -2 : 2);
        move = (short)moveMade;
        Symbol s = b.whoWon();
        if (s == Symbol.O)                            //I win: good end state, don't need children because game won't reach any state past this one
            score = 1;
        else if (s == Symbol.X)                       //opponent wins: bad end state, don't need children because game won't reach any state past this one
            score = -1;
        else if (numChildren > 0) {                     //numChildren == 0 means board is full: won't be any children
            children = new MinMaxTree[numChildren];
            int childNum = 0;
            for (int i = 0; i < 9; i++)
                if (b.symbolAt(i) == Symbol.Empty) { //if space is empty and it's my turn, create new node for board where I play in this space
                    next = new Board(b);
                    next.move(i, symbol);
                    children[childNum] = new MinMaxTree(next, numChildren - 1, !myTurn, i);
                    if (myTurn && children[childNum].getScore() > score || !myTurn && children[childNum].getScore() < score) {
                        score = children[childNum].getScore();
                    }
                    childNum++;
                }
            if (myTurn)
                children = getBestChildren();
        } else                                          //tie: neutral end state
            score = 0;
    }

    public static void main(String[] args)
    {
        FileOutputStream file;
        ObjectOutputStream out;
        try {
            //save tree for when I play first
            file = new FileOutputStream("MinMaxTree1.ser");
            out = new ObjectOutputStream(file);
            out.writeObject(new MinMaxTree(new Board(), 9, true, -1));
            out.flush();
            out.close();
            file.close();
            //save tree for when opponent plays first
            file = new FileOutputStream("MinMaxTree2.ser");
            out = new ObjectOutputStream(file);
            out.writeObject(new MinMaxTree(new Board(), 9, false, -1));
            out.flush();
            out.close();
            file.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public short getScore() {return score;}
    public String getBoard() {return board;}
    public short getMove() {return move;}
    public Symbol getSymbol() {return symbol;}
    public MinMaxTree[] getChildren() {return children;}
    public MinMaxTree setChild(String b)
    {
        for (MinMaxTree child : children)
            if (b.equals(child.getBoard()))
                return child;
        return this;
    }
    public MinMaxTree getBestChild()
    {
        double chance = Math.random();
        for (int i = 0; i < children.length; i++)
            if (chance < 1. * i / children.length)
                return children[i];
        return children[children.length - 1];
    }
    private MinMaxTree[] getBestChildren()
    {
        int bestScore = -2, num = 0;
        for (MinMaxTree child : children)
            if (child != null)
                if (child.getScore() > bestScore){
                    bestScore = child.getScore();
                    num = 1;
                } else if (child.getScore() == bestScore)
                num++;
        MinMaxTree[] bestChildren = new MinMaxTree[num];
        num = 0;
        for (MinMaxTree child : children)
            if (child != null)
                if (child.getScore() == bestScore)
                    bestChildren[num++] = child;
        return bestChildren;
    }
}
