package net.minecraft.world.border;

public interface IBorderListener {
   void onBorderSizeSet(WorldBorder p_177694_1_, double p_177694_2_);

   void onBorderSizeLerping(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_);

   void onBorderCenterSet(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_);

   void onBorderSetWarningTime(WorldBorder p_177691_1_, int p_177691_2_);

   void onBorderSetWarningBlocks(WorldBorder p_177690_1_, int p_177690_2_);

   void onBorderSetDamagePerBlock(WorldBorder p_177696_1_, double p_177696_2_);

   void onBorderSetDamageSafeZOne(WorldBorder p_177695_1_, double p_177695_2_);

   public static class Impl implements IBorderListener {
      private final WorldBorder worldBorder;

      public Impl(WorldBorder p_i50549_1_) {
         this.worldBorder = p_i50549_1_;
      }

      public void onBorderSizeSet(WorldBorder p_177694_1_, double p_177694_2_) {
         this.worldBorder.setSize(p_177694_2_);
      }

      public void onBorderSizeLerping(WorldBorder p_177692_1_, double p_177692_2_, double p_177692_4_, long p_177692_6_) {
         this.worldBorder.lerpSizeBetween(p_177692_2_, p_177692_4_, p_177692_6_);
      }

      public void onBorderCenterSet(WorldBorder p_177693_1_, double p_177693_2_, double p_177693_4_) {
         this.worldBorder.setCenter(p_177693_2_, p_177693_4_);
      }

      public void onBorderSetWarningTime(WorldBorder p_177691_1_, int p_177691_2_) {
         this.worldBorder.setWarningTime(p_177691_2_);
      }

      public void onBorderSetWarningBlocks(WorldBorder p_177690_1_, int p_177690_2_) {
         this.worldBorder.setWarningBlocks(p_177690_2_);
      }

      public void onBorderSetDamagePerBlock(WorldBorder p_177696_1_, double p_177696_2_) {
         this.worldBorder.setDamagePerBlock(p_177696_2_);
      }

      public void onBorderSetDamageSafeZOne(WorldBorder p_177695_1_, double p_177695_2_) {
         this.worldBorder.setDamageSafeZone(p_177695_2_);
      }
   }
}
