package net.minecraft.scoreboard;

import java.util.Comparator;
import javax.annotation.Nullable;

public class Score {
   public static final Comparator<Score> SCORE_COMPARATOR = (p_210221_0_, p_210221_1_) -> {
      if (p_210221_0_.getScore() > p_210221_1_.getScore()) {
         return 1;
      } else {
         return p_210221_0_.getScore() < p_210221_1_.getScore() ? -1 : p_210221_1_.getOwner().compareToIgnoreCase(p_210221_0_.getOwner());
      }
   };
   private final Scoreboard scoreboard;
   @Nullable
   private final ScoreObjective objective;
   private final String owner;
   private int count;
   private boolean locked;
   private boolean forceUpdate;

   public Score(Scoreboard p_i2309_1_, ScoreObjective p_i2309_2_, String p_i2309_3_) {
      this.scoreboard = p_i2309_1_;
      this.objective = p_i2309_2_;
      this.owner = p_i2309_3_;
      this.locked = true;
      this.forceUpdate = true;
   }

   public void add(int p_96649_1_) {
      if (this.objective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.setScore(this.getScore() + p_96649_1_);
      }
   }

   public void increment() {
      this.add(1);
   }

   public int getScore() {
      return this.count;
   }

   public void reset() {
      this.setScore(0);
   }

   public void setScore(int p_96647_1_) {
      int i = this.count;
      this.count = p_96647_1_;
      if (i != p_96647_1_ || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreboard().onScoreChanged(this);
      }

   }

   @Nullable
   public ScoreObjective getObjective() {
      return this.objective;
   }

   public String getOwner() {
      return this.owner;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean p_178815_1_) {
      this.locked = p_178815_1_;
   }
}
