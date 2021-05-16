package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AirItem extends Item {
   private final Block block;

   public AirItem(Block p_i48535_1_, Item.Properties p_i48535_2_) {
      super(p_i48535_2_);
      this.block = p_i48535_1_;
   }

   public String getDescriptionId() {
      return this.block.getDescriptionId();
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      this.block.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
   }
}
