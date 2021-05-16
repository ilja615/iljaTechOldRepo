package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ParticleArgument implements ArgumentType<IParticleData> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((p_208673_0_) -> {
      return new TranslationTextComponent("particle.notFound", p_208673_0_);
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static IParticleData getParticle(CommandContext<CommandSource> p_197187_0_, String p_197187_1_) {
      return p_197187_0_.getArgument(p_197187_1_, IParticleData.class);
   }

   public IParticleData parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return readParticle(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static IParticleData readParticle(StringReader p_197189_0_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_197189_0_);
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.getOptional(resourcelocation).orElseThrow(() -> {
         return ERROR_UNKNOWN_PARTICLE.create(resourcelocation);
      });
      return readParticle(p_197189_0_, particletype);
   }

   private static <T extends IParticleData> T readParticle(StringReader p_199816_0_, ParticleType<T> p_199816_1_) throws CommandSyntaxException {
      return p_199816_1_.getDeserializer().fromCommand(p_199816_1_, p_199816_0_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestResource(Registry.PARTICLE_TYPE.keySet(), p_listSuggestions_2_);
   }
}
