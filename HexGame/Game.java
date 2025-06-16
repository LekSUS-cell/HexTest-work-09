package HexGame;

public class Game {
    private final Board board;
    private boolean gameOver;
    private boolean won;
    private int[] hint;
    private boolean firstMove;

    public Game(Board board) {
        this.board = board;
        this.gameOver = false;
        this.won = false;
        this.hint = null;
        this.firstMove = true;
    }

    public void openFirstCell(int r, int c) {
        if (!board.getGrid().isValid(r, c) || !board.isActive(r, c) || !firstMove) return;
        Cell cell = board.getCell(r, c);
        if (cell.isFlagged() || cell.isRevealed()) return;

        // Генерируем поле, исключая стартовую клетку и её соседей
        board.generateBlues(r, c);
        cell.reveal();
        firstMove = false;
        hint = null;
        if (checkWin()) {
            gameOver = true;
            won = true;
        }
    }

    public void openCell(int r, int c) {
        if (gameOver || !board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        Cell cell = board.getCell(r, c);
        if (cell.isFlagged() || cell.isRevealed()) return;
        cell.reveal();
        hint = null;
        if (cell.isBlue()) {
            gameOver = true;
            won = false;
        } else if (checkWin()) {
            gameOver = true;
            won = true;
        }
    }

    public void toggleFlag(int r, int c) {
        if (gameOver || !board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        Cell cell = board.getCell(r, c);
        if (!cell.isRevealed()) {
            cell.toggleFlag();
            hint = null; // Сбрасываем подсказку после действия игрока
            if (checkWin()) {
                gameOver = true;
                won = true;
            }
        }
    }

    private boolean checkWin() {
        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (!board.isActive(r, c)) continue;
                Cell cell = board.getCell(r, c);
                if (cell.isBlue() && !cell.isFlagged()) return false;
                if (!cell.isBlue() && !cell.isRevealed()) return false;
            }
        }
        return true;
    }

    public int[] getHint() {
        if (hint != null) return hint;

        int totalBlues = board.getBlueCount();
        int flaggedCount = getFlaggedCount();

        // Случай 1: Все мины помечены, открываем безопасные клетки
        if (flaggedCount == totalBlues) {
            for (int r = 0; r < board.getGrid().getRows(); r++) {
                for (int c = 0; c < board.getGrid().getCols(); c++) {
                    if (!board.isActive(r, c) || board.getCell(r, c).isRevealed() || board.getCell(r, c).isFlagged()) continue;
                    hint = new int[]{r, c, 0}; // 0 = открыть (зелёный)
                    return hint;
                }
            }
        }

        // Случай 2: Проверяем открытые клетки
        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (!board.isActive(r, c) || board.getCell(r, c).isBlue() || !board.getCell(r, c).isRevealed()) continue;

                int clue = board.getCell(r, c).getClue();
                int unrevealedNeighbors = 0; // Неоткрытые и нефлагованные соседи
                int flaggedNeighbors = 0;    // Соседи с флагами

                // Подсчитываем соседей
                for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                    if (neighbor == -1) continue;
                    int nr = neighbor / board.getGrid().getCols();
                    int nc = neighbor % board.getGrid().getCols();
                    if (!board.isActive(nr, nc)) continue;
                    if (!board.getCell(nr, nc).isRevealed() && !board.getCell(nr, nc).isFlagged()) unrevealedNeighbors++;
                    if (board.getCell(nr, nc).isFlagged()) flaggedNeighbors++;
                }

                // Подсказка 1: clue - flagged = unrevealed (все неоткрытые соседи синие)
                if (clue - flaggedNeighbors == unrevealedNeighbors && unrevealedNeighbors > 0) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed() || board.getCell(nr, nc).isFlagged()) continue;
                        hint = new int[]{nr, nc, 1}; // 1 = флаг (оранжевый)
                        return hint;
                    }
                }

                // Подсказка 2: clue - flagged = 0 (все неоткрытые соседи безопасны)
                if (clue - flaggedNeighbors == 0 && unrevealedNeighbors > 0) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed() || board.getCell(nr, nc).isFlagged()) continue;
                        hint = new int[]{nr, nc, 0}; // 0 = открыть (зелёный)
                        return hint;
                    }
                }
            }
        }

        // Случай 3: Нет детерминированных подсказок ("50 на 50")
        return null; // Бот не может помочь, игрок должен угадать
    }

    public int getBlueCount() {
        return board.getBlueCount();
    }

    public int getFlaggedCount() {
        int count = 0;
        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (board.isActive(r, c) && board.getCell(r, c).isFlagged()) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isWon() { return won; }
    public Board getBoard() { return board; }
}