package common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import utils.MessageBuild;

import java.util.List;
/**
 * Author yujian
 * Description 二进制解码器
 * Date 2021/2/2
 */ 
public class ProxyDecode extends MessageToMessageDecoder<ByteBuf> {
    ChannelHandlerContext ctx;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.ctx = ctx;
        if(in.readableBytes() >= 8){
            int magic = in.readInt();
            if(magic == 9675){
                int type = in.readInt();
                if(type == Status.ping || type == Status.pong || type == Status.connbak){
                    out.add(MessageBuild.onlyType(type));
                }else{
                    int length = in.readInt();
                    if(in.readableBytes() >= length){
                        Message message = new Message();
                        byte[] bytes = new byte[length];
                        in.readBytes(bytes);
                        message.setType(type);
                        message.setMagic(magic);
                        message.setLength(length);
                        message.setData(new String(bytes));
                        out.add(message);
                    }
                }
            }
        }
    }


}
