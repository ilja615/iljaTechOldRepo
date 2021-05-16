package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;

public class AttributeModifierMap {
   private final Map<Attribute, ModifiableAttributeInstance> instances;

   public AttributeModifierMap(Map<Attribute, ModifiableAttributeInstance> p_i231503_1_) {
      this.instances = ImmutableMap.copyOf(p_i231503_1_);
   }

   private ModifiableAttributeInstance getAttributeInstance(Attribute p_233810_1_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.instances.get(p_233810_1_);
      if (modifiableattributeinstance == null) {
         throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTE.getKey(p_233810_1_));
      } else {
         return modifiableattributeinstance;
      }
   }

   public double getValue(Attribute p_233804_1_) {
      return this.getAttributeInstance(p_233804_1_).getValue();
   }

   public double getBaseValue(Attribute p_233807_1_) {
      return this.getAttributeInstance(p_233807_1_).getBaseValue();
   }

   public double getModifierValue(Attribute p_233805_1_, UUID p_233805_2_) {
      AttributeModifier attributemodifier = this.getAttributeInstance(p_233805_1_).getModifier(p_233805_2_);
      if (attributemodifier == null) {
         throw new IllegalArgumentException("Can't find modifier " + p_233805_2_ + " on attribute " + Registry.ATTRIBUTE.getKey(p_233805_1_));
      } else {
         return attributemodifier.getAmount();
      }
   }

   @Nullable
   public ModifiableAttributeInstance createInstance(Consumer<ModifiableAttributeInstance> p_233806_1_, Attribute p_233806_2_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.instances.get(p_233806_2_);
      if (modifiableattributeinstance == null) {
         return null;
      } else {
         ModifiableAttributeInstance modifiableattributeinstance1 = new ModifiableAttributeInstance(p_233806_2_, p_233806_1_);
         modifiableattributeinstance1.replaceFrom(modifiableattributeinstance);
         return modifiableattributeinstance1;
      }
   }

   public static AttributeModifierMap.MutableAttribute builder() {
      return new AttributeModifierMap.MutableAttribute();
   }

   public boolean hasAttribute(Attribute p_233809_1_) {
      return this.instances.containsKey(p_233809_1_);
   }

   public boolean hasModifier(Attribute p_233808_1_, UUID p_233808_2_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.instances.get(p_233808_1_);
      return modifiableattributeinstance != null && modifiableattributeinstance.getModifier(p_233808_2_) != null;
   }

   public static class MutableAttribute {
      private final Map<Attribute, ModifiableAttributeInstance> builder = Maps.newHashMap();
      private boolean instanceFrozen;

      private ModifiableAttributeInstance create(Attribute p_233817_1_) {
         ModifiableAttributeInstance modifiableattributeinstance = new ModifiableAttributeInstance(p_233817_1_, (p_233816_2_) -> {
            if (this.instanceFrozen) {
               throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getKey(p_233817_1_));
            }
         });
         this.builder.put(p_233817_1_, modifiableattributeinstance);
         return modifiableattributeinstance;
      }

      public AttributeModifierMap.MutableAttribute add(Attribute p_233814_1_) {
         this.create(p_233814_1_);
         return this;
      }

      public AttributeModifierMap.MutableAttribute add(Attribute p_233815_1_, double p_233815_2_) {
         ModifiableAttributeInstance modifiableattributeinstance = this.create(p_233815_1_);
         modifiableattributeinstance.setBaseValue(p_233815_2_);
         return this;
      }

      public AttributeModifierMap build() {
         this.instanceFrozen = true;
         return new AttributeModifierMap(this.builder);
      }
   }
}
