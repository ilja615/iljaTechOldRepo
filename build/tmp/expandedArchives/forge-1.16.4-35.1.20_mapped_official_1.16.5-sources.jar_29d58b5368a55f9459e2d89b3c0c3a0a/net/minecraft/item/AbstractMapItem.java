package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

public class AbstractMapItem extends Item {
   public AbstractMapItem(Item.Properties p_i48514_1_) {
      super(p_i48514_1_);
   }

   public boolean isComplex() {
      return true;
   }

   @Nullable
   public IPacket<?> getUpdatePacket(ItemStack p_150911_1_, World p_150911_2_, PlayerEntity p_150911_3_) {
      return null;
   }
}
