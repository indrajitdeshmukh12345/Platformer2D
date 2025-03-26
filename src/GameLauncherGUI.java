import game2D.GameInfoGUI;

import javax.swing.*;

public class GameLauncherGUI {

    public static int getSelectedLevel() {
        // Options for the dialog box
        Object[] options = {"Level 1", "Level 2", "Game Info"};

        // Display the dialog box
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select Level:",
                "Game Launcher",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Handle the user's choice
        switch (choice) {
            case 0: // Level 1
                return 1;
            case 1: // Level 2
                return 2;
            case 2: // Game Info

                return 3; // Indicate that no level was selected
            default:
                return 1; // Default to Level 1
        }
    }


}
