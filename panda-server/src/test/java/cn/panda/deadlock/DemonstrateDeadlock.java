package cn.panda.deadlock;
import cn.panda.deadlock.DynamicOrderDeadlock.Account;
import cn.panda.deadlock.DynamicOrderDeadlock.DollarAmount;
import java.util.Random;

public class DemonstrateDeadlock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;
    static final Random rnd = new Random();
    static final Account[] accounts = new Account[NUM_ACCOUNTS];

    public static void main(String[] args) {

        for (int i = 0; i < accounts.length; i++)
            accounts[i] = new Account();


        for (int i = 0; i < NUM_THREADS; i++)
            new TransferThread().start();
    }

    static class TransferThread extends Thread {
        public void run() {
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                try {
                    DynamicOrderDeadlock.transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                } catch (DynamicOrderDeadlock.InsufficientFundsException ignored) {
                }
            }
        }
    }
}
