package utils;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

public class CommonUtils {
    public static TypeEnum useNio(){
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.contains("linux") && isAvailable("io.netty.channel.epoll.Epoll")){
            return TypeEnum.EPOLL;
        }else if(osName.contains("mac") && isAvailable("io.netty.channel.kqueue.KQueue")){
            return TypeEnum.KQUEUE;
        }else {
            return TypeEnum.SELECT;
        }
    }
    public static int nThread(){
        return    Math.max(1, SystemPropertyUtil.getInt(
            "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
    }
    private static  boolean isAvailable(String clazz){
        try{
            Object obj = Class.forName(clazz).getMethod("isAvailable").invoke(null);
            return Boolean.parseBoolean(obj.toString());
        }catch (Exception e){
            return false;
        }
    }
}
