package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<ISuggestionProvider>> PROVIDERS_BY_NAME = Maps.newHashMap();
   private static final ResourceLocation DEFAULT_NAME = new ResourceLocation("ask_server");
   public static final SuggestionProvider<ISuggestionProvider> ASK_SERVER = register(DEFAULT_NAME, (p_197500_0_, p_197500_1_) -> {
      return p_197500_0_.getSource().customSuggestion(p_197500_0_, p_197500_1_);
   });
   public static final SuggestionProvider<CommandSource> ALL_RECIPES = register(new ResourceLocation("all_recipes"), (p_197501_0_, p_197501_1_) -> {
      return ISuggestionProvider.suggestResource(p_197501_0_.getSource().getRecipeNames(), p_197501_1_);
   });
   public static final SuggestionProvider<CommandSource> AVAILABLE_SOUNDS = register(new ResourceLocation("available_sounds"), (p_197495_0_, p_197495_1_) -> {
      return ISuggestionProvider.suggestResource(p_197495_0_.getSource().getAvailableSoundEvents(), p_197495_1_);
   });
   public static final SuggestionProvider<CommandSource> AVAILABLE_BIOMES = register(new ResourceLocation("available_biomes"), (p_239577_0_, p_239577_1_) -> {
      return ISuggestionProvider.suggestResource(p_239577_0_.getSource().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).keySet(), p_239577_1_);
   });
   public static final SuggestionProvider<CommandSource> SUMMONABLE_ENTITIES = register(new ResourceLocation("summonable_entities"), (p_201210_0_, p_201210_1_) -> {
      return ISuggestionProvider.suggestResource(Registry.ENTITY_TYPE.stream().filter(EntityType::canSummon), p_201210_1_, EntityType::getKey, (p_201209_0_) -> {
         return new TranslationTextComponent(Util.makeDescriptionId("entity", EntityType.getKey(p_201209_0_)));
      });
   });

   public static <S extends ISuggestionProvider> SuggestionProvider<S> register(ResourceLocation p_197494_0_, SuggestionProvider<ISuggestionProvider> p_197494_1_) {
      if (PROVIDERS_BY_NAME.containsKey(p_197494_0_)) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + p_197494_0_);
      } else {
         PROVIDERS_BY_NAME.put(p_197494_0_, p_197494_1_);
         return (SuggestionProvider<S>)new SuggestionProviders.Wrapper(p_197494_0_, p_197494_1_);
      }
   }

   public static SuggestionProvider<ISuggestionProvider> getProvider(ResourceLocation p_197498_0_) {
      return PROVIDERS_BY_NAME.getOrDefault(p_197498_0_, ASK_SERVER);
   }

   public static ResourceLocation getName(SuggestionProvider<ISuggestionProvider> p_197497_0_) {
      return p_197497_0_ instanceof SuggestionProviders.Wrapper ? ((SuggestionProviders.Wrapper)p_197497_0_).name : DEFAULT_NAME;
   }

   public static SuggestionProvider<ISuggestionProvider> safelySwap(SuggestionProvider<ISuggestionProvider> p_197496_0_) {
      return p_197496_0_ instanceof SuggestionProviders.Wrapper ? p_197496_0_ : ASK_SERVER;
   }

   public static class Wrapper implements SuggestionProvider<ISuggestionProvider> {
      private final SuggestionProvider<ISuggestionProvider> delegate;
      private final ResourceLocation name;

      public Wrapper(ResourceLocation p_i47984_1_, SuggestionProvider<ISuggestionProvider> p_i47984_2_) {
         this.delegate = p_i47984_2_;
         this.name = p_i47984_1_;
      }

      public CompletableFuture<Suggestions> getSuggestions(CommandContext<ISuggestionProvider> p_getSuggestions_1_, SuggestionsBuilder p_getSuggestions_2_) throws CommandSyntaxException {
         return this.delegate.getSuggestions(p_getSuggestions_1_, p_getSuggestions_2_);
      }
   }
}
