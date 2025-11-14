package ui;

import ServerFacade.ServerFacade;

import java.util.Scanner;

import static ui.OftenPrinted.LOGGEDOUT_HEADER;

public class CLI {
    private ServerFacade serverFacade;
    private CommandHelper helper;

    public CLI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.helper = new CommandHelper(serverFacade);
    }

    public void run() {
        System.out.println("Welcome to CS240 Chess");
        System.out.println("Enter help to get started");
        Scanner scanner = new Scanner(System.in);
        var exit = false;
        System.out.print(LOGGEDOUT_HEADER);
        while (!exit) {
            String command = scanner.nextLine();
            String output = helper.commandHelper(command);
            if (output.equalsIgnoreCase("exit")) {
                break;
            }
            System.out.println(output);
        }
    }
}
