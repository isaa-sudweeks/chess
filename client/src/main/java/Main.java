import static ui.AsciiAnimations.FRAMES;


public class Main {
    public static void main(final String[] args) throws InterruptedException {
        System.out.print("Welcome to CS240 Chess");
        Thread.sleep(800);

        int frame = 0;

        while (true) {
            System.out.print("\rWelcome to CS240 Chess" + FRAMES[frame]);
            System.out.flush();
            frame = (frame + 1) % FRAMES.length;
            Thread.sleep(120);
        }
    }
}