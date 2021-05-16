package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NBTPathArgument implements ArgumentType<NBTPathArgument.NBTPath> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
   public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.nbtpath.node.invalid"));
   public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType((p_208665_0_) -> {
      return new TranslationTextComponent("arguments.nbtpath.nothing_found", p_208665_0_);
   });

   public static NBTPathArgument nbtPath() {
      return new NBTPathArgument();
   }

   public static NBTPathArgument.NBTPath getPath(CommandContext<CommandSource> p_197148_0_, String p_197148_1_) {
      return p_197148_0_.getArgument(p_197148_1_, NBTPathArgument.NBTPath.class);
   }

   public NBTPathArgument.NBTPath parse(StringReader p_parse_1_) throws CommandSyntaxException {
      List<NBTPathArgument.INode> list = Lists.newArrayList();
      int i = p_parse_1_.getCursor();
      Object2IntMap<NBTPathArgument.INode> object2intmap = new Object2IntOpenHashMap<>();
      boolean flag = true;

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         NBTPathArgument.INode nbtpathargument$inode = parseNode(p_parse_1_, flag);
         list.add(nbtpathargument$inode);
         object2intmap.put(nbtpathargument$inode, p_parse_1_.getCursor() - i);
         flag = false;
         if (p_parse_1_.canRead()) {
            char c0 = p_parse_1_.peek();
            if (c0 != ' ' && c0 != '[' && c0 != '{') {
               p_parse_1_.expect('.');
            }
         }
      }

      return new NBTPathArgument.NBTPath(p_parse_1_.getString().substring(i, p_parse_1_.getCursor()), list.toArray(new NBTPathArgument.INode[0]), object2intmap);
   }

   private static NBTPathArgument.INode parseNode(StringReader p_218079_0_, boolean p_218079_1_) throws CommandSyntaxException {
      switch(p_218079_0_.peek()) {
      case '"':
         String s = p_218079_0_.readString();
         return readObjectNode(p_218079_0_, s);
      case '[':
         p_218079_0_.skip();
         int j = p_218079_0_.peek();
         if (j == 123) {
            CompoundNBT compoundnbt1 = (new JsonToNBT(p_218079_0_)).readStruct();
            p_218079_0_.expect(']');
            return new NBTPathArgument.ListNode(compoundnbt1);
         } else {
            if (j == 93) {
               p_218079_0_.skip();
               return NBTPathArgument.EmptyListNode.INSTANCE;
            }

            int i = p_218079_0_.readInt();
            p_218079_0_.expect(']');
            return new NBTPathArgument.CollectionNode(i);
         }
      case '{':
         if (!p_218079_1_) {
            throw ERROR_INVALID_NODE.createWithContext(p_218079_0_);
         }

         CompoundNBT compoundnbt = (new JsonToNBT(p_218079_0_)).readStruct();
         return new NBTPathArgument.CompoundNode(compoundnbt);
      default:
         String s1 = readUnquotedName(p_218079_0_);
         return readObjectNode(p_218079_0_, s1);
      }
   }

   private static NBTPathArgument.INode readObjectNode(StringReader p_218083_0_, String p_218083_1_) throws CommandSyntaxException {
      if (p_218083_0_.canRead() && p_218083_0_.peek() == '{') {
         CompoundNBT compoundnbt = (new JsonToNBT(p_218083_0_)).readStruct();
         return new NBTPathArgument.JsonNode(p_218083_1_, compoundnbt);
      } else {
         return new NBTPathArgument.StringNode(p_218083_1_);
      }
   }

   private static String readUnquotedName(StringReader p_197151_0_) throws CommandSyntaxException {
      int i = p_197151_0_.getCursor();

      while(p_197151_0_.canRead() && isAllowedInUnquotedName(p_197151_0_.peek())) {
         p_197151_0_.skip();
      }

      if (p_197151_0_.getCursor() == i) {
         throw ERROR_INVALID_NODE.createWithContext(p_197151_0_);
      } else {
         return p_197151_0_.getString().substring(i, p_197151_0_.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean isAllowedInUnquotedName(char p_197146_0_) {
      return p_197146_0_ != ' ' && p_197146_0_ != '"' && p_197146_0_ != '[' && p_197146_0_ != ']' && p_197146_0_ != '.' && p_197146_0_ != '{' && p_197146_0_ != '}';
   }

   private static Predicate<INBT> createTagPredicate(CompoundNBT p_218080_0_) {
      return (p_218081_1_) -> {
         return NBTUtil.compareNbt(p_218080_0_, p_218081_1_, true);
      };
   }

   static class CollectionNode implements NBTPathArgument.INode {
      private final int index;

      public CollectionNode(int p_i51153_1_) {
         this.index = p_i51153_1_;
      }

      public void getTag(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CollectionNBT) {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_218050_1_;
            int i = collectionnbt.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               p_218050_2_.add(collectionnbt.get(j));
            }
         }

      }

      public void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         this.getTag(p_218054_1_, p_218054_3_);
      }

      public INBT createPreferredParentTag() {
         return new ListNBT();
      }

      public int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (p_218051_1_ instanceof CollectionNBT) {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_218051_1_;
            int i = collectionnbt.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               INBT inbt = collectionnbt.get(j);
               INBT inbt1 = p_218051_2_.get();
               if (!inbt1.equals(inbt) && collectionnbt.setTag(j, inbt1)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CollectionNBT) {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_218053_1_;
            int i = collectionnbt.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               collectionnbt.remove(j);
               return 1;
            }
         }

         return 0;
      }
   }

   static class CompoundNode implements NBTPathArgument.INode {
      private final Predicate<INBT> predicate;

      public CompoundNode(CompoundNBT p_i51149_1_) {
         this.predicate = NBTPathArgument.createTagPredicate(p_i51149_1_);
      }

      public void getTag(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CompoundNBT && this.predicate.test(p_218050_1_)) {
            p_218050_2_.add(p_218050_1_);
         }

      }

      public void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         this.getTag(p_218054_1_, p_218054_3_);
      }

      public INBT createPreferredParentTag() {
         return new CompoundNBT();
      }

      public int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         return 0;
      }

      public int removeTag(INBT p_218053_1_) {
         return 0;
      }
   }

   static class EmptyListNode implements NBTPathArgument.INode {
      public static final NBTPathArgument.EmptyListNode INSTANCE = new NBTPathArgument.EmptyListNode();

      private EmptyListNode() {
      }

      public void getTag(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CollectionNBT) {
            p_218050_2_.addAll((CollectionNBT)p_218050_1_);
         }

      }

      public void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         if (p_218054_1_ instanceof CollectionNBT) {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_218054_1_;
            if (collectionnbt.isEmpty()) {
               INBT inbt = p_218054_2_.get();
               if (collectionnbt.addTag(0, inbt)) {
                  p_218054_3_.add(inbt);
               }
            } else {
               p_218054_3_.addAll(collectionnbt);
            }
         }

      }

      public INBT createPreferredParentTag() {
         return new ListNBT();
      }

      public int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (!(p_218051_1_ instanceof CollectionNBT)) {
            return 0;
         } else {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_218051_1_;
            int i = collectionnbt.size();
            if (i == 0) {
               collectionnbt.addTag(0, p_218051_2_.get());
               return 1;
            } else {
               INBT inbt = p_218051_2_.get();
               int j = i - (int)collectionnbt.stream().filter(inbt::equals).count();
               if (j == 0) {
                  return 0;
               } else {
                  collectionnbt.clear();
                  if (!collectionnbt.addTag(0, inbt)) {
                     return 0;
                  } else {
                     for(int k = 1; k < i; ++k) {
                        collectionnbt.addTag(k, p_218051_2_.get());
                     }

                     return j;
                  }
               }
            }
         }
      }

      public int removeTag(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CollectionNBT) {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_218053_1_;
            int i = collectionnbt.size();
            if (i > 0) {
               collectionnbt.clear();
               return i;
            }
         }

         return 0;
      }
   }

   interface INode {
      void getTag(INBT p_218050_1_, List<INBT> p_218050_2_);

      void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_);

      INBT createPreferredParentTag();

      int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_);

      int removeTag(INBT p_218053_1_);

      default List<INBT> get(List<INBT> p_218056_1_) {
         return this.collect(p_218056_1_, this::getTag);
      }

      default List<INBT> getOrCreate(List<INBT> p_218052_1_, Supplier<INBT> p_218052_2_) {
         return this.collect(p_218052_1_, (p_218055_2_, p_218055_3_) -> {
            this.getOrCreateTag(p_218055_2_, p_218052_2_, p_218055_3_);
         });
      }

      default List<INBT> collect(List<INBT> p_218057_1_, BiConsumer<INBT, List<INBT>> p_218057_2_) {
         List<INBT> list = Lists.newArrayList();

         for(INBT inbt : p_218057_1_) {
            p_218057_2_.accept(inbt, list);
         }

         return list;
      }
   }

   static class JsonNode implements NBTPathArgument.INode {
      private final String name;
      private final CompoundNBT pattern;
      private final Predicate<INBT> predicate;

      public JsonNode(String p_i51150_1_, CompoundNBT p_i51150_2_) {
         this.name = p_i51150_1_;
         this.pattern = p_i51150_2_;
         this.predicate = NBTPathArgument.createTagPredicate(p_i51150_2_);
      }

      public void getTag(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CompoundNBT) {
            INBT inbt = ((CompoundNBT)p_218050_1_).get(this.name);
            if (this.predicate.test(inbt)) {
               p_218050_2_.add(inbt);
            }
         }

      }

      public void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         if (p_218054_1_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_218054_1_;
            INBT inbt = compoundnbt.get(this.name);
            if (inbt == null) {
               INBT compoundnbt1 = this.pattern.copy();
               compoundnbt.put(this.name, compoundnbt1);
               p_218054_3_.add(compoundnbt1);
            } else if (this.predicate.test(inbt)) {
               p_218054_3_.add(inbt);
            }
         }

      }

      public INBT createPreferredParentTag() {
         return new CompoundNBT();
      }

      public int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (p_218051_1_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_218051_1_;
            INBT inbt = compoundnbt.get(this.name);
            if (this.predicate.test(inbt)) {
               INBT inbt1 = p_218051_2_.get();
               if (!inbt1.equals(inbt)) {
                  compoundnbt.put(this.name, inbt1);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_218053_1_;
            INBT inbt = compoundnbt.get(this.name);
            if (this.predicate.test(inbt)) {
               compoundnbt.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   static class ListNode implements NBTPathArgument.INode {
      private final CompoundNBT pattern;
      private final Predicate<INBT> predicate;

      public ListNode(CompoundNBT p_i51151_1_) {
         this.pattern = p_i51151_1_;
         this.predicate = NBTPathArgument.createTagPredicate(p_i51151_1_);
      }

      public void getTag(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof ListNBT) {
            ListNBT listnbt = (ListNBT)p_218050_1_;
            listnbt.stream().filter(this.predicate).forEach(p_218050_2_::add);
         }

      }

      public void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         MutableBoolean mutableboolean = new MutableBoolean();
         if (p_218054_1_ instanceof ListNBT) {
            ListNBT listnbt = (ListNBT)p_218054_1_;
            listnbt.stream().filter(this.predicate).forEach((p_218060_2_) -> {
               p_218054_3_.add(p_218060_2_);
               mutableboolean.setTrue();
            });
            if (mutableboolean.isFalse()) {
               CompoundNBT compoundnbt = this.pattern.copy();
               listnbt.add(compoundnbt);
               p_218054_3_.add(compoundnbt);
            }
         }

      }

      public INBT createPreferredParentTag() {
         return new ListNBT();
      }

      public int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         int i = 0;
         if (p_218051_1_ instanceof ListNBT) {
            ListNBT listnbt = (ListNBT)p_218051_1_;
            int j = listnbt.size();
            if (j == 0) {
               listnbt.add(p_218051_2_.get());
               ++i;
            } else {
               for(int k = 0; k < j; ++k) {
                  INBT inbt = listnbt.get(k);
                  if (this.predicate.test(inbt)) {
                     INBT inbt1 = p_218051_2_.get();
                     if (!inbt1.equals(inbt) && listnbt.setTag(k, inbt1)) {
                        ++i;
                     }
                  }
               }
            }
         }

         return i;
      }

      public int removeTag(INBT p_218053_1_) {
         int i = 0;
         if (p_218053_1_ instanceof ListNBT) {
            ListNBT listnbt = (ListNBT)p_218053_1_;

            for(int j = listnbt.size() - 1; j >= 0; --j) {
               if (this.predicate.test(listnbt.get(j))) {
                  listnbt.remove(j);
                  ++i;
               }
            }
         }

         return i;
      }
   }

   public static class NBTPath {
      private final String original;
      private final Object2IntMap<NBTPathArgument.INode> nodeToOriginalPosition;
      private final NBTPathArgument.INode[] nodes;

      public NBTPath(String p_i51148_1_, NBTPathArgument.INode[] p_i51148_2_, Object2IntMap<NBTPathArgument.INode> p_i51148_3_) {
         this.original = p_i51148_1_;
         this.nodes = p_i51148_2_;
         this.nodeToOriginalPosition = p_i51148_3_;
      }

      public List<INBT> get(INBT p_218071_1_) throws CommandSyntaxException {
         List<INBT> list = Collections.singletonList(p_218071_1_);

         for(NBTPathArgument.INode nbtpathargument$inode : this.nodes) {
            list = nbtpathargument$inode.get(list);
            if (list.isEmpty()) {
               throw this.createNotFoundException(nbtpathargument$inode);
            }
         }

         return list;
      }

      public int countMatching(INBT p_218069_1_) {
         List<INBT> list = Collections.singletonList(p_218069_1_);

         for(NBTPathArgument.INode nbtpathargument$inode : this.nodes) {
            list = nbtpathargument$inode.get(list);
            if (list.isEmpty()) {
               return 0;
            }
         }

         return list.size();
      }

      private List<INBT> getOrCreateParents(INBT p_218072_1_) throws CommandSyntaxException {
         List<INBT> list = Collections.singletonList(p_218072_1_);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            NBTPathArgument.INode nbtpathargument$inode = this.nodes[i];
            int j = i + 1;
            list = nbtpathargument$inode.getOrCreate(list, this.nodes[j]::createPreferredParentTag);
            if (list.isEmpty()) {
               throw this.createNotFoundException(nbtpathargument$inode);
            }
         }

         return list;
      }

      public List<INBT> getOrCreate(INBT p_218073_1_, Supplier<INBT> p_218073_2_) throws CommandSyntaxException {
         List<INBT> list = this.getOrCreateParents(p_218073_1_);
         NBTPathArgument.INode nbtpathargument$inode = this.nodes[this.nodes.length - 1];
         return nbtpathargument$inode.getOrCreate(list, p_218073_2_);
      }

      private static int apply(List<INBT> p_218075_0_, Function<INBT, Integer> p_218075_1_) {
         return p_218075_0_.stream().map(p_218075_1_).reduce(0, (p_218074_0_, p_218074_1_) -> {
            return p_218074_0_ + p_218074_1_;
         });
      }

      public int set(INBT p_218076_1_, Supplier<INBT> p_218076_2_) throws CommandSyntaxException {
         List<INBT> list = this.getOrCreateParents(p_218076_1_);
         NBTPathArgument.INode nbtpathargument$inode = this.nodes[this.nodes.length - 1];
         return apply(list, (p_218077_2_) -> {
            return nbtpathargument$inode.setTag(p_218077_2_, p_218076_2_);
         });
      }

      public int remove(INBT p_218068_1_) {
         List<INBT> list = Collections.singletonList(p_218068_1_);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            list = this.nodes[i].get(list);
         }

         NBTPathArgument.INode nbtpathargument$inode = this.nodes[this.nodes.length - 1];
         return apply(list, nbtpathargument$inode::removeTag);
      }

      private CommandSyntaxException createNotFoundException(NBTPathArgument.INode p_218070_1_) {
         int i = this.nodeToOriginalPosition.getInt(p_218070_1_);
         return NBTPathArgument.ERROR_NOTHING_FOUND.create(this.original.substring(0, i));
      }

      public String toString() {
         return this.original;
      }
   }

   static class StringNode implements NBTPathArgument.INode {
      private final String name;

      public StringNode(String p_i51154_1_) {
         this.name = p_i51154_1_;
      }

      public void getTag(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CompoundNBT) {
            INBT inbt = ((CompoundNBT)p_218050_1_).get(this.name);
            if (inbt != null) {
               p_218050_2_.add(inbt);
            }
         }

      }

      public void getOrCreateTag(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         if (p_218054_1_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_218054_1_;
            INBT inbt;
            if (compoundnbt.contains(this.name)) {
               inbt = compoundnbt.get(this.name);
            } else {
               inbt = p_218054_2_.get();
               compoundnbt.put(this.name, inbt);
            }

            p_218054_3_.add(inbt);
         }

      }

      public INBT createPreferredParentTag() {
         return new CompoundNBT();
      }

      public int setTag(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (p_218051_1_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_218051_1_;
            INBT inbt = p_218051_2_.get();
            INBT inbt1 = compoundnbt.put(this.name, inbt);
            if (!inbt.equals(inbt1)) {
               return 1;
            }
         }

         return 0;
      }

      public int removeTag(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_218053_1_;
            if (compoundnbt.contains(this.name)) {
               compoundnbt.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }
}
