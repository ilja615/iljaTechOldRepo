package net.minecraft.block.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.util.CachedBlockInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
   private static final Joiner COMMA_JOINED = Joiner.on(",");
   private final List<String[]> pattern = Lists.newArrayList();
   private final Map<Character, Predicate<CachedBlockInfo>> lookup = Maps.newHashMap();
   private int height;
   private int width;

   private BlockPatternBuilder() {
      this.lookup.put(' ', Predicates.alwaysTrue());
   }

   public BlockPatternBuilder aisle(String... p_177659_1_) {
      if (!ArrayUtils.isEmpty((Object[])p_177659_1_) && !StringUtils.isEmpty(p_177659_1_[0])) {
         if (this.pattern.isEmpty()) {
            this.height = p_177659_1_.length;
            this.width = p_177659_1_[0].length();
         }

         if (p_177659_1_.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + p_177659_1_.length + ")");
         } else {
            for(String s : p_177659_1_) {
               if (s.length() != this.width) {
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + s.length() + ")");
               }

               for(char c0 : s.toCharArray()) {
                  if (!this.lookup.containsKey(c0)) {
                     this.lookup.put(c0, (Predicate<CachedBlockInfo>)null);
                  }
               }
            }

            this.pattern.add(p_177659_1_);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder where(char p_177662_1_, Predicate<CachedBlockInfo> p_177662_2_) {
      this.lookup.put(p_177662_1_, p_177662_2_);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.createPattern());
   }

   private Predicate<CachedBlockInfo>[][][] createPattern() {
      this.ensureAllCharactersMatched();
      Predicate<CachedBlockInfo>[][][] predicate = (Predicate[][][])Array.newInstance(Predicate.class, this.pattern.size(), this.height, this.width);

      for(int i = 0; i < this.pattern.size(); ++i) {
         for(int j = 0; j < this.height; ++j) {
            for(int k = 0; k < this.width; ++k) {
               predicate[i][j][k] = this.lookup.get((this.pattern.get(i))[j].charAt(k));
            }
         }
      }

      return predicate;
   }

   private void ensureAllCharactersMatched() {
      List<Character> list = Lists.newArrayList();

      for(Entry<Character, Predicate<CachedBlockInfo>> entry : this.lookup.entrySet()) {
         if (entry.getValue() == null) {
            list.add(entry.getKey());
         }
      }

      if (!list.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join(list) + " are missing");
      }
   }
}
