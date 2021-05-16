package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifiableAttributeInstance {
   private final Attribute attribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap<>();
   private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet<>();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;
   private final Consumer<ModifiableAttributeInstance> onDirty;

   public ModifiableAttributeInstance(Attribute p_i231501_1_, Consumer<ModifiableAttributeInstance> p_i231501_2_) {
      this.attribute = p_i231501_1_;
      this.onDirty = p_i231501_2_;
      this.baseValue = p_i231501_1_.getDefaultValue();
   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double p_111128_1_) {
      if (p_111128_1_ != this.baseValue) {
         this.baseValue = p_111128_1_;
         this.setDirty();
      }
   }

   public Set<AttributeModifier> getModifiers(AttributeModifier.Operation p_225504_1_) {
      return this.modifiersByOperation.computeIfAbsent(p_225504_1_, (p_233768_0_) -> {
         return Sets.newHashSet();
      });
   }

   public Set<AttributeModifier> getModifiers() {
      return ImmutableSet.copyOf(this.modifierById.values());
   }

   @Nullable
   public AttributeModifier getModifier(UUID p_111127_1_) {
      return this.modifierById.get(p_111127_1_);
   }

   public boolean hasModifier(AttributeModifier p_180374_1_) {
      return this.modifierById.get(p_180374_1_.getId()) != null;
   }

   private void addModifier(AttributeModifier p_111121_1_) {
      AttributeModifier attributemodifier = this.modifierById.putIfAbsent(p_111121_1_.getId(), p_111121_1_);
      if (attributemodifier != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(p_111121_1_.getOperation()).add(p_111121_1_);
         this.setDirty();
      }
   }

   public void addTransientModifier(AttributeModifier p_233767_1_) {
      this.addModifier(p_233767_1_);
   }

   public void addPermanentModifier(AttributeModifier p_233769_1_) {
      this.addModifier(p_233769_1_);
      this.permanentModifiers.add(p_233769_1_);
   }

   protected void setDirty() {
      this.dirty = true;
      this.onDirty.accept(this);
   }

   public void removeModifier(AttributeModifier p_111124_1_) {
      this.getModifiers(p_111124_1_.getOperation()).remove(p_111124_1_);
      this.modifierById.remove(p_111124_1_.getId());
      this.permanentModifiers.remove(p_111124_1_);
      this.setDirty();
   }

   public void removeModifier(UUID p_188479_1_) {
      AttributeModifier attributemodifier = this.getModifier(p_188479_1_);
      if (attributemodifier != null) {
         this.removeModifier(attributemodifier);
      }

   }

   public boolean removePermanentModifier(UUID p_233770_1_) {
      AttributeModifier attributemodifier = this.getModifier(p_233770_1_);
      if (attributemodifier != null && this.permanentModifiers.contains(attributemodifier)) {
         this.removeModifier(attributemodifier);
         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void removeModifiers() {
      for(AttributeModifier attributemodifier : this.getModifiers()) {
         this.removeModifier(attributemodifier);
      }

   }

   public double getValue() {
      if (this.dirty) {
         this.cachedValue = this.calculateValue();
         this.dirty = false;
      }

      return this.cachedValue;
   }

   private double calculateValue() {
      double d0 = this.getBaseValue();

      for(AttributeModifier attributemodifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
         d0 += attributemodifier.getAmount();
      }

      double d1 = d0;

      for(AttributeModifier attributemodifier1 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
         d1 += d0 * attributemodifier1.getAmount();
      }

      for(AttributeModifier attributemodifier2 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         d1 *= 1.0D + attributemodifier2.getAmount();
      }

      return this.attribute.sanitizeValue(d1);
   }

   private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation p_220370_1_) {
      return this.modifiersByOperation.getOrDefault(p_220370_1_, Collections.emptySet());
   }

   public void replaceFrom(ModifiableAttributeInstance p_233763_1_) {
      this.baseValue = p_233763_1_.baseValue;
      this.modifierById.clear();
      this.modifierById.putAll(p_233763_1_.modifierById);
      this.permanentModifiers.clear();
      this.permanentModifiers.addAll(p_233763_1_.permanentModifiers);
      this.modifiersByOperation.clear();
      p_233763_1_.modifiersByOperation.forEach((p_233764_1_, p_233764_2_) -> {
         this.getModifiers(p_233764_1_).addAll(p_233764_2_);
      });
      this.setDirty();
   }

   public CompoundNBT save() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", Registry.ATTRIBUTE.getKey(this.attribute).toString());
      compoundnbt.putDouble("Base", this.baseValue);
      if (!this.permanentModifiers.isEmpty()) {
         ListNBT listnbt = new ListNBT();

         for(AttributeModifier attributemodifier : this.permanentModifiers) {
            listnbt.add(attributemodifier.save());
         }

         compoundnbt.put("Modifiers", listnbt);
      }

      return compoundnbt;
   }

   public void load(CompoundNBT p_233765_1_) {
      this.baseValue = p_233765_1_.getDouble("Base");
      if (p_233765_1_.contains("Modifiers", 9)) {
         ListNBT listnbt = p_233765_1_.getList("Modifiers", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            AttributeModifier attributemodifier = AttributeModifier.load(listnbt.getCompound(i));
            if (attributemodifier != null) {
               this.modifierById.put(attributemodifier.getId(), attributemodifier);
               this.getModifiers(attributemodifier.getOperation()).add(attributemodifier);
               this.permanentModifiers.add(attributemodifier);
            }
         }
      }

      this.setDirty();
   }
}
