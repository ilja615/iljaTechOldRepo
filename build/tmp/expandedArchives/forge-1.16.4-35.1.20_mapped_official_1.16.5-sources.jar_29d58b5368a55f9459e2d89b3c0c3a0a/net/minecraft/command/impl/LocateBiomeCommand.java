package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand {
   public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((p_241052_0_) -> {
      return new TranslationTextComponent("commands.locatebiome.invalid", p_241052_0_);
   });
   private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((p_241050_0_) -> {
      return new TranslationTextComponent("commands.locatebiome.notFound", p_241050_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_241046_0_) {
      p_241046_0_.register(Commands.literal("locatebiome").requires((p_241048_0_) -> {
         return p_241048_0_.hasPermission(2);
      }).then(Commands.argument("biome", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_BIOMES).executes((p_241047_0_) -> {
         return locateBiome(p_241047_0_.getSource(), p_241047_0_.getArgument("biome", ResourceLocation.class));
      })));
   }

   private static int locateBiome(CommandSource p_241049_0_, ResourceLocation p_241049_1_) throws CommandSyntaxException {
      Biome biome = p_241049_0_.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(p_241049_1_).orElseThrow(() -> {
         return ERROR_INVALID_BIOME.create(p_241049_1_);
      });
      BlockPos blockpos = new BlockPos(p_241049_0_.getPosition());
      BlockPos blockpos1 = p_241049_0_.getLevel().findNearestBiome(biome, blockpos, 6400, 8);
      String s = p_241049_1_.toString();
      if (blockpos1 == null) {
         throw ERROR_BIOME_NOT_FOUND.create(s);
      } else {
         return LocateCommand.showLocateResult(p_241049_0_, s, blockpos, blockpos1, "commands.locatebiome.success");
      }
   }
}
