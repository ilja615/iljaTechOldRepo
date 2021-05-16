package net.minecraft.data;

import javax.annotation.Nullable;

public final class StockTextureAliases {
   public static final StockTextureAliases ALL = create("all");
   public static final StockTextureAliases TEXTURE = create("texture", ALL);
   public static final StockTextureAliases PARTICLE = create("particle", TEXTURE);
   public static final StockTextureAliases END = create("end", ALL);
   public static final StockTextureAliases BOTTOM = create("bottom", END);
   public static final StockTextureAliases TOP = create("top", END);
   public static final StockTextureAliases FRONT = create("front", ALL);
   public static final StockTextureAliases BACK = create("back", ALL);
   public static final StockTextureAliases SIDE = create("side", ALL);
   public static final StockTextureAliases NORTH = create("north", SIDE);
   public static final StockTextureAliases SOUTH = create("south", SIDE);
   public static final StockTextureAliases EAST = create("east", SIDE);
   public static final StockTextureAliases WEST = create("west", SIDE);
   public static final StockTextureAliases UP = create("up");
   public static final StockTextureAliases DOWN = create("down");
   public static final StockTextureAliases CROSS = create("cross");
   public static final StockTextureAliases PLANT = create("plant");
   public static final StockTextureAliases WALL = create("wall", ALL);
   public static final StockTextureAliases RAIL = create("rail");
   public static final StockTextureAliases WOOL = create("wool");
   public static final StockTextureAliases PATTERN = create("pattern");
   public static final StockTextureAliases PANE = create("pane");
   public static final StockTextureAliases EDGE = create("edge");
   public static final StockTextureAliases FAN = create("fan");
   public static final StockTextureAliases STEM = create("stem");
   public static final StockTextureAliases UPPER_STEM = create("upperstem");
   public static final StockTextureAliases CROP = create("crop");
   public static final StockTextureAliases DIRT = create("dirt");
   public static final StockTextureAliases FIRE = create("fire");
   public static final StockTextureAliases LANTERN = create("lantern");
   public static final StockTextureAliases PLATFORM = create("platform");
   public static final StockTextureAliases UNSTICKY = create("unsticky");
   public static final StockTextureAliases TORCH = create("torch");
   public static final StockTextureAliases LAYER0 = create("layer0");
   public static final StockTextureAliases LIT_LOG = create("lit_log");
   private final String id;
   @Nullable
   private final StockTextureAliases parent;

   private static StockTextureAliases create(String p_240431_0_) {
      return new StockTextureAliases(p_240431_0_, (StockTextureAliases)null);
   }

   private static StockTextureAliases create(String p_240432_0_, StockTextureAliases p_240432_1_) {
      return new StockTextureAliases(p_240432_0_, p_240432_1_);
   }

   private StockTextureAliases(String p_i232547_1_, @Nullable StockTextureAliases p_i232547_2_) {
      this.id = p_i232547_1_;
      this.parent = p_i232547_2_;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public StockTextureAliases getParent() {
      return this.parent;
   }

   public String toString() {
      return "#" + this.id;
   }
}
