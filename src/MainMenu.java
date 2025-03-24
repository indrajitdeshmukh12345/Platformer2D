import game2D.GameCore;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MainMenu extends GameCore {
    private boolean startGame = false;
    private boolean levelSelect = false;

    public static void main(String[] args) {
        MainMenu menu = new MainMenu();
        menu.init();
        menu.run(false, Game.screenWidth, Game.screenHeight);
    }


    public void init() {
        setSize(Game.screenWidth, Game.screenHeight);
        setVisible(true);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Main Menu", getWidth() / 2 - 70, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press 'S' to Start Game", getWidth() / 2 - 100, 200);
        g.drawString("Press 'L' to Select Level", getWidth() / 2 - 100, 250);
        g.drawString("Press 'ESC' to Exit", getWidth() / 2 - 100, 300);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_S:
                startGame = true;
                stop();
                break;
            case KeyEvent.VK_L:
                levelSelect = true;
                stop();
                break;
            case KeyEvent.VK_ESCAPE:
                stop();
                break;
        }
    }

    public boolean isStartGame() {
        return startGame;
    }

    public boolean isLevelSelect() {
        return levelSelect;
    }
}
