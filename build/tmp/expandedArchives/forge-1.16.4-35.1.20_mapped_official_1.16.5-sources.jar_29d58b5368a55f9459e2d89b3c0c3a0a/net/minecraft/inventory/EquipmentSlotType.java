package net.minecraft.inventory;

public enum EquipmentSlotType {
   MAINHAND(EquipmentSlotType.Group.HAND, 0, 0, "mainhand"),
   OFFHAND(EquipmentSlotType.Group.HAND, 1, 5, "offhand"),
   FEET(EquipmentSlotType.Group.ARMOR, 0, 1, "feet"),
   LEGS(EquipmentSlotType.Group.ARMOR, 1, 2, "legs"),
   CHEST(EquipmentSlotType.Group.ARMOR, 2, 3, "chest"),
   HEAD(EquipmentSlotType.Group.ARMOR, 3, 4, "head");

   private final EquipmentSlotType.Group type;
   private final int index;
   private final int filterFlag;
   private final String name;

   private EquipmentSlotType(EquipmentSlotType.Group p_i46808_3_, int p_i46808_4_, int p_i46808_5_, String p_i46808_6_) {
      this.type = p_i46808_3_;
      this.index = p_i46808_4_;
      this.filterFlag = p_i46808_5_;
      this.name = p_i46808_6_;
   }

   public EquipmentSlotType.Group getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public int getFilterFlag() {
      return this.filterFlag;
   }

   public String getName() {
      return this.name;
   }

   public static EquipmentSlotType byName(String p_188451_0_) {
      for(EquipmentSlotType equipmentslottype : values()) {
         if (equipmentslottype.getName().equals(p_188451_0_)) {
            return equipmentslottype;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + p_188451_0_ + "'");
   }

   public static EquipmentSlotType byTypeAndIndex(EquipmentSlotType.Group p_220318_0_, int p_220318_1_) {
      for(EquipmentSlotType equipmentslottype : values()) {
         if (equipmentslottype.getType() == p_220318_0_ && equipmentslottype.getIndex() == p_220318_1_) {
            return equipmentslottype;
         }
      }

      throw new IllegalArgumentException("Invalid slot '" + p_220318_0_ + "': " + p_220318_1_);
   }

   public static enum Group {
      HAND,
      ARMOR;
   }
}
