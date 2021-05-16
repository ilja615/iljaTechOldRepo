package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PointOfInterest {
   private final BlockPos pos;
   private final PointOfInterestType poiType;
   private int freeTickets;
   private final Runnable setDirty;

   public static Codec<PointOfInterest> codec(Runnable p_234150_0_) {
      return RecordCodecBuilder.create((p_234151_1_) -> {
         return p_234151_1_.group(BlockPos.CODEC.fieldOf("pos").forGetter((p_234153_0_) -> {
            return p_234153_0_.pos;
         }), Registry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter((p_234152_0_) -> {
            return p_234152_0_.poiType;
         }), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter((p_234149_0_) -> {
            return p_234149_0_.freeTickets;
         }), RecordCodecBuilder.point(p_234150_0_)).apply(p_234151_1_, PointOfInterest::new);
      });
   }

   private PointOfInterest(BlockPos p_i50295_1_, PointOfInterestType p_i50295_2_, int p_i50295_3_, Runnable p_i50295_4_) {
      this.pos = p_i50295_1_.immutable();
      this.poiType = p_i50295_2_;
      this.freeTickets = p_i50295_3_;
      this.setDirty = p_i50295_4_;
   }

   public PointOfInterest(BlockPos p_i50296_1_, PointOfInterestType p_i50296_2_, Runnable p_i50296_3_) {
      this(p_i50296_1_, p_i50296_2_, p_i50296_2_.getMaxTickets(), p_i50296_3_);
   }

   protected boolean acquireTicket() {
      if (this.freeTickets <= 0) {
         return false;
      } else {
         --this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   protected boolean releaseTicket() {
      if (this.freeTickets >= this.poiType.getMaxTickets()) {
         return false;
      } else {
         ++this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   public boolean hasSpace() {
      return this.freeTickets > 0;
   }

   public boolean isOccupied() {
      return this.freeTickets != this.poiType.getMaxTickets();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public PointOfInterestType getPoiType() {
      return this.poiType;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? Objects.equals(this.pos, ((PointOfInterest)p_equals_1_).pos) : false;
      }
   }

   public int hashCode() {
      return this.pos.hashCode();
   }
}
