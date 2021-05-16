package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DispenserTileEntity extends LockableLootTileEntity {
   private static final Random RANDOM = new Random();
   private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

   protected DispenserTileEntity(TileEntityType<?> p_i48286_1_) {
      super(p_i48286_1_);
   }

   public DispenserTileEntity() {
      this(TileEntityType.DISPENSER);
   }

   public int getContainerSize() {
      return 9;
   }

   public int getRandomSlot() {
      this.unpackLootTable((PlayerEntity)null);
      int i = -1;
      int j = 1;

      for(int k = 0; k < this.items.size(); ++k) {
         if (!this.items.get(k).isEmpty() && RANDOM.nextInt(j++) == 0) {
            i = k;
         }
      }

      return i;
   }

   public int addItem(ItemStack p_146019_1_) {
      for(int i = 0; i < this.items.size(); ++i) {
         if (this.items.get(i).isEmpty()) {
            this.setItem(i, p_146019_1_);
            return i;
         }
      }

      return -1;
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.dispenser");
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(p_230337_2_)) {
         ItemStackHelper.loadAllItems(p_230337_2_, this.items);
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (!this.trySaveLootTable(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.items);
      }

      return p_189515_1_;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.items = p_199721_1_;
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new DispenserContainer(p_213906_1_, p_213906_2_, this);
   }
}
