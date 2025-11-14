package ui;

public class OftenPrinted {
    public static String PRELOGINHELP =
            "help - List possible commands\n" +
                    "quit - Exits the program\n" +
                    "login <username> <password> - Login to play chess\n" +
                    "register <username> <password> <email> - to register for an account";

    public static String LOGINHELP =
            "help - List possible commands\n" +
                    "logout - logout of chess\n" +
                    "create <NAME> - Create a game with the given name\n" +
                    "list - list the games\n" +
                    "join <ID> [WHITE|BLACK] - join game <ID> on a certain side\n" +
                    "observe <ID> - Watch game <ID>\n" +
                    "quit - quite the program";
    public static String LOGGEDOUT_HEADER = "[LOGGED_OUT]>>> ";
    public static String LOGGEDIN_HEADER = "[LOGGED_IN]>>> ";
}
