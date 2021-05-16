package net.minecraft.util.registry;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream STDOUT = System.out;
   private static boolean isBootstrapped;
   private static final Logger LOGGER = LogManager.getLogger();

   public static void bootStrap() {
      if (!isBootstrapped) {
         isBootstrapped = true;
         if (Registry.REGISTRY.keySet().isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
         } else {
            FireBlock.bootStrap();
            ComposterBlock.bootStrap();
            if (EntityType.getKey(EntityType.PLAYER) == null) {
               throw new IllegalStateException("Failed loading EntityTypes");
            } else {
               PotionBrewing.bootStrap();
               EntityOptions.bootStrap();
               IDispenseItemBehavior.bootStrap();
               ArgumentTypes.bootStrap();
               TagRegistryManager.bootStrap();
               net.minecraftforge.registries.GameData.vanillaSnapshot();
               if (false) // skip redirectOutputToLog, Forge already redirects stdout and stderr output to log so that they print with more context
               wrapStreams();
            }
         }
      }
   }

   private static <T> void checkTranslations(Iterable<T> p_240912_0_, Function<T, String> p_240912_1_, Set<String> p_240912_2_) {
      LanguageMap languagemap = LanguageMap.getInstance();
      p_240912_0_.forEach((p_218818_3_) -> {
         String s = p_240912_1_.apply(p_218818_3_);
         if (!languagemap.has(s)) {
            p_240912_2_.add(s);
         }

      });
   }

   private static void checkGameruleTranslations(final Set<String> p_240913_0_) {
      final LanguageMap languagemap = LanguageMap.getInstance();
      GameRules.visitGameRuleTypes(new GameRules.IRuleEntryVisitor() {
         public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_) {
            if (!languagemap.has(p_223481_1_.getDescriptionId())) {
               p_240913_0_.add(p_223481_1_.getId());
            }

         }
      });
   }

   public static Set<String> getMissingTranslations() {
      Set<String> set = new TreeSet<>();
      checkTranslations(Registry.ATTRIBUTE, Attribute::getDescriptionId, set);
      checkTranslations(Registry.ENTITY_TYPE, EntityType::getDescriptionId, set);
      checkTranslations(Registry.MOB_EFFECT, Effect::getDescriptionId, set);
      checkTranslations(Registry.ITEM, Item::getDescriptionId, set);
      checkTranslations(Registry.ENCHANTMENT, Enchantment::getDescriptionId, set);
      checkTranslations(Registry.BLOCK, Block::getDescriptionId, set);
      checkTranslations(Registry.CUSTOM_STAT, (p_218820_0_) -> {
         return "stat." + p_218820_0_.toString().replace(':', '.');
      }, set);
      checkGameruleTranslations(set);
      return set;
   }

   public static void validate() {
      if (!isBootstrapped) {
         throw new IllegalArgumentException("Not bootstrapped");
      } else {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            getMissingTranslations().forEach((p_218817_0_) -> {
               LOGGER.error("Missing translations: " + p_218817_0_);
            });
            Commands.validate();
         }

      }
   }

   private static void wrapStreams() {
      if (LOGGER.isDebugEnabled()) {
         System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggingPrintStream("STDOUT", STDOUT));
      } else {
         System.setErr(new LoggingPrintStream("STDERR", System.err));
         System.setOut(new LoggingPrintStream("STDOUT", STDOUT));
      }

   }

   public static void realStdoutPrintln(String p_179870_0_) {
      STDOUT.println(p_179870_0_);
   }
}
