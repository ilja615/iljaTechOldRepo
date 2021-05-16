package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance implements Comparable<EffectInstance>, net.minecraftforge.common.extensions.IForgeEffectInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Effect effect;
   private int duration;
   private int amplifier;
   private boolean splash;
   private boolean ambient;
   @OnlyIn(Dist.CLIENT)
   private boolean noCounter;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private EffectInstance hiddenEffect;

   public EffectInstance(Effect p_i46811_1_) {
      this(p_i46811_1_, 0, 0);
   }

   public EffectInstance(Effect p_i46812_1_, int p_i46812_2_) {
      this(p_i46812_1_, p_i46812_2_, 0);
   }

   public EffectInstance(Effect p_i46813_1_, int p_i46813_2_, int p_i46813_3_) {
      this(p_i46813_1_, p_i46813_2_, p_i46813_3_, false, true);
   }

   public EffectInstance(Effect p_i46814_1_, int p_i46814_2_, int p_i46814_3_, boolean p_i46814_4_, boolean p_i46814_5_) {
      this(p_i46814_1_, p_i46814_2_, p_i46814_3_, p_i46814_4_, p_i46814_5_, p_i46814_5_);
   }

   public EffectInstance(Effect p_i48980_1_, int p_i48980_2_, int p_i48980_3_, boolean p_i48980_4_, boolean p_i48980_5_, boolean p_i48980_6_) {
      this(p_i48980_1_, p_i48980_2_, p_i48980_3_, p_i48980_4_, p_i48980_5_, p_i48980_6_, (EffectInstance)null);
   }

   public EffectInstance(Effect p_i230050_1_, int p_i230050_2_, int p_i230050_3_, boolean p_i230050_4_, boolean p_i230050_5_, boolean p_i230050_6_, @Nullable EffectInstance p_i230050_7_) {
      this.effect = p_i230050_1_;
      this.duration = p_i230050_2_;
      this.amplifier = p_i230050_3_;
      this.ambient = p_i230050_4_;
      this.visible = p_i230050_5_;
      this.showIcon = p_i230050_6_;
      this.hiddenEffect = p_i230050_7_;
   }

   public EffectInstance(EffectInstance p_i1577_1_) {
      this.effect = p_i1577_1_.effect;
      this.setDetailsFrom(p_i1577_1_);
   }

   void setDetailsFrom(EffectInstance p_230117_1_) {
      this.duration = p_230117_1_.duration;
      this.amplifier = p_230117_1_.amplifier;
      this.ambient = p_230117_1_.ambient;
      this.visible = p_230117_1_.visible;
      this.showIcon = p_230117_1_.showIcon;
      this.curativeItems = p_230117_1_.curativeItems == null ? null : new java.util.ArrayList<net.minecraft.item.ItemStack>(p_230117_1_.curativeItems);
   }

   public boolean update(EffectInstance p_199308_1_) {
      if (this.effect != p_199308_1_.effect) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (p_199308_1_.amplifier > this.amplifier) {
         if (p_199308_1_.duration < this.duration) {
            EffectInstance effectinstance = this.hiddenEffect;
            this.hiddenEffect = new EffectInstance(this);
            this.hiddenEffect.hiddenEffect = effectinstance;
         }

         this.amplifier = p_199308_1_.amplifier;
         this.duration = p_199308_1_.duration;
         flag = true;
      } else if (p_199308_1_.duration > this.duration) {
         if (p_199308_1_.amplifier == this.amplifier) {
            this.duration = p_199308_1_.duration;
            flag = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new EffectInstance(p_199308_1_);
         } else {
            this.hiddenEffect.update(p_199308_1_);
         }
      }

      if (!p_199308_1_.ambient && this.ambient || flag) {
         this.ambient = p_199308_1_.ambient;
         flag = true;
      }

      if (p_199308_1_.visible != this.visible) {
         this.visible = p_199308_1_.visible;
         flag = true;
      }

      if (p_199308_1_.showIcon != this.showIcon) {
         this.showIcon = p_199308_1_.showIcon;
         flag = true;
      }

      return flag;
   }

   public Effect getEffect() {
      return this.effect == null ? null : this.effect.delegate.get();
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean showIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity p_76455_1_, Runnable p_76455_2_) {
      if (this.duration > 0) {
         if (this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
            this.applyEffect(p_76455_1_);
         }

         this.tickDownDuration();
         if (this.duration == 0 && this.hiddenEffect != null) {
            this.setDetailsFrom(this.hiddenEffect);
            this.hiddenEffect = this.hiddenEffect.hiddenEffect;
            p_76455_2_.run();
         }
      }

      return this.duration > 0;
   }

   private int tickDownDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.tickDownDuration();
      }

      return --this.duration;
   }

   public void applyEffect(LivingEntity p_76457_1_) {
      if (this.duration > 0) {
         this.effect.applyEffectTick(p_76457_1_, this.amplifier);
      }

   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getDescriptionId() + ", Duration: " + this.duration;
      }

      if (this.splash) {
         s = s + ", Splash: true";
      }

      if (!this.visible) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof EffectInstance)) {
         return false;
      } else {
         EffectInstance effectinstance = (EffectInstance)p_equals_1_;
         return this.duration == effectinstance.duration && this.amplifier == effectinstance.amplifier && this.splash == effectinstance.splash && this.ambient == effectinstance.ambient && this.effect.equals(effectinstance.effect);
      }
   }

   public int hashCode() {
      int i = this.effect.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.splash ? 1 : 0);
      return 31 * i + (this.ambient ? 1 : 0);
   }

   public CompoundNBT save(CompoundNBT p_82719_1_) {
      p_82719_1_.putByte("Id", (byte)Effect.getId(this.getEffect()));
      this.writeDetailsTo(p_82719_1_);
      return p_82719_1_;
   }

   private void writeDetailsTo(CompoundNBT p_230119_1_) {
      p_230119_1_.putByte("Amplifier", (byte)this.getAmplifier());
      p_230119_1_.putInt("Duration", this.getDuration());
      p_230119_1_.putBoolean("Ambient", this.isAmbient());
      p_230119_1_.putBoolean("ShowParticles", this.isVisible());
      p_230119_1_.putBoolean("ShowIcon", this.showIcon());
      if (this.hiddenEffect != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.hiddenEffect.save(compoundnbt);
         p_230119_1_.put("HiddenEffect", compoundnbt);
      }
      writeCurativeItems(p_230119_1_);

   }

   public static EffectInstance load(CompoundNBT p_82722_0_) {
      int i = p_82722_0_.getByte("Id") & 0xFF;
      Effect effect = Effect.byId(i);
      return effect == null ? null : loadSpecifiedEffect(effect, p_82722_0_);
   }

   private static EffectInstance loadSpecifiedEffect(Effect p_230116_0_, CompoundNBT p_230116_1_) {
      int i = p_230116_1_.getByte("Amplifier");
      int j = p_230116_1_.getInt("Duration");
      boolean flag = p_230116_1_.getBoolean("Ambient");
      boolean flag1 = true;
      if (p_230116_1_.contains("ShowParticles", 1)) {
         flag1 = p_230116_1_.getBoolean("ShowParticles");
      }

      boolean flag2 = flag1;
      if (p_230116_1_.contains("ShowIcon", 1)) {
         flag2 = p_230116_1_.getBoolean("ShowIcon");
      }

      EffectInstance effectinstance = null;
      if (p_230116_1_.contains("HiddenEffect", 10)) {
         effectinstance = loadSpecifiedEffect(p_230116_0_, p_230116_1_.getCompound("HiddenEffect"));
      }

      return readCurativeItems(new EffectInstance(p_230116_0_, j, i < 0 ? 0 : i, flag, flag1, flag2, effectinstance), p_230116_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setNoCounter(boolean p_100012_1_) {
      this.noCounter = p_100012_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNoCounter() {
      return this.noCounter;
   }

   public int compareTo(EffectInstance p_compareTo_1_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getEffect().getGuiSortColor(this), p_compareTo_1_.getEffect().getGuiSortColor(this)).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getEffect().getGuiSortColor(this), p_compareTo_1_.getEffect().getGuiSortColor(this)).result();
   }

   //======================= FORGE START ===========================
   private java.util.List<net.minecraft.item.ItemStack> curativeItems;

   @Override
   public java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
      if (this.curativeItems == null) //Lazy load this so that we don't create a circular dep on Items.
         this.curativeItems = getEffect().getCurativeItems();
      return this.curativeItems;
   }
   @Override
   public void setCurativeItems(java.util.List<net.minecraft.item.ItemStack> curativeItems) {
      this.curativeItems = curativeItems;
   }
   private static EffectInstance readCurativeItems(EffectInstance effect, CompoundNBT nbt) {
      if (nbt.contains("CurativeItems", net.minecraftforge.common.util.Constants.NBT.TAG_LIST)) {
         java.util.List<net.minecraft.item.ItemStack> items = new java.util.ArrayList<net.minecraft.item.ItemStack>();
         net.minecraft.nbt.ListNBT list = nbt.getList("CurativeItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
         for (int i = 0; i < list.size(); i++) {
            items.add(net.minecraft.item.ItemStack.of(list.getCompound(i)));
         }
         effect.setCurativeItems(items);
      }

      return effect;
   }
}
