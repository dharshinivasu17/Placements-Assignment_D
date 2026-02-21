import java.math.BigInteger;
import java.util.*;

public class PolynomialSecret {
    static class Share {
        int base;
        String value;
        Share(int b, String v) { base = b; value = v; }
    }

    public static BigInteger baseToInt(String value, int base) {
        BigInteger res = BigInteger.ZERO;
        String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
        for (char c : value.toLowerCase().toCharArray()) {
            int d = digits.indexOf(c);
            if (d >= base || d < 0) throw new IllegalArgumentException("Invalid digit");
            res = res.multiply(BigInteger.valueOf(base)).add(BigInteger.valueOf(d));
        }
        return res;
    }

    public static BigInteger modPow(BigInteger b, BigInteger e, BigInteger m) {
        BigInteger res = BigInteger.ONE;
        b = b.mod(m);
        while (e.compareTo(BigInteger.ZERO) > 0) {
            if (e.mod(BigInteger.TWO).equals(BigInteger.ONE)) res = res.multiply(b).mod(m);
            b = b.multiply(b).mod(m);
            e = e.divide(BigInteger.TWO);
        }
        return res;
    }

    public static BigInteger modInv(BigInteger a, BigInteger m) {
        return modPow(a, m.subtract(BigInteger.ONE), m);
    }

    public static BigInteger lagrangeP0(List<BigInteger> xs, List<BigInteger> ys, BigInteger mod) {
        int n = xs.size();
        BigInteger secret = BigInteger.ZERO;
        for (int i = 0; i < n; i++) {
            BigInteger term = ys.get(i);
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                BigInteger num = xs.get(j).negate().mod(mod);
                BigInteger den = xs.get(i).subtract(xs.get(j)).mod(mod);
                term = term.multiply(num).multiply(modInv(den, mod)).mod(mod);
            }
            secret = secret.add(term).mod(mod);
        }
        return secret;
    }

    public static void solveTestcase(String name, Map<Integer, Share> shares, int k, BigInteger mod) {
        System.out.println("\n=== " + name + " ===");
        List<BigInteger> xs = new ArrayList<>(), ys = new ArrayList<>();
        int count = 0;
        TreeSet<Integer> sortedX = new TreeSet<>(shares.keySet());
        for (int x : sortedX) {
            if (count >= k) break;
            Share sh = shares.get(x);
            BigInteger y = baseToInt(sh.value, sh.base);
            xs.add(BigInteger.valueOf(x));
            ys.add(y);
            System.out.println("Share x=" + x + ", y=" + y);
            count++;
        }
        BigInteger secret = lagrangeP0(xs, ys, mod);
        System.out.println("Secret: " + secret);
    }

    public static void main(String[] args) {
        // SAMPLE TESTCASE (k=3, small MOD)
        Map<Integer, Share> sample = new TreeMap<>();
        sample.put(1, new Share(10, "4"));
        sample.put(2, new Share(2, "111"));
        sample.put(3, new Share(10, "12"));
        sample.put(6, new Share(4, "213"));
        solveTestcase("SAMPLE", sample, 3, BigInteger.valueOf(13));

        // MAIN TESTCASE 2 (k=7, large MOD > 2.2e20)
        Map<Integer, Share> main = new TreeMap<>();
        main.put(1, new Share(6, "13444211440455345511"));
        main.put(2, new Share(15, "aed7015a346d635"));
        main.put(3, new Share(15, "6aeeb69631c227c"));
        main.put(4, new Share(16, "e1b5e05623d881f"));
        main.put(5, new Share(8, "316034514573652620673"));
        main.put(6, new Share(3, "2122212201122002221120200210011020220200"));
        main.put(7, new Share(3, "20120221122211000100210021102001201112121"));
        main.put(8, new Share(6, "20220554335330240002224253"));
        main.put(9, new Share(12, "45153788322a1255483"));
        main.put(10, new Share(7, "1101613130313526312514143"));
        BigInteger BIG_MOD = new BigInteger("1000000000000000000009");  // 10^21+9 prime
        solveTestcase("MAIN TESTCASE", main, 7, BIG_MOD);
    }
}
