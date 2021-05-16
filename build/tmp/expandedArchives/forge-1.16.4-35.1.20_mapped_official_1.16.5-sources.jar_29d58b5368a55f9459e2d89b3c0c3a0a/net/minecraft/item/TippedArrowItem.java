package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties p_i48457_1_) {
      super(p_i48457_1_);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.POISON);
   }

   public void fillItemCategory(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.allowdedIn(p_150895_1_)) {
         for(Potion potion : Registry.POTION) {
            if (!potion.getEffects().isEmpty()) {
               p_150895_2_.add(PotionUtils.setPotion(new ItemStack(this), potion));
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 0.125F);
   }

   public String getDescriptionId(ItemStack p_77667_1_) {
      return PotionUtils.getPotion(p_77667_1_).getName(this.getDescriptionId() + ".effect.");
   }
}
