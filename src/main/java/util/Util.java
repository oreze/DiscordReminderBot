package util;

public class Util {
    public static void sleep(long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        }
        catch (InterruptedException ex) {
            return;
        }
    }
}
