import ServerFacade.ServerFacade;
import ui.CLI;

public class Main {
    private static volatile boolean animating = true;

    public static void main(final String[] args) throws InterruptedException {
        String serverUrl = "http://localhost:8080";
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        CLI cli = new CLI(serverFacade);
        cli.run();

    }
}