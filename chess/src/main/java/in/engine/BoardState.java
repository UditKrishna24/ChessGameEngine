package in.engine;

class BoardState {

    public static char notateCoin(Coin c) {
        char res = '\0';
        if (c == null) {
            return res;
        }
        if (c instanceof King) {
            res = 'K';
        } else if (c instanceof Queen) {
            res = 'Q';
        } else if (c instanceof Knight) {
            res = 'N';
        } else if (c instanceof Bishop) {
            res = 'B';
        } else if (c instanceof Rook) {
            res = 'R';
        } else {
            res = 'P';
        }
        if (c.isBlack) {
            res += (97 - 65);
        }
        return res;
    }

    public static String FEN(Board b) {
        String res = "";
        for (int row = 7; row >= 0; --row) {
            int nullPosn = 0;
            for (int col = 0; col < 8; ++col) {
                char coin = notateCoin(b.board[row][col].coin);
                if (coin == '\0') {
                    nullPosn++;
                } else {
                    if (nullPosn != 0) {
                        res += Integer.toString(nullPosn);
                        nullPosn = 0;
                    }
                    res += coin;
                }
            }
            if (nullPosn != 0) {
                res += Integer.toString(nullPosn);
            }
            res += '/';
        }
        return res.substring(0, res.length() - 1);
    }

    public static String stateString(Board b) {
        String cmd = "";
        cmd += FEN(b);
        cmd += " ";
        cmd += (b.isBlackTurn) ? ("b") : ("w");
        cmd += " ";
        boolean flag = false;
        if (b.board[0][4].coin != null && b.board[0][4].coin instanceof King king && king.canCastle) {
            if (b.board[0][7].coin != null && b.board[0][7].coin instanceof Rook rook && rook.canCastle) {
                cmd += "K";
                flag = true;
            }
            if (b.board[0][0].coin != null && b.board[0][0].coin instanceof Rook rook && rook.canCastle) {
                cmd += "Q";
                flag = true;
            }
        }
        if (b.board[7][4].coin != null && b.board[7][4].coin instanceof King king && king.canCastle) {
            if (b.board[7][7].coin != null && b.board[7][7].coin instanceof Rook rook && rook.canCastle) {
                cmd += "k";
                flag = true;
            }
            if (b.board[7][0].coin != null && b.board[7][0].coin instanceof Rook rook && rook.canCastle) {
                cmd += "q";
                flag = true;
            }
        }
        if (!flag) {
            cmd += "-";
        }
        cmd += " e3 ";//EnPassant Position
        cmd += Integer.toString(b.halfMoveClock);
        cmd += " ";
        cmd += Integer.toString(b.fullMoveNos);
        return cmd;
    }
}
