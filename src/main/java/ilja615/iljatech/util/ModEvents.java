package ilja615.iljatech.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEvents
{
    @SubscribeEvent
    public static void onPlacePumpkin(final BlockEvent.EntityPlaceEvent event) {
        if (!event.isCanceled() && event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN
                && event.getLevel() instanceof Level) {
            // spawn golem

        }
    }
}
