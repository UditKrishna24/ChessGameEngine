package in.engine;

import java.io.PrintStream;
import java.util.*;

final class Board {

    static Board b;
    int halfMoveClock, fullMoveNos;
    boolean isMate;
    boolean isCheck;
    int blackPoints, whitePoints;
    boolean isBlackTurn;
    Square[][] board;
    private Stack<Coin> captured;
    private Stack<String> prevMoves;
    private int kingRowBlack, kingColBlack;
    private int kingRowWhite, kingColWhite;
    char flag;
    PrintStream out;
    String prevMove() {
        if (prevMoves.isEmpty()) {
            return "";
        } else {
            return prevMoves.peek();
        }
    }

    private boolean check(boolean checkForBlack) {
        for (int r = 0; r < 8; ++r) {
            for (int c = 0; c < 8; ++c) {
                if (board[r][c].coin == null) {
                    continue;
                }
                if (board[r][c].coin.isBlack == checkForBlack) {
                    continue;
                }
                if (board[r][c].coin.isBlack) {
                    if (board[r][c].coin.move(r, c, kingRowWhite, kingColWhite, board)) {
                        //out.println(r + " " + c);
                        return true;
                    }
                } else {
                    if (board[r][c].coin.move(r, c, kingRowBlack, kingColBlack, board)) {
                        //out.println(r + "   " + c);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean anyLegalMoveForCoin(int row, int col) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (move(row, col, i, j, false)) {
                    // out.println("From " + row + " " + col + " To " + i + " " + j);
                    undo();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkMate(boolean checkMateForBlack) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j].coin == null
                        || board[i][j].coin.isBlack != checkMateForBlack) {
                    continue;
                }
                if (anyLegalMoveForCoin(i, j)) {
                    return false;
                }
            }
        }
        isMate = true;
        return true;
    }

    void undo() {
        if (prevMoves.isEmpty()) {
            return;
        }
        isBlackTurn = !isBlackTurn;
        boolean isPromoted = false;
        if (!prevMoves.isEmpty() && prevMoves.peek().equals("PROMOTED")) {
            isPromoted = true;
            prevMoves.pop();
        }
        String prevMove = prevMoves.pop();
        halfMoveClock = Integer.parseInt(prevMove.substring(5));
        fullMoveNos--;
        int fromRow = prevMove.charAt(1) - 48, fromCol = prevMove.charAt(2) - 48;
        int toRow = prevMove.charAt(3) - 48, toCol = prevMove.charAt(4) - 48;
        if (prevMove.charAt(0) == '1') {
            switch (board[toRow][toCol].coin) {
                case King king ->
                    king.canCastle = true;
                case Rook rook ->
                    rook.canCastle = true;
                default -> {
                }
            }
        }
        board[fromRow][fromCol].coin = board[toRow][toCol].coin;
        board[toRow][toCol].coin = captured.pop();
        if (board[fromRow][fromCol].coin instanceof King king) {
            if (Math.abs(fromCol - toCol) > 1) {
                if (fromCol > toCol) {
                    board[toRow][0].coin = board[toRow][toCol + 1].coin;
                } else {
                    board[toRow][7].coin = board[toRow][toCol - 1].coin;
                }
                king.canCastle = true;
                Rook rook = (Rook) board[toRow][(fromCol > toCol) ? (0) : (7)].coin;
                rook.canCastle = true;
            }
            if (board[fromRow][fromCol].coin.isBlack) {
                kingRowBlack = fromRow;
                kingColBlack = fromCol;
            } else {
                kingRowWhite = fromRow;
                kingColBlack = fromCol;
            }
        }
        if (isPromoted) {
            board[fromRow][fromCol].coin = new Pawn(board[fromRow][fromCol].coin.isBlack);
        }
        if (board[toRow][toCol].coin == null) {
            return;
        }
        if (board[toRow][toCol].coin.isBlack) {
            whitePoints -= pointOf(board[toRow][toCol].coin);
        } else {
            blackPoints -= pointOf(board[toRow][toCol].coin);
        }
        isCheck = check(isBlackTurn);
    }

    private int pointOf(Coin captureCoin) {
        int point;
        if (captureCoin == null) {
            point = 0;
        } else if (captureCoin instanceof Rook) {
            point = 5;
        } else if (captureCoin instanceof Queen) {
            point = 9;
        } else if (captureCoin instanceof Bishop || captureCoin instanceof Knight) {
            point = 3;
        } else {
            point = 1;
        }
        return point;
    }

    private boolean castle(int fromRow, int fromCol, int toRow, int toCol) {
        if (board[fromRow][fromCol].coin instanceof King && !check(isBlackTurn)) {
            King king = (King) board[fromRow][fromCol].coin;
            if (!king.castle(toRow, toCol, board)) {
                return false;
            }
            Rook rook = (Rook) board[toRow][(toCol == 2) ? (0) : (7)].coin;
            board[toRow][(toCol == 2) ? (0) : (7)].coin = null;
            board[toRow][(toCol == 2) ? (3) : (5)].coin = king;
            board[toRow][fromCol].coin = null;
            if (isBlackTurn) {
                kingColBlack = (toCol == 2) ? (3) : (5);
            } else {
                (kingColWhite) = (toCol == 2) ? (3) : (5);
            }
            if (check(isBlackTurn)) {
                board[toRow][fromCol].coin = king;
                board[toRow][(toCol == 2) ? (3) : (5)].coin = null;
                board[toRow][(toCol == 2) ? (0) : (7)].coin = rook;
                if (isBlackTurn) {
                    kingColBlack = fromCol;
                } else {
                    kingColWhite = fromCol;
                }
                return false;
            }
            if (isBlackTurn) {
                kingColBlack = (toCol == 2) ? (2) : (6);
            } else {
                kingColWhite = (toCol == 2) ? (2) : (6);
            }
            board[toRow][(toCol == 2) ? (2) : (6)].coin = king;
            board[toRow][(toCol == 2) ? (3) : (5)].coin = null;
            if (check(isBlackTurn)) {
                board[toRow][fromCol].coin = king;
                board[toRow][(toCol == 2) ? (2) : (6)].coin = null;
                board[toRow][(toCol == 2) ? (0) : (7)].coin = rook;
                if (isBlackTurn) {
                    kingColBlack = fromCol;
                } else {
                    kingColWhite = fromCol;
                }
                return false;
            }
            if (isBlackTurn) {
                kingColBlack = fromCol;
            } else {
                kingColWhite = fromCol;
            }
            board[toRow][(toCol == 2) ? (3) : (5)].coin = rook;
            board[toRow][fromCol].coin = king;
            board[toRow][(toCol == 2) ? (2) : (6)].coin = null;
            rook.canCastle = false;
            return true;
        }
        return false;
    }

    private boolean promote(int toRow, int toCol) {
        if ((board[toRow][toCol].coin instanceof Pawn) && (toRow == '7' || toRow == '0')) {
            Pawn pawn = (Pawn) board[toRow][toCol].coin;
            if (!pawn.promote(toRow, toCol, board,flag)) {
                undo();
                return false;
            }
            prevMoves.push("PROMOTED");
        }
        return true;
    }

    private boolean enPassant(int fromRow, int fromCol, int toRow, int toCol) {
        if (prevMoves.isEmpty() || !(board[fromRow][fromCol].coin instanceof Pawn)); else if ((board[fromRow][fromCol].coin.isBlack) ? (fromRow != 3 || toRow != 2) : (fromRow != 4 || toRow != 5)); else {
            String prevMove = prevMoves.peek();
            if ((!board[fromRow][fromCol].coin.isBlack) ? (prevMove.charAt(1) != '6' || prevMove.charAt(3) != '4') : (prevMove.charAt(1) != 1 || prevMove.charAt(3) != '3')) {
            } else if (!(board[fromRow][toCol].coin instanceof Pawn)); else if (prevMove.charAt(2) == prevMove.charAt(4) && (prevMove.charAt(2) == toCol + 48)) {
                board[toRow][toCol].coin = board[fromRow][toCol].coin;
                board[fromRow][toCol].coin = null;
                return true;
            }
        }
        return false;
    }

    boolean move(int fromRow, int fromCol, int toRow, int toCol, boolean verbose) {
        if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7) {
            if (verbose) {
                out.println("Move out of Bound...");
            }
            return false;
        }
        if (toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
            if (verbose) {
                out.println("Move out of Bound...");
            }
            return false;
        }
        if (board[fromRow][fromCol].coin == null) {
            if (verbose) {
                out.println("No Coin is there...");
            }
            return false;
        }
        if (board[fromRow][fromCol].coin.isBlack != isBlackTurn) {
            if (verbose) {
                out.println("Its not your Turn...");
            }
            return false;
        }
        if (!board[fromRow][fromCol].coin.move(fromRow, fromCol, toRow, toCol, board)) {
            if (castle(fromRow, fromCol, toRow, toCol)); else if (enPassant(fromRow, fromCol, toRow, toCol)); else {
                if (verbose) {
                    out.println("The coin move is not feasible...");
                }
                return false;
            }
        }
        if (board[toRow][toCol].coin != null) {
            if (board[toRow][toCol].coin.isBlack == board[fromRow][fromCol].coin.isBlack) {
                if (verbose) {
                    out.println("Don't Capture your army.....");
                }
                return false;
            }
            captured.push(board[toRow][toCol].coin);
            int point = pointOf(board[toRow][toCol].coin);
            if (board[toRow][toCol].coin.isBlack) {
                whitePoints += point;
            } else {
                blackPoints += point;
            }
        } else {
            captured.push(null);
        }
        char castleFlag = '0';
        switch (board[fromRow][fromCol].coin) {
            case King king -> {
                if (board[fromRow][fromCol].coin.isBlack) {
                    kingColBlack = toCol;
                    kingRowBlack = toRow;
                } else {
                    kingColWhite = toCol;
                    kingRowWhite = toRow;
                }
                if (king.canCastle) {
                    castleFlag = '1';
                }
                king.canCastle = false;
            }
            case Rook rook -> {
                if (rook.canCastle) {
                    castleFlag = '1';
                }
                rook.canCastle = false;
            }
            default -> {
            }
        }
        String str = "";
        str += castleFlag;
        str += (char) (fromRow + 48);
        str += (char) (fromCol + 48);
        str += (char) (toRow + 48);
        str += (char) (toCol + 48);
        str += Integer.toString(halfMoveClock);
        prevMoves.push(str);
        board[toRow][toCol].coin = board[fromRow][fromCol].coin;
        board[fromRow][fromCol].coin = null;
        boolean flag = check(isBlackTurn);
        isBlackTurn = !isBlackTurn;
        if (flag) {
            undo();
            if (verbose) {
                out.println("There is Check on move...");
            }
            return false;
        }
        if (!promote(toRow, toCol)) {
            return false;
        }
        isCheck = check(isBlackTurn);
        if (verbose) {
            isMate = checkMate(isBlackTurn);
        }
        if (prevMoves.peek().equals("PROMOTED") || captured.peek() != null || board[toRow][toCol].coin instanceof Pawn) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }
        fullMoveNos++;
        return true;
    }

    void resetBoard() {
        captured = new Stack<>();
        prevMoves = new Stack<>();
        captured.push(null);
        blackPoints = 0;
        whitePoints = 0;
        halfMoveClock = 0;
        fullMoveNos = 0;
        isMate = false;
        isCheck = false;
        isBlackTurn = false;
        kingColBlack = 'e' - 97;
        kingColWhite = 'e' - 97;
        kingRowBlack = 7;
        kingRowWhite = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                board[i][j].coin = null;
            }
        }
        for (int i = 0; i < 8; ++i) {
            board[6][i].coin = new Pawn(true);
            board[1][i].coin = new Pawn(false);
            switch (i) {
                case 0, 7 -> {
                    board[7][i].coin = new Rook(true);
                    board[0][i].coin = new Rook(false);
                }
                case 1, 6 -> {
                    board[7][i].coin = new Knight(true);
                    board[0][i].coin = new Knight(false);
                }
                case 2, 5 -> {
                    board[7][i].coin = new Bishop(true);
                    board[0][i].coin = new Bishop(false);
                }
                case 3 -> {
                    board[7][i].coin = new Queen(true);
                    board[0][i].coin = new Queen(false);
                }
                default -> {
                    board[7][i].coin = new King(true);
                    board[0][i].coin = new King(false);
                }
            }
        }
    }

    private Board() {
        board = new Square[8][8];

        boolean colorStart = false;
        for (int i = 0; i < 8; ++i) {
            boolean colorCurrent = colorStart;
            for (int j = 0; j < 8; ++j) {
                board[i][j] = new Square(colorCurrent);
                colorCurrent = !colorCurrent;
            }
            colorStart = !colorStart;
        }
        resetBoard();
    }

    public static Board getInstance(PrintStream outStream) {
        if (b == null) {
            b = new Board();
            b.out=outStream;
        }
        return b;
    }
}
