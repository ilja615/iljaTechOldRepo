package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.serializers.BrigadierSerializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.test.TestArgArgument;
import net.minecraft.test.TestTypeArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> BY_CLASS = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> BY_NAME = Maps.newHashMap();

   public static <T extends ArgumentType<?>> void register(String p_218136_0_, Class<T> p_218136_1_, IArgumentSerializer<T> p_218136_2_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_218136_0_);
      if (BY_CLASS.containsKey(p_218136_1_)) {
         throw new IllegalArgumentException("Class " + p_218136_1_.getName() + " already has a serializer!");
      } else if (BY_NAME.containsKey(resourcelocation)) {
         throw new IllegalArgumentException("'" + resourcelocation + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry<T> entry = new ArgumentTypes.Entry<>(p_218136_1_, p_218136_2_, resourcelocation);
         BY_CLASS.put(p_218136_1_, entry);
         BY_NAME.put(resourcelocation, entry);
      }
   }

   public static void bootStrap() {
      BrigadierSerializers.bootstrap();
      register("entity", EntityArgument.class, new EntityArgument.Serializer());
      register("game_profile", GameProfileArgument.class, new ArgumentSerializer<>(GameProfileArgument::gameProfile));
      register("block_pos", BlockPosArgument.class, new ArgumentSerializer<>(BlockPosArgument::blockPos));
      register("column_pos", ColumnPosArgument.class, new ArgumentSerializer<>(ColumnPosArgument::columnPos));
      register("vec3", Vec3Argument.class, new ArgumentSerializer<>(Vec3Argument::vec3));
      register("vec2", Vec2Argument.class, new ArgumentSerializer<>(Vec2Argument::vec2));
      register("block_state", BlockStateArgument.class, new ArgumentSerializer<>(BlockStateArgument::block));
      register("block_predicate", BlockPredicateArgument.class, new ArgumentSerializer<>(BlockPredicateArgument::blockPredicate));
      register("item_stack", ItemArgument.class, new ArgumentSerializer<>(ItemArgument::item));
      register("item_predicate", ItemPredicateArgument.class, new ArgumentSerializer<>(ItemPredicateArgument::itemPredicate));
      register("color", ColorArgument.class, new ArgumentSerializer<>(ColorArgument::color));
      register("component", ComponentArgument.class, new ArgumentSerializer<>(ComponentArgument::textComponent));
      register("message", MessageArgument.class, new ArgumentSerializer<>(MessageArgument::message));
      register("nbt_compound_tag", NBTCompoundTagArgument.class, new ArgumentSerializer<>(NBTCompoundTagArgument::compoundTag));
      register("nbt_tag", NBTTagArgument.class, new ArgumentSerializer<>(NBTTagArgument::nbtTag));
      register("nbt_path", NBTPathArgument.class, new ArgumentSerializer<>(NBTPathArgument::nbtPath));
      register("objective", ObjectiveArgument.class, new ArgumentSerializer<>(ObjectiveArgument::objective));
      register("objective_criteria", ObjectiveCriteriaArgument.class, new ArgumentSerializer<>(ObjectiveCriteriaArgument::criteria));
      register("operation", OperationArgument.class, new ArgumentSerializer<>(OperationArgument::operation));
      register("particle", ParticleArgument.class, new ArgumentSerializer<>(ParticleArgument::particle));
      register("angle", AngleArgument.class, new ArgumentSerializer<>(AngleArgument::angle));
      register("rotation", RotationArgument.class, new ArgumentSerializer<>(RotationArgument::rotation));
      register("scoreboard_slot", ScoreboardSlotArgument.class, new ArgumentSerializer<>(ScoreboardSlotArgument::displaySlot));
      register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      register("swizzle", SwizzleArgument.class, new ArgumentSerializer<>(SwizzleArgument::swizzle));
      register("team", TeamArgument.class, new ArgumentSerializer<>(TeamArgument::team));
      register("item_slot", SlotArgument.class, new ArgumentSerializer<>(SlotArgument::slot));
      register("resource_location", ResourceLocationArgument.class, new ArgumentSerializer<>(ResourceLocationArgument::id));
      register("mob_effect", PotionArgument.class, new ArgumentSerializer<>(PotionArgument::effect));
      register("function", FunctionArgument.class, new ArgumentSerializer<>(FunctionArgument::functions));
      register("entity_anchor", EntityAnchorArgument.class, new ArgumentSerializer<>(EntityAnchorArgument::anchor));
      register("int_range", IRangeArgument.IntRange.class, new ArgumentSerializer<>(IRangeArgument::intRange));
      register("float_range", IRangeArgument.FloatRange.class, new ArgumentSerializer<>(IRangeArgument::floatRange));
      register("item_enchantment", EnchantmentArgument.class, new ArgumentSerializer<>(EnchantmentArgument::enchantment));
      register("entity_summon", EntitySummonArgument.class, new ArgumentSerializer<>(EntitySummonArgument::id));
      register("dimension", DimensionArgument.class, new ArgumentSerializer<>(DimensionArgument::dimension));
      register("time", TimeArgument.class, new ArgumentSerializer<>(TimeArgument::time));
      register("uuid", UUIDArgument.class, new ArgumentSerializer<>(UUIDArgument::uuid));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         register("test_argument", TestArgArgument.class, new ArgumentSerializer<>(TestArgArgument::testFunctionArgument));
         register("test_class", TestTypeArgument.class, new ArgumentSerializer<>(TestTypeArgument::testClassName));
      }

   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ResourceLocation p_197482_0_) {
      return BY_NAME.get(p_197482_0_);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ArgumentType<?> p_201040_0_) {
      return BY_CLASS.get(p_201040_0_.getClass());
   }

   public static <T extends ArgumentType<?>> void serialize(PacketBuffer p_197484_0_, T p_197484_1_) {
      ArgumentTypes.Entry<T> entry = (Entry<T>) get(p_197484_1_);
      if (entry == null) {
         LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", p_197484_1_, p_197484_1_.getClass());
         p_197484_0_.writeResourceLocation(new ResourceLocation(""));
      } else {
         p_197484_0_.writeResourceLocation(entry.name);
         entry.serializer.serializeToNetwork(p_197484_1_, p_197484_0_);
      }
   }

   @Nullable
   public static ArgumentType<?> deserialize(PacketBuffer p_197486_0_) {
      ResourceLocation resourcelocation = p_197486_0_.readResourceLocation();
      ArgumentTypes.Entry<?> entry = get(resourcelocation);
      if (entry == null) {
         LOGGER.error("Could not deserialize {}", (Object)resourcelocation);
         return null;
      } else {
         return entry.serializer.deserializeFromNetwork(p_197486_0_);
      }
   }

   private static <T extends ArgumentType<?>> void serializeToJson(JsonObject p_201042_0_, T p_201042_1_) {
      ArgumentTypes.Entry<T> entry = (Entry<T>) get(p_201042_1_);
      if (entry == null) {
         LOGGER.error("Could not serialize argument {} ({})!", p_201042_1_, p_201042_1_.getClass());
         p_201042_0_.addProperty("type", "unknown");
      } else {
         p_201042_0_.addProperty("type", "argument");
         p_201042_0_.addProperty("parser", entry.name.toString());
         JsonObject jsonobject = new JsonObject();
         entry.serializer.serializeToJson(p_201042_1_, jsonobject);
         if (jsonobject.size() > 0) {
            p_201042_0_.add("properties", jsonobject);
         }
      }

   }

   public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> p_200388_0_, CommandNode<S> p_200388_1_) {
      JsonObject jsonobject = new JsonObject();
      if (p_200388_1_ instanceof RootCommandNode) {
         jsonobject.addProperty("type", "root");
      } else if (p_200388_1_ instanceof LiteralCommandNode) {
         jsonobject.addProperty("type", "literal");
      } else if (p_200388_1_ instanceof ArgumentCommandNode) {
         serializeToJson(jsonobject, ((ArgumentCommandNode)p_200388_1_).getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", p_200388_1_, p_200388_1_.getClass());
         jsonobject.addProperty("type", "unknown");
      }

      JsonObject jsonobject1 = new JsonObject();

      for(CommandNode<S> commandnode : p_200388_1_.getChildren()) {
         jsonobject1.add(commandnode.getName(), serializeNodeToJson(p_200388_0_, commandnode));
      }

      if (jsonobject1.size() > 0) {
         jsonobject.add("children", jsonobject1);
      }

      if (p_200388_1_.getCommand() != null) {
         jsonobject.addProperty("executable", true);
      }

      if (p_200388_1_.getRedirect() != null) {
         Collection<String> collection = p_200388_0_.getPath(p_200388_1_.getRedirect());
         if (!collection.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(String s : collection) {
               jsonarray.add(s);
            }

            jsonobject.add("redirect", jsonarray);
         }
      }

      return jsonobject;
   }

   public static boolean isTypeRegistered(ArgumentType<?> p_243510_0_) {
      return get(p_243510_0_) != null;
   }

   public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> p_243511_0_) {
      Set<CommandNode<T>> set = Sets.newIdentityHashSet();
      Set<ArgumentType<?>> set1 = Sets.newHashSet();
      findUsedArgumentTypes(p_243511_0_, set1, set);
      return set1;
   }

   private static <T> void findUsedArgumentTypes(CommandNode<T> p_243512_0_, Set<ArgumentType<?>> p_243512_1_, Set<CommandNode<T>> p_243512_2_) {
      if (p_243512_2_.add(p_243512_0_)) {
         if (p_243512_0_ instanceof ArgumentCommandNode) {
            p_243512_1_.add(((ArgumentCommandNode)p_243512_0_).getType());
         }

         p_243512_0_.getChildren().forEach((p_243513_2_) -> {
            findUsedArgumentTypes(p_243513_2_, p_243512_1_, p_243512_2_);
         });
         CommandNode<T> commandnode = p_243512_0_.getRedirect();
         if (commandnode != null) {
            findUsedArgumentTypes(commandnode, p_243512_1_, p_243512_2_);
         }

      }
   }

   static class Entry<T extends ArgumentType<?>> {
      public final Class<T> clazz;
      public final IArgumentSerializer<T> serializer;
      public final ResourceLocation name;

      private Entry(Class<T> p_i48088_1_, IArgumentSerializer<T> p_i48088_2_, ResourceLocation p_i48088_3_) {
         this.clazz = p_i48088_1_;
         this.serializer = p_i48088_2_;
         this.name = p_i48088_3_;
      }
   }
   @javax.annotation.Nullable public static ResourceLocation getId(ArgumentType<?> type) {
      Entry<?> entry = get(type);
      return entry == null ? null : entry.name;
   }
}
