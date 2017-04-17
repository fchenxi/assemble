package cn.panda.deadlock;

import java.util.ArrayList;
import java.util.List;

public class DemonstrateLRDeadLock {

    private final List<LeftTask> leftTasks;
    private final List<RightTask> rightTasks;
    public DemonstrateLRDeadLock() {
        List<LeftTask> lefts = new ArrayList<LeftTask>();
        for (int i = 0; i < 10; i++) {
            LeftTask order = new LeftTask();
            lefts.add(order);
        }
        List<RightTask> rights = new ArrayList<RightTask>();
        for (int i = 0; i < 10; i++) {
            RightTask order = new RightTask();
            rights.add(order);
        }
        this.leftTasks = lefts;
        this.rightTasks = rights;
    }

    private void start() {
        for (LeftTask leftTask : leftTasks) {
            leftTask.start();
        }
        for (RightTask rightTask  : rightTasks) {
            rightTask.start();
        }
    }
    public static void main(String[] args) {
        new DemonstrateLRDeadLock().start();
    }

    class LeftTask extends Thread {
        @Override
        public void run() {
            LeftRightDeadlock deadlock = new LeftRightDeadlock();
            deadlock.leftRight();
            deadlock.rightLeft();
        }
    }
    class RightTask extends Thread {
        @Override
        public void run() {
            LeftRightDeadlock deadlock = new LeftRightDeadlock();
            deadlock.rightLeft();
        }
    }
}
