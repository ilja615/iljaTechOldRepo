package net.minecraft.client.settings;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HotbarSnapshot extends ForwardingList<ItemStack> {
   private final NonNullList<ItemStack> items = NonNullList.withSize(PlayerInventory.getSelectionSize(), ItemStack.EMPTY);

   protected List<ItemStack> delegate() {
      return this.items;
   }

   public ListNBT createTag() {
      ListNBT listnbt = new ListNBT();

      for(ItemStack itemstack : this.delegate()) {
         listnbt.add(itemstack.save(new CompoundNBT()));
      }

      return listnbt;
   }

   public void fromTag(ListNBT p_192833_1_) {
      List<ItemStack> list = this.delegate();

      for(int i = 0; i < list.size(); ++i) {
         list.set(i, ItemStack.of(p_192833_1_.getCompound(i)));
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.delegate()) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
