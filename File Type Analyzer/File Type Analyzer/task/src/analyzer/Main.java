package analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Path path = Paths.get(args[0]);
        String patternDb = args[1];
        List<FilePattern> filePatterns = readDb(Paths.get(patternDb));

        List<File> files = new ArrayList<>();
        execute(path.toFile(), files);

        ExecutorService service = Executors.newFixedThreadPool(Math.max(1, (Runtime.getRuntime().availableProcessors() - 1) * 2));

        long startTime = System.nanoTime();
        List<Callable<String>> tasks = files.stream().map(file -> checkFile(file, filePatterns)).collect(Collectors.toList());
        List<Future<String>> list = service.invokeAll(tasks);

        for (Future<String> stringFuture : list) {
            System.out.println(stringFuture.get());
        }
        try {
            service.shutdown();
            boolean exited = service.awaitTermination(60, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long elapsedNanos = System.nanoTime() - startTime;
    }

    private static List<FilePattern> readDb(Path path) throws IOException {
        List<String> strings = Files.readAllLines(path);


        List<FilePattern> list = new ArrayList<>();
        for(int i=strings.size()-1; i>=0; i--) {
            String s = strings.get(i).replace("\"", "");
            String[] split = s.split(";");
            FilePattern filePattern = new FilePattern();
            filePattern.rang = Integer.parseInt(split[0]);
            filePattern.template = split[1];
            filePattern.type = split[2];
            list.add(filePattern);
        }
        list.sort((o1, o2) -> Integer.compare(o2.rang, o1.rang));
        return list;

    }
    private static class FilePattern {
        String template;
        String type;
        int rang;

        @Override
        public String toString() {
            return "FilePattern{" +
                    "template='" + template + '\'' +
                    ", type='" + type + '\'' +
                    ", rang=" + rang +
                    '}';
        }
    }

    private static Callable<String> checkFile(File file, List<FilePattern> filePatterns) {
        return () -> {
            try {
                Context context = new Context();
                context.setSearchMethod(new RabinKarp());
                byte[] data = Files.readAllBytes(file.toPath());
                String s = new String(data, StandardCharsets.UTF_8);
                for (FilePattern filePattern : filePatterns) {
                    Match search = context.search(s, filePattern.template);
                    if (search.hasFound) {
                        return file.getName() + ": " + filePattern.type;
                    }
                }
                return file.getName() + ": Unknown file type";
            } catch (Throwable ex) {
                ex.printStackTrace();
                throw ex;
            }
        };
    }

    private static void execute(File dir, List<File> files) {
        if (dir.listFiles() == null) {
            files.add(dir);
            return;
        }
        for(File file : dir.listFiles()) {
            execute(file, files);
        }
    }



    private interface Search {
        Match search(String searchText, String pattern);
    }

    private static class Naive implements Search {

        @Override
        public Match search(String searchText, String pattern) {
            Match m = new Match();
            m.hasFound = searchText.contains(pattern);
            return m;
        }
    }

    private static class RabinKarp implements Search {
        @Override
        public Match search(String text, String search) {
            if (text.length() < search.length()) {
                Match m = new Match();
                m.hasFound = false;
                return m;
            }
            byte[] searchArray = search.getBytes(StandardCharsets.UTF_8);
            byte[] textArray = text.getBytes(StandardCharsets.UTF_8);

            // p and x
            Random r = new Random();
            int p = 4999;
            int x = r.nextInt(p - 1); // 1 < x < p

            //
            int pLength = searchArray.length;
            // x^|P|
            long xP = 1;
            for (int i = 1; i < pLength; i++) {
                xP = mod(xP * x, p);
            }

            List<Integer> ids = new ArrayList<>();
            long searchHash = getHashCode(search, p, x);
            int finIndex = textArray.length - searchArray.length;
            long lastHash = getHashCode(textArray, finIndex, searchArray.length, p, x);
            if (mod(lastHash, p) == mod(searchHash, p)) {
                if (compareArrays(textArray, finIndex, searchArray)) {
                    ids.add(finIndex);
                }
            }

            // right to left
            for (int i = textArray.length - searchArray.length - 1; i >= 0; i--) {
                long mod = mod(lastHash - textArray[i + pLength] * xP, p);
                long newHash = mod(x * mod + textArray[i], p);
                // hashes are equal, need to check string
                if (newHash == searchHash) {
                    if (compareArrays(textArray, i, searchArray)) {
                        ids.add(i);
                    }
                }
                lastHash = newHash;
            }
            Match m = new Match();
            m.hasFound = !ids.isEmpty();
            return m;
        }

        private int getHashCode(byte[] ascii, int p, int x) {
            return getHashCode(ascii, 0, ascii.length, p, x);
        }

        private int getHashCode(byte[] ascii, int start, int length, int p, int x) {
            long xi = 1;
            long sum = 0;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    xi = mod(xi * x, p);
                }
                sum += ascii[i + start] * xi;
            }
            long result = mod(sum, p);
            return (int) result;
        }

        private int getHashCode(String string, int p, int x) {
            byte[] ascii = string.getBytes(StandardCharsets.US_ASCII);
            return getHashCode(ascii, p, x);
        }


        /**
         * Для вычисления по модулю на случай отрицательного числа a
         *
         * @param a число которое мы делим по модулю
         * @param p модуль
         * @return остаток по модулю
         */
        private long mod(long a, long p) {
            return ((a % p) + p) % p;
        }

        /**
         * Сравниваем байтовые массивы
         *
         * @param bigArray    исходный массив который будем сравниватть
         * @param start       индекс, начиная с которого будем сравнивать второй массив
         * @param searchArray второй массив который будем сравнивать
         * @return равны ли массивы?
         */
        private boolean compareArrays(byte[] bigArray, int start, byte[] searchArray) {
            for (int i = start; i < start + searchArray.length; i++) {
                if (bigArray[i] != searchArray[i - start]) {
                    return false;
                }
            }
            return true;
        }

    }
    private static class KMP implements Search {

        @Override
        public Match search(String searchText, String pattern) {
            Match match = new Match();
            int n = searchText.length();
            int m = pattern.length();

            if(m > n) {
                match.hasFound = false;
                return match;
            }
            int[] p = computePrefix(pattern);
            boolean occurs = false;
            for(int i = 0, j = 0; i < n; i++) {
                while(searchText.charAt(i) == pattern.charAt(j)) {
                    i++;
                    j++;
                    if(i == n || j == m) break;
                }
                if(j == m) {
                    occurs = true;
                    break;
                }

                if(j != 0) {
                    j = p[j - 1];
                    i--;
                }
            }
            match.hasFound = occurs;

//            int index = 0;
//            while (index < searchText.length() - pattern.length()) {
//                for (int i=0; i<pattern.length(); i++) {
//                    if(pattern.charAt(i) != searchText.charAt(index + i)) {
//                        int shift = 1;
//                        if (i > 1) {
//                            shift = i - 1 - p[i - 2];
//                        }
//                        index = index + shift;
//                        break;
//                    } else {
//                        if (i == pattern.length() - 1) {
//                            m.hasFound = true;
//                            return m;
//                        }
//                    }
//                }
//            }
            return match;
        }
        private static int[] prefix(String pattern) {
            int[] prefix = new int[pattern.length()];
            prefix[0] = 0;
            for (int i=1; i<pattern.length(); i++) {
                int j = prefix[i-1];
                if (pattern.charAt(i) == pattern.charAt(j)) {
                    prefix[i] = prefix[i-1] + 1;
                } else {
                    int k = j;
                    while(k > 0) {
                        k = prefix[k-1];
                        if (pattern.charAt(i) == pattern.charAt(k)) {
                            prefix[i] = prefix[j-1] + 1;
                            break;
                        }
                    }
                    // no need set 0,
                }
            }
            return prefix;
        }

        public static int[] computePrefix(String input) {
            int[] pi = new int[input.length()];
            int k = 0;
            for (int q = 1; q < input.length(); q++) {
                char target = input.charAt(q);
                while (k > 0 && input.charAt(k) != target) {
                    k = pi[k - 1];
                }
                if (input.charAt(k) == target) {
                    k++;
                }
                pi[q] = k;
            }
            return pi;
        }
    }

    private static class Context implements Search {
        Search search;
        void setSearchMethod(Search search) {
            this.search = search;
        }

        @Override
        public Match search(String searchText, String pattern) {

            Match result = search.search(searchText, pattern);

            return result;
        }
    }

    private static class Match {
        boolean hasFound;

        @Override
        public String toString() {
            return "Match{" +
                    "hasFound=" + hasFound +
                    '}';
        }
    }
}
