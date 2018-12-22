import controller.Server;
import controller.ConsoleSpammer;
import model.Variables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static model.Variables.CONFIG_FILENAME;

public class Bootstrapper {

    private final Path mcPalLocationDir;
    private String backupPath;
    private String maxHeapSize;
    private String jarName;
//    private final Path worldName;
    private List<String> additionalCommandsToRunAfterBackup;

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Bootstrapper main = new Bootstrapper(args);
        main.bootServer();
    }

    public Bootstrapper(String... args) throws IOException, URISyntaxException {
        mcPalLocationDir = evaluatePathOfJar();

        if (args.length != 0) {
            final List<String> arguments = Arrays.asList(args);
            extractArgumentsFromCommandLine(arguments);
            writeConfigFile(mcPalLocationDir, args);
        } else if (args.length == 0 && Files.exists(mcPalLocationDir.resolve(CONFIG_FILENAME))) {
            final List<String> arguments = Files.readAllLines(mcPalLocationDir.resolve(CONFIG_FILENAME));
            Files.delete(mcPalLocationDir.resolve(CONFIG_FILENAME));
            extractArgumentsFromCommandLine(arguments);
        } else {
            throwInvalidStartArgumentsException();
        }

//        worldName = searchWorldName(mcPalLocationDir);

//        checkEula(mcPalLocationDir);
    }

    private Path evaluatePathOfJar() throws URISyntaxException {
        Path fromPath = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        if (!Files.exists(fromPath)) throw new IllegalArgumentException(Variables.SERVER_FILE_NOT_FOUND);
        return fromPath;
    }

    private void extractArgumentsFromCommandLine(List<String> arguments) {
        backupPath = extractSingleArgument(arguments, Variables.BACKUP_PATH_PREFIX);
        if (backupPath.isEmpty()) throwInvalidStartArgumentsException();
        additionalCommandsToRunAfterBackup = extractAdditionalArguments(arguments);
    }

    private void bootServer() throws IOException {
        final Server MCpal = new Server(mcPalLocationDir, backupPath, additionalCommandsToRunAfterBackup);
        MCpal.start();
    }

    private static boolean isOneOfThemNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) return true;
        }
        return false;
    }

    private static List<String> extractAdditionalArguments(List<String> arguments) {
        return arguments.stream()
                .filter(a -> a.startsWith(Variables.ADDITIONAL_ARGUMENT_PREFIX))
                .map(arg -> arg.substring(Variables.ADDITIONAL_ARGUMENT_PREFIX.length()))
                .collect(Collectors.toList());
    }

    private static String extractSingleArgument(List<String> arguments, String argumentPrefix) {
        return arguments.stream()
                .filter(arg -> arg.startsWith(argumentPrefix))
                .findFirst()
                .map(arg -> arg.substring(argumentPrefix.length()))
                .orElse(null);
    }

    private static Path searchWorldName(Path fromPath) {
        try {
            final DirectoryStream<Path> dirStream = Files.newDirectoryStream(fromPath);
            for (Path currentElement : dirStream) {
                if (Files.isDirectory(currentElement) && couldThisDirectoryPossiblyBeTheWorldFolder(currentElement)) {
                    return currentElement.getFileName();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new ConsoleSpammer(Variables.WORLD_DID_NOT_EXIST)).start();
        return null;
    }

    private static void checkEula(Path fromPath) {
        try {
            final Path eulaPath = fromPath.resolve("eula.txt");
            final File eulaFile = eulaPath.toFile();
            if (Files.exists(eulaPath)) {
                final List<String> readAllLines = Files.readAllLines(eulaPath);
                final StringBuilder sb = new StringBuilder();
                readAllLines.forEach(line -> sb.append(line + System.getProperty("line.separator")));
                String eula = sb.toString();
                if (eula.contains("eula=false")) {
                    eula = eula.replace("eula=false", "eula=true");
                    FileWriter fw = new FileWriter(eulaFile);
                    fw.write(eula);
                    fw.flush();
                    fw.close();
                }
            } else {
                new Thread(new ConsoleSpammer(Variables.EULA_NOT_FOUND)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean couldThisDirectoryPossiblyBeTheWorldFolder(Path currentElement) {
        Path dim1File = currentElement.resolve("DIM1");
        return Files.exists(dim1File);
    }

    private static void throwInvalidStartArgumentsException() {
        throw new IllegalStateException(Variables.INVALID_INPUT_PARAMETERS);
    }

    private static void writeConfigFile(Path fromPath, String[] args) throws IOException {
        if (!Files.exists(fromPath.resolve(CONFIG_FILENAME)))
            Files.createFile(Paths.get(fromPath + "/" + CONFIG_FILENAME));
        final FileWriter fw = new FileWriter(fromPath + "/" + CONFIG_FILENAME);
        for (String parameter : args) {
            fw.write(parameter + System.getProperty("line.separator"));
        }
        fw.flush();
        fw.close();
    }

}
