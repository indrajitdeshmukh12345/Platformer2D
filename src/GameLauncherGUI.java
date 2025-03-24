import javax.swing.*;

public class GameLauncherGUI {
    public static int getSelectedLevel() {
        Object[] options = {"Level 1", "Level 2"};
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
        return (choice == 1) ? 2 : 1; // Default to Level 1 if canceled
    }
}
