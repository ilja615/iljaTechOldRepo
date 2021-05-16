package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Map.Entry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.gen.feature.structure.Structure;

public class LocateCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198528_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("locate").requires((p_198533_0_) -> {
         return p_198533_0_.hasPermission(2);
      });

      if (false)
      for(Entry<String, Structure<?>> entry : Structure.STRUCTURES_REGISTRY.entrySet()) {
         literalargumentbuilder = literalargumentbuilder.then(Commands.literal(entry.getKey()).executes((p_241056_1_) -> {
            return locate(p_241056_1_.getSource(), entry.getValue());
         }));
      }
      else {
      for (Structure<?> structureFeature : net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES) {
         String name = structureFeature.getRegistryName().toString().replace("minecraft:", "");
         literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
               .executes(ctx -> locate(ctx.getSource(), structureFeature)));
      }}

      p_198528_0_.register(literalargumentbuilder);
   }

   private static int locate(CommandSource p_241053_0_, Structure<?> p_241053_1_) throws CommandSyntaxException {
      BlockPos blockpos = new BlockPos(p_241053_0_.getPosition());
      BlockPos blockpos1 = p_241053_0_.getLevel().findNearestMapFeature(p_241053_1_, blockpos, 100, false);
      if (blockpos1 == null) {
         throw ERROR_FAILED.create();
      } else {
         return showLocateResult(p_241053_0_, p_241053_1_.getFeatureName(), blockpos, blockpos1, "commands.locate.success");
      }
   }

   public static int showLocateResult(CommandSource p_241054_0_, String p_241054_1_, BlockPos p_241054_2_, BlockPos p_241054_3_, String p_241054_4_) {
      int i = MathHelper.floor(dist(p_241054_2_.getX(), p_241054_2_.getZ(), p_241054_3_.getX(), p_241054_3_.getZ()));
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", p_241054_3_.getX(), "~", p_241054_3_.getZ())).withStyle((p_241055_1_) -> {
         return p_241055_1_.withColor(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + p_241054_3_.getX() + " ~ " + p_241054_3_.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
      });
      p_241054_0_.sendSuccess(new TranslationTextComponent(p_241054_4_, p_241054_1_, itextcomponent, i), false);
      return i;
   }

   private static float dist(int p_211907_0_, int p_211907_1_, int p_211907_2_, int p_211907_3_) {
      int i = p_211907_2_ - p_211907_0_;
      int j = p_211907_3_ - p_211907_1_;
      return MathHelper.sqrt((float)(i * i + j * j));
   }
}
