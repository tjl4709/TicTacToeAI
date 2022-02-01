import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MinMaxTree implements Serializable
{
    private String board;
    private short score;
    private MinMaxTree[] children;

    private MinMaxTree(String b, int numChildren, boolean myTurn)
    {
        board = b;
        score = (short) (myTurn ? -2 : 2);
        if (TicTacToe.winMove('O', board))                            //I win: good end state, don't need children because game won't reach any state past this one
            score = 1;
        else if (TicTacToe.winMove('X', board))                       //opponent wins: bad end state, don't need children because game won't reach any state past this one
            score = -1;
        else if (numChildren > 0) {                     //numChildren == 0 means board is full: won't be any children
            children = new MinMaxTree[numChildren];
            int childNum = 0;
            for (int i = 0; i < 9; i++)
                if (board.charAt(i) == 'e' && myTurn) { //if space is empty and it's my turn, create new node for board where I play in this space
                    children[childNum] = new MinMaxTree(board.substring(0, i) + 'O' + board.substring(i + 1, 9), numChildren - 1, false);
                    score = (short) Math.max(score, children[childNum++].getScore());
                } else if (board.charAt(i) == 'e') {    //if space is empty and it's not my turn, create new node for board where opponent plays in this space
                    children[childNum] = new MinMaxTree(board.substring(0, i) + 'X' + board.substring(i + 1, 9), numChildren - 1, true);
                    score = (short) Math.min(score, children[childNum++].getScore());
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
            out.writeObject(new MinMaxTree("eeeeeeeee", 9, true));
            //save tree for when opponent plays first
            file = new FileOutputStream("MinMaxTree2.ser");
            out = new ObjectOutputStream(file);
            out.writeObject(new MinMaxTree("eeeeeeeee", 9, false));
            out.close();
            file.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public short getScore() {return score;}
    public String getBoard() {return board;}
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
