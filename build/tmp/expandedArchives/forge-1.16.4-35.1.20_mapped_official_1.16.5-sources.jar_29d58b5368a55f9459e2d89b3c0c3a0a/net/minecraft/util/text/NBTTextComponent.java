package net.minecraft.util.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NBTTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final boolean interpreting;
   protected final String nbtPathPattern;
   @Nullable
   protected final NBTPathArgument.NBTPath compiledNbtPath;

   @Nullable
   private static NBTPathArgument.NBTPath compileNbtPath(String p_218672_0_) {
      try {
         return (new NBTPathArgument()).parse(new StringReader(p_218672_0_));
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public NBTTextComponent(String p_i50781_1_, boolean p_i50781_2_) {
      this(p_i50781_1_, compileNbtPath(p_i50781_1_), p_i50781_2_);
   }

   protected NBTTextComponent(String p_i50782_1_, @Nullable NBTPathArgument.NBTPath p_i50782_2_, boolean p_i50782_3_) {
      this.nbtPathPattern = p_i50782_1_;
      this.compiledNbtPath = p_i50782_2_;
      this.interpreting = p_i50782_3_;
   }

   protected abstract Stream<CompoundNBT> getData(CommandSource p_218673_1_) throws CommandSyntaxException;

   public String getNbtPath() {
      return this.nbtPathPattern;
   }

   public boolean isInterpreting() {
      return this.interpreting;
   }

   public IFormattableTextComponent resolve(@Nullable CommandSource p_230535_1_, @Nullable net.minecraft.entity.Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException {
      if (p_230535_1_ != null && this.compiledNbtPath != null) {
         Stream<String> stream = this.getData(p_230535_1_).flatMap((p_218675_1_) -> {
            try {
               return this.compiledNbtPath.get(p_218675_1_).stream();
            } catch (CommandSyntaxException commandsyntaxexception) {
               return Stream.empty();
            }
         }).map(INBT::getAsString);
         return (IFormattableTextComponent)(this.interpreting ? stream.flatMap((p_223137_3_) -> {
            try {
               IFormattableTextComponent iformattabletextcomponent = ITextComponent.Serializer.fromJson(p_223137_3_);
               return Stream.of(TextComponentUtils.updateForEntity(p_230535_1_, iformattabletextcomponent, p_230535_2_, p_230535_3_));
            } catch (Exception exception) {
               LOGGER.warn("Failed to parse component: " + p_223137_3_, (Throwable)exception);
               return Stream.of();
            }
         }).reduce((p_240704_0_, p_240704_1_) -> {
            return p_240704_0_.append(", ").append(p_240704_1_);
         }).orElse(new StringTextComponent("")) : new StringTextComponent(Joiner.on(", ").join(stream.iterator())));
      } else {
         return new StringTextComponent("");
      }
   }

   public static class Block extends NBTTextComponent {
      private final String posPattern;
      @Nullable
      private final ILocationArgument compiledPos;

      public Block(String p_i51294_1_, boolean p_i51294_2_, String p_i51294_3_) {
         super(p_i51294_1_, p_i51294_2_);
         this.posPattern = p_i51294_3_;
         this.compiledPos = this.compilePos(this.posPattern);
      }

      @Nullable
      private ILocationArgument compilePos(String p_218682_1_) {
         try {
            return BlockPosArgument.blockPos().parse(new StringReader(p_218682_1_));
         } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
         }
      }

      private Block(String p_i51295_1_, @Nullable NBTPathArgument.NBTPath p_i51295_2_, boolean p_i51295_3_, String p_i51295_4_, @Nullable ILocationArgument p_i51295_5_) {
         super(p_i51295_1_, p_i51295_2_, p_i51295_3_);
         this.posPattern = p_i51295_4_;
         this.compiledPos = p_i51295_5_;
      }

      @Nullable
      public String getPos() {
         return this.posPattern;
      }

      public NBTTextComponent.Block plainCopy() {
         return new NBTTextComponent.Block(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.posPattern, this.compiledPos);
      }

      protected Stream<CompoundNBT> getData(CommandSource p_218673_1_) {
         if (this.compiledPos != null) {
            ServerWorld serverworld = p_218673_1_.getLevel();
            BlockPos blockpos = this.compiledPos.getBlockPos(p_218673_1_);
            if (serverworld.isLoaded(blockpos)) {
               TileEntity tileentity = serverworld.getBlockEntity(blockpos);
               if (tileentity != null) {
                  return Stream.of(tileentity.save(new CompoundNBT()));
               }
            }
         }

         return Stream.empty();
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof NBTTextComponent.Block)) {
            return false;
         } else {
            NBTTextComponent.Block nbttextcomponent$block = (NBTTextComponent.Block)p_equals_1_;
            return Objects.equals(this.posPattern, nbttextcomponent$block.posPattern) && Objects.equals(this.nbtPathPattern, nbttextcomponent$block.nbtPathPattern) && super.equals(p_equals_1_);
         }
      }

      public String toString() {
         return "BlockPosArgument{pos='" + this.posPattern + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }

   public static class Entity extends NBTTextComponent {
      private final String selectorPattern;
      @Nullable
      private final EntitySelector compiledSelector;

      public Entity(String p_i51292_1_, boolean p_i51292_2_, String p_i51292_3_) {
         super(p_i51292_1_, p_i51292_2_);
         this.selectorPattern = p_i51292_3_;
         this.compiledSelector = compileSelector(p_i51292_3_);
      }

      @Nullable
      private static EntitySelector compileSelector(String p_218686_0_) {
         try {
            EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_218686_0_));
            return entityselectorparser.parse();
         } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
         }
      }

      private Entity(String p_i51293_1_, @Nullable NBTPathArgument.NBTPath p_i51293_2_, boolean p_i51293_3_, String p_i51293_4_, @Nullable EntitySelector p_i51293_5_) {
         super(p_i51293_1_, p_i51293_2_, p_i51293_3_);
         this.selectorPattern = p_i51293_4_;
         this.compiledSelector = p_i51293_5_;
      }

      public String getSelector() {
         return this.selectorPattern;
      }

      public NBTTextComponent.Entity plainCopy() {
         return new NBTTextComponent.Entity(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.selectorPattern, this.compiledSelector);
      }

      protected Stream<CompoundNBT> getData(CommandSource p_218673_1_) throws CommandSyntaxException {
         if (this.compiledSelector != null) {
            List<? extends net.minecraft.entity.Entity> list = this.compiledSelector.findEntities(p_218673_1_);
            return list.stream().map(NBTPredicate::getEntityTagToCompare);
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof NBTTextComponent.Entity)) {
            return false;
         } else {
            NBTTextComponent.Entity nbttextcomponent$entity = (NBTTextComponent.Entity)p_equals_1_;
            return Objects.equals(this.selectorPattern, nbttextcomponent$entity.selectorPattern) && Objects.equals(this.nbtPathPattern, nbttextcomponent$entity.nbtPathPattern) && super.equals(p_equals_1_);
         }
      }

      public String toString() {
         return "EntityNbtComponent{selector='" + this.selectorPattern + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }

   public static class Storage extends NBTTextComponent {
      private final ResourceLocation id;

      public Storage(String p_i226087_1_, boolean p_i226087_2_, ResourceLocation p_i226087_3_) {
         super(p_i226087_1_, p_i226087_2_);
         this.id = p_i226087_3_;
      }

      public Storage(String p_i226086_1_, @Nullable NBTPathArgument.NBTPath p_i226086_2_, boolean p_i226086_3_, ResourceLocation p_i226086_4_) {
         super(p_i226086_1_, p_i226086_2_, p_i226086_3_);
         this.id = p_i226086_4_;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public NBTTextComponent.Storage plainCopy() {
         return new NBTTextComponent.Storage(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.id);
      }

      protected Stream<CompoundNBT> getData(CommandSource p_218673_1_) {
         CompoundNBT compoundnbt = p_218673_1_.getServer().getCommandStorage().get(this.id);
         return Stream.of(compoundnbt);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof NBTTextComponent.Storage)) {
            return false;
         } else {
            NBTTextComponent.Storage nbttextcomponent$storage = (NBTTextComponent.Storage)p_equals_1_;
            return Objects.equals(this.id, nbttextcomponent$storage.id) && Objects.equals(this.nbtPathPattern, nbttextcomponent$storage.nbtPathPattern) && super.equals(p_equals_1_);
         }
      }

      public String toString() {
         return "StorageNbtComponent{id='" + this.id + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }
   }
}
