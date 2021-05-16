package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkStarItem extends Item {
   public FireworkStarItem(Item.Properties p_i48496_1_) {
      super(p_i48496_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      CompoundNBT compoundnbt = p_77624_1_.getTagElement("Explosion");
      if (compoundnbt != null) {
         appendHoverText(compoundnbt, p_77624_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void appendHoverText(CompoundNBT p_195967_0_, List<ITextComponent> p_195967_1_) {
      FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.byId(p_195967_0_.getByte("Type"));
      p_195967_1_.add((new TranslationTextComponent("item.minecraft.firework_star.shape." + fireworkrocketitem$shape.getName())).withStyle(TextFormatting.GRAY));
      int[] aint = p_195967_0_.getIntArray("Colors");
      if (aint.length > 0) {
         p_195967_1_.add(appendColors((new StringTextComponent("")).withStyle(TextFormatting.GRAY), aint));
      }

      int[] aint1 = p_195967_0_.getIntArray("FadeColors");
      if (aint1.length > 0) {
         p_195967_1_.add(appendColors((new TranslationTextComponent("item.minecraft.firework_star.fade_to")).append(" ").withStyle(TextFormatting.GRAY), aint1));
      }

      if (p_195967_0_.getBoolean("Trail")) {
         p_195967_1_.add((new TranslationTextComponent("item.minecraft.firework_star.trail")).withStyle(TextFormatting.GRAY));
      }

      if (p_195967_0_.getBoolean("Flicker")) {
         p_195967_1_.add((new TranslationTextComponent("item.minecraft.firework_star.flicker")).withStyle(TextFormatting.GRAY));
      }

   }

   @OnlyIn(Dist.CLIENT)
   private static ITextComponent appendColors(IFormattableTextComponent p_200298_0_, int[] p_200298_1_) {
      for(int i = 0; i < p_200298_1_.length; ++i) {
         if (i > 0) {
            p_200298_0_.append(", ");
         }

         p_200298_0_.append(getColorName(p_200298_1_[i]));
      }

      return p_200298_0_;
   }

   @OnlyIn(Dist.CLIENT)
   private static ITextComponent getColorName(int p_200297_0_) {
      DyeColor dyecolor = DyeColor.byFireworkColor(p_200297_0_);
      return dyecolor == null ? new TranslationTextComponent("item.minecraft.firework_star.custom_color") : new TranslationTextComponent("item.minecraft.firework_star." + dyecolor.getName());
   }
}
