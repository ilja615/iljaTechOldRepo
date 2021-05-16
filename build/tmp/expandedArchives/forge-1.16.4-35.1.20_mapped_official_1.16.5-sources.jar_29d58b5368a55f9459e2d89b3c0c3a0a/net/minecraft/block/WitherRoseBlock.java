package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WitherRoseBlock extends FlowerBlock {
   public WitherRoseBlock(Effect p_i49968_1_, AbstractBlock.Properties p_i49968_2_) {
      super(p_i49968_1_, 8, p_i49968_2_);
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return super.mayPlaceOn(p_200014_1_, p_200014_2_, p_200014_3_) || p_200014_1_.is(Blocks.NETHERRACK) || p_200014_1_.is(Blocks.SOUL_SAND) || p_200014_1_.is(Blocks.SOUL_SOIL);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      VoxelShape voxelshape = this.getShape(p_180655_1_, p_180655_2_, p_180655_3_, ISelectionContext.empty());
      Vector3d vector3d = voxelshape.bounds().getCenter();
      double d0 = (double)p_180655_3_.getX() + vector3d.x;
      double d1 = (double)p_180655_3_.getZ() + vector3d.z;

      for(int i = 0; i < 3; ++i) {
         if (p_180655_4_.nextBoolean()) {
            p_180655_2_.addParticle(ParticleTypes.SMOKE, d0 + p_180655_4_.nextDouble() / 5.0D, (double)p_180655_3_.getY() + (0.5D - p_180655_4_.nextDouble()), d1 + p_180655_4_.nextDouble() / 5.0D, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isClientSide && p_196262_2_.getDifficulty() != Difficulty.PEACEFUL) {
         if (p_196262_4_ instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)p_196262_4_;
            if (!livingentity.isInvulnerableTo(DamageSource.WITHER)) {
               livingentity.addEffect(new EffectInstance(Effects.WITHER, 40));
            }
         }

      }
   }
}
