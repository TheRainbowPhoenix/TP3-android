package ml.pho3.tp3.service;

/**
 * Created by HP on 10/03/2019.
 */
public class serviceLock {
    private static serviceLock ourInstance = new serviceLock();

    public static serviceLock getInstance() {
        return ourInstance;
    }

    public static boolean locked = false;

    public static boolean askLock() {
        return locked;
    }

    public static void free() {
        locked = false;
    }

    public static boolean isAvailable() {
        return !locked;
    }

    public static void lock() {
        locked = true;
    }


    private serviceLock() {
    }
}
