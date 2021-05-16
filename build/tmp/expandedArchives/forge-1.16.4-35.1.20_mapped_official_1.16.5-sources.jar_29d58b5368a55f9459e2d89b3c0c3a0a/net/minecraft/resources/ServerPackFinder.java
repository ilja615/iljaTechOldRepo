package net.minecraft.resources;

import java.util.function.Consumer;

public class ServerPackFinder implements IPackFinder {
   private final VanillaPack vanillaPack = new VanillaPack("minecraft");

   public void loadPacks(Consumer<ResourcePackInfo> p_230230_1_, ResourcePackInfo.IFactory p_230230_2_) {
      ResourcePackInfo resourcepackinfo = ResourcePackInfo.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, p_230230_2_, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILT_IN);
      if (resourcepackinfo != null) {
         p_230230_1_.accept(resourcepackinfo);
      }

   }
}
