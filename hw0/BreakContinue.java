public class BreakContinue {
    public static void windowPosSum(int[] a, int n) {
        /** your code here */
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0) {
                continue;
            } else if (i == a.length - 1) {
                break;
            } else {
                int j = i;
                while (j < a.length - 1) {
                    j += 1;
                    a[i] += a[j];
                    if (j == i + n) {
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] a = {1, 2, -3, 4, 5, 4};
        int n = 3;
        windowPosSum(a, n);

        // Should print 4, 8, -3, 13, 9, 4
        System.out.println(java.util.Arrays.toString(a));
    }
}