package cn.panda.consumer;

import cn.panda.AlarmAgent;
import cn.panda.AlarmInfo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AlarmSendingThread extends AbstractTerminatableThread {

    private final AlarmAgent alarmAgent = new AlarmAgent();

    private final BlockingQueue<AlarmInfo> alarmQueue;
    private final ConcurrentMap<String, AtomicInteger> submittedAlarmRegistry;

    public AlarmSendingThread() {
        this.alarmQueue = new ArrayBlockingQueue<AlarmInfo>(100);
        this.submittedAlarmRegistry = new ConcurrentHashMap<String, AtomicInteger>();
    }


    @Override
    protected void doRun() throws Exception {
        AlarmInfo alarm;
        alarm = alarmQueue.take();
        terminationToken.reservations.decrementAndGet();

        try {
            alarmAgent.sendAlarm(alarm);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (AlarmType.RESUME == alarm.type) {
            String key = AlarmType.FAULT.toString() + ":" + alarm.getId() + '@'
                    + alarm.getExtraInfo();
            submittedAlarmRegistry.remove(key);
        }
    }

    public int sendAlarm(AlarmInfo alarmInfo) {
        AlarmType type = alarmInfo.type;
        String id = alarmInfo.getId();
        String extraInfo = alarmInfo.getExtraInfo();

        if (terminationToken.isToShutdown()) {
            System.err.println("rejected alarm: " + id + " , " + extraInfo);
            return -1;
        }
        int duplicateSubmissionCount = 0;
        try {
            AtomicInteger prevSubmittedCounter;
            prevSubmittedCounter = submittedAlarmRegistry.putIfAbsent(
                    type.toString() + ":" + '@' + extraInfo, new AtomicInteger(0));

            if (prevSubmittedCounter == null) {
                terminationToken.reservations.incrementAndGet();
                alarmQueue.put(alarmInfo);
            } else {
                duplicateSubmissionCount = prevSubmittedCounter.incrementAndGet();
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
        return duplicateSubmissionCount;
    }

    @Override
    protected void doCleanup(Exception e) {
        if (e != null && !(e instanceof InterruptedException)) {
            System.err.println(e.getMessage());
        }
        alarmAgent.disconnect();
    }
}
