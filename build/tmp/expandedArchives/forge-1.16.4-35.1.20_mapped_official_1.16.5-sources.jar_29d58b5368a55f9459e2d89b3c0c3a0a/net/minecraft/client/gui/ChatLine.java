package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatLine<T> {
   private final int addedTime;
   private final T message;
   private final int id;

   public ChatLine(int p_i242050_1_, T p_i242050_2_, int p_i242050_3_) {
      this.message = p_i242050_2_;
      this.addedTime = p_i242050_1_;
      this.id = p_i242050_3_;
   }

   public T getMessage() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.id;
   }
}
