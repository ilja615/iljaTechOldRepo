package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ListNBT extends CollectionNBT<INBT> {
   public static final INBTType<ListNBT> TYPE = new INBTType<ListNBT>() {
      public ListNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(296L);
         if (p_225649_2_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            byte b0 = p_225649_1_.readByte();
            int i = p_225649_1_.readInt();
            if (b0 == 0 && i > 0) {
               throw new RuntimeException("Missing type on ListTag");
            } else {
               p_225649_3_.accountBits(32L * (long)i);
               INBTType<?> inbttype = NBTTypes.getType(b0);
               List<INBT> list = Lists.newArrayListWithCapacity(i);

               for(int j = 0; j < i; ++j) {
                  list.add(inbttype.load(p_225649_1_, p_225649_2_ + 1, p_225649_3_));
               }

               return new ListNBT(list, b0);
            }
         }
      }

      public String getName() {
         return "LIST";
      }

      public String getPrettyName() {
         return "TAG_List";
      }
   };
   private static final ByteSet INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
   private final List<INBT> list;
   private byte type;

   private ListNBT(List<INBT> p_i226078_1_, byte p_i226078_2_) {
      this.list = p_i226078_1_;
      this.type = p_i226078_2_;
   }

   public ListNBT() {
      this(Lists.newArrayList(), (byte)0);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      if (this.list.isEmpty()) {
         this.type = 0;
      } else {
         this.type = this.list.get(0).getId();
      }

      p_74734_1_.writeByte(this.type);
      p_74734_1_.writeInt(this.list.size());

      for(INBT inbt : this.list) {
         inbt.write(p_74734_1_);
      }

   }

   public byte getId() {
      return 9;
   }

   public INBTType<ListNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[");

      for(int i = 0; i < this.list.size(); ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.list.get(i));
      }

      return stringbuilder.append(']').toString();
   }

   private void updateTypeAfterRemove() {
      if (this.list.isEmpty()) {
         this.type = 0;
      }

   }

   public INBT remove(int p_remove_1_) {
      INBT inbt = this.list.remove(p_remove_1_);
      this.updateTypeAfterRemove();
      return inbt;
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public CompoundNBT getCompound(int p_150305_1_) {
      if (p_150305_1_ >= 0 && p_150305_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_150305_1_);
         if (inbt.getId() == 10) {
            return (CompoundNBT)inbt;
         }
      }

      return new CompoundNBT();
   }

   public ListNBT getList(int p_202169_1_) {
      if (p_202169_1_ >= 0 && p_202169_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_202169_1_);
         if (inbt.getId() == 9) {
            return (ListNBT)inbt;
         }
      }

      return new ListNBT();
   }

   public short getShort(int p_202170_1_) {
      if (p_202170_1_ >= 0 && p_202170_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_202170_1_);
         if (inbt.getId() == 2) {
            return ((ShortNBT)inbt).getAsShort();
         }
      }

      return 0;
   }

   public int getInt(int p_186858_1_) {
      if (p_186858_1_ >= 0 && p_186858_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_186858_1_);
         if (inbt.getId() == 3) {
            return ((IntNBT)inbt).getAsInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int p_150306_1_) {
      if (p_150306_1_ >= 0 && p_150306_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_150306_1_);
         if (inbt.getId() == 11) {
            return ((IntArrayNBT)inbt).getAsIntArray();
         }
      }

      return new int[0];
   }

   public double getDouble(int p_150309_1_) {
      if (p_150309_1_ >= 0 && p_150309_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_150309_1_);
         if (inbt.getId() == 6) {
            return ((DoubleNBT)inbt).getAsDouble();
         }
      }

      return 0.0D;
   }

   public float getFloat(int p_150308_1_) {
      if (p_150308_1_ >= 0 && p_150308_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_150308_1_);
         if (inbt.getId() == 5) {
            return ((FloatNBT)inbt).getAsFloat();
         }
      }

      return 0.0F;
   }

   public String getString(int p_150307_1_) {
      if (p_150307_1_ >= 0 && p_150307_1_ < this.list.size()) {
         INBT inbt = this.list.get(p_150307_1_);
         return inbt.getId() == 8 ? inbt.getAsString() : inbt.toString();
      } else {
         return "";
      }
   }

   public int size() {
      return this.list.size();
   }

   public INBT get(int p_get_1_) {
      return this.list.get(p_get_1_);
   }

   public INBT set(int p_set_1_, INBT p_set_2_) {
      INBT inbt = this.get(p_set_1_);
      if (!this.setTag(p_set_1_, p_set_2_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_set_2_.getId(), this.type));
      } else {
         return inbt;
      }
   }

   public void add(int p_add_1_, INBT p_add_2_) {
      if (!this.addTag(p_add_1_, p_add_2_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_add_2_.getId(), this.type));
      }
   }

   public boolean setTag(int p_218659_1_, INBT p_218659_2_) {
      if (this.updateType(p_218659_2_)) {
         this.list.set(p_218659_1_, p_218659_2_);
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int p_218660_1_, INBT p_218660_2_) {
      if (this.updateType(p_218660_2_)) {
         this.list.add(p_218660_1_, p_218660_2_);
         return true;
      } else {
         return false;
      }
   }

   private boolean updateType(INBT p_218661_1_) {
      if (p_218661_1_.getId() == 0) {
         return false;
      } else if (this.type == 0) {
         this.type = p_218661_1_.getId();
         return true;
      } else {
         return this.type == p_218661_1_.getId();
      }
   }

   public ListNBT copy() {
      Iterable<INBT> iterable = (Iterable<INBT>)(NBTTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, INBT::copy));
      List<INBT> list = Lists.newArrayList(iterable);
      return new ListNBT(list, this.type);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ListNBT && Objects.equals(this.list, ((ListNBT)p_equals_1_).list);
      }
   }

   public int hashCode() {
      return this.list.hashCode();
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      if (this.isEmpty()) {
         return new StringTextComponent("[]");
      } else if (INLINE_ELEMENT_TYPES.contains(this.type) && this.size() <= 8) {
         String s1 = ", ";
         IFormattableTextComponent iformattabletextcomponent2 = new StringTextComponent("[");

         for(int j = 0; j < this.list.size(); ++j) {
            if (j != 0) {
               iformattabletextcomponent2.append(", ");
            }

            iformattabletextcomponent2.append(this.list.get(j).getPrettyDisplay());
         }

         iformattabletextcomponent2.append("]");
         return iformattabletextcomponent2;
      } else {
         IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("[");
         if (!p_199850_1_.isEmpty()) {
            iformattabletextcomponent.append("\n");
         }

         String s = String.valueOf(',');

         for(int i = 0; i < this.list.size(); ++i) {
            IFormattableTextComponent iformattabletextcomponent1 = new StringTextComponent(Strings.repeat(p_199850_1_, p_199850_2_ + 1));
            iformattabletextcomponent1.append(this.list.get(i).getPrettyDisplay(p_199850_1_, p_199850_2_ + 1));
            if (i != this.list.size() - 1) {
               iformattabletextcomponent1.append(s).append(p_199850_1_.isEmpty() ? " " : "\n");
            }

            iformattabletextcomponent.append(iformattabletextcomponent1);
         }

         if (!p_199850_1_.isEmpty()) {
            iformattabletextcomponent.append("\n").append(Strings.repeat(p_199850_1_, p_199850_2_));
         }

         iformattabletextcomponent.append("]");
         return iformattabletextcomponent;
      }
   }

   public byte getElementType() {
      return this.type;
   }

   public void clear() {
      this.list.clear();
      this.type = 0;
   }
}
