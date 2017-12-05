package com.gome.pop.fup.easyid.handler;

import com.gome.pop.fup.easyid.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 *
 * @author fupeng-ds
 */
public class EncoderHandler extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		byte[] data = KryoUtil.objToByte(msg);
		out.writeInt(data.length);
		out.writeBytes(data);
	}

}
