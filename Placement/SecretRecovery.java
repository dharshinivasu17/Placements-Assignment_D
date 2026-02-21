import java.math.BigInteger;
import java.util.*;

public class SecretRecovery {
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

    public static void main(String[] args) {
        // SECOND TESTCASE ONLY (n=10, k=7)
        Map<Integer, Share> shares = new TreeMap<>();
        shares.put(1, new Share(6, "13444211440455345511"));
        shares.put(2, new Share(15, "aed7015a346d635"));
        shares.put(3, new Share(15, "6aeeb69631c227c"));
        shares.put(4, new Share(16, "e1b5e05623d881f"));
        shares.put(5, new Share(8, "316034514573652620673"));
        shares.put(6, new Share(3, "2122212201122002221120200210011020220200"));
        shares.put(7, new Share(3, "20120221122211000100210021102001201112121"));
        shares.put(8, new Share(6, "20220554335330240002224253"));
        shares.put(9, new Share(12, "45153788322a1255483"));
        shares.put(10, new Share(7, "1101613130313526312514143"));

        // Use first 7 shares (x=1 to x=7)
        List<BigInteger> xs = new ArrayList<>(), ys = new ArrayList<>();
        int k = 7;
        int count = 0;
        for (int x : shares.keySet()) {
            if (count >= k) break;
            Share sh = shares.get(x);
            BigInteger y = baseToInt(sh.value, sh.base);
            xs.add(BigInteger.valueOf(x));
            ys.add(y);
            System.out.println("Share x=" + x + ", base=" + sh.base + ", y=" + y);
            count++;
        }

        // Large prime modulus > max(y) ≈ 2.2e20
        BigInteger MOD = new BigInteger("1000000000000000000009");
        
        BigInteger secret = lagrangeP0(xs, ys, MOD);
        System.out.println("\nSECRET: " + secret);
    }
}
