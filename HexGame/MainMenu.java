package HexGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JPanel {
    private final JFrame frame;
    private final JPanel container;
    private final CardLayout cardLayout;
    private BufferedImage backgroundImage;

    public MainMenu(JFrame frame, JPanel container, CardLayout cardLayout) {
        this.frame = frame;
        this.container = container;
        this.cardLayout = cardLayout;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // Загружаем фоновое изображение
        try {
            backgroundImage = ImageIO.read(new File("background.png"));
        } catch (IOException e) {
            System.err.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
            backgroundImage = null;
        }

        // Заголовок
        JLabel titleLabel = new JLabel("Hexcells Infinite");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        add(Box.createVerticalStrut(100));
        add(titleLabel);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 20));
        buttonPanel.setMaximumSize(new Dimension(200, 120)); // Фиксированный размер панели

        JButton playButton = new JButton("Играть");
        playButton.setFont(new Font("Arial", Font.PLAIN, 18));
        playButton.setPreferredSize(new Dimension(180, 40)); // Фиксированный размер кнопки
        playButton.addActionListener(e -> showLevelSelection());
        buttonPanel.add(playButton);

        JButton exitButton = new JButton("Выход");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
        exitButton.setPreferredSize(new Dimension(180, 40)); // Фиксированный размер кнопки
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(30));
        add(buttonPanel);
    }

    private void showLevelSelection() {
        JPanel levelPanel = new BackgroundPanel();
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.Y_AXIS));

        JLabel levelLabel = new JLabel("Выберите уровень");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setForeground(Color.WHITE);
        levelPanel.add(Box.createVerticalStrut(100));
        levelPanel.add(levelLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 20));
        buttonPanel.setMaximumSize(new Dimension(200, 180)); // Фиксированный размер панели

        JButton easyButton = new JButton("Лёгкий");
        easyButton.setFont(new Font("Arial", Font.PLAIN, 18));
        easyButton.setPreferredSize(new Dimension(180, 40)); // Фиксированный размер кнопки
        easyButton.addActionListener(e -> startLevel(Board.Level.EASY, "EasyGame"));
        buttonPanel.add(easyButton);

        JButton mediumButton = new JButton("Средний");
        mediumButton.setFont(new Font("Arial", Font.PLAIN, 18));
        mediumButton.setPreferredSize(new Dimension(180, 40)); // Фиксированный размер кнопки
        mediumButton.addActionListener(e -> startLevel(Board.Level.MEDIUM, "MediumGame"));
        buttonPanel.add(mediumButton);

        JButton hardButton = new JButton("Сложный");
        hardButton.setFont(new Font("Arial", Font.PLAIN, 18));
        hardButton.setPreferredSize(new Dimension(180, 40)); // Фиксированный размер кнопки
        hardButton.addActionListener(e -> startLevel(Board.Level.HARD, "HardGame"));
        buttonPanel.add(hardButton);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelPanel.add(Box.createVerticalStrut(30));
        levelPanel.add(buttonPanel);

        container.add(levelPanel, "LevelSelection");
        cardLayout.show(container, "LevelSelection");
    }

    private void startLevel(Board.Level level, String cardName) {
        Dimension size = frame.getSize();
        // Удаляем старый уровень, если он существует
        Component[] components = container.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                container.remove(comp);
            }
        }
        // Создаём новый уровень
        HexcellsUI newGame = new HexcellsUI(frame, container, cardLayout, level);
        newGame.setName(cardName);
        container.add(newGame, cardName);
        cardLayout.show(container, cardName);
        frame.setSize(size); // Восстанавливаем размер
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private class BackgroundPanel extends JPanel {
        public BackgroundPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}