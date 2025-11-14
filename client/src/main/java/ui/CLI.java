package ui;

import ServerFacade.ServerFacade;

import java.util.Scanner;

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
        while (!exit) {
            System.out.print("> ");

            String command = scanner.nextLine();
            String output = helper.commandHelper(command);
            if (output.equalsIgnoreCase("exit")) {
                exit = true;
                break;
            }
            System.out.println(output);
        }
    }
}
