package core;

import utils.Status;

import handler.ServerHandler;
import io.netty.channel.Channel;
import utils.MessageBuild;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author yujian
 * description 1
 * create 2021-02-02 10:42
 **/
public class KeepAlive {
    private final ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    public void ping(){
        threadPoolExecutor.scheduleAtFixedRate(() -> {
            for (Channel channel : ServerHandler.keepaliveChannel) {
                if(channel.isOpen() && channel.isActive()){
                    channel.writeAndFlush(MessageBuild.onlyType(Status.ping));
                }else{
                    channel.close();
                }

            }
        },0,20,TimeUnit.SECONDS);
    }
}
