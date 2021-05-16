package net.minecraft.nbt;

public class NBTTypes {
   private static final INBTType<?>[] TYPES = new INBTType[]{EndNBT.TYPE, ByteNBT.TYPE, ShortNBT.TYPE, IntNBT.TYPE, LongNBT.TYPE, FloatNBT.TYPE, DoubleNBT.TYPE, ByteArrayNBT.TYPE, StringNBT.TYPE, ListNBT.TYPE, CompoundNBT.TYPE, IntArrayNBT.TYPE, LongArrayNBT.TYPE};

   public static INBTType<?> getType(int p_229710_0_) {
      return p_229710_0_ >= 0 && p_229710_0_ < TYPES.length ? TYPES[p_229710_0_] : INBTType.createInvalid(p_229710_0_);
   }
}
