package client.handler;

import client.core.NettyClientServer;
import com.alibaba.fastjson.JSON;
import common.Message;
import common.Status;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author yujian
 * Description 客户端处理器
 * Date 2021/2/2
 */
public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    public static Map<Integer, Channel> proxyPortChannel = new ConcurrentHashMap<>();
    public static Map<Integer, Channel> portChannel = new ConcurrentHashMap<>();
    private final String                proxyAddress;
    private final       int    proxyPort;
    private final       int    mappingPort;
    public ClientHandler(String proxyAddress,int proxyPort,int mappingPort){
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
        this.mappingPort = mappingPort;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {
                connbak();
            } else if (idleEvent.state() == IdleState.WRITER_IDLE) {
                connbak();
            } else if (idleEvent.state() == IdleState.ALL_IDLE) {
               ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发送conn
        Message message = new Message();
        message.setType(Status.conn);
        Map<String,Integer> map = new HashMap<>();
        map.put("bindPort",mappingPort);
        portChannel.put(mappingPort,ctx.channel());
        String data = JSON.toJSONString(map);
        message.setLength(data.getBytes().length);
        message.setData(data);
        ctx.channel().writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        int type = msg.getType();
        if(type == Status.connbak){
            connbak();
            System.out.println("创建内网穿透成功" + ctx.channel().remoteAddress());
        }else if(type == Status.ping){
            // ctx.channel().writeAndFlush(  ctx.channel().writeAndFlush( ctx.channel().writeAndFlush(MessageBuild.onlyType(Status.pong))));
        }else if(type == Status.data){
            data(msg);
        }
    }
    private synchronized void data(Message msg){
        String  data    = msg.getData();
        Map     map     = JSON.parseObject(data, Map.class);
        String  port    = (String)map.get("port");
        Channel channel = proxyPortChannel.get(Integer.parseInt(port));
        if(channel != null ){
            String result = (String)map.get("data");
            channel.writeAndFlush(result.getBytes());
        }
    }
    private  void connbak(){
        NettyClientServer nettyClientServer = new NettyClientServer();
      nettyClientServer.start(proxyAddress,proxyPort,new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ByteArrayDecoder());
                ch.pipeline().addLast(new ByteArrayEncoder());
                ch.pipeline().addLast(new ProxyClientHandler(mappingPort));
                //将proxyClientActive方法的put channel拿到此处，否则重连后第一次请求会有canceled的问题
                ClientHandler.proxyPortChannel.put(mappingPort,ch);
            }
        });
    }

}
