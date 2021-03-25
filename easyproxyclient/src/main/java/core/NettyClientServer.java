package core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import utils.CommonUtils;
import utils.TypeEnum;
/**
 * Author yujian
 * Description Bootstrap启动类
 * Date 2021/2/2
 */
public class NettyClientServer {

    public  void start(String proxyAddress,int port,ChannelHandler handler){
        TypeEnum typeEnum = CommonUtils.useNio();
        EventLoopGroup clientGroup;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            if (typeEnum == TypeEnum.EPOLL) {
                clientGroup = new EpollEventLoopGroup(CommonUtils.nThread(),new ClientThreadFactory("clientEPollBoss"));
                bootstrap.group(clientGroup).channel(EpollSocketChannel.class);;
            } else if(typeEnum == TypeEnum.KQUEUE){
                clientGroup = new KQueueEventLoopGroup(CommonUtils.nThread(), new ClientThreadFactory("clientKQueueBoss"));
                bootstrap.group(clientGroup).channel(KQueueSocketChannel.class);
            } else {
                clientGroup = new NioEventLoopGroup(CommonUtils.nThread(), new ClientThreadFactory("clientNio"));
                bootstrap.group(clientGroup).channel(NioSocketChannel.class);
            }
            bootstrap.handler(handler);
            ChannelFuture f = bootstrap.connect(proxyAddress,port).sync();
             f.channel().closeFuture().addListener(future -> clientGroup.shutdownGracefully());
        } catch (Exception e) {
            System.err.printf("检查本地端口%s是否已经启动",port+"");
            System.exit(1);
        }
    }
}
