package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetVisibility {
   private static final int FACINGS = Direction.values().length;
   private final BitSet data = new BitSet(FACINGS * FACINGS);

   public void add(Set<Direction> p_178620_1_) {
      for(Direction direction : p_178620_1_) {
         for(Direction direction1 : p_178620_1_) {
            this.set(direction, direction1, true);
         }
      }

   }

   public void set(Direction p_178619_1_, Direction p_178619_2_, boolean p_178619_3_) {
      this.data.set(p_178619_1_.ordinal() + p_178619_2_.ordinal() * FACINGS, p_178619_3_);
      this.data.set(p_178619_2_.ordinal() + p_178619_1_.ordinal() * FACINGS, p_178619_3_);
   }

   public void setAll(boolean p_178618_1_) {
      this.data.set(0, this.data.size(), p_178618_1_);
   }

   public boolean visibilityBetween(Direction p_178621_1_, Direction p_178621_2_) {
      return this.data.get(p_178621_1_.ordinal() + p_178621_2_.ordinal() * FACINGS);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(' ');

      for(Direction direction : Direction.values()) {
         stringbuilder.append(' ').append(direction.toString().toUpperCase().charAt(0));
      }

      stringbuilder.append('\n');

      for(Direction direction2 : Direction.values()) {
         stringbuilder.append(direction2.toString().toUpperCase().charAt(0));

         for(Direction direction1 : Direction.values()) {
            if (direction2 == direction1) {
               stringbuilder.append("  ");
            } else {
               boolean flag = this.visibilityBetween(direction2, direction1);
               stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
            }
         }

         stringbuilder.append('\n');
      }

      return stringbuilder.toString();
   }
}
