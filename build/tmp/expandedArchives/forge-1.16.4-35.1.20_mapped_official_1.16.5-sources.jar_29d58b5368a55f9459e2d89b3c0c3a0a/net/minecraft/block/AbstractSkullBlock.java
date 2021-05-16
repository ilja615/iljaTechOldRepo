package net.minecraft.block;

import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSkullBlock extends ContainerBlock implements IArmorVanishable {
   private final SkullBlock.ISkullType type;

   public AbstractSkullBlock(SkullBlock.ISkullType p_i48452_1_, AbstractBlock.Properties p_i48452_2_) {
      super(p_i48452_2_);
      this.type = p_i48452_1_;
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new SkullTileEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public SkullBlock.ISkullType getType() {
      return this.type;
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
