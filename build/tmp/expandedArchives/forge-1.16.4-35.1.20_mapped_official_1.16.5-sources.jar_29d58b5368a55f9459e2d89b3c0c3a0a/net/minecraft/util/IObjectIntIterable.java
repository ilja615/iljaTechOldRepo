package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T> extends Iterable<T> {
   int getId(T p_148757_1_);

   @Nullable
   T byId(int p_148745_1_);
}
