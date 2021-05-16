package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class EntitySummonArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType((p_211367_0_) -> {
      return new TranslationTextComponent("entity.notFound", p_211367_0_);
   });

   public static EntitySummonArgument id() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation getSummonableEntity(CommandContext<CommandSource> p_211368_0_, String p_211368_1_) throws CommandSyntaxException {
      return verifyCanSummon(p_211368_0_.getArgument(p_211368_1_, ResourceLocation.class));
   }

   private static ResourceLocation verifyCanSummon(ResourceLocation p_211365_0_) throws CommandSyntaxException {
      Registry.ENTITY_TYPE.getOptional(p_211365_0_).filter(EntityType::canSummon).orElseThrow(() -> {
         return ERROR_UNKNOWN_ENTITY.create(p_211365_0_);
      });
      return p_211365_0_;
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return verifyCanSummon(ResourceLocation.read(p_parse_1_));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
