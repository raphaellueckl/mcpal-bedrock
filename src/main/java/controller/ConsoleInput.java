package controller;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class ConsoleInput implements Runnable {
	
	private Process sProcess;
	private PrintWriter consolePrinter;

	@Override
	public void run() {
		final Scanner scan = new Scanner(System.in);
		this.sProcess = Server.serverProcess;
		consolePrinter = new PrintWriter(new OutputStreamWriter(Server.serverProcess.getOutputStream()));
		String msg;
		while (!Thread.currentThread().isInterrupted()) {
			msg = scan.nextLine();
			handleCommandMessage(msg);
		}
		scan.close();
	}

	private void handleCommandMessage(String msg) {
		if (msg.equals("stop")) {
			Server.stopMinecraftServer(Server.serverProcess, ":Server stop:", true);
		} else if (msg.equals("istop")) {
			Server.stopMinecraftServer(Server.serverProcess, ":Server stop:", false);
		} else if (msg.startsWith("stop")) {
			Server.stopMinecraftServer(Server.serverProcess, ":Server stop:", true);
		} else if (msg.equals("start")) {
			Server.startMinecraftServer();
		} else if (msg.equals("backup")) {
			Server.backupServer();
		} else if (msg.equals("help")) {
			printToConsole("Here is a list of MCpal's commands you can use here on the console:\n" +
					"stop       Shutdown the server\n" +
					"start      Start the server\n" +
					"backup     Shutdown, backup, restart the server\n\n" +
					"for further information about the addidtional commands, type \"help-commands\"");
		} else if (msg.equals("help-commands")) {
			printToConsole(
					"You can add additional calls for other programs. What does this mean?\n" +
							"If you write \"c:COMMAND\", MCpal will identify that everything after \"c:\" is a " +
							"command that should be called in a separate process." +
							"Example: If you installed \"Minecraft Overviewer\" and want to create a Minecraft Map " +
							"after every backup, just use this example-command (linux):\n" +
							"\"c:overviewer.py --rendermodes=smooth-lighting {2}/world /home/username/minecraft_server/map\"\n" +
							"This will execute the overviewer in a separate background-process using your newly created backup.\n" +
							"{2} is a dynamic argument. If you wanna find out what to do with them, type \"help-dynargs\"");
		} else if (msg.equals("help-dynargs")) {
			printToConsole("For the additional arguments, you can use some arguments that will be replaced before executing the process:\n" +
					"{1} => The name of your minecraft-world\n" +
					"{2} => The path to the the last backup that has been done.\n" +
					"Example: If you write \"{2}/minecraft_server.jar\", \"{2}\" will be replaced with " +
					"\"PATH_TO_YOUR_LAST_BACKUP/minecraft_server.jar\"");
		} else {
			printToConsole(msg);
		}
	}

	private void printToConsole(String msg) {
		if (!sProcess.equals(Server.serverProcess)) {
			consolePrinter = new PrintWriter(Server.serverProcess.getOutputStream());
			sProcess = Server.serverProcess;
		}
		consolePrinter.println(msg);
		consolePrinter.flush();
	}
}
