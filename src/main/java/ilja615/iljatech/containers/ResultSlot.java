package ilja615.iljatech.containers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ResultSlot extends SlotItemHandler
{
    private int removeCount;

    public ResultSlot(IItemHandlerModifiable itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
    }

    public boolean mayPlace(ItemStack p_39553_) {
        return false;
    }

    public ItemStack remove(int p_39548_) {
        if (this.hasItem()) {
            this.removeCount += Math.min(p_39548_, this.getItem().getCount());
        }

        return super.remove(p_39548_);
    }

    public void onTake(Player p_150563_, ItemStack p_150564_) {
        this.checkTakeAchievements(p_150564_);
        super.onTake(p_150563_, p_150564_);
    }

    protected void onQuickCraft(ItemStack p_39555_, int p_39556_) {
        this.removeCount += p_39556_;
        this.checkTakeAchievements(p_39555_);
    }
}
