package net.minecraft.util.math.vector;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.TriplePermutation;
import net.minecraft.util.Util;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;

public enum Orientation implements IStringSerializable {
   IDENTITY("identity", TriplePermutation.P123, false, false, false),
   ROT_180_FACE_XY("rot_180_face_xy", TriplePermutation.P123, true, true, false),
   ROT_180_FACE_XZ("rot_180_face_xz", TriplePermutation.P123, true, false, true),
   ROT_180_FACE_YZ("rot_180_face_yz", TriplePermutation.P123, false, true, true),
   ROT_120_NNN("rot_120_nnn", TriplePermutation.P231, false, false, false),
   ROT_120_NNP("rot_120_nnp", TriplePermutation.P312, true, false, true),
   ROT_120_NPN("rot_120_npn", TriplePermutation.P312, false, true, true),
   ROT_120_NPP("rot_120_npp", TriplePermutation.P231, true, false, true),
   ROT_120_PNN("rot_120_pnn", TriplePermutation.P312, true, true, false),
   ROT_120_PNP("rot_120_pnp", TriplePermutation.P231, true, true, false),
   ROT_120_PPN("rot_120_ppn", TriplePermutation.P231, false, true, true),
   ROT_120_PPP("rot_120_ppp", TriplePermutation.P312, false, false, false),
   ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", TriplePermutation.P213, true, true, true),
   ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", TriplePermutation.P213, false, false, true),
   ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", TriplePermutation.P321, true, true, true),
   ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", TriplePermutation.P321, false, true, false),
   ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", TriplePermutation.P132, true, true, true),
   ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", TriplePermutation.P132, true, false, false),
   ROT_90_X_NEG("rot_90_x_neg", TriplePermutation.P132, false, false, true),
   ROT_90_X_POS("rot_90_x_pos", TriplePermutation.P132, false, true, false),
   ROT_90_Y_NEG("rot_90_y_neg", TriplePermutation.P321, true, false, false),
   ROT_90_Y_POS("rot_90_y_pos", TriplePermutation.P321, false, false, true),
   ROT_90_Z_NEG("rot_90_z_neg", TriplePermutation.P213, false, true, false),
   ROT_90_Z_POS("rot_90_z_pos", TriplePermutation.P213, true, false, false),
   INVERSION("inversion", TriplePermutation.P123, true, true, true),
   INVERT_X("invert_x", TriplePermutation.P123, true, false, false),
   INVERT_Y("invert_y", TriplePermutation.P123, false, true, false),
   INVERT_Z("invert_z", TriplePermutation.P123, false, false, true),
   ROT_60_REF_NNN("rot_60_ref_nnn", TriplePermutation.P312, true, true, true),
   ROT_60_REF_NNP("rot_60_ref_nnp", TriplePermutation.P231, true, false, false),
   ROT_60_REF_NPN("rot_60_ref_npn", TriplePermutation.P231, false, false, true),
   ROT_60_REF_NPP("rot_60_ref_npp", TriplePermutation.P312, false, false, true),
   ROT_60_REF_PNN("rot_60_ref_pnn", TriplePermutation.P231, false, true, false),
   ROT_60_REF_PNP("rot_60_ref_pnp", TriplePermutation.P312, true, false, false),
   ROT_60_REF_PPN("rot_60_ref_ppn", TriplePermutation.P312, false, true, false),
   ROT_60_REF_PPP("rot_60_ref_ppp", TriplePermutation.P231, true, true, true),
   SWAP_XY("swap_xy", TriplePermutation.P213, false, false, false),
   SWAP_YZ("swap_yz", TriplePermutation.P132, false, false, false),
   SWAP_XZ("swap_xz", TriplePermutation.P321, false, false, false),
   SWAP_NEG_XY("swap_neg_xy", TriplePermutation.P213, true, true, false),
   SWAP_NEG_YZ("swap_neg_yz", TriplePermutation.P132, false, true, true),
   SWAP_NEG_XZ("swap_neg_xz", TriplePermutation.P321, true, false, true),
   ROT_90_REF_X_NEG("rot_90_ref_x_neg", TriplePermutation.P132, true, false, true),
   ROT_90_REF_X_POS("rot_90_ref_x_pos", TriplePermutation.P132, true, true, false),
   ROT_90_REF_Y_NEG("rot_90_ref_y_neg", TriplePermutation.P321, true, true, false),
   ROT_90_REF_Y_POS("rot_90_ref_y_pos", TriplePermutation.P321, false, true, true),
   ROT_90_REF_Z_NEG("rot_90_ref_z_neg", TriplePermutation.P213, false, true, true),
   ROT_90_REF_Z_POS("rot_90_ref_z_pos", TriplePermutation.P213, true, false, true);

   private final Matrix3f transformation;
   private final String name;
   @Nullable
   private Map<Direction, Direction> rotatedDirections;
   private final boolean invertX;
   private final boolean invertY;
   private final boolean invertZ;
   private final TriplePermutation permutation;
   private static final Orientation[][] cayleyTable = Util.make(new Orientation[values().length][values().length], (p_235532_0_) -> {
      Map<Pair<TriplePermutation, BooleanList>, Orientation> map = Arrays.stream(values()).collect(Collectors.toMap((p_235536_0_) -> {
         return Pair.of(p_235536_0_.permutation, p_235536_0_.packInversions());
      }, (p_235535_0_) -> {
         return p_235535_0_;
      }));

      for(Orientation orientation : values()) {
         for(Orientation orientation1 : values()) {
            BooleanList booleanlist = orientation.packInversions();
            BooleanList booleanlist1 = orientation1.packInversions();
            TriplePermutation triplepermutation = orientation1.permutation.compose(orientation.permutation);
            BooleanArrayList booleanarraylist = new BooleanArrayList(3);

            for(int i = 0; i < 3; ++i) {
               booleanarraylist.add(booleanlist.getBoolean(i) ^ booleanlist1.getBoolean(orientation.permutation.permutation(i)));
            }

            p_235532_0_[orientation.ordinal()][orientation1.ordinal()] = map.get(Pair.of(triplepermutation, booleanarraylist));
         }
      }

   });
   private static final Orientation[] inverseTable = Arrays.stream(values()).map((p_235534_0_) -> {
      return Arrays.stream(values()).filter((p_235528_1_) -> {
         return p_235534_0_.compose(p_235528_1_) == IDENTITY;
      }).findAny().get();
   }).toArray((p_235526_0_) -> {
      return new Orientation[p_235526_0_];
   });

   private Orientation(String p_i231784_3_, TriplePermutation p_i231784_4_, boolean p_i231784_5_, boolean p_i231784_6_, boolean p_i231784_7_) {
      this.name = p_i231784_3_;
      this.invertX = p_i231784_5_;
      this.invertY = p_i231784_6_;
      this.invertZ = p_i231784_7_;
      this.permutation = p_i231784_4_;
      this.transformation = new Matrix3f();
      this.transformation.m00 = p_i231784_5_ ? -1.0F : 1.0F;
      this.transformation.m11 = p_i231784_6_ ? -1.0F : 1.0F;
      this.transformation.m22 = p_i231784_7_ ? -1.0F : 1.0F;
      this.transformation.mul(p_i231784_4_.transformation());
   }

   private BooleanList packInversions() {
      return new BooleanArrayList(new boolean[]{this.invertX, this.invertY, this.invertZ});
   }

   public Orientation compose(Orientation p_235527_1_) {
      return cayleyTable[this.ordinal()][p_235527_1_.ordinal()];
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public Direction rotate(Direction p_235530_1_) {
      if (this.rotatedDirections == null) {
         this.rotatedDirections = Maps.newEnumMap(Direction.class);

         for(Direction direction : Direction.values()) {
            Direction.Axis direction$axis = direction.getAxis();
            Direction.AxisDirection direction$axisdirection = direction.getAxisDirection();
            Direction.Axis direction$axis1 = Direction.Axis.values()[this.permutation.permutation(direction$axis.ordinal())];
            Direction.AxisDirection direction$axisdirection1 = this.inverts(direction$axis1) ? direction$axisdirection.opposite() : direction$axisdirection;
            Direction direction1 = Direction.fromAxisAndDirection(direction$axis1, direction$axisdirection1);
            this.rotatedDirections.put(direction, direction1);
         }
      }

      return this.rotatedDirections.get(p_235530_1_);
   }

   public boolean inverts(Direction.Axis p_235529_1_) {
      switch(p_235529_1_) {
      case X:
         return this.invertX;
      case Y:
         return this.invertY;
      case Z:
      default:
         return this.invertZ;
      }
   }

   public JigsawOrientation rotate(JigsawOrientation p_235531_1_) {
      return JigsawOrientation.fromFrontAndTop(this.rotate(p_235531_1_.front()), this.rotate(p_235531_1_.top()));
   }
}
