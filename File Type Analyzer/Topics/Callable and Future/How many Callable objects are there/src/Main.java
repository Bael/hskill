import java.util.concurrent.*;


class FutureUtils {

    public static int determineCallableDepth(Callable callable) throws Exception {
        int counter = 1;
        Object result = callable.call();
        while (true) {
            if (!(result instanceof Callable<?>)) {
                break;
            }
            result = ((Callable) result).call();
            counter ++;
        }
        return counter;
        // write your code here
    }

}