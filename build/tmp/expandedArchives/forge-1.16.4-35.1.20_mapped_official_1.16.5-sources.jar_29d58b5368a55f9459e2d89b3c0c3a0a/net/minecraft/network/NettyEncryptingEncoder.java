package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class NettyEncryptingEncoder extends MessageToByteEncoder<ByteBuf> {
   private final NettyEncryptionTranslator cipher;

   public NettyEncryptingEncoder(Cipher p_i45142_1_) {
      this.cipher = new NettyEncryptionTranslator(p_i45142_1_);
   }

   protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception {
      this.cipher.encipher(p_encode_2_, p_encode_3_);
   }
}
