package HexGame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hexcells Infinite");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setMinimumSize(new Dimension(400, 400));

            CardLayout cardLayout = new CardLayout();
            JPanel container = new JPanel(cardLayout);

            MainMenu mainMenu = new MainMenu(frame, container, cardLayout);
            HexcellsUI easyGame = new HexcellsUI(frame, container, cardLayout, Board.Level.EASY);
            HexcellsUI mediumGame = new HexcellsUI(frame, container, cardLayout, Board.Level.MEDIUM);
            HexcellsUI hardGame = new HexcellsUI(frame, container, cardLayout, Board.Level.HARD);

            container.add(mainMenu, "MainMenu");
            container.add(easyGame, "EasyGame");
            container.add(mediumGame, "MediumGame");
            container.add(hardGame, "HardGame");

            frame.setContentPane(container);
            frame.setSize(800, 600); // Начальный размер
            frame.setLocationRelativeTo(null);
            cardLayout.show(container, "MainMenu");
            frame.setVisible(true);
        });
    }
}