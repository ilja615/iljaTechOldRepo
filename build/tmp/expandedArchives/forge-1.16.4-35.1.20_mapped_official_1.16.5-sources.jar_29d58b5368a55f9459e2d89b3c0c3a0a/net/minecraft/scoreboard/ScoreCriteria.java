package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.stats.StatType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;

public class ScoreCriteria {
   public static final Map<String, ScoreCriteria> CRITERIA_BY_NAME = Maps.newHashMap();
   public static final ScoreCriteria DUMMY = new ScoreCriteria("dummy");
   public static final ScoreCriteria TRIGGER = new ScoreCriteria("trigger");
   public static final ScoreCriteria DEATH_COUNT = new ScoreCriteria("deathCount");
   public static final ScoreCriteria KILL_COUNT_PLAYERS = new ScoreCriteria("playerKillCount");
   public static final ScoreCriteria KILL_COUNT_ALL = new ScoreCriteria("totalKillCount");
   public static final ScoreCriteria HEALTH = new ScoreCriteria("health", true, ScoreCriteria.RenderType.HEARTS);
   public static final ScoreCriteria FOOD = new ScoreCriteria("food", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria AIR = new ScoreCriteria("air", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria ARMOR = new ScoreCriteria("armor", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria EXPERIENCE = new ScoreCriteria("xp", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria LEVEL = new ScoreCriteria("level", true, ScoreCriteria.RenderType.INTEGER);
   public static final ScoreCriteria[] TEAM_KILL = new ScoreCriteria[]{new ScoreCriteria("teamkill." + TextFormatting.BLACK.getName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_BLUE.getName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_GREEN.getName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_AQUA.getName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_RED.getName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_PURPLE.getName()), new ScoreCriteria("teamkill." + TextFormatting.GOLD.getName()), new ScoreCriteria("teamkill." + TextFormatting.GRAY.getName()), new ScoreCriteria("teamkill." + TextFormatting.DARK_GRAY.getName()), new ScoreCriteria("teamkill." + TextFormatting.BLUE.getName()), new ScoreCriteria("teamkill." + TextFormatting.GREEN.getName()), new ScoreCriteria("teamkill." + TextFormatting.AQUA.getName()), new ScoreCriteria("teamkill." + TextFormatting.RED.getName()), new ScoreCriteria("teamkill." + TextFormatting.LIGHT_PURPLE.getName()), new ScoreCriteria("teamkill." + TextFormatting.YELLOW.getName()), new ScoreCriteria("teamkill." + TextFormatting.WHITE.getName())};
   public static final ScoreCriteria[] KILLED_BY_TEAM = new ScoreCriteria[]{new ScoreCriteria("killedByTeam." + TextFormatting.BLACK.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_BLUE.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_GREEN.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_AQUA.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_RED.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_PURPLE.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.GOLD.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.GRAY.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.DARK_GRAY.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.BLUE.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.GREEN.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.AQUA.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.RED.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.LIGHT_PURPLE.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.YELLOW.getName()), new ScoreCriteria("killedByTeam." + TextFormatting.WHITE.getName())};
   private final String name;
   private final boolean readOnly;
   private final ScoreCriteria.RenderType renderType;

   public ScoreCriteria(String p_i47676_1_) {
      this(p_i47676_1_, false, ScoreCriteria.RenderType.INTEGER);
   }

   protected ScoreCriteria(String p_i47677_1_, boolean p_i47677_2_, ScoreCriteria.RenderType p_i47677_3_) {
      this.name = p_i47677_1_;
      this.readOnly = p_i47677_2_;
      this.renderType = p_i47677_3_;
      CRITERIA_BY_NAME.put(p_i47677_1_, this);
   }

   public static Optional<ScoreCriteria> byName(String p_216390_0_) {
      if (CRITERIA_BY_NAME.containsKey(p_216390_0_)) {
         return Optional.of(CRITERIA_BY_NAME.get(p_216390_0_));
      } else {
         int i = p_216390_0_.indexOf(58);
         return i < 0 ? Optional.empty() : Registry.STAT_TYPE.getOptional(ResourceLocation.of(p_216390_0_.substring(0, i), '.')).flatMap((p_216392_2_) -> {
            return getStat(p_216392_2_, ResourceLocation.of(p_216390_0_.substring(i + 1), '.'));
         });
      }
   }

   private static <T> Optional<ScoreCriteria> getStat(StatType<T> p_216391_0_, ResourceLocation p_216391_1_) {
      return p_216391_0_.getRegistry().getOptional(p_216391_1_).map(p_216391_0_::get);
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public ScoreCriteria.RenderType getDefaultRenderType() {
      return this.renderType;
   }

   public static enum RenderType {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String id;
      private static final Map<String, ScoreCriteria.RenderType> BY_ID;

      private RenderType(String p_i49784_3_) {
         this.id = p_i49784_3_;
      }

      public String getId() {
         return this.id;
      }

      public static ScoreCriteria.RenderType byId(String p_211839_0_) {
         return BY_ID.getOrDefault(p_211839_0_, INTEGER);
      }

      static {
         Builder<String, ScoreCriteria.RenderType> builder = ImmutableMap.builder();

         for(ScoreCriteria.RenderType scorecriteria$rendertype : values()) {
            builder.put(scorecriteria$rendertype.id, scorecriteria$rendertype);
         }

         BY_ID = builder.build();
      }
   }
}
