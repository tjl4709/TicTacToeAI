import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Champion
{
    private MinMaxTree root, first, second;

    public void setTree(boolean playFirst)
    {
        if (playFirst)
            if (first == null) {
                readTree("MinMaxTree1.ser");
                first = root;
            } else
                root = first;
        else if (second == null) {
            readTree("MinMaxTree2.ser");
            second = root;
        } else
            root = second;
    }
    private void readTree(String filename)
    {
        try {
            FileInputStream file;
            file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);
            root = (MinMaxTree)in.readObject();
            in.close();
            file.close();
        } catch (Exception e) {e.printStackTrace();}
    }
    public String move(String board)
    {
        root = root.setChild(board).getBestChild();
        return root.getBoard();
    }
}