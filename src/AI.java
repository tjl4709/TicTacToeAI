import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.io.Serializable;

public class AI implements Serializable
{
    private HashMap<String, ArrayList<Integer>> pos = new HashMap<>();              //key is current position and value is the moves available to ai
    private HashMap<String, Integer> gamelog = new HashMap<>();                     //key is current position and value is the move made by ai
    private int games, won, lost, tied, exceptions;
    private char symbol;

    public AI(char s)
    {
        symbol = s;
    }
    public String move(String currentPos) {                     //makes ai's move
        if (currentPos.contains("e") && currentPos.indexOf('e') == currentPos.lastIndexOf('e'))
            return makeMoveAt(currentPos, currentPos.indexOf('e'));

        int move;
        ArrayList<Integer> moves = new ArrayList<>();
        if (!pos.containsKey(currentPos) || pos.get(currentPos).size() == 0) {
            for (int i = 0; i < 9; i++)                         //if all choices deleted or new board state, it resets
                if (currentPos.charAt(i) == 'e')                //checks which spaces are empty
                    for (int j = 0; j < 1; j++)                 //adds j choices for each empty space
                        moves.add(i);
            pos.put(currentPos, moves);
            move = getRand(moves);
            gamelog.put(currentPos,move);
            return makeMoveAt(currentPos, move);
        }                                                       //ai knows what to do
        move = getRand(pos.get(currentPos));                    //chooses random empty space
        gamelog.put(currentPos, move);                          //adds game position and ai's move to the gamelog
        return makeMoveAt(currentPos, move);
    }
    private String makeMoveAt(String currentPos, int i)
    {
        return currentPos.substring(0, i) + symbol + currentPos.substring(i + 1, 9);
    }
    private int getRand(ArrayList<Integer> moves)             // chooses random index from list
    {
        if (moves.size() == 0) {
            exceptions++;
            return 0;
        }
        double choice = Math.random();                          //creates random decimal 0<= choice < 1
        for (int i = 1; i < moves.size(); i++)
            if (choice < 1. * i / moves.size())                 //checks if choice is under i/n terms of list
                return moves.get(i - 1);                        //intellij doesn't like that the return is after an if statement even-
        return moves.get(moves.size() - 1);                     //tho it will have an output, so this is a fail safe of the last term
    }
    public void learn(boolean win, boolean loss)                //how the ai changes it's behavior
    {
        games++;
        if (win) {                                              //if the ai won
            for (String key : gamelog.keySet())
                if (pos.get(key).size() < 15)
                    for (int j = 0; j < 1; j++)
                        pos.get(key).add(gamelog.get(key));     //adds j more terms of the winning moves
            won++;
        } else if (loss) {                                      //if the opponent won
            for (String key : gamelog.keySet())
                pos.get(key).remove(gamelog.get(key));          //gets rid of losing moves
            lost++;
        } else {                                                //if the board was full and no one won
            for (String key : gamelog.keySet())
                if (pos.get(key).size() > 11)
                    pos.get(key).remove(gamelog.get(key));
                else if (pos.get(key).size() < 5)
                    pos.get(key).add(gamelog.get(key));
            tied++;
        }
    }
    public void stupify()                                       //resets ai
    {
        pos.clear();
        gamelog.clear();
        games = won = lost = tied = 0;
    }

    public void setPos(HashMap<String, ArrayList<Integer>> in)
    {
        pos = in;
    }

    public HashMap<String, ArrayList<Integer>> getPos() { return pos; }
    public Set<String> getKeys() { return pos.keySet(); }
    public Collection<ArrayList<Integer>> getValues() { return pos.values(); }
    public void clearGamelog(){ gamelog.clear(); }
    public HashMap<String, Integer> getGamelog() { return gamelog; }
    public Set<String> getGameKeys() { return gamelog.keySet(); }
    public Collection<Integer> getGameValues() { return gamelog.values(); }
    public int getGames() { return games; }
    public int getWon() { return won; }
    public int getLost() { return lost; }
    public int getTied() { return tied; }
    public int getExceptions() { return exceptions; }

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
        System.out.println(str);
    }
}