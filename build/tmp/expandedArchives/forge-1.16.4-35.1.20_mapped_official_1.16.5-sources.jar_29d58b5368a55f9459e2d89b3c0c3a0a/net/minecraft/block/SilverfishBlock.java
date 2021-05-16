package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SilverfishBlock extends Block {
   private final Block hostBlock;
   private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();

   public SilverfishBlock(Block p_i48374_1_, AbstractBlock.Properties p_i48374_2_) {
      super(p_i48374_2_);
      this.hostBlock = p_i48374_1_;
      BLOCK_BY_HOST_BLOCK.put(p_i48374_1_, this);
   }

   public Block getHostBlock() {
      return this.hostBlock;
   }

   public static boolean isCompatibleHostBlock(BlockState p_196466_0_) {
      return BLOCK_BY_HOST_BLOCK.containsKey(p_196466_0_.getBlock());
   }

   private void spawnInfestation(ServerWorld p_235505_1_, BlockPos p_235505_2_) {
      SilverfishEntity silverfishentity = EntityType.SILVERFISH.create(p_235505_1_);
      silverfishentity.moveTo((double)p_235505_2_.getX() + 0.5D, (double)p_235505_2_.getY(), (double)p_235505_2_.getZ() + 0.5D, 0.0F, 0.0F);
      p_235505_1_.addFreshEntity(silverfishentity);
      silverfishentity.spawnAnim();
   }

   public void spawnAfterBreak(BlockState p_220062_1_, ServerWorld p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAfterBreak(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
      if (p_220062_2_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, p_220062_4_) == 0) {
         this.spawnInfestation(p_220062_2_, p_220062_3_);
      }

   }

   public void wasExploded(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
      if (p_180652_1_ instanceof ServerWorld) {
         this.spawnInfestation((ServerWorld)p_180652_1_, p_180652_2_);
      }

   }

   public static BlockState stateByHostBlock(Block p_196467_0_) {
      return BLOCK_BY_HOST_BLOCK.get(p_196467_0_).defaultBlockState();
   }
}
