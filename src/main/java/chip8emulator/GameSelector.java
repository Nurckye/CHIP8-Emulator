
package chip8emulator;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import java.io.FileNotFoundException;
import java.util.*;

import java.io.File; // Import the File class
import java.util.Scanner; // Import the Scanner class to read text files

import java.awt.Color;
import javax.swing.JFrame;

import org.json.*;

class GameBoard extends Thread {
    private String path;

    GameBoard(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("CHIP8 Emulator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Drawing canvas = new Drawing();
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

        canvas.cpDrive.load_game(path);
        canvas.run();
    }
}

class GameSelector extends JFrame implements ItemListener {
    private static final long serialVersionUID = 1L;
    static JFrame selectorFrame;
    static JLabel lbl;
    static JComboBox c1;
    static JButton b1;
    static Hashtable<String, Hashtable<String, String>> romDict = new Hashtable<String, Hashtable<String, String>>();
    static String selectedTitle;

    static GameBoard gameb;

    // main class
    public static String readJSONfile(String path) {
        try {
            File myObj = new File(path);
            String data = "";
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
                data += myReader.nextLine();
            myReader.close();

            return data;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        // create a new frame
        selectorFrame = new JFrame("Select game");
        selectorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // create a object
        // System.out.println(GameSelector.readJSONfile("./roms/roms.json"));
        JSONObject jsonRomsObj = new JSONObject(GameSelector.readJSONfile("./roms/roms.json"));

        Vector<String> romNames = new Vector<String>();
        JSONArray games = jsonRomsObj.getJSONArray("games");
        selectedTitle = games.getJSONObject(0).getString("title");
        for (int i = 0; i < games.length(); i++) {
            Hashtable<String, String> infoDict = new Hashtable<String, String>();
            romNames.addElement(games.getJSONObject(i).getString("title"));
            infoDict.put("file", games.getJSONObject(i).getString("file"));
            infoDict.put("description", games.getJSONObject(i).getString("description"));
            romDict.put(games.getJSONObject(i).getString("title"), infoDict);
        }
        GameSelector s = new GameSelector();

        // set layout of frame
        selectorFrame.setLayout(new FlowLayout());

        c1 = new JComboBox(romNames);

        c1.addItemListener(s);
        lbl = new JLabel("Select the game ");
        b1 = new JButton("Play game");
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameb = new GameBoard("./roms/" + romDict.get(selectedTitle).get("file"));
                gameb.start();
            }
        });
        lbl.setForeground(Color.red);

        JPanel p = new JPanel();
        p.add(lbl);
        p.add(c1);
        selectorFrame.add(p);
        selectorFrame.add(b1);

        selectorFrame.setSize(500, 120);

        selectorFrame.show();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == c1) {
            selectedTitle = (String) c1.getSelectedItem();
        }
    }
}
