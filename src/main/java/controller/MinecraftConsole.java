package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MinecraftConsole implements Runnable {

    private final BufferedReader consoleInputReader;

    public MinecraftConsole(InputStream consoleInputStream) {
        consoleInputReader = new BufferedReader(new InputStreamReader(consoleInputStream));
    }

    @Override
    public void run() {
        String line;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if ((line = consoleInputReader.readLine()) == null) break;
                System.out.println(line);
                if (line.contains("Stopping the server")) Server.isServerRunning = false;
            }
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }
}
