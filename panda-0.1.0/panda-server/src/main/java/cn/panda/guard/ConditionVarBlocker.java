package cn.panda.guard;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionVarBlocker implements Blocker{
    private final Lock lock;
    private final Condition condition;

    public ConditionVarBlocker() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }
    @Override
    public <V> V callWithGuard(GuardedAction<V> guardedAction) throws Exception {
        // By internal condition queue
        // lock.lockInterruptibly();
        if (lock.tryLock()) {
            V result;
            try {
                final Predicate guard = guardedAction.guard;
                while (!guard.evaluate()) {
                    condition.await();
                }
                result = guardedAction.call();
                return result;
            } finally {
                lock.unlock();
            }
        }
        return null;
        // By lock
//        synchronized (lock){
//            V result;
//            final Predicate guard = guardedAction.guard;
//            while(!guard.evaluate()){
//                //condition.await();
//                lock.wait();
//            }
//            result = guardedAction.call();
//            return result;
//        }
    }

    @Override
    public void signalAfter(Callable<Boolean> stateOperation) throws Exception {
        // lock.lockInterruptibly();
        if (lock.tryLock()) {
            try {
                if (stateOperation.call()) {
                    condition.signal();
                }
            } finally {
                lock.unlock();
            }
        }

//        synchronized (lock) {
//            if (stateOperation.call()) {
//                lock.notify();
//            }
//        }

    }
    @Override
    public void broadcastAfter(Callable<Boolean> stateOperation) throws Exception {
        lock.lockInterruptibly();
        try {
            if (stateOperation.call()) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void signal() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
