package net.minecraft.client.gui;

import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBossInfo extends BossInfo {
   protected float targetPercent;
   protected long setTime;

   public ClientBossInfo(SUpdateBossInfoPacket p_i46605_1_) {
      super(p_i46605_1_.getId(), p_i46605_1_.getName(), p_i46605_1_.getColor(), p_i46605_1_.getOverlay());
      this.targetPercent = p_i46605_1_.getPercent();
      this.percent = p_i46605_1_.getPercent();
      this.setTime = Util.getMillis();
      this.setDarkenScreen(p_i46605_1_.shouldDarkenScreen());
      this.setPlayBossMusic(p_i46605_1_.shouldPlayMusic());
      this.setCreateWorldFog(p_i46605_1_.shouldCreateWorldFog());
   }

   public void setPercent(float p_186735_1_) {
      this.percent = this.getPercent();
      this.targetPercent = p_186735_1_;
      this.setTime = Util.getMillis();
   }

   public float getPercent() {
      long i = Util.getMillis() - this.setTime;
      float f = MathHelper.clamp((float)i / 100.0F, 0.0F, 1.0F);
      return MathHelper.lerp(f, this.percent, this.targetPercent);
   }

   public void update(SUpdateBossInfoPacket p_186765_1_) {
      switch(p_186765_1_.getOperation()) {
      case UPDATE_NAME:
         this.setName(p_186765_1_.getName());
         break;
      case UPDATE_PCT:
         this.setPercent(p_186765_1_.getPercent());
         break;
      case UPDATE_STYLE:
         this.setColor(p_186765_1_.getColor());
         this.setOverlay(p_186765_1_.getOverlay());
         break;
      case UPDATE_PROPERTIES:
         this.setDarkenScreen(p_186765_1_.shouldDarkenScreen());
         this.setPlayBossMusic(p_186765_1_.shouldPlayMusic());
      }

   }
}
