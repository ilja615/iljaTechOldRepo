package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String pattern;
   @Nullable
   private final EntitySelector selector;

   public SelectorTextComponent(String p_i45996_1_) {
      this.pattern = p_i45996_1_;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_i45996_1_));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         LOGGER.warn("Invalid selector component: {}", p_i45996_1_, commandsyntaxexception.getMessage());
      }

      this.selector = entityselector;
   }

   public String getPattern() {
      return this.pattern;
   }

   public IFormattableTextComponent resolve(@Nullable CommandSource p_230535_1_, @Nullable Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException {
      return (IFormattableTextComponent)(p_230535_1_ != null && this.selector != null ? EntitySelector.joinNames(this.selector.findEntities(p_230535_1_)) : new StringTextComponent(""));
   }

   public String getContents() {
      return this.pattern;
   }

   public SelectorTextComponent plainCopy() {
      return new SelectorTextComponent(this.pattern);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof SelectorTextComponent)) {
         return false;
      } else {
         SelectorTextComponent selectortextcomponent = (SelectorTextComponent)p_equals_1_;
         return this.pattern.equals(selectortextcomponent.pattern) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
