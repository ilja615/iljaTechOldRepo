package net.minecraft.command.impl.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.NBTTagArgument;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class DataCommand {
   private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType((p_208922_0_) -> {
      return new TranslationTextComponent("commands.data.get.invalid", p_208922_0_);
   });
   private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType((p_208919_0_) -> {
      return new TranslationTextComponent("commands.data.get.unknown", p_208919_0_);
   });
   private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.get.multiple"));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType((p_218931_0_) -> {
      return new TranslationTextComponent("commands.data.modify.expected_list", p_218931_0_);
   });
   private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType((p_218948_0_) -> {
      return new TranslationTextComponent("commands.data.modify.expected_object", p_218948_0_);
   });
   private static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType((p_218943_0_) -> {
      return new TranslationTextComponent("commands.data.modify.invalid_index", p_218943_0_);
   });
   public static final List<Function<String, DataCommand.IDataProvider>> ALL_PROVIDERS = ImmutableList.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER, StorageAccessor.PROVIDER);
   public static final List<DataCommand.IDataProvider> TARGET_PROVIDERS = ALL_PROVIDERS.stream().map((p_218925_0_) -> {
      return p_218925_0_.apply("target");
   }).collect(ImmutableList.toImmutableList());
   public static final List<DataCommand.IDataProvider> SOURCE_PROVIDERS = ALL_PROVIDERS.stream().map((p_218947_0_) -> {
      return p_218947_0_.apply("source");
   }).collect(ImmutableList.toImmutableList());

   public static void register(CommandDispatcher<CommandSource> p_198937_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("data").requires((p_198939_0_) -> {
         return p_198939_0_.hasPermission(2);
      });

      for(DataCommand.IDataProvider datacommand$idataprovider : TARGET_PROVIDERS) {
         literalargumentbuilder.then(datacommand$idataprovider.wrap(Commands.literal("merge"), (p_198943_1_) -> {
            return p_198943_1_.then(Commands.argument("nbt", NBTCompoundTagArgument.compoundTag()).executes((p_198936_1_) -> {
               return mergeData(p_198936_1_.getSource(), datacommand$idataprovider.access(p_198936_1_), NBTCompoundTagArgument.getCompoundTag(p_198936_1_, "nbt"));
            }));
         })).then(datacommand$idataprovider.wrap(Commands.literal("get"), (p_198940_1_) -> {
            return p_198940_1_.executes((p_198944_1_) -> {
               return getData(p_198944_1_.getSource(), datacommand$idataprovider.access(p_198944_1_));
            }).then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198945_1_) -> {
               return getData(p_198945_1_.getSource(), datacommand$idataprovider.access(p_198945_1_), NBTPathArgument.getPath(p_198945_1_, "path"));
            }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_198935_1_) -> {
               return getNumeric(p_198935_1_.getSource(), datacommand$idataprovider.access(p_198935_1_), NBTPathArgument.getPath(p_198935_1_, "path"), DoubleArgumentType.getDouble(p_198935_1_, "scale"));
            })));
         })).then(datacommand$idataprovider.wrap(Commands.literal("remove"), (p_198934_1_) -> {
            return p_198934_1_.then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198941_1_) -> {
               return removeData(p_198941_1_.getSource(), datacommand$idataprovider.access(p_198941_1_), NBTPathArgument.getPath(p_198941_1_, "path"));
            }));
         })).then(decorateModification((p_218924_0_, p_218924_1_) -> {
            p_218924_0_.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(p_218924_1_.create((p_218930_0_, p_218930_1_, p_218930_2_, p_218930_3_) -> {
               int i = IntegerArgumentType.getInteger(p_218930_0_, "index");
               return insertAtIndex(i, p_218930_1_, p_218930_2_, p_218930_3_);
            })))).then(Commands.literal("prepend").then(p_218924_1_.create((p_218932_0_, p_218932_1_, p_218932_2_, p_218932_3_) -> {
               return insertAtIndex(0, p_218932_1_, p_218932_2_, p_218932_3_);
            }))).then(Commands.literal("append").then(p_218924_1_.create((p_218941_0_, p_218941_1_, p_218941_2_, p_218941_3_) -> {
               return insertAtIndex(-1, p_218941_1_, p_218941_2_, p_218941_3_);
            }))).then(Commands.literal("set").then(p_218924_1_.create((p_218954_0_, p_218954_1_, p_218954_2_, p_218954_3_) -> {
               return p_218954_2_.set(p_218954_1_, Iterables.getLast(p_218954_3_)::copy);
            }))).then(Commands.literal("merge").then(p_218924_1_.create((p_218927_0_, p_218927_1_, p_218927_2_, p_218927_3_) -> {
               Collection<INBT> collection = p_218927_2_.getOrCreate(p_218927_1_, CompoundNBT::new);
               int i = 0;

               for(INBT inbt : collection) {
                  if (!(inbt instanceof CompoundNBT)) {
                     throw ERROR_EXPECTED_OBJECT.create(inbt);
                  }

                  CompoundNBT compoundnbt = (CompoundNBT)inbt;
                  CompoundNBT compoundnbt1 = compoundnbt.copy();

                  for(INBT inbt1 : p_218927_3_) {
                     if (!(inbt1 instanceof CompoundNBT)) {
                        throw ERROR_EXPECTED_OBJECT.create(inbt1);
                     }

                     compoundnbt.merge((CompoundNBT)inbt1);
                  }

                  i += compoundnbt1.equals(compoundnbt) ? 0 : 1;
               }

               return i;
            })));
         }));
      }

      p_198937_0_.register(literalargumentbuilder);
   }

   private static int insertAtIndex(int p_218944_0_, CompoundNBT p_218944_1_, NBTPathArgument.NBTPath p_218944_2_, List<INBT> p_218944_3_) throws CommandSyntaxException {
      Collection<INBT> collection = p_218944_2_.getOrCreate(p_218944_1_, ListNBT::new);
      int i = 0;

      for(INBT inbt : collection) {
         if (!(inbt instanceof CollectionNBT)) {
            throw ERROR_EXPECTED_LIST.create(inbt);
         }

         boolean flag = false;
         CollectionNBT<?> collectionnbt = (CollectionNBT)inbt;
         int j = p_218944_0_ < 0 ? collectionnbt.size() + p_218944_0_ + 1 : p_218944_0_;

         for(INBT inbt1 : p_218944_3_) {
            try {
               if (collectionnbt.addTag(j, inbt1.copy())) {
                  ++j;
                  flag = true;
               }
            } catch (IndexOutOfBoundsException indexoutofboundsexception) {
               throw ERROR_INVALID_INDEX.create(j);
            }
         }

         i += flag ? 1 : 0;
      }

      return i;
   }

   private static ArgumentBuilder<CommandSource, ?> decorateModification(BiConsumer<ArgumentBuilder<CommandSource, ?>, DataCommand.IModificationSourceArgumentBuilder> p_218935_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("modify");

      for(DataCommand.IDataProvider datacommand$idataprovider : TARGET_PROVIDERS) {
         datacommand$idataprovider.wrap(literalargumentbuilder, (p_218940_2_) -> {
            ArgumentBuilder<CommandSource, ?> argumentbuilder = Commands.argument("targetPath", NBTPathArgument.nbtPath());

            for(DataCommand.IDataProvider datacommand$idataprovider1 : SOURCE_PROVIDERS) {
               p_218935_0_.accept(argumentbuilder, (p_218934_2_) -> {
                  return datacommand$idataprovider1.wrap(Commands.literal("from"), (p_218929_3_) -> {
                     return p_218929_3_.executes((p_218937_3_) -> {
                        List<INBT> list = Collections.singletonList(datacommand$idataprovider1.access(p_218937_3_).getData());
                        return manipulateData(p_218937_3_, datacommand$idataprovider, p_218934_2_, list);
                     }).then(Commands.argument("sourcePath", NBTPathArgument.nbtPath()).executes((p_218936_3_) -> {
                        IDataAccessor idataaccessor = datacommand$idataprovider1.access(p_218936_3_);
                        NBTPathArgument.NBTPath nbtpathargument$nbtpath = NBTPathArgument.getPath(p_218936_3_, "sourcePath");
                        List<INBT> list = nbtpathargument$nbtpath.get(idataaccessor.getData());
                        return manipulateData(p_218936_3_, datacommand$idataprovider, p_218934_2_, list);
                     }));
                  });
               });
            }

            p_218935_0_.accept(argumentbuilder, (p_218949_1_) -> {
               return Commands.literal("value").then(Commands.argument("value", NBTTagArgument.nbtTag()).executes((p_218952_2_) -> {
                  List<INBT> list = Collections.singletonList(NBTTagArgument.getNbtTag(p_218952_2_, "value"));
                  return manipulateData(p_218952_2_, datacommand$idataprovider, p_218949_1_, list);
               }));
            });
            return p_218940_2_.then(argumentbuilder);
         });
      }

      return literalargumentbuilder;
   }

   private static int manipulateData(CommandContext<CommandSource> p_218933_0_, DataCommand.IDataProvider p_218933_1_, DataCommand.IModificationType p_218933_2_, List<INBT> p_218933_3_) throws CommandSyntaxException {
      IDataAccessor idataaccessor = p_218933_1_.access(p_218933_0_);
      NBTPathArgument.NBTPath nbtpathargument$nbtpath = NBTPathArgument.getPath(p_218933_0_, "targetPath");
      CompoundNBT compoundnbt = idataaccessor.getData();
      int i = p_218933_2_.modify(p_218933_0_, compoundnbt, nbtpathargument$nbtpath, p_218933_3_);
      if (i == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         idataaccessor.setData(compoundnbt);
         p_218933_0_.getSource().sendSuccess(idataaccessor.getModifiedSuccess(), true);
         return i;
      }
   }

   private static int removeData(CommandSource p_198942_0_, IDataAccessor p_198942_1_, NBTPathArgument.NBTPath p_198942_2_) throws CommandSyntaxException {
      CompoundNBT compoundnbt = p_198942_1_.getData();
      int i = p_198942_2_.remove(compoundnbt);
      if (i == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         p_198942_1_.setData(compoundnbt);
         p_198942_0_.sendSuccess(p_198942_1_.getModifiedSuccess(), true);
         return i;
      }
   }

   private static INBT getSingleTag(NBTPathArgument.NBTPath p_218928_0_, IDataAccessor p_218928_1_) throws CommandSyntaxException {
      Collection<INBT> collection = p_218928_0_.get(p_218928_1_.getData());
      Iterator<INBT> iterator = collection.iterator();
      INBT inbt = iterator.next();
      if (iterator.hasNext()) {
         throw ERROR_MULTIPLE_TAGS.create();
      } else {
         return inbt;
      }
   }

   private static int getData(CommandSource p_201228_0_, IDataAccessor p_201228_1_, NBTPathArgument.NBTPath p_201228_2_) throws CommandSyntaxException {
      INBT inbt = getSingleTag(p_201228_2_, p_201228_1_);
      int i;
      if (inbt instanceof NumberNBT) {
         i = MathHelper.floor(((NumberNBT)inbt).getAsDouble());
      } else if (inbt instanceof CollectionNBT) {
         i = ((CollectionNBT)inbt).size();
      } else if (inbt instanceof CompoundNBT) {
         i = ((CompoundNBT)inbt).size();
      } else {
         if (!(inbt instanceof StringNBT)) {
            throw ERROR_GET_NON_EXISTENT.create(p_201228_2_.toString());
         }

         i = inbt.getAsString().length();
      }

      p_201228_0_.sendSuccess(p_201228_1_.getPrintSuccess(inbt), false);
      return i;
   }

   private static int getNumeric(CommandSource p_198938_0_, IDataAccessor p_198938_1_, NBTPathArgument.NBTPath p_198938_2_, double p_198938_3_) throws CommandSyntaxException {
      INBT inbt = getSingleTag(p_198938_2_, p_198938_1_);
      if (!(inbt instanceof NumberNBT)) {
         throw ERROR_GET_NOT_NUMBER.create(p_198938_2_.toString());
      } else {
         int i = MathHelper.floor(((NumberNBT)inbt).getAsDouble() * p_198938_3_);
         p_198938_0_.sendSuccess(p_198938_1_.getPrintSuccess(p_198938_2_, p_198938_3_, i), false);
         return i;
      }
   }

   private static int getData(CommandSource p_198947_0_, IDataAccessor p_198947_1_) throws CommandSyntaxException {
      p_198947_0_.sendSuccess(p_198947_1_.getPrintSuccess(p_198947_1_.getData()), false);
      return 1;
   }

   private static int mergeData(CommandSource p_198946_0_, IDataAccessor p_198946_1_, CompoundNBT p_198946_2_) throws CommandSyntaxException {
      CompoundNBT compoundnbt = p_198946_1_.getData();
      CompoundNBT compoundnbt1 = compoundnbt.copy().merge(p_198946_2_);
      if (compoundnbt.equals(compoundnbt1)) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         p_198946_1_.setData(compoundnbt1);
         p_198946_0_.sendSuccess(p_198946_1_.getModifiedSuccess(), true);
         return 1;
      }
   }

   public interface IDataProvider {
      IDataAccessor access(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException;

      ArgumentBuilder<CommandSource, ?> wrap(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_);
   }

   interface IModificationSourceArgumentBuilder {
      ArgumentBuilder<CommandSource, ?> create(DataCommand.IModificationType p_create_1_);
   }

   interface IModificationType {
      int modify(CommandContext<CommandSource> p_modify_1_, CompoundNBT p_modify_2_, NBTPathArgument.NBTPath p_modify_3_, List<INBT> p_modify_4_) throws CommandSyntaxException;
   }
}
