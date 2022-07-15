import java.util.*;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        List<List<Integer>> a = new ArrayList<>(calc(n));
        a.sort(Main::comparator);
        for (var list : a) {
            for (var x : list) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }
    private static int comparator(List<Integer> integers, List<Integer> integers1) {
        for (int i = 0; i < Math.min(integers.size(), integers1.size()); i++) {
            if (integers.get(i) < integers1.get(i)) {
                return -1;
            } else {
                if (integers.get(i) > integers1.get(i)) {
                    return 1;
                }
            }
        }
        if (integers.size() == integers1.size()) {
            return 0;
        }
        return 1;
    }

    private static final Map<Integer, Set<List<Integer>>> map = new HashMap<>();

    private static Set<List<Integer>> calc(int n) {
        var set = new HashSet<List<Integer>>();
        if (n == 1) {
            var singleList = new ArrayList<Integer>();
            singleList.add(1);
            set.add(singleList);
            return set;
        }

        for (int k = 0; k < n; k++) {
            //n - k
            if (!map.containsKey(k)) {
                map.put(k, calc(k));
            }

            Set<List<Integer>> result = map.get(k);
            if (result.isEmpty()) {
                List<Integer> s = new ArrayList<>();
                s.add(n);
                set.add(s);
            }
            for (var list : result) {
                List<Integer> accum = new ArrayList<>(list);
                accum.add(0, n - k);
                accum.sort(Comparator.reverseOrder());
                set.add(accum);
            }
        }
        return set;
    }
}