package net.minecraft.world;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int value;

   private TickPriority(int p_i48976_3_) {
      this.value = p_i48976_3_;
   }

   public static TickPriority byValue(int p_205397_0_) {
      for(TickPriority tickpriority : values()) {
         if (tickpriority.value == p_205397_0_) {
            return tickpriority;
         }
      }

      return p_205397_0_ < EXTREMELY_HIGH.value ? EXTREMELY_HIGH : EXTREMELY_LOW;
   }

   public int getValue() {
      return this.value;
   }
}
