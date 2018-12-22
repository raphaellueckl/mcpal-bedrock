package controller;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class Backup implements Callable<String> {

    private final Path sourceDirPath;
    private Path targetDirPath;

    public Backup(Path sourceDirPath, Path targetDirPath) {
        this.sourceDirPath = sourceDirPath;
        this.targetDirPath = targetDirPath;
    }

    @Override
    public String call() throws Exception {
        final String backupFolderName = evaluateNewBackupFolderName();
        targetDirPath = targetDirPath.resolve(backupFolderName);
        sync(sourceDirPath);
        return targetDirPath.toString();
    }

    private static String evaluateNewBackupFolderName() throws IOException {
        final LocalDateTime now = LocalDateTime.now();
        return String.valueOf(now.getYear()) +
                (twoDigitFormat(String.valueOf(now.getMonth().getValue())) +
                twoDigitFormat(String.valueOf(now.getDayOfMonth())) + "_" +
                twoDigitFormat(String.valueOf(now.getHour())) + twoDigitFormat(String.valueOf(now.getMinute())));
    }

    //TODO Outsource
    private static String twoDigitFormat(String term) {
        return term.length() < 2 ? "0" + term : term;
    }

    private int sync(Path currentFolder) {
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentFolder);
            for (final Path currentElement : directoryStream) {
                if (Files.isDirectory(currentElement)) sync(currentElement);
                else if (Files.isRegularFile(currentElement)) {
                    final Path relativePath = sourceDirPath.relativize(currentElement);
                    final Path targetPath = targetDirPath.resolve(relativePath);
                    if (!Files.exists(targetPath))
                        Files.createDirectories(targetPath.getParent());
                    Files.copy(currentElement, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
