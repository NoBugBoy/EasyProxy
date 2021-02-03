package server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.Message;
import common.Status;
import common.TcpQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import server.core.NettyServer;
import utils.MessageBuild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author yujian
 * @Description 服务端处理器
 * @Date 2021/2/2
 */ 
public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    public static Map<String, Channel> clientChannel    = new ConcurrentHashMap<>();
    public static volatile Set<Channel>        keepaliveChannel = new CopyOnWriteArraySet<>();
    private final Integer              time;
    private final boolean sync;
    public ServerHandler(Integer time,boolean sync){
     this.time = time;
     this.sync = sync;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        keepaliveChannel.add(ctx.channel());
    }

    // @Override
    // public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //     ctx.flush();
    // }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("serverhander" + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        int              type = msg.getType();
        if(type == Status.conn){
            //注册
            this.register(msg,ctx);
            ctx.channel().writeAndFlush(MessageBuild.onlyType(Status.connbak));
        }else if(type == Status.back){
            Map map = JSONObject.parseObject(msg.getData(), Map.class);
            String data = (String)map.get("data");
            if(sync){
                TcpQueue.getQueue(String.valueOf(map.get("port"))).add(data.getBytes());
            }else{
                ProxyServerHandler.channels.writeAndFlush(data.getBytes());
            }
        }else if(type == Status.ping){
        }
    }
    private void register(Message message,ChannelHandlerContext ctx){
        Map map      = JSON.parseObject(message.getData(), Map.class);
        int bindPort = (int)map.get("bindPort");
        clientChannel.put(bindPort+"",ctx.channel());
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ByteArrayDecoder());
                ch.pipeline().addLast(new ByteArrayEncoder());
                ch.pipeline().addLast(new ProxyServerHandler(bindPort+"",time,sync));
            }
        }, bindPort);

        System.out.println("启动代理端口: " + bindPort);
    }

}

