package handler;


import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.codec.Charsets;
import utils.Message;
import utils.Status;

import java.util.HashMap;
import java.util.Map;
/**
 * Author yujian
 * Description 本地服务的proxy 与被穿透的服务交互
 * Date 2021/2/2
 */ 
public class ProxyClientHandler extends SimpleChannelInboundHandler<byte[]> {
    private final int mappingPort;

    public ProxyClientHandler(int mappingPort) {
        this.mappingPort = mappingPort;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        System.out.println("穿透客户端响应:" + new String(msg, Charsets.UTF_8));
        Message message = new Message();
        message.setType(Status.back);
        Map map = new HashMap<>(2);
        map.put("port",mappingPort);
        map.put("data",new String(msg));
        String data = JSON.toJSONString(map);
        message.setLength(data.getBytes().length);
        message.setData(data);
        Channel channel = ClientHandler.portChannel.get(mappingPort);
        channel.writeAndFlush(message);
    }


}
