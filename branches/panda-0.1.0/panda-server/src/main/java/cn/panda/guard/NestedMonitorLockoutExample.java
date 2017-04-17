package cn.panda.guard;

import cn.panda.Debug;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class NestedMonitorLockoutExample {
    public static void main(String[] args) {
        final Helper helper = new Helper();
        Debug.info("Before calling guaredMethod.");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String result;
                result = helper.xGuarededMethod("test");
                Debug.info(result);
            }
        });
        t.start();
        final Timer timer = new Timer();
        // Delay 50ms and release guard.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                helper.xStateChanged();
                timer.cancel();
            }
        }, new Date(), 10);

    }
    private static class Helper {
        private volatile boolean isStateOK = false;
        private final Predicate stateBeOK = new Predicate() {
            @Override
            public boolean evaluate() {
                return isStateOK;
            }
        };
        private final Blocker blocker = new ConditionVarBlocker();
        public String xGuarededMethod(final String message) {
            GuardedAction<String> ga = new GuardedAction<String>(stateBeOK) {
                @Override
                public String call() throws Exception {
                    return message + "->received.";
                }
            };
            String result = null;
            try {
                result = blocker.callWithGuard(ga);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return result;
        }
        public void xStateChanged() {
            try {
                blocker.signalAfter(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        isStateOK = true;
                        Debug.info("state ok.");
                        return Boolean.TRUE;
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
