package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ScoreTextComponent extends TextComponent implements ITargetedTextComponent {
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;

   @Nullable
   private static EntitySelector parseSelector(String p_240707_0_) {
      try {
         return (new EntitySelectorParser(new StringReader(p_240707_0_))).parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public ScoreTextComponent(String p_i45997_1_, String p_i45997_2_) {
      this(p_i45997_1_, parseSelector(p_i45997_1_), p_i45997_2_);
   }

   private ScoreTextComponent(String p_i232569_1_, @Nullable EntitySelector p_i232569_2_, String p_i232569_3_) {
      this.name = p_i232569_1_;
      this.selector = p_i232569_2_;
      this.objective = p_i232569_3_;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   private String findTargetName(CommandSource p_240705_1_) throws CommandSyntaxException {
      if (this.selector != null) {
         List<? extends Entity> list = this.selector.findEntities(p_240705_1_);
         if (!list.isEmpty()) {
            if (list.size() != 1) {
               throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            }

            return list.get(0).getScoreboardName();
         }
      }

      return this.name;
   }

   private String getScore(String p_240706_1_, CommandSource p_240706_2_) {
      MinecraftServer minecraftserver = p_240706_2_.getServer();
      if (minecraftserver != null) {
         Scoreboard scoreboard = minecraftserver.getScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjective(this.objective);
         if (scoreboard.hasPlayerScore(p_240706_1_, scoreobjective)) {
            Score score = scoreboard.getOrCreatePlayerScore(p_240706_1_, scoreobjective);
            return Integer.toString(score.getScore());
         }
      }

      return "";
   }

   public ScoreTextComponent plainCopy() {
      return new ScoreTextComponent(this.name, this.selector, this.objective);
   }

   public IFormattableTextComponent resolve(@Nullable CommandSource p_230535_1_, @Nullable Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException {
      if (p_230535_1_ == null) {
         return new StringTextComponent("");
      } else {
         String s = this.findTargetName(p_230535_1_);
         String s1 = p_230535_2_ != null && s.equals("*") ? p_230535_2_.getScoreboardName() : s;
         return new StringTextComponent(this.getScore(s1, p_230535_1_));
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ScoreTextComponent)) {
         return false;
      } else {
         ScoreTextComponent scoretextcomponent = (ScoreTextComponent)p_equals_1_;
         return this.name.equals(scoretextcomponent.name) && this.objective.equals(scoretextcomponent.objective) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
