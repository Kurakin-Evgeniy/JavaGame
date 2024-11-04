package edu.mephi.java.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JPanel implements ActionListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    private final int INITIAL_SPEED = 3; // Moves per second
    private final int ACCELERATION_DIVIDER = 10; // Speed +1 after eating ACCELERATION_DIVIDER number of fruits
    private final int FRUITS_COUNT = 3;
    private final java.util.List<Point> fruits = new ArrayList<>();
    private boolean gameOver = false;
    private int score;
    private Snake snake;
    private Thread gameThread;
    private Direction currentDirection;
    private Direction nextDirection;

    public Game() {
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> {
                        if (currentDirection == Direction.DOWN)
                            return;
                        nextDirection = Direction.UP;
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (currentDirection == Direction.UP)
                            return;
                        nextDirection = Direction.DOWN;
                    }
                    case KeyEvent.VK_LEFT -> {
                        if (currentDirection == Direction.RIGHT)
                            return;
                        nextDirection = Direction.LEFT;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (currentDirection == Direction.LEFT)
                            return;
                        nextDirection = Direction.RIGHT;
                    }
                }
            }
        });
        restart();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                g.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
            }
        }

        g.setColor(Color.RED);
        for (Point point : fruits) {
            g.fillRect(point.x * TILE_SIZE, point.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }

        boolean isHead = true;
        for (Point point : snake) {
            if (isHead) {
                g.setColor(new Color(0, 100, 0));
                g.fillRect(point.x * TILE_SIZE, point.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
                g.setColor(Color.GREEN);
                isHead = false;
                continue;
            }
            g.fillRect(point.x * TILE_SIZE, point.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }
    }

    private void tick() {
        gameOver = !snake.move(nextDirection);
        currentDirection = nextDirection;
        if (fruits.contains(snake.getHeadPosition())) {
            snake.eatFruit();
            score++;
            fruits.remove(snake.getHeadPosition());
            generateFruit();
        }
        repaint();
    }

    private void generateFruit() {
        Random random = new Random();
        java.util.List<Point> snakePts = new ArrayList<>();
        java.util.List<Point> freePoints = new ArrayList<>();

        for (Point point : snake) {
            snakePts.add(point);
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Point p = new Point(i, j);
                if (!snakePts.contains(p) && !fruits.contains(p))
                    freePoints.add(p);
            }
        }

        if (freePoints.isEmpty())
            return;
        int pointIndex = random.nextInt(0, freePoints.size());
        fruits.add(freePoints.get(pointIndex));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public void restart() {
        gameOver = false;
        snake = new Snake(new Point(WIDTH, HEIGHT));
        nextDirection = Direction.RIGHT;
        fruits.clear();
        for (int i = 0; i < FRUITS_COUNT; i++) {
            generateFruit();
        }
        score = 0;
        if (gameThread != null && gameThread.isAlive())
            gameThread.interrupt();
        gameThread = new Thread(() -> {
            while (!gameOver) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000 / (INITIAL_SPEED + score / ACCELERATION_DIVIDER));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tick();
            }
            gameOverMessage();
        });
        gameThread.start();
        repaint();
    }

    private void gameOverMessage() {
        int restart = JOptionPane.showConfirmDialog(this, "Счёт: " + score + "\n\n Начать заново?", "Конец игры", JOptionPane.YES_NO_OPTION );
        if (restart == JOptionPane.YES_OPTION) {
            restart();
            return;
        }
        Container parent = getParent();
        while (!(parent instanceof JFrame)) {
            parent = parent.getParent();
        }
        ((JFrame)parent).dispose();

    }

    public boolean isGameOver() {
        return gameOver;
    }
}
