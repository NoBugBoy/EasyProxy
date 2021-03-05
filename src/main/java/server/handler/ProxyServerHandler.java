package server.handler;

import com.alibaba.fastjson.JSON;
import common.Message;
import common.TcpQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.codec.Charsets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author yujian
 * Description proxy处理器，只处理某一个proxy端口的数据
 * Date 2021/2/2
 */
public class ProxyServerHandler extends SimpleChannelInboundHandler<byte[]> {
    public final static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final                       String       port;
    private final Integer time;
    private final boolean sync;
    ProxyServerHandler(String port,Integer time,boolean sync){
        this.port = port;
        this.time = time;
        this.sync = sync;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(!sync){
            channels.add(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.err.println("server porxy hander" +cause.getMessage());
        ctx.close();
    }

    // @Override
    // public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //     ctx.flush();
    // }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        Channel channel = ServerHandler.clientChannel.get(port);
        String  data      = new String(msg, Charsets.UTF_8);
        boolean http = false;
        System.out.println(data);
        if(data.contains("favicon")){
            return;
        }
        if(data.contains("HTTP/1")){
            http = true;
        }
        if (channel != null) {
            Message message = new Message();
            message.setType(3);
            Map<String, Object> map = new HashMap<>(2);
            map.put("port", port);
            map.put("data", data);
            data = JSON.toJSONString(map);
            message.setLength(data.getBytes().length);
            message.setData(data);
            channel.writeAndFlush(message);
            if(this.sync){
                Object take = TcpQueue.getQueue(port).poll(checkTime(time),TimeUnit.SECONDS);
                if(take == null){
                    String response = time + " seconds timeout or empty response";
                    ctx.channel().writeAndFlush(http?response(response):response.getBytes());
                    ctx.close();
                }else{
                    ctx.channel().writeAndFlush(take);
                    ctx.close();
                }
            }
        }
    }
    public int checkTime(Integer time){
        if(time == null){
            return 3;
        }
        if(time <= 3){
            return 3;
        }
        if(time > 30){
            return 30;
        }
        return 3;
    }
    //如果是http请求则返回http response格式
    public byte[] response(String msg){
        StringBuilder sb =new StringBuilder();
        sb.append("HTTP/1.1 200\n" + "Content-Type: text/html;charset=UTF-8\n" + "Content-Length: ").append(
            msg.length()).append("\n").append("Date: Tue, 02 Feb 2021 08:16:14 GMT\n").append(
            "Keep-Alive: timeout=60\n").append("Connection: keep-alive\n" + "\n").append(msg);
        return sb.toString().getBytes();
    }
}

