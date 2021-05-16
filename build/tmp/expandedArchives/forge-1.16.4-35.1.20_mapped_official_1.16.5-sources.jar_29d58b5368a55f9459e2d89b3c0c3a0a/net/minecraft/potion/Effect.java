package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Effect extends net.minecraftforge.registries.ForgeRegistryEntry<Effect> implements net.minecraftforge.common.extensions.IForgeEffect {
   private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
   private final EffectType category;
   private final int color;
   @Nullable
   private String descriptionId;

   @Nullable
   public static Effect byId(int p_188412_0_) {
      return Registry.MOB_EFFECT.byId(p_188412_0_);
   }

   public static int getId(Effect p_188409_0_) {
      return Registry.MOB_EFFECT.getId(p_188409_0_);
   }

   protected Effect(EffectType p_i50391_1_, int p_i50391_2_) {
      this.category = p_i50391_1_;
      this.color = p_i50391_2_;
   }

   public void applyEffectTick(LivingEntity p_76394_1_, int p_76394_2_) {
      if (this == Effects.REGENERATION) {
         if (p_76394_1_.getHealth() < p_76394_1_.getMaxHealth()) {
            p_76394_1_.heal(1.0F);
         }
      } else if (this == Effects.POISON) {
         if (p_76394_1_.getHealth() > 1.0F) {
            p_76394_1_.hurt(DamageSource.MAGIC, 1.0F);
         }
      } else if (this == Effects.WITHER) {
         p_76394_1_.hurt(DamageSource.WITHER, 1.0F);
      } else if (this == Effects.HUNGER && p_76394_1_ instanceof PlayerEntity) {
         ((PlayerEntity)p_76394_1_).causeFoodExhaustion(0.005F * (float)(p_76394_2_ + 1));
      } else if (this == Effects.SATURATION && p_76394_1_ instanceof PlayerEntity) {
         if (!p_76394_1_.level.isClientSide) {
            ((PlayerEntity)p_76394_1_).getFoodData().eat(p_76394_2_ + 1, 1.0F);
         }
      } else if ((this != Effects.HEAL || p_76394_1_.isInvertedHealAndHarm()) && (this != Effects.HARM || !p_76394_1_.isInvertedHealAndHarm())) {
         if (this == Effects.HARM && !p_76394_1_.isInvertedHealAndHarm() || this == Effects.HEAL && p_76394_1_.isInvertedHealAndHarm()) {
            p_76394_1_.hurt(DamageSource.MAGIC, (float)(6 << p_76394_2_));
         }
      } else {
         p_76394_1_.heal((float)Math.max(4 << p_76394_2_, 0));
      }

   }

   public void applyInstantenousEffect(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity p_180793_3_, int p_180793_4_, double p_180793_5_) {
      if ((this != Effects.HEAL || p_180793_3_.isInvertedHealAndHarm()) && (this != Effects.HARM || !p_180793_3_.isInvertedHealAndHarm())) {
         if (this == Effects.HARM && !p_180793_3_.isInvertedHealAndHarm() || this == Effects.HEAL && p_180793_3_.isInvertedHealAndHarm()) {
            int j = (int)(p_180793_5_ * (double)(6 << p_180793_4_) + 0.5D);
            if (p_180793_1_ == null) {
               p_180793_3_.hurt(DamageSource.MAGIC, (float)j);
            } else {
               p_180793_3_.hurt(DamageSource.indirectMagic(p_180793_1_, p_180793_2_), (float)j);
            }
         } else {
            this.applyEffectTick(p_180793_3_, p_180793_4_);
         }
      } else {
         int i = (int)(p_180793_5_ * (double)(4 << p_180793_4_) + 0.5D);
         p_180793_3_.heal((float)i);
      }

   }

   public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
      if (this == Effects.REGENERATION) {
         int k = 50 >> p_76397_2_;
         if (k > 0) {
            return p_76397_1_ % k == 0;
         } else {
            return true;
         }
      } else if (this == Effects.POISON) {
         int j = 25 >> p_76397_2_;
         if (j > 0) {
            return p_76397_1_ % j == 0;
         } else {
            return true;
         }
      } else if (this == Effects.WITHER) {
         int i = 40 >> p_76397_2_;
         if (i > 0) {
            return p_76397_1_ % i == 0;
         } else {
            return true;
         }
      } else {
         return this == Effects.HUNGER;
      }
   }

   public boolean isInstantenous() {
      return false;
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("effect", Registry.MOB_EFFECT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent(this.getDescriptionId());
   }

   public EffectType getCategory() {
      return this.category;
   }

   public int getColor() {
      return this.color;
   }

   public Effect addAttributeModifier(Attribute p_220304_1_, String p_220304_2_, double p_220304_3_, AttributeModifier.Operation p_220304_5_) {
      AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(p_220304_2_), this::getDescriptionId, p_220304_3_, p_220304_5_);
      this.attributeModifiers.put(p_220304_1_, attributemodifier);
      return this;
   }

   public Map<Attribute, AttributeModifier> getAttributeModifiers() {
      return this.attributeModifiers;
   }

   public void removeAttributeModifiers(LivingEntity p_111187_1_, AttributeModifierManager p_111187_2_, int p_111187_3_) {
      for(Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
         ModifiableAttributeInstance modifiableattributeinstance = p_111187_2_.getInstance(entry.getKey());
         if (modifiableattributeinstance != null) {
            modifiableattributeinstance.removeModifier(entry.getValue());
         }
      }

   }

   public void addAttributeModifiers(LivingEntity p_111185_1_, AttributeModifierManager p_111185_2_, int p_111185_3_) {
      for(Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
         ModifiableAttributeInstance modifiableattributeinstance = p_111185_2_.getInstance(entry.getKey());
         if (modifiableattributeinstance != null) {
            AttributeModifier attributemodifier = entry.getValue();
            modifiableattributeinstance.removeModifier(attributemodifier);
            modifiableattributeinstance.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), this.getDescriptionId() + " " + p_111185_3_, this.getAttributeModifierValue(p_111185_3_, attributemodifier), attributemodifier.getOperation()));
         }
      }

   }

   public double getAttributeModifierValue(int p_111183_1_, AttributeModifier p_111183_2_) {
      return p_111183_2_.getAmount() * (double)(p_111183_1_ + 1);
   }

   public boolean isBeneficial() {
      return this.category == EffectType.BENEFICIAL;
   }
}
