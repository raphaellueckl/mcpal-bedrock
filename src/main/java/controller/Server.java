package controller;

import model.Variables;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static model.Variables.MCPAL_TAG;

/**
 * Additional ideas:
 * - Count the players on the server. If 0, it's unnecessary to wait 10 seconds before stopping it (for instance)
 * - Custom parameters with default values
 * - Fix the console input
 */
public class Server {

    public static volatile boolean isServerRunning = false;

    public static Path MC_PAL_LOCATION_DIR;
    public static Path BACKUP_TARGET_DIR_PATH;

    private static List<String> ADDITIONAL_COMMANDS_AFTER_BACKUP;
    private static Thread consoleThread;
    private static Thread consoleWriterThread;
    public static volatile Process serverProcess;

    public Server(Path mcPalLocationDir, String targetDir, List<String> additionalThingsToRun) {
        MC_PAL_LOCATION_DIR = mcPalLocationDir;
        BACKUP_TARGET_DIR_PATH = Paths.get(targetDir);
        ADDITIONAL_COMMANDS_AFTER_BACKUP = additionalThingsToRun;

        printStartupInfo();
    }

    private void printStartupInfo() {
        System.out.println("***********************");
        System.out.println("Path of the server:   " + MC_PAL_LOCATION_DIR);
        System.out.println("Path for the backups: " + BACKUP_TARGET_DIR_PATH);
        System.out.println("Additional commands:  ");
        ADDITIONAL_COMMANDS_AFTER_BACKUP.forEach(c -> System.out.println("                      " + c));
        System.out.println("***********************");
    }

    public void start() throws IOException {
        startMinecraftServer();

        consoleWriterThread = new Thread(new ConsoleInput());
        consoleWriterThread.start();

        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        int oneDayInSeconds = 86400;
        int secondsUntil4Am = calculateTimeInSecondsTo4AM();
        service.scheduleWithFixedDelay(Server::backupServer, secondsUntil4Am, oneDayInSeconds, TimeUnit.SECONDS);
    }

    private int calculateTimeInSecondsTo4AM() {
        final LocalDateTime now = LocalDateTime.now();
        LocalDateTime secondsTo4AM = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 4, 0);
        if (secondsTo4AM.isBefore(now)) secondsTo4AM = secondsTo4AM.plusDays(1);
        System.out.println(MCPAL_TAG + "Time until Backup starts: " + twoDigitFormat(String.valueOf(ChronoUnit.HOURS.between(now, secondsTo4AM))) +
                ":" + twoDigitFormat(String.valueOf(ChronoUnit.MINUTES.between(now, secondsTo4AM) % 60)) + " h");
        return (int) ChronoUnit.SECONDS.between(now, secondsTo4AM);
    }

    private static String twoDigitFormat(String term) {
        return term.length() < 2 ? "0" + term : term;
    }

    public static void startMinecraftServer() {

        if (isServerRunning) {
            System.out.println(Variables.SERVER_ALREADY_RUNNING);
        } else {
            Process process = null;
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder("./bedrock_server");
                processBuilder.environment().put("LD_LIBRARY_PATH", ".");
                processBuilder.directory(MC_PAL_LOCATION_DIR.toFile());
                process = processBuilder.start();

                if (consoleThread != null) consoleThread.interrupt();
                consoleThread = new Thread(new MinecraftConsole(process.getInputStream()));
                consoleThread.start();

                isServerRunning = true;

            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                serverProcess = process;
            }

        }
    }

    public static void stopMinecraftServer(Process process, String reason, boolean runWithCountdown) {
        if (isServerRunning) {
            final PrintWriter consoleWriter = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
            if (runWithCountdown) printCountDown(consoleWriter, reason);

            consoleWriter.println("stop");
            consoleWriter.flush();
            //w.close();
            try {
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                process.destroy();
                isServerRunning = false;
            }
        } else {
            System.out.println(Variables.SERVER_ALREADY_STOPPED);
        }
    }

    private static void printCountDown(PrintWriter w, String reason) {
        w.println("say " + reason + " begins in 10...");
        w.flush();
        try {Thread.sleep(1000);} catch (InterruptedException e) {}
        for (int i=9; i>0; --i) {
            w.println("say " + i + "...");
            w.flush();
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
        }
        w.println("say GAME OVER!!!!!!!!!!!!!");
        w.flush();
        try {Thread.sleep(200);} catch (InterruptedException e) {}
    }

    public static synchronized void backupServer() {
        try {
            Server.stopMinecraftServer(Server.serverProcess, ":Server backup:", true);

            Thread.sleep(2000);

            Files.createDirectories(BACKUP_TARGET_DIR_PATH);
            final Backup backupHandler = new Backup(MC_PAL_LOCATION_DIR, BACKUP_TARGET_DIR_PATH);
            final FutureTask<String> futureTask = new FutureTask<>(backupHandler);
            new Thread(futureTask).start();
            final String backupStorePath = futureTask.get();

            final List<String> commandListClone = new ArrayList<>(ADDITIONAL_COMMANDS_AFTER_BACKUP);
            commandListClone.replaceAll(command -> command.replace("{2}", backupStorePath));
            new Thread(() -> {
                for (String command : commandListClone) {
                    final List<String> parametersOfCommand = Arrays.asList(command.split(" "));
                    final ProcessBuilder processBuilder = new ProcessBuilder(parametersOfCommand);
                    processBuilder.environment().put(Variables.ENVIRONMENT_VARIABLE_CURRENT_BACKUP_DIR_PATH, backupStorePath);
                    try {
                        processBuilder.start();
                        System.out.println(MCPAL_TAG + "Process successful: " + parametersOfCommand.get(0));
                    } catch (IOException e) {
                        System.out.println(MCPAL_TAG + "The following process failed: " + command);
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread.sleep(2000);
            startMinecraftServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}