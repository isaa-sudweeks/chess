import ui.CLI;

public class Main {
    public static void main(final String[] args) throws InterruptedException {
        String serverUrl = "http://localhost:8080";
        CLI cli = new CLI(serverUrl);
        cli.run();

    }
}