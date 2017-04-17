package cn.panda.consumer;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken {
    protected volatile boolean toShutdown = false;

    public final AtomicInteger reservations = new AtomicInteger(0);

    private final Queue<WeakReference<Terminatable>> coordinatedThreads;

    public TerminationToken(){
        coordinatedThreads = new ConcurrentLinkedDeque<WeakReference<Terminatable>>();
    }

    protected void register(Terminatable thread){
        coordinatedThreads.add(new WeakReference<Terminatable>(thread));
    }

    protected void notifyThreadTermination(Terminatable thread){
        WeakReference<Terminatable> wrThread;
        Terminatable otherThread;
        while ((wrThread = coordinatedThreads.poll()) != null){
            otherThread = wrThread.get();
            if(otherThread != null && otherThread != thread){
                otherThread.terminate();
            }
        }
    }

    public boolean isToShutdown() {
        return toShutdown;
    }

    public void setToShutdown(boolean toShutdown) {
        this.toShutdown = toShutdown;
    }
}
