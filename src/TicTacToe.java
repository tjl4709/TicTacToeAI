import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class TicTacToe
{
    private static String currentPos = "eeeeeeeee";             //9 spaces of board; e=empty
    private static Scanner keyboard = new Scanner(System.in);
    private static AI ai = new AI('X'), ai2 = new AI('O');
    private static Champion champ = new Champion();

    public static void main(String[] args) {
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
                HashMap<String, ArrayList<Integer>> pos = (HashMap<String, ArrayList<Integer>>) in.readObject();
                ai.setPos(pos);
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
            clearBoard();
            ai.clearGamelog();
            ai2.clearGamelog();
            if (Math.random() < .5) {                           //50% chance ai goes first
                if (play == 'm') {
                    champ.setTree(true);
                    currentPos = champ.move(currentPos);
                } else if (play == 'c') {
                    champ.setTree(false);
                    currentPos = ai.move(currentPos);
                } else
                    currentPos= ai.move(currentPos);                //sets the space the ai chooses to "X"
            } else if (play == 'c')
                champ.setTree(true);
            else if (play == 'm')
                champ.setTree(false);
            while (!(winMove('X') || winMove('O') || full())) {           //checks if the player won
                drawBoard();
                if (play == 'p')
                    currentPos = ai2.move(currentPos);
                else if (play == 'c')
                    currentPos = champ.move(currentPos);
                else
                    currentPos = playerMove(play != 'm');                  //player inputs 1-9

                if (winMove('O') || winMove('X') || full())
                    break;                                      //skips ai move if the player won or board is full

                if (play != 'm')
                    currentPos = ai.move(currentPos);
                else
                    currentPos = champ.move(currentPos);
            }

            drawBoard();
            if (play != 'm')
                ai.learn(winMove('X'), winMove('O'));         //teaches the "ai"
            if (play == 'p') ai2.learn(winMove('O'), winMove('X'));

            boolean oWin = winMove('O'), xWin = winMove('X');
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
//            System.out.println("\nExceptions: " + ai.getExceptions());
        }

        try {
            FileOutputStream file = new FileOutputStream("file.ser");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(ai.getPos());
            out.close();
            file.close();
        } catch (Exception e) {System.out.println(e + " caught");}
    }

    private static void drawBoard()
    {
        String str = currentPos.replace('e',' ');
        System.out.println("   |   |");
        System.out.println(" " + str.charAt(6) + " | " + str.charAt(7) + " | " + str.charAt(8));
        System.out.println("___|___|___");
        System.out.println("   |   |");
        System.out.println(" " + str.charAt(3) + " | " + str.charAt(4) + " | " + str.charAt(5));
        System.out.println("___|___|___");
        System.out.println("   |   |");
        System.out.println(" " + str.charAt(0) + " | " + str.charAt(1) + " | " + str.charAt(2));
        System.out.println("   |   |\n");
    }
    private static void clearBoard() {currentPos = "eeeeeeeee";}
    private static String playerMove(boolean playO)                 //robust question of where the player wants to go
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
            } else if (currentPos.charAt(move - 1) != 'e'){          //checks to make sure their box is empty
                System.out.println("Please choose a square that has not been played yet.");
                move = 0;
            }
        }
        if (playO)
            return currentPos.substring(0, move - 1) + 'O' + currentPos.substring(move, 9);
        return currentPos.substring(0, move - 1) + 'X' + currentPos.substring(move, 9);
    }
    private static boolean winMove(char c) {return winMove(c, currentPos);}
    static boolean winMove(char c, String board)//checks if current position has a win
    {
        if (board.charAt(4) == c && (board.charAt(0) == c && board.charAt(8) == c || board.charAt(2) == c && board.charAt(6) == c))
            return true;                                    //checks diagonals
        for (int i = 0; i < 3; i++)
            if ((board.charAt(i) == c && board.charAt(i + 3) == c && board.charAt(i + 6) == c) || board.substring(3 * i,3 * i + 3).equals("" + c + c + c))
                return true;                                //checks rows and columns
        return false;
    }
    private static boolean full()               //checks if all spaces are used
    {
        return currentPos.indexOf('e') == -1;
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