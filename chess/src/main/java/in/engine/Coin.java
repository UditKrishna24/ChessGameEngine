package in.engine;

import java.util.*;

interface coinPromotion {

    boolean promote(int toRow, int toCol, Square[][] board,char flag);
}

interface castling {

    boolean castle(int toRow, int toCol, Square[][] board);
}

abstract class Coin {

    boolean isBlack;//coin color

    Coin(boolean color) {
        this.isBlack = color;
    }

    abstract boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board);

    boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        if (fromRow == toRow) {
            for (int c = Math.min(fromCol, toCol) + 1; c < Math.max(toCol, fromCol); ++c) {
                if (board[toRow][c].coin != null) {
                    return false;
                }
            }
        } else if (fromCol == toCol) {
            for (int r = Math.min(fromRow, toRow) + 1; r < Math.max(toRow, fromRow); ++r) {
                if (board[r][toCol].coin != null) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) {
            return false;
        }
        int i = 1, j = 1;
        if (fromRow > toRow) {
            i = -1;
        }
        if (fromCol > toCol) {
            j = -1;
        }
        for (int r = fromRow + i, c = fromCol + j; r < Math.max(toRow, fromRow) && r > Math.min(toRow, fromRow); c += j, r += i) {
            if (board[r][c].coin != null) {
                return false;
            }
        }
        return true;
    }
}

class King extends Coin implements castling {

    boolean canCastle;

    King(boolean color) {
        super(color);
        canCastle = true;
    }

    @Override
    boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        return ((Math.abs(fromRow - toRow) <= 1) && (Math.abs(fromCol - toCol) <= 1));
    }

    @Override
    public boolean castle(int toRow, int toCol, Square[][] board) {
        boolean flag = false;
        if ((!canCastle) || (isBlack && toRow != 7) || (!isBlack && toRow != 0)); else if ((toCol == 2 && board[toRow][0].coin instanceof Rook) || (toCol == 6 && board[toRow][7].coin instanceof Rook)) {
            Rook rook = (Rook) board[toRow][(toCol == 2) ? (0) : (7)].coin;
            if ((rook.canCastle) && (rook.move(toRow, (toCol == 2) ? (0) : (7), toRow, 4, board))) {
                flag = true;
            }
        }
        return flag;
    }
}

class Queen extends Coin {

    Queen(boolean color) {
        super(color);
    }

    @Override
    boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        return (isValidBishopMove(fromRow, fromCol, toRow, toCol, board) || isValidRookMove(fromRow, fromCol, toRow, toCol, board));
    }
}

class Bishop extends Coin {

    Bishop(boolean color) {
        super(color);
    }

    @Override
    boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        return isValidBishopMove(fromRow, fromCol, toRow, toCol, board);
    }
}

class Rook extends Coin {

    boolean canCastle;

    Rook(boolean color) {
        super(color);
        canCastle = true;
    }

    @Override
    boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        return isValidRookMove(fromRow, fromCol, toRow, toCol, board);
    }
}

class Knight extends Coin {

    Knight(boolean color) {
        super(color);
    }

    @Override
    boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        if (Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 1) {
            return true;
        } else if (Math.abs(fromRow - toRow) == 1 && Math.abs(fromCol - toCol) == 2) {
            return true;
        }
        return false;
    }
}

class Pawn extends Coin implements coinPromotion {

    Pawn(boolean color) {
        super(color);
    }

    @Override
    boolean move(int fromRow, int fromCol, int toRow, int toCol, Square[][] board) {
        boolean flag = false;
        if (Math.abs(toCol - fromCol) == 0) {
            if (isBlack && (fromRow - toRow == 1) || (fromRow == 6 && toRow == 4)) {
                flag = true;
            } else if (!isBlack && (toRow - fromRow == 1 || (fromRow == 1 && toRow == 3))) {
                flag = true;
            }
        } else if (Math.abs(toCol - fromCol) == 1 && ((!isBlack && toRow - fromRow == 1) || (isBlack && fromRow - toRow == 1)) && board[toRow][toCol].coin != null) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean promote(int toRow, int toCol, Square[][] board,char flag) {
        char promoteCoin = flag;
        switch (promoteCoin) {
            case 'q':
                board[toRow][toCol].coin = new Queen(isBlack);
                break;
            case 'r':
                board[toRow][toCol].coin = new Rook(isBlack);
                break;
            case 'n':
                board[toRow][toCol].coin = new Knight(isBlack);
                break;
            case 'b':
                board[toRow][toCol].coin = new Bishop(isBlack);
                break;
            default:
                return false;
        }
        return true;
    }
}
