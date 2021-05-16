package net.minecraft.entity.passive.fish;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TropicalFishEntity extends AbstractGroupFishEntity {
   private static final DataParameter<Integer> DATA_ID_TYPE_VARIANT = EntityDataManager.defineId(TropicalFishEntity.class, DataSerializers.INT);
   private static final ResourceLocation[] BASE_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
   private static final ResourceLocation[] PATTERN_A_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
   private static final ResourceLocation[] PATTERN_B_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
   public static final int[] COMMON_VARIANTS = new int[]{calculateVariant(TropicalFishEntity.Type.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), calculateVariant(TropicalFishEntity.Type.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), calculateVariant(TropicalFishEntity.Type.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), calculateVariant(TropicalFishEntity.Type.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(TropicalFishEntity.Type.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), calculateVariant(TropicalFishEntity.Type.KOB, DyeColor.ORANGE, DyeColor.WHITE), calculateVariant(TropicalFishEntity.Type.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), calculateVariant(TropicalFishEntity.Type.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), calculateVariant(TropicalFishEntity.Type.CLAYFISH, DyeColor.WHITE, DyeColor.RED), calculateVariant(TropicalFishEntity.Type.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(TropicalFishEntity.Type.GLITTER, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(TropicalFishEntity.Type.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), calculateVariant(TropicalFishEntity.Type.DASHER, DyeColor.CYAN, DyeColor.PINK), calculateVariant(TropicalFishEntity.Type.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), calculateVariant(TropicalFishEntity.Type.BETTY, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFishEntity.Type.SNOOPER, DyeColor.GRAY, DyeColor.RED), calculateVariant(TropicalFishEntity.Type.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFishEntity.Type.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(TropicalFishEntity.Type.KOB, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFishEntity.Type.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), calculateVariant(TropicalFishEntity.Type.DASHER, DyeColor.CYAN, DyeColor.YELLOW), calculateVariant(TropicalFishEntity.Type.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
   private boolean isSchool = true;

   private static int calculateVariant(TropicalFishEntity.Type p_204214_0_, DyeColor p_204214_1_, DyeColor p_204214_2_) {
      return p_204214_0_.getBase() & 255 | (p_204214_0_.getIndex() & 255) << 8 | (p_204214_1_.getId() & 255) << 16 | (p_204214_2_.getId() & 255) << 24;
   }

   public TropicalFishEntity(EntityType<? extends TropicalFishEntity> p_i50242_1_, World p_i50242_2_) {
      super(p_i50242_1_, p_i50242_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static String getPredefinedName(int p_212324_0_) {
      return "entity.minecraft.tropical_fish.predefined." + p_212324_0_;
   }

   @OnlyIn(Dist.CLIENT)
   public static DyeColor getBaseColor(int p_212326_0_) {
      return DyeColor.byId(getBaseColorIdx(p_212326_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static DyeColor getPatternColor(int p_212323_0_) {
      return DyeColor.byId(getPatternColorIdx(p_212323_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static String getFishTypeName(int p_212327_0_) {
      int i = getBaseVariant(p_212327_0_);
      int j = getPatternVariant(p_212327_0_);
      return "entity.minecraft.tropical_fish.type." + TropicalFishEntity.Type.getPatternName(i, j);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setVariant(p_70037_1_.getInt("Variant"));
   }

   public void setVariant(int p_204215_1_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_204215_1_);
   }

   public boolean isMaxGroupSizeReached(int p_204209_1_) {
      return !this.isSchool;
   }

   public int getVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   protected void saveToBucketTag(ItemStack p_204211_1_) {
      super.saveToBucketTag(p_204211_1_);
      CompoundNBT compoundnbt = p_204211_1_.getOrCreateTag();
      compoundnbt.putInt("BucketVariantTag", this.getVariant());
   }

   protected ItemStack getBucketItemStack() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   private static int getBaseColorIdx(int p_204216_0_) {
      return (p_204216_0_ & 16711680) >> 16;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] getBaseColor() {
      return DyeColor.byId(getBaseColorIdx(this.getVariant())).getTextureDiffuseColors();
   }

   @OnlyIn(Dist.CLIENT)
   private static int getPatternColorIdx(int p_204212_0_) {
      return (p_204212_0_ & -16777216) >> 24;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] getPatternColor() {
      return DyeColor.byId(getPatternColorIdx(this.getVariant())).getTextureDiffuseColors();
   }

   @OnlyIn(Dist.CLIENT)
   public static int getBaseVariant(int p_212325_0_) {
      return Math.min(p_212325_0_ & 255, 1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getBaseVariant() {
      return getBaseVariant(this.getVariant());
   }

   @OnlyIn(Dist.CLIENT)
   private static int getPatternVariant(int p_204213_0_) {
      return Math.min((p_204213_0_ & '\uff00') >> 8, 5);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getPatternTextureLocation() {
      return getBaseVariant(this.getVariant()) == 0 ? PATTERN_A_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())] : PATTERN_B_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())];
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBaseTextureLocation() {
      return BASE_TEXTURE_LOCATIONS[getBaseVariant(this.getVariant())];
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      p_213386_4_ = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
      if (p_213386_5_ != null && p_213386_5_.contains("BucketVariantTag", 3)) {
         this.setVariant(p_213386_5_.getInt("BucketVariantTag"));
         return p_213386_4_;
      } else {
         int i;
         int j;
         int k;
         int l;
         if (p_213386_4_ instanceof TropicalFishEntity.TropicalFishData) {
            TropicalFishEntity.TropicalFishData tropicalfishentity$tropicalfishdata = (TropicalFishEntity.TropicalFishData)p_213386_4_;
            i = tropicalfishentity$tropicalfishdata.base;
            j = tropicalfishentity$tropicalfishdata.pattern;
            k = tropicalfishentity$tropicalfishdata.baseColor;
            l = tropicalfishentity$tropicalfishdata.patternColor;
         } else if ((double)this.random.nextFloat() < 0.9D) {
            int i1 = Util.getRandom(COMMON_VARIANTS, this.random);
            i = i1 & 255;
            j = (i1 & '\uff00') >> 8;
            k = (i1 & 16711680) >> 16;
            l = (i1 & -16777216) >> 24;
            p_213386_4_ = new TropicalFishEntity.TropicalFishData(this, i, j, k, l);
         } else {
            this.isSchool = false;
            i = this.random.nextInt(2);
            j = this.random.nextInt(6);
            k = this.random.nextInt(15);
            l = this.random.nextInt(15);
         }

         this.setVariant(i | j << 8 | k << 16 | l << 24);
         return p_213386_4_;
      }
   }

   static class TropicalFishData extends AbstractGroupFishEntity.GroupData {
      private final int base;
      private final int pattern;
      private final int baseColor;
      private final int patternColor;

      private TropicalFishData(TropicalFishEntity p_i49859_1_, int p_i49859_2_, int p_i49859_3_, int p_i49859_4_, int p_i49859_5_) {
         super(p_i49859_1_);
         this.base = p_i49859_2_;
         this.pattern = p_i49859_3_;
         this.baseColor = p_i49859_4_;
         this.patternColor = p_i49859_5_;
      }
   }

   static enum Type {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int base;
      private final int index;
      private static final TropicalFishEntity.Type[] VALUES = values();

      private Type(int p_i49832_3_, int p_i49832_4_) {
         this.base = p_i49832_3_;
         this.index = p_i49832_4_;
      }

      public int getBase() {
         return this.base;
      }

      public int getIndex() {
         return this.index;
      }

      @OnlyIn(Dist.CLIENT)
      public static String getPatternName(int p_212548_0_, int p_212548_1_) {
         return VALUES[p_212548_1_ + 6 * p_212548_0_].getName();
      }

      @OnlyIn(Dist.CLIENT)
      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
