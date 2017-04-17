package cn.panda.master;

import cn.panda.Debug;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ParrallePrimeGeneratorExample {

    public static void main(String[] args) throws Exception {
        PrimeGeneratorService primeGeneratorService = new PrimeGeneratorService();
        Set<BigInteger> result = primeGeneratorService.generatePrime(10000);
        System.out.println("Generated " + result.size() + " prime:");
        System.out.println(result);
    }
}

class Range {
    public final int lowerBound;
    public final int upperBound;

    public Range(int lowerBound, int upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException(
                    "upperBound should not be less than lowerBound");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return "Range{" +
                "lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                '}';
    }
}

class PrimeGeneratorService extends
        AbstractMaster<Range, Set<BigInteger>, Set<BigInteger>> {

    public PrimeGeneratorService() {
        this.init();
    }

    @Override
    protected TaskDivideStrategy<Range> newTaskDivideStrtegy(final Object... params) {
        final int numOfSlaves = slaves.size();
        final int originalTaskScale = (Integer) params[0];
        final int subTaskScale = originalTaskScale / numOfSlaves;
        final int subTaskCount = (0 == (originalTaskScale % numOfSlaves)) ?
                numOfSlaves : numOfSlaves + 1;

        TaskDivideStrategy tds = new TaskDivideStrategy() {
            private int i = 1;

            @Override
            public Object nextChunk() {
                int upperBound;
                if (i < subTaskCount) {
                    upperBound = i * subTaskScale;
                } else if (i == subTaskCount) {
                    upperBound = originalTaskScale;
                } else {
                    return null;
                }
                int lowerBound = (i - 1) * subTaskScale + 1;
                i++;
                return new Range(lowerBound, upperBound);

            }
        };
        return tds;
    }

    @Override
    protected Set<? extends SlaveSpec<Range, Set<BigInteger>>> createSlaves() {
        Set<PrimeGenerator> slaves = new HashSet<PrimeGenerator>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            slaves.add(new PrimeGenerator(new ArrayBlockingQueue<Runnable>(2)));
        }
        return slaves;
    }

    @Override
    protected Set<BigInteger> combineResults(Iterator<Future<Set<BigInteger>>> subResult) {
        Set<BigInteger> result = new TreeSet<BigInteger>();

        while (subResult.hasNext()) {
            try {
                result.addAll(subResult.next().get());
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (SubTaskFailureException.class.isInstance(cause)) {
                    RetryInfo retryInfo = ((SubTaskFailureException) cause).retryInfo;
                    Object subTask = retryInfo.subTask;
                    Debug.info("failed subTask: " + subTask);
                    System.err.println(e.getMessage());
                }
            }
        }
        return result;
    }

    public Set<BigInteger> generatePrime(int upperBound) throws Exception {
        return this.service(upperBound);
    }

    private static class PrimeGenerator extends
            WorkerThreadSlave<Range, Set<BigInteger>> {

        public PrimeGenerator(BlockingQueue<Runnable> taskQueue) {
            super(taskQueue);
        }

        @Override
        protected Set<BigInteger> doProcess(Range range) throws Exception {
            Set<BigInteger> result = new TreeSet<BigInteger>();
            BigInteger start = BigInteger.valueOf(range.lowerBound);
            BigInteger end = BigInteger.valueOf(range.upperBound);

            while ((start = start.nextProbablePrime()).compareTo(end) == -1) {
                result.add(start);
            }
            return result;
        }
    }
}
