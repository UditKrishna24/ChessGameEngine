package in.engine;

import java.io.*;

public class StockfishEngine {

    static StockfishEngine bot;
    private Process engineProcess;
    private BufferedReader reader;
    private BufferedWriter writer;

    private StockfishEngine() {

    }

    public static StockfishEngine getInstance() {
        if (bot == null) {
            bot = new StockfishEngine();
        }
        return bot;
    }

    public boolean startEngine(String path) {
        try {
            engineProcess = new ProcessBuilder(path).redirectErrorStream(true).start();
            reader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public String readOutput() throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            if (line.contains("bestmove") || line.contains("uciok") || line.contains("readyok")) {
                break;  // Wait only for critical responses

            }
        }
        return output.toString();
    }

    public void stopEngine() throws IOException {
        sendCommand("quit");
        reader.close();
        writer.close();
        engineProcess.destroy();
    }

    // 🔍 Get best move given a FEN or startpos+moves
    public String getBestMove(String state) throws IOException {
        sendCommand("uci");
        readOutput(); // wait for uciok
        sendCommand("isready");
        readOutput(); // wait for readyok
        sendCommand("ucinewgame");
        sendCommand("position fen " + state);
        sendCommand("go movetime 1000");
        String output = readOutput();
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1]; // e.g., "e7e5"
            }
        }
        return "no move found";
    }
}
