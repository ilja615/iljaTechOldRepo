package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SkullPlayerBlock extends SkullBlock {
   public SkullPlayerBlock(AbstractBlock.Properties p_i48354_1_) {
      super(SkullBlock.Types.PLAYER, p_i48354_1_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
      if (tileentity instanceof SkullTileEntity) {
         SkullTileEntity skulltileentity = (SkullTileEntity)tileentity;
         GameProfile gameprofile = null;
         if (p_180633_5_.hasTag()) {
            CompoundNBT compoundnbt = p_180633_5_.getTag();
            if (compoundnbt.contains("SkullOwner", 10)) {
               gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
            } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundnbt.getString("SkullOwner"))) {
               gameprofile = new GameProfile((UUID)null, compoundnbt.getString("SkullOwner"));
            }
         }

         skulltileentity.setOwner(gameprofile);
      }

   }
}
