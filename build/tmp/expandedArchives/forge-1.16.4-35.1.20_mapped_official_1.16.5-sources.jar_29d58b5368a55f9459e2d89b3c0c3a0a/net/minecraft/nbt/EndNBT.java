package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EndNBT implements INBT {
   public static final INBTType<EndNBT> TYPE = new INBTType<EndNBT>() {
      public EndNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) {
         p_225649_3_.accountBits(64L);
         return EndNBT.INSTANCE;
      }

      public String getName() {
         return "END";
      }

      public String getPrettyName() {
         return "TAG_End";
      }

      public boolean isValue() {
         return true;
      }
   };
   public static final EndNBT INSTANCE = new EndNBT();

   private EndNBT() {
   }

   public void write(DataOutput p_74734_1_) throws IOException {
   }

   public byte getId() {
      return 0;
   }

   public INBTType<EndNBT> getType() {
      return TYPE;
   }

   public String toString() {
      return "END";
   }

   public EndNBT copy() {
      return this;
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      return StringTextComponent.EMPTY;
   }
}
