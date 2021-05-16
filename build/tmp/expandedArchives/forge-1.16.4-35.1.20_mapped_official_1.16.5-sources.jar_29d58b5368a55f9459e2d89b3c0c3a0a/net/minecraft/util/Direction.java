package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum Direction implements IStringSerializable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vector3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vector3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vector3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vector3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vector3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vector3i(1, 0, 0));

   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vector3i normal;
   private static final Direction[] VALUES = values();
   private static final Map<String, Direction> BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName, (p_199787_0_) -> {
      return p_199787_0_;
   }));
   private static final Direction[] BY_3D_DATA = Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
      return p_199790_0_.data3d;
   })).toArray((p_199788_0_) -> {
      return new Direction[p_199788_0_];
   });
   private static final Direction[] BY_2D_DATA = Arrays.stream(VALUES).filter((p_199786_0_) -> {
      return p_199786_0_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_199789_0_) -> {
      return p_199789_0_.data2d;
   })).toArray((p_199791_0_) -> {
      return new Direction[p_199791_0_];
   });
   private static final Long2ObjectMap<Direction> BY_NORMAL = Arrays.stream(VALUES).collect(Collectors.toMap((p_218385_0_) -> {
      return (new BlockPos(p_218385_0_.getNormal())).asLong();
   }, (p_218384_0_) -> {
      return p_218384_0_;
   }, (p_218386_0_, p_218386_1_) -> {
      throw new IllegalArgumentException("Duplicate keys");
   }, Long2ObjectOpenHashMap::new));

   private Direction(int p_i46016_3_, int p_i46016_4_, int p_i46016_5_, String p_i46016_6_, Direction.AxisDirection p_i46016_7_, Direction.Axis p_i46016_8_, Vector3i p_i46016_9_) {
      this.data3d = p_i46016_3_;
      this.data2d = p_i46016_5_;
      this.oppositeIndex = p_i46016_4_;
      this.name = p_i46016_6_;
      this.axis = p_i46016_8_;
      this.axisDirection = p_i46016_7_;
      this.normal = p_i46016_9_;
   }

   public static Direction[] orderedByNearest(Entity p_196054_0_) {
      float f = p_196054_0_.getViewXRot(1.0F) * ((float)Math.PI / 180F);
      float f1 = -p_196054_0_.getViewYRot(1.0F) * ((float)Math.PI / 180F);
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      Direction direction = flag ? EAST : WEST;
      Direction direction1 = flag1 ? UP : DOWN;
      Direction direction2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return makeDirectionArray(direction1, direction, direction2);
         } else {
            return f10 > f7 ? makeDirectionArray(direction, direction2, direction1) : makeDirectionArray(direction, direction1, direction2);
         }
      } else if (f7 > f10) {
         return makeDirectionArray(direction1, direction2, direction);
      } else {
         return f9 > f7 ? makeDirectionArray(direction2, direction, direction1) : makeDirectionArray(direction2, direction1, direction);
      }
   }

   private static Direction[] makeDirectionArray(Direction p_196053_0_, Direction p_196053_1_, Direction p_196053_2_) {
      return new Direction[]{p_196053_0_, p_196053_1_, p_196053_2_, p_196053_2_.getOpposite(), p_196053_1_.getOpposite(), p_196053_0_.getOpposite()};
   }

   @OnlyIn(Dist.CLIENT)
   public static Direction rotate(Matrix4f p_229385_0_, Direction p_229385_1_) {
      Vector3i vector3i = p_229385_1_.getNormal();
      Vector4f vector4f = new Vector4f((float)vector3i.getX(), (float)vector3i.getY(), (float)vector3i.getZ(), 0.0F);
      vector4f.transform(p_229385_0_);
      return getNearest(vector4f.x(), vector4f.y(), vector4f.z());
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion getRotation() {
      Quaternion quaternion = Vector3f.XP.rotationDegrees(90.0F);
      switch(this) {
      case DOWN:
         return Vector3f.XP.rotationDegrees(180.0F);
      case UP:
         return Quaternion.ONE.copy();
      case NORTH:
         quaternion.mul(Vector3f.ZP.rotationDegrees(180.0F));
         return quaternion;
      case SOUTH:
         return quaternion;
      case WEST:
         quaternion.mul(Vector3f.ZP.rotationDegrees(90.0F));
         return quaternion;
      case EAST:
      default:
         quaternion.mul(Vector3f.ZP.rotationDegrees(-90.0F));
         return quaternion;
      }
   }

   public int get3DDataValue() {
      return this.data3d;
   }

   public int get2DDataValue() {
      return this.data2d;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise() {
      switch(this) {
      case NORTH:
         return EAST;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      case EAST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   public Direction getCounterClockWise() {
      switch(this) {
      case NORTH:
         return WEST;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      case EAST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getStepX() {
      return this.normal.getX();
   }

   public int getStepY() {
      return this.normal.getY();
   }

   public int getStepZ() {
      return this.normal.getZ();
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3f step() {
      return new Vector3f((float)this.getStepX(), (float)this.getStepY(), (float)this.getStepZ());
   }

   public String getName() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String p_176739_0_) {
      return p_176739_0_ == null ? null : BY_NAME.get(p_176739_0_.toLowerCase(Locale.ROOT));
   }

   public static Direction from3DDataValue(int p_82600_0_) {
      return BY_3D_DATA[MathHelper.abs(p_82600_0_ % BY_3D_DATA.length)];
   }

   public static Direction from2DDataValue(int p_176731_0_) {
      return BY_2D_DATA[MathHelper.abs(p_176731_0_ % BY_2D_DATA.length)];
   }

   @Nullable
   public static Direction fromNormal(int p_218383_0_, int p_218383_1_, int p_218383_2_) {
      return BY_NORMAL.get(BlockPos.asLong(p_218383_0_, p_218383_1_, p_218383_2_));
   }

   public static Direction fromYRot(double p_176733_0_) {
      return from2DDataValue(MathHelper.floor(p_176733_0_ / 90.0D + 0.5D) & 3);
   }

   public static Direction fromAxisAndDirection(Direction.Axis p_211699_0_, Direction.AxisDirection p_211699_1_) {
      switch(p_211699_0_) {
      case X:
         return p_211699_1_ == Direction.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return p_211699_1_ == Direction.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return p_211699_1_ == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandom(Random p_239631_0_) {
      return Util.getRandom(VALUES, p_239631_0_);
   }

   public static Direction getNearest(double p_210769_0_, double p_210769_2_, double p_210769_4_) {
      return getNearest((float)p_210769_0_, (float)p_210769_2_, (float)p_210769_4_);
   }

   public static Direction getNearest(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
      Direction direction = NORTH;
      float f = Float.MIN_VALUE;

      for(Direction direction1 : VALUES) {
         float f1 = p_176737_0_ * (float)direction1.normal.getX() + p_176737_1_ * (float)direction1.normal.getY() + p_176737_2_ * (float)direction1.normal.getZ();
         if (f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static Direction get(Direction.AxisDirection p_181076_0_, Direction.Axis p_181076_1_) {
      for(Direction direction : VALUES) {
         if (direction.getAxisDirection() == p_181076_0_ && direction.getAxis() == p_181076_1_) {
            return direction;
         }
      }

      throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
   }

   public Vector3i getNormal() {
      return this.normal;
   }

   public boolean isFacingAngle(float p_243532_1_) {
      float f = p_243532_1_ * ((float)Math.PI / 180F);
      float f1 = -MathHelper.sin(f);
      float f2 = MathHelper.cos(f);
      return (float)this.normal.getX() * f1 + (float)this.normal.getZ() * f2 > 0.0F;
   }

   public static enum Axis implements IStringSerializable, Predicate<Direction> {
      X("x") {
         public int choose(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_1_;
         }

         public double choose(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_1_;
         }
      },
      Y("y") {
         public int choose(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_2_;
         }

         public double choose(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_3_;
         }
      },
      Z("z") {
         public int choose(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_3_;
         }

         public double choose(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_5_;
         }
      };

      private static final Direction.Axis[] VALUES = values();
      public static final Codec<Direction.Axis> CODEC = IStringSerializable.fromEnum(Direction.Axis::values, Direction.Axis::byName);
      private static final Map<String, Direction.Axis> BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Direction.Axis::getName, (p_199785_0_) -> {
         return p_199785_0_;
      }));
      private final String name;

      private Axis(String p_i49394_3_) {
         this.name = p_i49394_3_;
      }

      @Nullable
      public static Direction.Axis byName(String p_176717_0_) {
         return BY_NAME.get(p_176717_0_.toLowerCase(Locale.ROOT));
      }

      public String getName() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public static Direction.Axis getRandom(Random p_239634_0_) {
         return Util.getRandom(VALUES, p_239634_0_);
      }

      public boolean test(@Nullable Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return Direction.Plane.HORIZONTAL;
         case Y:
            return Direction.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract int choose(int p_196052_1_, int p_196052_2_, int p_196052_3_);

      public abstract double choose(double p_196051_1_, double p_196051_3_, double p_196051_5_);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int step;
      private final String name;

      private AxisDirection(int p_i46014_3_, String p_i46014_4_) {
         this.step = p_i46014_3_;
         this.name = p_i46014_4_;
      }

      public int getStep() {
         return this.step;
      }

      public String toString() {
         return this.name;
      }

      public Direction.AxisDirection opposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }
   }

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] faces;
      private final Direction.Axis[] axis;

      private Plane(Direction[] p_i49393_3_, Direction.Axis[] p_i49393_4_) {
         this.faces = p_i49393_3_;
         this.axis = p_i49393_4_;
      }

      public Direction getRandomDirection(Random p_179518_1_) {
         return Util.getRandom(this.faces, p_179518_1_);
      }

      public boolean test(@Nullable Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      public Stream<Direction> stream() {
         return Arrays.stream(this.faces);
      }
   }
}
