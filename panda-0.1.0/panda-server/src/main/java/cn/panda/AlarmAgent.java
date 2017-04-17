package cn.panda;

import cn.panda.guard.Blocker;
import cn.panda.guard.ConditionVarBlocker;
import cn.panda.guard.GuardedAction;
import cn.panda.guard.Predicate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class AlarmAgent {
    private volatile boolean connnectedToServer = false;
    private final Predicate agentConnected = new Predicate() {
        @Override
        public boolean evaluate() {
            return connnectedToServer;
        }
    };

    // Pattern user
    private final Blocker blocker = new ConditionVarBlocker();

    // Heart beat
    private final Timer heartbeatTimer = new Timer(true);

    public void sendAlarm(final AlarmInfo alarm) throws Exception{
        GuardedAction<Void> guardedAction = new GuardedAction<Void>(agentConnected) {
            @Override
            public Void call() throws Exception {
                // TODO do someting else.
                return null;
            }
        };
        blocker.callWithGuard(guardedAction);
    }

    private void doSendAlarm(AlarmInfo alarm){
        // Do something else.
        try{
            System.out.println("Sending alarm " + alarm);
        }catch (Exception e){

        }
    }
    public void init(){
        // Do something else.
        Thread connectingThread = new Thread();
        connectingThread.start();
        heartbeatTimer.schedule(new HeartbeatTask(), 60000, 2000);
    }
    public void disconnect(){
        System.out.println("Disconnected from alarm server.");
        connnectedToServer = false;
    }
    protected void onConnected(){
        try{
            blocker.signalAfter(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    connnectedToServer = true;
                    return Boolean.TRUE;
                }
            });
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    protected void onDisconnected(){
        connnectedToServer = false;
    }
    private class ConnectingTask implements Runnable{

        @Override
        public void run() {
            // Do something else.
            // Monitor connect to server.
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                throw new RuntimeException(e.getMessage());
            }
            onConnected();
        }
    }
    private class HeartbeatTask extends TimerTask{
        // Do something else.
        @Override
        public void run() {
            if(!testConnection()){
                onDisconnected();
                reconnect();
            }
        }
        private boolean testConnection(){
            // Do something else.
            return true;
        }
        private void reconnect(){
            ConnectingTask connectingThread = new ConnectingTask();
            connectingThread.run();
        }
    }
}
