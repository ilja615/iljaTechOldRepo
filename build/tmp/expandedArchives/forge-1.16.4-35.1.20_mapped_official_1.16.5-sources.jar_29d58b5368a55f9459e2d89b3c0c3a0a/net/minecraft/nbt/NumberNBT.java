package net.minecraft.nbt;

public abstract class NumberNBT implements INBT {
   protected NumberNBT() {
   }

   public abstract long getAsLong();

   public abstract int getAsInt();

   public abstract short getAsShort();

   public abstract byte getAsByte();

   public abstract double getAsDouble();

   public abstract float getAsFloat();

   public abstract Number getAsNumber();
}
