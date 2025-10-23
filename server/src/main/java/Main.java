import server.Server;

public class Main {
    public static void main(final String[] args) {
        final Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}