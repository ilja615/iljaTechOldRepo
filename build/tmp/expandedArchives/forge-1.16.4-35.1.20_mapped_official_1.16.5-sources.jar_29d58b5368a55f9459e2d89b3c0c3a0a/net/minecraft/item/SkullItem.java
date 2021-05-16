package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallOrFloorItem {
   public SkullItem(Block p_i48477_1_, Block p_i48477_2_, Item.Properties p_i48477_3_) {
      super(p_i48477_1_, p_i48477_2_, p_i48477_3_);
   }

   public ITextComponent getName(ItemStack p_200295_1_) {
      if (p_200295_1_.getItem() == Items.PLAYER_HEAD && p_200295_1_.hasTag()) {
         String s = null;
         CompoundNBT compoundnbt = p_200295_1_.getTag();
         if (compoundnbt.contains("SkullOwner", 8)) {
            s = compoundnbt.getString("SkullOwner");
         } else if (compoundnbt.contains("SkullOwner", 10)) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("SkullOwner");
            if (compoundnbt1.contains("Name", 8)) {
               s = compoundnbt1.getString("Name");
            }
         }

         if (s != null) {
            return new TranslationTextComponent(this.getDescriptionId() + ".named", s);
         }
      }

      return super.getName(p_200295_1_);
   }

   public boolean verifyTagAfterLoad(CompoundNBT p_179215_1_) {
      super.verifyTagAfterLoad(p_179215_1_);
      if (p_179215_1_.contains("SkullOwner", 8) && !StringUtils.isBlank(p_179215_1_.getString("SkullOwner"))) {
         GameProfile gameprofile = new GameProfile((UUID)null, p_179215_1_.getString("SkullOwner"));
         gameprofile = SkullTileEntity.updateGameprofile(gameprofile);
         p_179215_1_.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
         return true;
      } else {
         return false;
      }
   }
}
