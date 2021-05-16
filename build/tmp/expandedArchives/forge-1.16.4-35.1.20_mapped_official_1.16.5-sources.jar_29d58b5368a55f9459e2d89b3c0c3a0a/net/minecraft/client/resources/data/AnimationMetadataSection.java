package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
      public Pair<Integer, Integer> getFrameSize(int p_225641_1_, int p_225641_2_) {
         return Pair.of(p_225641_1_, p_225641_2_);
      }
   };
   private final List<AnimationFrame> frames;
   private final int frameWidth;
   private final int frameHeight;
   private final int defaultFrameTime;
   private final boolean interpolatedFrames;

   public AnimationMetadataSection(List<AnimationFrame> p_i46088_1_, int p_i46088_2_, int p_i46088_3_, int p_i46088_4_, boolean p_i46088_5_) {
      this.frames = p_i46088_1_;
      this.frameWidth = p_i46088_2_;
      this.frameHeight = p_i46088_3_;
      this.defaultFrameTime = p_i46088_4_;
      this.interpolatedFrames = p_i46088_5_;
   }

   private static boolean isDivisionInteger(int p_229303_0_, int p_229303_1_) {
      return p_229303_0_ / p_229303_1_ * p_229303_1_ == p_229303_0_;
   }

   public Pair<Integer, Integer> getFrameSize(int p_225641_1_, int p_225641_2_) {
      Pair<Integer, Integer> pair = this.calculateFrameSize(p_225641_1_, p_225641_2_);
      int i = pair.getFirst();
      int j = pair.getSecond();
      if (isDivisionInteger(p_225641_1_, i) && isDivisionInteger(p_225641_2_, j)) {
         return pair;
      } else {
         throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", p_225641_1_, p_225641_2_, i, j));
      }
   }

   private Pair<Integer, Integer> calculateFrameSize(int p_229304_1_, int p_229304_2_) {
      if (this.frameWidth != -1) {
         return this.frameHeight != -1 ? Pair.of(this.frameWidth, this.frameHeight) : Pair.of(this.frameWidth, p_229304_2_);
      } else if (this.frameHeight != -1) {
         return Pair.of(p_229304_1_, this.frameHeight);
      } else {
         int i = Math.min(p_229304_1_, p_229304_2_);
         return Pair.of(i, i);
      }
   }

   public int getFrameHeight(int p_229301_1_) {
      return this.frameHeight == -1 ? p_229301_1_ : this.frameHeight;
   }

   public int getFrameWidth(int p_229302_1_) {
      return this.frameWidth == -1 ? p_229302_1_ : this.frameWidth;
   }

   public int getFrameCount() {
      return this.frames.size();
   }

   public int getDefaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean isInterpolatedFrames() {
      return this.interpolatedFrames;
   }

   private AnimationFrame getFrame(int p_130072_1_) {
      return this.frames.get(p_130072_1_);
   }

   public int getFrameTime(int p_110472_1_) {
      AnimationFrame animationframe = this.getFrame(p_110472_1_);
      return animationframe.isTimeUnknown() ? this.defaultFrameTime : animationframe.getTime();
   }

   public int getFrameIndex(int p_110468_1_) {
      return this.frames.get(p_110468_1_).getIndex();
   }

   public Set<Integer> getUniqueFrameIndices() {
      Set<Integer> set = Sets.newHashSet();

      for(AnimationFrame animationframe : this.frames) {
         set.add(animationframe.getIndex());
      }

      return set;
   }
}
