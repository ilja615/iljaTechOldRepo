package net.minecraft.resources;

import java.util.function.Consumer;

public interface IPackFinder {
   void loadPacks(Consumer<ResourcePackInfo> p_230230_1_, ResourcePackInfo.IFactory p_230230_2_);
}
