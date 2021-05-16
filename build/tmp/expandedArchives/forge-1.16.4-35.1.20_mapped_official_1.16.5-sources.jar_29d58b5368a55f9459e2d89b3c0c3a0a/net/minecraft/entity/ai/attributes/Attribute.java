package net.minecraft.entity.ai.attributes;

public class Attribute extends net.minecraftforge.registries.ForgeRegistryEntry<Attribute> {
   private final double defaultValue;
   private boolean syncable;
   private final String descriptionId;

   protected Attribute(String p_i231500_1_, double p_i231500_2_) {
      this.defaultValue = p_i231500_2_;
      this.descriptionId = p_i231500_1_;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public Attribute setSyncable(boolean p_233753_1_) {
      this.syncable = p_233753_1_;
      return this;
   }

   public double sanitizeValue(double p_111109_1_) {
      return p_111109_1_;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }
}
