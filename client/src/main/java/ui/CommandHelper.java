package ui;

import ServerFacade.ServerFacade;
import exception.ResponseException;
import model.LoginRequest;
import model.RegisterLoginResult;
import model.RegisterRequest;

import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.OftenPrinted.*;

public class CommandHelper {
    private ServerFacade serverFacade;
    private int state = 0;
    private String authToken;

    public CommandHelper(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String commandHelper(String command) {
        switch (state) {
            case 0:
                return preLogin(command);
            case 1:
                return postLogin(command);
            default:
                return "";
        }
    }

    private String postLogin(String command) {
        if (command.equalsIgnoreCase("help")) {
            return LOGINHELP;
        } else {
            return "The command " + command + " is unknown type help to get a list of commands\n" + LOGGEDIN_HEADER;
        }
    }

    private String preLogin(String command) {
        if (command.equalsIgnoreCase("help")) {
            return PRELOGINHELP;
        }
        if (command.equalsIgnoreCase("quit")) {
            return "exit";
        }
        if (command.contains("register")) {
            String[] parts = command.split(" ");
            try {
                RegisterLoginResult result = serverFacade.register(new RegisterRequest(parts[1], parts[2], parts[3]));
                this.authToken = result.authToken();
                state = 1;
                return ERASE_SCREEN + "\n Welcome to chess " + result.username();
            } catch (ResponseException e) {
                return "There was an error: " + e.getMessage() + "\n" + LOGGEDOUT_HEADER;
            }
        }
        if (command.contains("login")) {
            String[] parts = command.split(" ");
            try {
                RegisterLoginResult result = serverFacade.login(new LoginRequest(parts[1], parts[2]));
                this.authToken = result.authToken();
                state = 1;
                return ERASE_SCREEN + "\n Welcome to chess " + result.username() + "\n" + LOGGEDIN_HEADER;
            } catch (ResponseException e) {
                return "There was an error: " + e.getMessage() + "\n" + LOGGEDOUT_HEADER;
            }
        }
        return "The command " + command + " was not recognized type help to get a list of commands\n" + LOGGEDOUT_HEADER;
    }
}
