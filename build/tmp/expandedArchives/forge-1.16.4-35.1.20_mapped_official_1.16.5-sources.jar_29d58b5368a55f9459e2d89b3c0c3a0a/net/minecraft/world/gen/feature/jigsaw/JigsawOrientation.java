package net.minecraft.world.gen.feature.jigsaw;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

public enum JigsawOrientation implements IStringSerializable {
   DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
   DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
   DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
   DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
   UP_EAST("up_east", Direction.UP, Direction.EAST),
   UP_NORTH("up_north", Direction.UP, Direction.NORTH),
   UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
   UP_WEST("up_west", Direction.UP, Direction.WEST),
   WEST_UP("west_up", Direction.WEST, Direction.UP),
   EAST_UP("east_up", Direction.EAST, Direction.UP),
   NORTH_UP("north_up", Direction.NORTH, Direction.UP),
   SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

   private static final Int2ObjectMap<JigsawOrientation> LOOKUP_TOP_FRONT = new Int2ObjectOpenHashMap<>(values().length);
   private final String name;
   private final Direction top;
   private final Direction front;

   private static int lookupKey(Direction p_239643_0_, Direction p_239643_1_) {
      return p_239643_0_.ordinal() << 3 | p_239643_1_.ordinal();
   }

   private JigsawOrientation(String p_i232507_3_, Direction p_i232507_4_, Direction p_i232507_5_) {
      this.name = p_i232507_3_;
      this.front = p_i232507_4_;
      this.top = p_i232507_5_;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static JigsawOrientation fromFrontAndTop(Direction p_239641_0_, Direction p_239641_1_) {
      int i = lookupKey(p_239641_1_, p_239641_0_);
      return LOOKUP_TOP_FRONT.get(i);
   }

   public Direction front() {
      return this.front;
   }

   public Direction top() {
      return this.top;
   }

   static {
      for(JigsawOrientation jigsaworientation : values()) {
         LOOKUP_TOP_FRONT.put(lookupKey(jigsaworientation.top, jigsaworientation.front), jigsaworientation);
      }

   }
}
