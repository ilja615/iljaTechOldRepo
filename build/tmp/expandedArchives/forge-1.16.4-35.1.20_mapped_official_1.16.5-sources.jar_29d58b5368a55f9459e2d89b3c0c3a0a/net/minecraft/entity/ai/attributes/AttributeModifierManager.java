package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeModifierManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<Attribute, ModifiableAttributeInstance> attributes = Maps.newHashMap();
   private final Set<ModifiableAttributeInstance> dirtyAttributes = Sets.newHashSet();
   private final AttributeModifierMap supplier;

   public AttributeModifierManager(AttributeModifierMap p_i231502_1_) {
      this.supplier = p_i231502_1_;
   }

   private void onAttributeModified(ModifiableAttributeInstance p_233783_1_) {
      if (p_233783_1_.getAttribute().isClientSyncable()) {
         this.dirtyAttributes.add(p_233783_1_);
      }

   }

   public Set<ModifiableAttributeInstance> getDirtyAttributes() {
      return this.dirtyAttributes;
   }

   public Collection<ModifiableAttributeInstance> getSyncableAttributes() {
      return this.attributes.values().stream().filter((p_233796_0_) -> {
         return p_233796_0_.getAttribute().isClientSyncable();
      }).collect(Collectors.toList());
   }

   @Nullable
   public ModifiableAttributeInstance getInstance(Attribute p_233779_1_) {
      return this.attributes.computeIfAbsent(p_233779_1_, (p_233798_1_) -> {
         return this.supplier.createInstance(this::onAttributeModified, p_233798_1_);
      });
   }

   public boolean hasAttribute(Attribute p_233790_1_) {
      return this.attributes.get(p_233790_1_) != null || this.supplier.hasAttribute(p_233790_1_);
   }

   public boolean hasModifier(Attribute p_233782_1_, UUID p_233782_2_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.attributes.get(p_233782_1_);
      return modifiableattributeinstance != null ? modifiableattributeinstance.getModifier(p_233782_2_) != null : this.supplier.hasModifier(p_233782_1_, p_233782_2_);
   }

   public double getValue(Attribute p_233795_1_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.attributes.get(p_233795_1_);
      return modifiableattributeinstance != null ? modifiableattributeinstance.getValue() : this.supplier.getValue(p_233795_1_);
   }

   public double getBaseValue(Attribute p_233797_1_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.attributes.get(p_233797_1_);
      return modifiableattributeinstance != null ? modifiableattributeinstance.getBaseValue() : this.supplier.getBaseValue(p_233797_1_);
   }

   public double getModifierValue(Attribute p_233791_1_, UUID p_233791_2_) {
      ModifiableAttributeInstance modifiableattributeinstance = this.attributes.get(p_233791_1_);
      return modifiableattributeinstance != null ? modifiableattributeinstance.getModifier(p_233791_2_).getAmount() : this.supplier.getModifierValue(p_233791_1_, p_233791_2_);
   }

   public void removeAttributeModifiers(Multimap<Attribute, AttributeModifier> p_233785_1_) {
      p_233785_1_.asMap().forEach((p_233781_1_, p_233781_2_) -> {
         ModifiableAttributeInstance modifiableattributeinstance = this.attributes.get(p_233781_1_);
         if (modifiableattributeinstance != null) {
            p_233781_2_.forEach(modifiableattributeinstance::removeModifier);
         }

      });
   }

   public void addTransientAttributeModifiers(Multimap<Attribute, AttributeModifier> p_233793_1_) {
      p_233793_1_.forEach((p_233780_1_, p_233780_2_) -> {
         ModifiableAttributeInstance modifiableattributeinstance = this.getInstance(p_233780_1_);
         if (modifiableattributeinstance != null) {
            modifiableattributeinstance.removeModifier(p_233780_2_);
            modifiableattributeinstance.addTransientModifier(p_233780_2_);
         }

      });
   }

   @OnlyIn(Dist.CLIENT)
   public void assignValues(AttributeModifierManager p_233784_1_) {
      p_233784_1_.attributes.values().forEach((p_233792_1_) -> {
         ModifiableAttributeInstance modifiableattributeinstance = this.getInstance(p_233792_1_.getAttribute());
         if (modifiableattributeinstance != null) {
            modifiableattributeinstance.replaceFrom(p_233792_1_);
         }

      });
   }

   public ListNBT save() {
      ListNBT listnbt = new ListNBT();

      for(ModifiableAttributeInstance modifiableattributeinstance : this.attributes.values()) {
         listnbt.add(modifiableattributeinstance.save());
      }

      return listnbt;
   }

   public void load(ListNBT p_233788_1_) {
      for(int i = 0; i < p_233788_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_233788_1_.getCompound(i);
         String s = compoundnbt.getString("Name");
         Util.ifElse(Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(s)), (p_233787_2_) -> {
            ModifiableAttributeInstance modifiableattributeinstance = this.getInstance(p_233787_2_);
            if (modifiableattributeinstance != null) {
               modifiableattributeinstance.load(compoundnbt);
            }

         }, () -> {
            LOGGER.warn("Ignoring unknown attribute '{}'", (Object)s);
         });
      }

   }
}
