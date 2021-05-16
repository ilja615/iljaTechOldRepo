package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ChestMinecartEntity extends ContainerMinecartEntity {
   public ChestMinecartEntity(EntityType<? extends ChestMinecartEntity> p_i50124_1_, World p_i50124_2_) {
      super(p_i50124_1_, p_i50124_2_);
   }

   public ChestMinecartEntity(World p_i1715_1_, double p_i1715_2_, double p_i1715_4_, double p_i1715_6_) {
      super(EntityType.CHEST_MINECART, p_i1715_2_, p_i1715_4_, p_i1715_6_, p_i1715_1_);
   }

   public void destroy(DamageSource p_94095_1_) {
      super.destroy(p_94095_1_);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.CHEST);
      }

   }

   public int getContainerSize() {
      return 27;
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.CHEST;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
   }

   public int getDefaultDisplayOffset() {
      return 8;
   }

   public Container createMenu(int p_213968_1_, PlayerInventory p_213968_2_) {
      return ChestContainer.threeRows(p_213968_1_, p_213968_2_, this);
   }
}
