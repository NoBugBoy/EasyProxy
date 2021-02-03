package server.core;

import common.ProxyDecode;
import common.ProxyEncode;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import server.handler.ServerHandler;
/**
 * @Author yujian
 * @Description 服务端管道
 * @Date 2021/2/2
 */ 
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private final Integer time;
    private final boolean sync;
    public ServerInitializer(Integer time,boolean sync){
        this.time = time;
        this.sync = sync;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,8,4,0,0));
        ch.pipeline().addLast(new ProxyDecode());
        ch.pipeline().addLast(new ProxyEncode());
        ch.pipeline().addLast(new ServerHandler(time,sync));

    }
}
