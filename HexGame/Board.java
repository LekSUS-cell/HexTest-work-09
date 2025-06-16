package HexGame;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    private final Cell[][] cells;
    private final HexGrid grid;
    private final Random random;
    private final double blueProbability;
    private final boolean[][] isActive;

    public enum Level {
        EASY(5, 5, 0.2, true),
        MEDIUM(7, 7, 0.3, true),
        HARD(9, 9, 0.35, false);

        final int rows;
        final int cols;
        final double blueProbability;
        final boolean isRectangular;

        Level(int rows, int cols, double blueProbability, boolean isRectangular) {
            this.rows = rows;
            this.cols = cols;
            this.blueProbability = blueProbability;
            this.isRectangular = isRectangular;
        }
    }

    public Board(Level level) {
        this.cells = new Cell[level.rows][level.cols];
        this.grid = new HexGrid(level.rows, level.cols);
        this.random = new Random();
        this.blueProbability = level.blueProbability;
        this.isActive = new boolean[level.rows][level.cols];
        initialize(level);
    }

    private void initialize(Level level) {
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (level.isRectangular) {
                    isActive[r][c] = true;
                } else {
                    int midRow = (level.rows - 1) / 2;
                    int midCol = (level.cols - 1) / 2;
                    isActive[r][c] = Math.abs(r - midRow) + Math.abs(c - midCol) <= midRow;
                }
                cells[r][c] = isActive[r][c] ? new Cell() : null;
            }
        }
    }

    public void generateBlues(int startR, int startC) {
        ArrayList<int[]> forbidden = new ArrayList<>();
        forbidden.add(new int[]{startR, startC});
        System.out.println("Стартовая ячейка [" + startR + "," + startC + "] безопасная");

        // Добавляем соседей стартовой ячейки в запрещённые
        for (int neighbor : grid.getNeighbors(startR, startC)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (isActive[nr][nc]) {
                    forbidden.add(new int[]{nr, nc});
                    System.out.println("Сосед [" + nr + "," + nc + "] безопасный");
                }
            }
        }

        // Собираем все активные клетки
        ArrayList<int[]> candidates = new ArrayList<>();
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (isActive[r][c]) {
                    candidates.add(new int[]{r, c});
                }
            }
        }

        // Удаляем запрещённые клетки из кандидатов
        candidates.removeIf(pos -> {
            for (int[] forbiddenPos : forbidden) {
                if (pos[0] == forbiddenPos[0] && pos[1] == forbiddenPos[1]) {
                    return true;
                }
            }
            return false;
        });

        // Размещаем синие клетки
        int blueCount = (int) (candidates.size() * blueProbability);
        for (int i = 0; i < blueCount && !candidates.isEmpty(); i++) {
            int index = random.nextInt(candidates.size());
            int[] pos = candidates.remove(index);
            cells[pos[0]][pos[1]].setBlue(true);
            System.out.println("Синяя клетка размещена в [" + pos[0] + "," + pos[1] + "]");
        }

        // Вычисляем clue для всех клеток
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (isActive[r][c] && !cells[r][c].isBlue()) {
                    cells[r][c].setClue(countBlueNeighbors(r, c));
                }
            }
        }
    }

    private int countBlueNeighbors(int r, int c) {
        int count = 0;
        for (int neighbor : grid.getNeighbors(r, c)) {
            if (neighbor != -1) {
                int nr = neighbor / grid.getCols();
                int nc = neighbor % grid.getCols();
                if (isActive[nr][nc] && cells[nr][nc].isBlue()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getBlueCount() {
        int count = 0;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (isActive[r][c] && cells[r][c].isBlue()) {
                    count++;
                }
            }
        }
        return count;
    }

    public Cell getCell(int r, int c) { return cells[r][c]; }
    public HexGrid getGrid() { return grid; }
    public boolean isActive(int r, int c) { return isActive[r][c]; }
}