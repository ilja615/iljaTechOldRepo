package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IMutableSearchTree<T> extends ISearchTree<T> {
   void add(T p_217872_1_);

   void clear();

   void refresh();
}
