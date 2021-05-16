package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class BannerItem extends WallOrFloorItem {
   public BannerItem(Block p_i48529_1_, Block p_i48529_2_, Item.Properties p_i48529_3_) {
      super(p_i48529_1_, p_i48529_2_, p_i48529_3_);
      Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_1_);
      Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static void appendHoverTextFromBannerBlockEntityTag(ItemStack p_185054_0_, List<ITextComponent> p_185054_1_) {
      CompoundNBT compoundnbt = p_185054_0_.getTagElement("BlockEntityTag");
      if (compoundnbt != null && compoundnbt.contains("Patterns")) {
         ListNBT listnbt = compoundnbt.getList("Patterns", 10);

         for(int i = 0; i < listnbt.size() && i < 6; ++i) {
            CompoundNBT compoundnbt1 = listnbt.getCompound(i);
            DyeColor dyecolor = DyeColor.byId(compoundnbt1.getInt("Color"));
            BannerPattern bannerpattern = BannerPattern.byHash(compoundnbt1.getString("Pattern"));
            if (bannerpattern != null) {
               p_185054_1_.add((new TranslationTextComponent("block.minecraft.banner." + bannerpattern.getFilename() + '.' + dyecolor.getName())).withStyle(TextFormatting.GRAY));
            }
         }

      }
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      appendHoverTextFromBannerBlockEntityTag(p_77624_1_, p_77624_3_);
   }
}
