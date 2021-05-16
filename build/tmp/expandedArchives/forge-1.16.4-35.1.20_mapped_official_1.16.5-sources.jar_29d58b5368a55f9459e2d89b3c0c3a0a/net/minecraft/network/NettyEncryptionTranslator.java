package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
   private final Cipher cipher;
   private byte[] heapIn = new byte[0];
   private byte[] heapOut = new byte[0];

   protected NettyEncryptionTranslator(Cipher p_i45140_1_) {
      this.cipher = p_i45140_1_;
   }

   private byte[] bufToByte(ByteBuf p_150502_1_) {
      int i = p_150502_1_.readableBytes();
      if (this.heapIn.length < i) {
         this.heapIn = new byte[i];
      }

      p_150502_1_.readBytes(this.heapIn, 0, i);
      return this.heapIn;
   }

   protected ByteBuf decipher(ChannelHandlerContext p_150503_1_, ByteBuf p_150503_2_) throws ShortBufferException {
      int i = p_150503_2_.readableBytes();
      byte[] abyte = this.bufToByte(p_150503_2_);
      ByteBuf bytebuf = p_150503_1_.alloc().heapBuffer(this.cipher.getOutputSize(i));
      bytebuf.writerIndex(this.cipher.update(abyte, 0, i, bytebuf.array(), bytebuf.arrayOffset()));
      return bytebuf;
   }

   protected void encipher(ByteBuf p_150504_1_, ByteBuf p_150504_2_) throws ShortBufferException {
      int i = p_150504_1_.readableBytes();
      byte[] abyte = this.bufToByte(p_150504_1_);
      int j = this.cipher.getOutputSize(i);
      if (this.heapOut.length < j) {
         this.heapOut = new byte[j];
      }

      p_150504_2_.writeBytes(this.heapOut, 0, this.cipher.update(abyte, 0, i, this.heapOut));
   }
}
