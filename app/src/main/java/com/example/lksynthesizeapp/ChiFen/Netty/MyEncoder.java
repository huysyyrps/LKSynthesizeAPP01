package com.example.lksynthesizeapp.ChiFen.Netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        String s = o.toString();
    }

//    public byte[] HexStringToBytes(String bData) {
//        char[] chars = bData.toCharArray();
//        byte[] b = new byte[chars.length/2];
//        for (int i = 0; i < b.length; i++) {
//            int pos = i * 2;
//            b[i] = (byte) ("0123456789ABCDEF".indexOf(chars[pos]) << 4 | "0123456789ABCDEF".indexOf(chars[pos+1]));
//        }
//        return b;
//    }
//
//    @Override
//    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List list) throws Exception {
//        String s = o.toString();
//        list.add(HexStringToBytes(s));
//    }
}
