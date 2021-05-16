package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Potion extends net.minecraftforge.registries.ForgeRegistryEntry<Potion> {
   private final net.minecraftforge.common.util.ReverseTagWrapper<Potion> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, () -> net.minecraft.tags.TagCollectionManager.getInstance().getCustomTypeCollection(net.minecraftforge.registries.ForgeRegistries.POTION_TYPES));
   private final String name;
   private final ImmutableList<EffectInstance> effects;

   public static Potion byName(String p_185168_0_) {
      return Registry.POTION.get(ResourceLocation.tryParse(p_185168_0_));
   }

   public Potion(EffectInstance... p_i46739_1_) {
      this((String)null, p_i46739_1_);
   }

   public Potion(@Nullable String p_i46740_1_, EffectInstance... p_i46740_2_) {
      this.name = p_i46740_1_;
      this.effects = ImmutableList.copyOf(p_i46740_2_);
   }

   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   public boolean isIn(net.minecraft.tags.ITag<Potion> tag) {
      return tag.contains(this);
   }

   public String getName(String p_185174_1_) {
      return p_185174_1_ + (this.name == null ? Registry.POTION.getKey(this).getPath() : this.name);
   }

   public List<EffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffects() {
      if (!this.effects.isEmpty()) {
         for(EffectInstance effectinstance : this.effects) {
            if (effectinstance.getEffect().isInstantenous()) {
               return true;
            }
         }
      }

      return false;
   }
}
