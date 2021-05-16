package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern implements net.minecraftforge.common.IExtensibleEnum {
   BASE("base", "b", false),
   SQUARE_BOTTOM_LEFT("square_bottom_left", "bl"),
   SQUARE_BOTTOM_RIGHT("square_bottom_right", "br"),
   SQUARE_TOP_LEFT("square_top_left", "tl"),
   SQUARE_TOP_RIGHT("square_top_right", "tr"),
   STRIPE_BOTTOM("stripe_bottom", "bs"),
   STRIPE_TOP("stripe_top", "ts"),
   STRIPE_LEFT("stripe_left", "ls"),
   STRIPE_RIGHT("stripe_right", "rs"),
   STRIPE_CENTER("stripe_center", "cs"),
   STRIPE_MIDDLE("stripe_middle", "ms"),
   STRIPE_DOWNRIGHT("stripe_downright", "drs"),
   STRIPE_DOWNLEFT("stripe_downleft", "dls"),
   STRIPE_SMALL("small_stripes", "ss"),
   CROSS("cross", "cr"),
   STRAIGHT_CROSS("straight_cross", "sc"),
   TRIANGLE_BOTTOM("triangle_bottom", "bt"),
   TRIANGLE_TOP("triangle_top", "tt"),
   TRIANGLES_BOTTOM("triangles_bottom", "bts"),
   TRIANGLES_TOP("triangles_top", "tts"),
   DIAGONAL_LEFT("diagonal_left", "ld"),
   DIAGONAL_RIGHT("diagonal_up_right", "rd"),
   DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud"),
   DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud"),
   CIRCLE_MIDDLE("circle", "mc"),
   RHOMBUS_MIDDLE("rhombus", "mr"),
   HALF_VERTICAL("half_vertical", "vh"),
   HALF_HORIZONTAL("half_horizontal", "hh"),
   HALF_VERTICAL_MIRROR("half_vertical_right", "vhr"),
   HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb"),
   BORDER("border", "bo"),
   CURLY_BORDER("curly_border", "cbo"),
   GRADIENT("gradient", "gra"),
   GRADIENT_UP("gradient_up", "gru"),
   BRICKS("bricks", "bri"),
   GLOBE("globe", "glb", true),
   CREEPER("creeper", "cre", true),
   SKULL("skull", "sku", true),
   FLOWER("flower", "flo", true),
   MOJANG("mojang", "moj", true),
   PIGLIN("piglin", "pig", true);

   private static final BannerPattern[] VALUES = values();
   public static final int COUNT = VALUES.length;
   public static final int PATTERN_ITEM_COUNT = (int)Arrays.stream(VALUES).filter((p_235649_0_) -> {
      return p_235649_0_.hasPatternItem;
   }).count();
   public static final int AVAILABLE_PATTERNS = COUNT - PATTERN_ITEM_COUNT - 1;
   private final boolean hasPatternItem;
   private final String filename;
   private final String hashname;

   private BannerPattern(String p_i47245_3_, String p_i47245_4_) {
      this(p_i47245_3_, p_i47245_4_, false);
   }

   private BannerPattern(String p_i231861_3_, String p_i231861_4_, boolean p_i231861_5_) {
      this.filename = p_i231861_3_;
      this.hashname = p_i231861_4_;
      this.hasPatternItem = p_i231861_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation location(boolean p_226957_1_) {
      String s = p_226957_1_ ? "banner" : "shield";
      return new ResourceLocation("entity/" + s + "/" + this.getFilename());
   }

   @OnlyIn(Dist.CLIENT)
   public String getFilename() {
      return this.filename;
   }

   public String getHashname() {
      return this.hashname;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static BannerPattern byHash(String p_190994_0_) {
      for(BannerPattern bannerpattern : values()) {
         if (bannerpattern.hashname.equals(p_190994_0_)) {
            return bannerpattern;
         }
      }

      return null;
   }

   public static BannerPattern create(String enumName, String fileNameIn, String hashNameIn) {
      throw new IllegalStateException("Enum not extended");
   }

   public static BannerPattern create(String enumName, String fileNameIn, String hashNameIn, boolean hasPatternItem) {
      throw new IllegalStateException("Enum not extended");
   }

   public static class Builder {
      private final List<Pair<BannerPattern, DyeColor>> patterns = Lists.newArrayList();

      public BannerPattern.Builder addPattern(BannerPattern p_222477_1_, DyeColor p_222477_2_) {
         this.patterns.add(Pair.of(p_222477_1_, p_222477_2_));
         return this;
      }

      public ListNBT toListTag() {
         ListNBT listnbt = new ListNBT();

         for(Pair<BannerPattern, DyeColor> pair : this.patterns) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Pattern", (pair.getLeft()).hashname);
            compoundnbt.putInt("Color", pair.getRight().getId());
            listnbt.add(compoundnbt);
         }

         return listnbt;
      }
   }
}
