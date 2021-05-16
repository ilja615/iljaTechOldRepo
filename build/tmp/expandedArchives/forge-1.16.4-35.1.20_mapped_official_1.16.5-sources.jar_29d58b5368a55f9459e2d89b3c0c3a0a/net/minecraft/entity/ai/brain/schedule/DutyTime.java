package net.minecraft.entity.ai.brain.schedule;

public class DutyTime {
   private final int timeStamp;
   private final float value;

   public DutyTime(int p_i50139_1_, float p_i50139_2_) {
      this.timeStamp = p_i50139_1_;
      this.value = p_i50139_2_;
   }

   public int getTimeStamp() {
      return this.timeStamp;
   }

   public float getValue() {
      return this.value;
   }
}
