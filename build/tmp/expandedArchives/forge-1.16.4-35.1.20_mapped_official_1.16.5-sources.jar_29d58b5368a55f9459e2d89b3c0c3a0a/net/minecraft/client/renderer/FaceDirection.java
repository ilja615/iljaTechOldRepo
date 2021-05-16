package net.minecraft.client.renderer;

import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum FaceDirection {
   DOWN(new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MAX_Z)),
   UP(new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MIN_Z)),
   NORTH(new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MIN_Z)),
   SOUTH(new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MAX_Z)),
   WEST(new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MIN_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MAX_Z)),
   EAST(new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MAX_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MIN_Y, FaceDirection.Constants.MIN_Z), new FaceDirection.VertexInformation(FaceDirection.Constants.MAX_X, FaceDirection.Constants.MAX_Y, FaceDirection.Constants.MIN_Z));

   private static final FaceDirection[] BY_FACING = Util.make(new FaceDirection[6], (p_209235_0_) -> {
      p_209235_0_[FaceDirection.Constants.MIN_Y] = DOWN;
      p_209235_0_[FaceDirection.Constants.MAX_Y] = UP;
      p_209235_0_[FaceDirection.Constants.MIN_Z] = NORTH;
      p_209235_0_[FaceDirection.Constants.MAX_Z] = SOUTH;
      p_209235_0_[FaceDirection.Constants.MIN_X] = WEST;
      p_209235_0_[FaceDirection.Constants.MAX_X] = EAST;
   });
   private final FaceDirection.VertexInformation[] infos;

   public static FaceDirection fromFacing(Direction p_179027_0_) {
      return BY_FACING[p_179027_0_.get3DDataValue()];
   }

   private FaceDirection(FaceDirection.VertexInformation... p_i46272_3_) {
      this.infos = p_i46272_3_;
   }

   public FaceDirection.VertexInformation getVertexInfo(int p_179025_1_) {
      return this.infos[p_179025_1_];
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Constants {
      public static final int MAX_Z = Direction.SOUTH.get3DDataValue();
      public static final int MAX_Y = Direction.UP.get3DDataValue();
      public static final int MAX_X = Direction.EAST.get3DDataValue();
      public static final int MIN_Z = Direction.NORTH.get3DDataValue();
      public static final int MIN_Y = Direction.DOWN.get3DDataValue();
      public static final int MIN_X = Direction.WEST.get3DDataValue();
   }

   @OnlyIn(Dist.CLIENT)
   public static class VertexInformation {
      public final int xFace;
      public final int yFace;
      public final int zFace;

      private VertexInformation(int p_i46270_1_, int p_i46270_2_, int p_i46270_3_) {
         this.xFace = p_i46270_1_;
         this.yFace = p_i46270_2_;
         this.zFace = p_i46270_3_;
      }
   }
}
