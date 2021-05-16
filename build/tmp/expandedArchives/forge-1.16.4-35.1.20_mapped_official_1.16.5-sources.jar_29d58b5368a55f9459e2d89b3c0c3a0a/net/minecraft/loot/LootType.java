package net.minecraft.loot;

public class LootType<T> {
   private final ILootSerializer<? extends T> serializer;

   public LootType(ILootSerializer<? extends T> p_i232166_1_) {
      this.serializer = p_i232166_1_;
   }

   public ILootSerializer<? extends T> getSerializer() {
      return this.serializer;
   }
}
