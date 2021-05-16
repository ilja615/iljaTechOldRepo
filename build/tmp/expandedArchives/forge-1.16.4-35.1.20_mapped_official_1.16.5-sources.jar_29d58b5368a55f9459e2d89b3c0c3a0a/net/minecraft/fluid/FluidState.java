package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class FluidState extends StateHolder<Fluid, FluidState> implements net.minecraftforge.common.extensions.IForgeFluidState {
   public static final Codec<FluidState> CODEC = codec(Registry.FLUID, Fluid::defaultFluidState).stable();

   public FluidState(Fluid p_i232145_1_, ImmutableMap<Property<?>, Comparable<?>> p_i232145_2_, MapCodec<FluidState> p_i232145_3_) {
      super(p_i232145_1_, p_i232145_2_, p_i232145_3_);
   }

   public Fluid getType() {
      return this.owner;
   }

   public boolean isSource() {
      return this.getType().isSource(this);
   }

   public boolean isEmpty() {
      return this.getType().isEmpty();
   }

   public float getHeight(IBlockReader p_215679_1_, BlockPos p_215679_2_) {
      return this.getType().getHeight(this, p_215679_1_, p_215679_2_);
   }

   public float getOwnHeight() {
      return this.getType().getOwnHeight(this);
   }

   public int getAmount() {
      return this.getType().getAmount(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderBackwardUpFace(IBlockReader p_205586_1_, BlockPos p_205586_2_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos = p_205586_2_.offset(i, 0, j);
            FluidState fluidstate = p_205586_1_.getFluidState(blockpos);
            if (!fluidstate.getType().isSame(this.getType()) && !p_205586_1_.getBlockState(blockpos).isSolidRender(p_205586_1_, blockpos)) {
               return true;
            }
         }
      }

      return false;
   }

   public void tick(World p_206880_1_, BlockPos p_206880_2_) {
      this.getType().tick(p_206880_1_, p_206880_2_, this);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World p_206881_1_, BlockPos p_206881_2_, Random p_206881_3_) {
      this.getType().animateTick(p_206881_1_, p_206881_2_, this, p_206881_3_);
   }

   public boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   public void randomTick(World p_206891_1_, BlockPos p_206891_2_, Random p_206891_3_) {
      this.getType().randomTick(p_206891_1_, p_206891_2_, this, p_206891_3_);
   }

   public Vector3d getFlow(IBlockReader p_215673_1_, BlockPos p_215673_2_) {
      return this.getType().getFlow(p_215673_1_, p_215673_2_, this);
   }

   public BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticle() {
      return this.getType().getDripParticle();
   }

   public boolean is(ITag<Fluid> p_206884_1_) {
      return this.getType().is(p_206884_1_);
   }

   @Deprecated //Forge: Use more sensitive version
   public float getExplosionResistance() {
      return this.getType().getExplosionResistance();
   }

   public boolean canBeReplacedWith(IBlockReader p_215677_1_, BlockPos p_215677_2_, Fluid p_215677_3_, Direction p_215677_4_) {
      return this.getType().canBeReplacedWith(this, p_215677_1_, p_215677_2_, p_215677_3_, p_215677_4_);
   }

   public VoxelShape getShape(IBlockReader p_215676_1_, BlockPos p_215676_2_) {
      return this.getType().getShape(this, p_215676_1_, p_215676_2_);
   }
}
