package core;


import handler.ClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import utils.ProxyDecode;
import utils.ProxyEncode;

import java.util.concurrent.TimeUnit;

/**
 * Author yujian
 * Description 客户端管道
 * Date 2021/2/2
 */ 
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final String proxyAddress;
    private final       int    proxyPort;
    private final       int    mappingPort;
    public ClientInitializer(String proxyAddress,int proxyPort,int mappingPort){
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
        this.mappingPort = mappingPort;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //防止粘包 不配置偶尔会出现padding的情况
        //数据包最大长度  数据包长度偏移量 数据包长度字节数 剩余长度 忽略长度
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,8,4,0,0));
        ch.pipeline().addLast(new ProxyDecode());
        ch.pipeline().addLast(new ProxyEncode());
        ch.pipeline().addLast(new IdleStateHandler(60,60,300,
            TimeUnit.SECONDS));
        ch.pipeline().addLast(new ClientHandler(proxyAddress,proxyPort,mappingPort));

    }
}
