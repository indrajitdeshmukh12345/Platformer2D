package game2D;

import javax.swing.*;
import java.awt.*;

public class GameInfoGUI {

    public static void createGameInfoWindow() {
        // Create the main window
        JFrame frame = new JFrame("Game Information");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLACK);

        // Create a JTextArea to display the game information
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Pixel-style font
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Add the game information text
        String gameInfo = "How to Play:\n" +
                "1. Use the arrow keys to move your character.\n" +
                "2. Press 'UP arrow keys' to jump.\n" +
                "3. Press 'd' to debug the game.\n" +
                "4. Press 'RMB' to dash i.e. move player few block R or L depending on last facing side\n"+
                "Player can dash through walls\n"+
                "5. Press 'LMB' to shoot fireballs in the direction of mouse click\n\n"+
                "Key Bindings:\n" +
                "- Arrow Keys: Move\n" +
                "- UP arrow: Jump\n" +
                "- D: Debug\n" +
                "- L: To turn off the Light effect\n"+
                "- RMB: shoot at Cursor location\n"+
                "- RMB: Dash Through wall or run faster\n\n" +
                "Other Info:\n" +
                "- Collect Torches to increase your Torch Health.\n" +
                "- Bullets are expensive as they cost you Torch health use wisely."+
                "- Avoid or shoot at enemies to stay alive.\n" +
                "- Spikes can kill you.\n "+
                "- Red Ball enemies can deal you 50 health points.\n"+
                "- Player can push Yellow block and throw on spikes to deactivate them.\n"+
                "- Reach the end of the level before the torch health becomes zero to win!";
        textArea.setText(gameInfo);

        // Add the text area to a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane, BorderLayout.CENTER);

        // Center the window on the screen
        frame.setLocationRelativeTo(null);

        // Make the window visible
        frame.setVisible(true);
    }
}

