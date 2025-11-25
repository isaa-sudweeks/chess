package ui;

import exception.ResponseException;
import serverfacade.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.OftenPrinted.LOGGEDOUT_HEADER;

public class CLI {
    private ServerFacade serverFacade;
    private CommandHelper helper;

    public CLI(String serverURL) {

        this.serverFacade = new ServerFacade(serverURL);
        try {
            this.helper = new CommandHelper(serverFacade, serverURL);
        } catch (ResponseException e) {
            System.out.println("There was an error setting up the server");
        }
    }

    public void run() {
        System.out.println(ERASE_SCREEN +
                SET_TEXT_BOLD +
                SET_TEXT_COLOR_BLUE +
                "♚ Welcome to CS240 Chess ♚" +
                RESET_ALL_FORMATTING);
        System.out.println(
                SET_TEXT_COLOR_LIGHT_GREY +
                        "Type help at any time to see the available commands." +
                        RESET_ALL_FORMATTING);
        Scanner scanner = new Scanner(System.in);
        var exit = false;
        System.out.print(LOGGEDOUT_HEADER);
        while (!exit) {
            String command = scanner.nextLine();
            String output = helper.commandHelper(command);
            if (output.equalsIgnoreCase("exit")) {
                break;
            }
            System.out.print(output);
        }
    }
}
