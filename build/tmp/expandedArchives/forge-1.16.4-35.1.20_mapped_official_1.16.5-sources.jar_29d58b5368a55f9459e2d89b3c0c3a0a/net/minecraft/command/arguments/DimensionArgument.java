package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DimensionArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Stream.of(World.OVERWORLD, World.NETHER).map((p_212593_0_) -> {
      return p_212593_0_.location().toString();
   }).collect(Collectors.toList());
   private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((p_212594_0_) -> {
      return new TranslationTextComponent("argument.dimension.invalid", p_212594_0_);
   });

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return ResourceLocation.read(p_parse_1_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return p_listSuggestions_1_.getSource() instanceof ISuggestionProvider ? ISuggestionProvider.suggestResource(((ISuggestionProvider)p_listSuggestions_1_.getSource()).levels().stream().map(RegistryKey::location), p_listSuggestions_2_) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static DimensionArgument dimension() {
      return new DimensionArgument();
   }

   public static ServerWorld getDimension(CommandContext<CommandSource> p_212592_0_, String p_212592_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_212592_0_.getArgument(p_212592_1_, ResourceLocation.class);
      RegistryKey<World> registrykey = RegistryKey.create(Registry.DIMENSION_REGISTRY, resourcelocation);
      ServerWorld serverworld = p_212592_0_.getSource().getServer().getLevel(registrykey);
      if (serverworld == null) {
         throw ERROR_INVALID_VALUE.create(resourcelocation);
      } else {
         return serverworld;
      }
   }
}
