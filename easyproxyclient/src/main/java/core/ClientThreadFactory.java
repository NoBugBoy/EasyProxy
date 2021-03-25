package core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThreadFactory implements ThreadFactory {
    private final String groupName;
    public ClientThreadFactory(String groupName){
        this.groupName = groupName;
    }
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(groupName + "-" + threadNumber.incrementAndGet());
        return thread;
    }
}
