package common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * @Author yujian
 * @Description 二进制编码器
 * @Date 2021/2/2
 */ 
public class ProxyEncode extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        out.writeInt(9675);
        out.writeInt(msg.getType());
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getData().getBytes());
    }
}
