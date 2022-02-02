import java.util.*;
import java.io.Serializable;

public class AI implements Serializable
{
    private Map<String, MoveChance> pos = new HashMap<>();              //key is current position and value is the moves available to ai
    private Map<String, Integer> gamelog = new HashMap<>();               //key is current position and value is the move made by ai
    private int games, won, lost, tied;
    private Symbol sym;

    public AI(Symbol s)
    {
        sym = s;
    }
    public void move(Board b) {                     //makes ai's move
        int move;
        if (b.numEmpty() == 1)
            for (move = 0; move < 9; move++)
                if (b.symbolAt(move) == Symbol.Empty) {
                    b.move(move, sym);
                    return;
                }

        boolean found = false;
        Board extra = new Board(b);
        extra.flipVertical();
        if (pos.containsKey(b.toString())) found = true;
        else if (pos.containsKey(extra.toString())) {
            found = true;
            b = extra;
        }
        for (int i = 0; i < 3 && !found; i++) {
            b.rotate90CW();
            extra.rotate90CW();
            if (pos.containsKey(b.toString())) found = true;
            else if (pos.containsKey(extra.toString())) {
                found = true;
                b = extra;
            }
        }
        if (!found) pos.put(b.toString(), new MoveChance(b));

        MoveChance moves;
        moves = pos.get(b.toString());
        if (moves.size() == 0)
            moves.reset(b);
        move = moves.getRand();
        gamelog.put(b.toString(), move);
        move = b.transformPos(move);
        b.resetTrans();
        b.move(move, sym);
    }
    public void learn(boolean win, boolean loss)                //how the ai changes it's behavior
    {
        games++;
        if (win) {                                              //if the ai won
            for (String key : gamelog.keySet())
                pos.get(key).add(gamelog.get(key), 50, 1);
            won++;
        } else if (loss) {                                      //if the opponent won
            for (String key : gamelog.keySet())
                pos.get(key).remove(gamelog.get(key), 0, 1);
            lost++;
        } else {                                                //if the board was full and no one won
            for (String key : gamelog.keySet())
                if (!pos.get(key).remove(gamelog.get(key), 11, 1))
                    pos.get(key).add(gamelog.get(key), 5, 1);
            tied++;
        }
    }
    public void stupify()                                       //resets ai
    {
        pos.clear();
        gamelog.clear();
        games = won = lost = tied = 0;
    }

    public void setPos(Map<String, MoveChance> in)
    {
        pos = in;
    }

    public Map<String, MoveChance> getPos() { return pos; }
    public Set<String> getKeys() { return pos.keySet(); }
    public Collection<MoveChance> getValues() { return pos.values(); }
    public void clearGamelog(){ gamelog.clear(); }
    public Map<String, Integer> getGamelog() { return gamelog; }
    public Set<String> getGameKeys() { return gamelog.keySet(); }
    public Collection<Integer> getGameValues() { return gamelog.values(); }
    public int getGames() { return games; }
    public int getWon() { return won; }
    public int getLost() { return lost; }
    public int getTied() { return tied; }

    public void printPos()
    {
        String str = pos.toString().replaceAll(" ", "");
        final int lineBreak = 500;
        int i;
        while (str.length() > lineBreak) {
            i = lineBreak;
            while (!"=,".contains("" + str.charAt(i)))
                i--;
            i++;
            System.out.println(str.substring(0, i));
            str = str.substring(i);
        }
        if (str.length() > 0)
            System.out.println(str);
    }
}