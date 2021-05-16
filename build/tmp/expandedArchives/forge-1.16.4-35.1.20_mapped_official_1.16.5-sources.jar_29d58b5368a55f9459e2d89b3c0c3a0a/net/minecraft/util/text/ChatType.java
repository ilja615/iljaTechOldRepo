package net.minecraft.util.text;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ChatType {
   CHAT((byte)0, false),
   SYSTEM((byte)1, true),
   GAME_INFO((byte)2, true);

   private final byte index;
   private final boolean interrupt;

   private ChatType(byte p_i50783_3_, boolean p_i50783_4_) {
      this.index = p_i50783_3_;
      this.interrupt = p_i50783_4_;
   }

   public byte getIndex() {
      return this.index;
   }

   public static ChatType getForIndex(byte p_192582_0_) {
      for(ChatType chattype : values()) {
         if (p_192582_0_ == chattype.index) {
            return chattype;
         }
      }

      return CHAT;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldInterrupt() {
      return this.interrupt;
   }
}
