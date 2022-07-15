import java.util.Scanner;

public class Main {

    int[] cache;

    public int fib(int n){

        if (cache[n] == 0) {
            if (n == 0) {
                cache[n] = 0;
            } else {
                if (n ==1 || n == 2) {
                    cache[n] = 1;
                } else {
                    cache[n] = fib(n-1) + fib(n - 2);
                }
            }
        }
        return cache[n];
    }

    public int signedFib(int n) {
        cache = new int[n + 1];

        int fib = fib(n);
        if (n % 2 == 0) {
            return - fib;
        }
        return fib;
    }

    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        Main main = new Main();

        System.out.println(main.signedFib(n));
    }
}
