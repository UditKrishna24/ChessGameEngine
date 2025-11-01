package in.engine;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

class ChessBoardGUI extends JFrame {

    private static ChessBoardGUI instance;
    private final JPanel boardPanel;
    private final JLabel turnLabel;
    private final HashMap<String, ImageIcon> imageMap = new HashMap<>();

    private ChessBoardGUI() {
        setTitle("Chess Board");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        turnLabel = new JLabel("White's Turn", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(turnLabel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridBagLayout());
        add(boardPanel, BorderLayout.CENTER);

        loadImages();
        setVisible(true);
    }

    private void loadImages() {
        String[] pieces = {"wK", "wQ", "wR", "wB", "wN", "wP", "bK", "bQ", "bR", "bB", "bN", "bP"};
        for (String piece : pieces) {
            try {
                ImageIcon icon = new ImageIcon("src/main/resources/chessPieces/" + piece + ".png");
                Image scaled = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                imageMap.put(piece, new ImageIcon(scaled));
            } catch (Exception e) {
                System.err.println("Missing image: " + piece + ".png");
            }
        }
    }

    private void updateBoard(Board board) {
        boardPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();

        turnLabel.setText(board.isBlackTurn ? "Black's Turn" : "White's Turn");

        int fromRow = -1, fromCol = -1, toRow = -1, toCol = -1;
        String move = board.prevMove();
        if (move.length() != 0) {

            if (move.length() >= 5) {
                fromRow = move.charAt(1) - '0';
                fromCol = move.charAt(2) - '0';
                toRow = move.charAt(3) - '0';
                toCol = move.charAt(4) - '0';
            }
        }

        gbc.gridy = 0;
        for (int col = 0; col <= 8; col++) {
            gbc.gridx = col;
            if (col == 0) {
                boardPanel.add(new JLabel(" "), gbc);
            } else {
                JLabel label = new JLabel(String.valueOf((char) ('a' + col - 1)), SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(64, 20));
                boardPanel.add(label, gbc);
            }
        }

        for (int row = 7; row >= 0; row--) {
            gbc.gridy = 8 - row;

            for (int col = 0; col <= 8; col++) {
                gbc.gridx = col;

                if (col == 0) {
                    JLabel label = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
                    label.setPreferredSize(new Dimension(20, 64));
                    boardPanel.add(label, gbc);
                } else {
                    JPanel square = new JPanel(new BorderLayout());
                    square.setOpaque(true);

                    boolean isHighlight = (row == fromRow && col - 1 == fromCol) || (row == toRow && col - 1 == toCol);
                    Color baseColor = (row + col - 1) % 2 == 0 ? Color.WHITE : Color.GRAY;
                    square.setBackground(isHighlight ? Color.YELLOW : baseColor);

                    // Force paint even if no component inside
                    square.setPreferredSize(new Dimension(64, 64));

                    Coin coin = board.board[row][col - 1].coin;
                    if (coin != null) {
                        String prefix = coin.isBlack ? "b" : "w";
                        String type = coin instanceof King ? "K"
                                : coin instanceof Queen ? "Q"
                                        : coin instanceof Rook ? "R"
                                                : coin instanceof Bishop ? "B"
                                                        : coin instanceof Knight ? "N" : "P";
                        JLabel pieceLabel = new JLabel(imageMap.get(prefix + type));
                        square.add(pieceLabel);
                    }

                    boardPanel.add(square, gbc);
                }
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public static ChessBoardGUI getInstance() {
        if (instance == null) {
            instance = new ChessBoardGUI();
        }
        return instance;
    }

    public void displayBoard(Board board) {
        updateBoard(board);
    }
}
