package net.minecraft.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;

public enum DyeColor implements IStringSerializable {
   WHITE(0, "white", 16383998, MaterialColor.SNOW, 15790320, 16777215),
   ORANGE(1, "orange", 16351261, MaterialColor.COLOR_ORANGE, 15435844, 16738335),
   MAGENTA(2, "magenta", 13061821, MaterialColor.COLOR_MAGENTA, 12801229, 16711935),
   LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.COLOR_LIGHT_BLUE, 6719955, 10141901),
   YELLOW(4, "yellow", 16701501, MaterialColor.COLOR_YELLOW, 14602026, 16776960),
   LIME(5, "lime", 8439583, MaterialColor.COLOR_LIGHT_GREEN, 4312372, 12582656),
   PINK(6, "pink", 15961002, MaterialColor.COLOR_PINK, 14188952, 16738740),
   GRAY(7, "gray", 4673362, MaterialColor.COLOR_GRAY, 4408131, 8421504),
   LIGHT_GRAY(8, "light_gray", 10329495, MaterialColor.COLOR_LIGHT_GRAY, 11250603, 13882323),
   CYAN(9, "cyan", 1481884, MaterialColor.COLOR_CYAN, 2651799, 65535),
   PURPLE(10, "purple", 8991416, MaterialColor.COLOR_PURPLE, 8073150, 10494192),
   BLUE(11, "blue", 3949738, MaterialColor.COLOR_BLUE, 2437522, 255),
   BROWN(12, "brown", 8606770, MaterialColor.COLOR_BROWN, 5320730, 9127187),
   GREEN(13, "green", 6192150, MaterialColor.COLOR_GREEN, 3887386, 65280),
   RED(14, "red", 11546150, MaterialColor.COLOR_RED, 11743532, 16711680),
   BLACK(15, "black", 1908001, MaterialColor.COLOR_BLACK, 1973019, 0);

   private static final DyeColor[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray((p_199795_0_) -> {
      return new DyeColor[p_199795_0_];
   });
   private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap<>(Arrays.stream(values()).collect(Collectors.toMap((p_199793_0_) -> {
      return p_199793_0_.fireworkColor;
   }, (p_199794_0_) -> {
      return p_199794_0_;
   })));
   private final int id;
   private final String name;
   private final MaterialColor color;
   private final int textureDiffuseColor;
   private final int textureDiffuseColorBGR;
   private final float[] textureDiffuseColors;
   private final int fireworkColor;
   private final net.minecraftforge.common.Tags.IOptionalNamedTag<Item> tag;
   private final int textColor;

   private DyeColor(int p_i50049_3_, String p_i50049_4_, int p_i50049_5_, MaterialColor p_i50049_6_, int p_i50049_7_, int p_i50049_8_) {
      this.id = p_i50049_3_;
      this.name = p_i50049_4_;
      this.textureDiffuseColor = p_i50049_5_;
      this.color = p_i50049_6_;
      this.textColor = p_i50049_8_;
      int i = (p_i50049_5_ & 16711680) >> 16;
      int j = (p_i50049_5_ & '\uff00') >> 8;
      int k = (p_i50049_5_ & 255) >> 0;
      this.textureDiffuseColorBGR = k << 16 | j << 8 | i << 0;
      this.tag = net.minecraft.tags.ItemTags.createOptional(new net.minecraft.util.ResourceLocation("forge", "dyes/" + p_i50049_4_));
      this.textureDiffuseColors = new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
      this.fireworkColor = p_i50049_7_;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public float[] getTextureDiffuseColors() {
      return this.textureDiffuseColors;
   }

   public MaterialColor getMaterialColor() {
      return this.color;
   }

   public int getFireworkColor() {
      return this.fireworkColor;
   }

   public int getTextColor() {
      return this.textColor;
   }

   public static DyeColor byId(int p_196056_0_) {
      if (p_196056_0_ < 0 || p_196056_0_ >= BY_ID.length) {
         p_196056_0_ = 0;
      }

      return BY_ID[p_196056_0_];
   }

   public static DyeColor byName(String p_204271_0_, DyeColor p_204271_1_) {
      for(DyeColor dyecolor : values()) {
         if (dyecolor.name.equals(p_204271_0_)) {
            return dyecolor;
         }
      }

      return p_204271_1_;
   }

   @Nullable
   public static DyeColor byFireworkColor(int p_196058_0_) {
      return BY_FIREWORK_COLOR.get(p_196058_0_);
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public int getColorValue() {
      return textureDiffuseColor;
   }

   public net.minecraftforge.common.Tags.IOptionalNamedTag<Item> getTag() {
      return tag;
   }

   @Nullable
   public static DyeColor getColor(ItemStack stack) {
      if (stack.getItem() instanceof DyeItem)
         return ((DyeItem)stack.getItem()).getDyeColor();

      for (DyeColor color : BY_ID) {
         if (stack.getItem().is(color.getTag()))
             return color;
      }

      return null;
   }
}
