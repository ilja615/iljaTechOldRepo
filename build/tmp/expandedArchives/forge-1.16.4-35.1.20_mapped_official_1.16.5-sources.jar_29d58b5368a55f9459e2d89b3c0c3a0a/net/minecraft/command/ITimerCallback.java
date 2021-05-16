package net.minecraft.command;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface ITimerCallback<T> {
   void handle(T p_212869_1_, TimerCallbackManager<T> p_212869_2_, long p_212869_3_);

   public abstract static class Serializer<T, C extends ITimerCallback<T>> {
      private final ResourceLocation id;
      private final Class<?> cls;

      public Serializer(ResourceLocation p_i51270_1_, Class<?> p_i51270_2_) {
         this.id = p_i51270_1_;
         this.cls = p_i51270_2_;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public Class<?> getCls() {
         return this.cls;
      }

      public abstract void serialize(CompoundNBT p_212847_1_, C p_212847_2_);

      public abstract C deserialize(CompoundNBT p_212846_1_);
   }
}
