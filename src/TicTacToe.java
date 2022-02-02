import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class TicTacToe
{
    private static Scanner keyboard = new Scanner(System.in);
    private static AI ai = new AI(Symbol.X), ai2 = new AI(Symbol.O);
    private static Champion champ = new Champion();
    private static Board board = new Board();

    public static void main(String[] args) {
        Symbol s;
        System.out.println("The aim of the game is to get three \"O\"s in a row. The board" +
                "\nis laid out like the number pad with the 9th space on the top" +
                "\nright and the 1st space on the bottom left. On your turn, " +
                "\nchoose an open space to play." +
                "\nThank you for choosing TicTacToeAI, we hope you enjoy!\n");
        char play;
        System.out.println("Would you like to reset the AI? (y/n)");
        if (cont().equalsIgnoreCase("n"))
            try {
                FileInputStream file = new FileInputStream("file.ser");
                ObjectInputStream in = new ObjectInputStream(file);
                Map<String, MoveChance> pos = (Map<String, MoveChance>) in.readObject();
                ai.setPos((HashMap<String, MoveChance>) pos);
//                ai2.setPos(pos);
                in.close();
                file.close();
            } catch (Exception e) {System.out.println(e + " caught");}
        System.out.println("How would you like to play:\n(a)i - play against the ai\n(p)ractice - train the ai with a "+
                "practice ai\n(c)hampion - train the ai with a min-max algorithm\n(m)inmax - play against the min-max" +
                " algorithm");
        play = contc();
        int games = 0;
        if (play == 'p' || play == 'c') {
            System.out.print("How many practice games would you like?\n10^");
            games = (int) Math.pow(10, numPractice());
        }

        boolean cont = true;
        while (cont) {
            board.clear();
            ai.clearGamelog();
            ai2.clearGamelog();
            if (Math.random() < .5) {                           //50% chance ai goes first
                if (play == 'm') {
                    champ.setTree(true);
                    champ.move(board);
                } else if (play == 'c') {
                    champ.setTree(false);
                    ai.move(board);
                } else
                    ai.move(board);                //sets the space the ai chooses to "X"
            } else if (play == 'c')
                champ.setTree(true);
            else if (play == 'm')
                champ.setTree(false);

            while (board.whoWon() == Symbol.Empty && !board.isFull()) {           //checks if the player won
                board.draw();
                if (play == 'p') ai2.move(board);
                else if (play == 'c') champ.move(board);
                else playerMove(play != 'm');                  //player inputs 1-9

                if (board.whoWon() != Symbol.Empty || board.isFull())
                    break;                                      //skips ai move if the player won or board is full
                //board.draw();
                if (play != 'm') ai.move(board);
                else champ.move(board);
            }

            board.draw();
            s = board.whoWon();
            boolean oWin = s == Symbol.O, xWin = s == Symbol.X;
            if (play != 'm') ai.learn(xWin, oWin);         //teaches the "ai"
            if (play == 'p') ai2.learn(xWin, oWin);

            if (oWin && play != 'm' || xWin && play == 'm')
                System.out.println("You Win!");
            else if (xWin || oWin)
                System.out.println("You Lose.");
            else
                System.out.println("It's a Draw.");
            System.out.println("Game #" + ai.getGames());
            if (play == 'a' || play == 'm') {
                System.out.println("Would you like to play again? (y/n)");
                cont = !cont().equalsIgnoreCase("n");
            } else
                cont = ai.getGames() < games;
        }

        if (play != 'm') {
            System.out.println("\nThank you for playing.");
            ai.printPos();
            System.out.println("Games won by AI: " + ai.getWon() + "\nGames lost by AI: " + ai.getLost()
                    + "\nGames tied: " + ai.getTied() + "\nTotal games: " + ai.getGames());
        }

        try {
            FileOutputStream file = new FileOutputStream("file.ser");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(ai.getPos());
            out.close();
            file.close();
        } catch (Exception e) {System.out.println(e + " caught");}
    }

    private static void playerMove(boolean playO)                 //robust question of where the player wants to go
    {
        System.out.println("Where would you like to go?");
        int move = 0;
        while(move < 1) {
            if (!keyboard.hasNextInt()) {                           //if they don't enter a number
                System.out.println("Please enter a number 1-9.");
                keyboard.nextLine();
                continue;
            }
            move = keyboard.nextInt();
            if (move < 1 || move > 9){                              //checks to make sure their number is on the board
                System.out.println("Please enter a number 1-9.");
                move = 0;
            } else if (board.symbolAt(move - 1) != Symbol.Empty){          //checks to make sure their box is empty
                System.out.println("Please choose a square that has not been played yet.");
                move = 0;
            }
        }
        if (playO)
            board.move(move-1, Symbol.O);
        board.move(move-1, Symbol.X);
    }
    private static String cont()                 //robust yes or no question
    {
        String cont = keyboard.next();
        if (!cont.equalsIgnoreCase("y") && !cont.equalsIgnoreCase("n")) {
            System.out.println("Please enter \"y\" for yes or \"n\" for no, thank you.");
            return cont();
        }
        return cont;
    }
    private static char contc()                 //robust yes or no question
    {
        char cont = keyboard.next().toLowerCase().charAt(0);
        if (cont != 'a' && cont != 'p' && cont != 'c' && cont != 'm') {
            System.out.println("Please enter \"a\" for ai, \"p\" for practice, \"c\" for champion,\nor \"m\" for " +
                    "min-max, thank you.");
            return contc();
        }
        return cont;
    }
    private static int numPractice()
    {
        int num;
        if (!keyboard.hasNextInt()) {
            System.out.println("Please enter an integer between and including 0 and 6.\nThank you.");
            num = numPractice();
        } else
            num = keyboard.nextInt();
        if (num < 0 || num > 6){
            System.out.println("Please enter an integer between and including 0 and 6.\nThank you.");
            num = numPractice();
        }
        return num;
    }
}