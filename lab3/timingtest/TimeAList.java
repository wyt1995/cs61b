package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.4f %12d %12.4f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> nums = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> counts = new AList<>();

        for (int i = 0; i < 8; i++) {
            int dataSize = (int) (1000 * Math.pow(2, i));
            int numOperation = 0;
            AList<Integer> temp = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < dataSize; j++) {
                temp.addLast(j);
                numOperation += 1;
            }
            nums.addLast(dataSize);
            times.addLast(sw.elapsedTime());
            counts.addLast(numOperation);
        }

        printTimingTable(nums, times, counts);
    }
}
