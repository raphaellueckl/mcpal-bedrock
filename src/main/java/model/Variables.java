package model;

public class Variables {
    public static final String CONFIG_FILENAME = "MCpal.cfg";
    public static final String MCPAL_TAG = "#MCpal: ";

    public static final String BACKUP_PATH_PREFIX = "b:";
    public static final String RAM_PREFIX = "r:";
    public static final String SERVER_JAR_PREFIX = "j:";
    public static final String ADDITIONAL_ARGUMENT_PREFIX = "a:";

    public static final String ENVIRONMENT_VARIABLE_CURRENT_BACKUP_DIR_PATH = "CURRENT_BACKUP_DIR_PATH";

    public static final String WORLD_DID_NOT_EXIST = MCPAL_TAG + "The world didn't exist when MCpal was started. Please " +
            "restart MCpal and it will handle that.";
    public static final String EULA_NOT_FOUND = MCPAL_TAG + "NO EULA FOUND!! Just restart MCpal, the eula will be " +
            "set to true automatically!";
    public static final String INVALID_INPUT_PARAMETERS = "Invalid Input Parameters. Please start MCpal like this:\n" +
            "java -jar MCpal.jar b:PATH_TO_BACKUP_FOLDER r:MAX_RAM j:NAME_OF_MINECRAFT_SERVER_JAR\n" +
            "Example: java -jar MCpal.jar b:\"C:\\Users\\Rudolf Ramses\\Minecraft_Server\" r:1024 j:minecraft_server.jar";
    public static final String SERVER_FILE_NOT_FOUND = "Couldn't find the Minecraft server file. " +
            "Please put MCpal into your Minecraft server directory.";
    public static final String SERVER_ALREADY_STOPPED = MCPAL_TAG + "Nothing to stop. Server is not active at the moment.";
    public static final String SERVER_ALREADY_RUNNING = MCPAL_TAG + "Server is already running, please stop it first using the \"stop\"-command";
}
