package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Stitcher {
   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

   private static final Comparator<Stitcher.Holder> HOLDER_COMPARATOR = Comparator.<Stitcher.Holder, Integer>comparing((p_217793_0_) -> {
      return -p_217793_0_.height;
   }).thenComparing((p_217795_0_) -> {
      return -p_217795_0_.width;
   }).thenComparing((p_217794_0_) -> {
      return p_217794_0_.spriteInfo.name();
   });
   private final int mipLevel;
   private final Set<Stitcher.Holder> texturesToBeStitched = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Slot> storage = Lists.newArrayListWithCapacity(256);
   private int storageX;
   private int storageY;
   private final int maxWidth;
   private final int maxHeight;

   public Stitcher(int p_i50910_1_, int p_i50910_2_, int p_i50910_3_) {
      this.mipLevel = p_i50910_3_;
      this.maxWidth = p_i50910_1_;
      this.maxHeight = p_i50910_2_;
   }

   public int getWidth() {
      return this.storageX;
   }

   public int getHeight() {
      return this.storageY;
   }

   public void registerSprite(TextureAtlasSprite.Info p_229211_1_) {
      Stitcher.Holder stitcher$holder = new Stitcher.Holder(p_229211_1_, this.mipLevel);
      this.texturesToBeStitched.add(stitcher$holder);
   }

   public void stitch() {
      List<Stitcher.Holder> list = Lists.newArrayList(this.texturesToBeStitched);
      list.sort(HOLDER_COMPARATOR);

      for(Stitcher.Holder stitcher$holder : list) {
         if (!this.addToStorage(stitcher$holder)) {
            LOGGER.info(new net.minecraftforge.fml.loading.AdvancedLogMessageAdapter(sb->{
               sb.append("Unable to fit: ").append(stitcher$holder.spriteInfo.name());
               sb.append(" - size: ").append(stitcher$holder.spriteInfo.width()).append("x").append(stitcher$holder.spriteInfo.height());
               sb.append(" - Maybe try a lower resolution resourcepack?\n");
               list.forEach(h-> sb.append("\t").append(h).append("\n"));
            }));
            throw new StitcherException(stitcher$holder.spriteInfo, list.stream().map((p_229212_0_) -> {
               return p_229212_0_.spriteInfo;
            }).collect(ImmutableList.toImmutableList()));
         }
      }

      this.storageX = MathHelper.smallestEncompassingPowerOfTwo(this.storageX);
      this.storageY = MathHelper.smallestEncompassingPowerOfTwo(this.storageY);
   }

   public void gatherSprites(Stitcher.ISpriteLoader p_229209_1_) {
      for(Stitcher.Slot stitcher$slot : this.storage) {
         stitcher$slot.walk((p_229210_2_) -> {
            Stitcher.Holder stitcher$holder = p_229210_2_.getHolder();
            TextureAtlasSprite.Info textureatlassprite$info = stitcher$holder.spriteInfo;
            p_229209_1_.load(textureatlassprite$info, this.storageX, this.storageY, p_229210_2_.getX(), p_229210_2_.getY());
         });
      }

   }

   private static int smallestFittingMinTexel(int p_147969_0_, int p_147969_1_) {
      return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
   }

   private boolean addToStorage(Stitcher.Holder p_94310_1_) {
      for(Stitcher.Slot stitcher$slot : this.storage) {
         if (stitcher$slot.add(p_94310_1_)) {
            return true;
         }
      }

      return this.expand(p_94310_1_);
   }

   private boolean expand(Stitcher.Holder p_94311_1_) {
      int i = MathHelper.smallestEncompassingPowerOfTwo(this.storageX);
      int j = MathHelper.smallestEncompassingPowerOfTwo(this.storageY);
      int k = MathHelper.smallestEncompassingPowerOfTwo(this.storageX + p_94311_1_.width);
      int l = MathHelper.smallestEncompassingPowerOfTwo(this.storageY + p_94311_1_.height);
      boolean flag1 = k <= this.maxWidth;
      boolean flag2 = l <= this.maxHeight;
      if (!flag1 && !flag2) {
         return false;
      } else {
         boolean flag3 = flag1 && i != k;
         boolean flag4 = flag2 && j != l;
         boolean flag;
         if (flag3 ^ flag4) {
            flag = !flag3 && flag1; // Forge: Fix stitcher not expanding entire height before growing width, and (potentially) growing larger then the max size.
         } else {
            flag = flag1 && i <= j;
         }

         Stitcher.Slot stitcher$slot;
         if (flag) {
            if (this.storageY == 0) {
               this.storageY = p_94311_1_.height;
            }

            stitcher$slot = new Stitcher.Slot(this.storageX, 0, p_94311_1_.width, this.storageY);
            this.storageX += p_94311_1_.width;
         } else {
            stitcher$slot = new Stitcher.Slot(0, this.storageY, this.storageX, p_94311_1_.height);
            this.storageY += p_94311_1_.height;
         }

         stitcher$slot.add(p_94311_1_);
         this.storage.add(stitcher$slot);
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Holder {
      public final TextureAtlasSprite.Info spriteInfo;
      public final int width;
      public final int height;

      public Holder(TextureAtlasSprite.Info p_i226045_1_, int p_i226045_2_) {
         this.spriteInfo = p_i226045_1_;
         this.width = Stitcher.smallestFittingMinTexel(p_i226045_1_.width(), p_i226045_2_);
         this.height = Stitcher.smallestFittingMinTexel(p_i226045_1_.height(), p_i226045_2_);
      }

      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.spriteInfo.name() + '}';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface ISpriteLoader {
      void load(TextureAtlasSprite.Info p_load_1_, int p_load_2_, int p_load_3_, int p_load_4_, int p_load_5_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Slot {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Slot> subSlots;
      private Stitcher.Holder holder;

      public Slot(int p_i1277_1_, int p_i1277_2_, int p_i1277_3_, int p_i1277_4_) {
         this.originX = p_i1277_1_;
         this.originY = p_i1277_2_;
         this.width = p_i1277_3_;
         this.height = p_i1277_4_;
      }

      public Stitcher.Holder getHolder() {
         return this.holder;
      }

      public int getX() {
         return this.originX;
      }

      public int getY() {
         return this.originY;
      }

      public boolean add(Stitcher.Holder p_94182_1_) {
         if (this.holder != null) {
            return false;
         } else {
            int i = p_94182_1_.width;
            int j = p_94182_1_.height;
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.holder = p_94182_1_;
                  return true;
               } else {
                  if (this.subSlots == null) {
                     this.subSlots = Lists.newArrayListWithCapacity(1);
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
                     int k = this.width - i;
                     int l = this.height - j;
                     if (l > 0 && k > 0) {
                        int i1 = Math.max(this.height, k);
                        int j1 = Math.max(this.width, l);
                        if (i1 >= j1) {
                           this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                           this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                           this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                        }
                     } else if (k == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                     } else if (l == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                     }
                  }

                  for(Stitcher.Slot stitcher$slot : this.subSlots) {
                     if (stitcher$slot.add(p_94182_1_)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void walk(Consumer<Stitcher.Slot> p_217792_1_) {
         if (this.holder != null) {
            p_217792_1_.accept(this);
         } else if (this.subSlots != null) {
            for(Stitcher.Slot stitcher$slot : this.subSlots) {
               stitcher$slot.walk(p_217792_1_);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
      }
   }
}
