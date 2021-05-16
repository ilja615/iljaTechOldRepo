package net.minecraft.advancements;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
   private final Set<Advancement> roots = Sets.newLinkedHashSet();
   private final Set<Advancement> tasks = Sets.newLinkedHashSet();
   private AdvancementList.IListener listener;

   @OnlyIn(Dist.CLIENT)
   private void remove(Advancement p_192090_1_) {
      for(Advancement advancement : p_192090_1_.getChildren()) {
         this.remove(advancement);
      }

      LOGGER.info("Forgot about advancement {}", (Object)p_192090_1_.getId());
      this.advancements.remove(p_192090_1_.getId());
      if (p_192090_1_.getParent() == null) {
         this.roots.remove(p_192090_1_);
         if (this.listener != null) {
            this.listener.onRemoveAdvancementRoot(p_192090_1_);
         }
      } else {
         this.tasks.remove(p_192090_1_);
         if (this.listener != null) {
            this.listener.onRemoveAdvancementTask(p_192090_1_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void remove(Set<ResourceLocation> p_192085_1_) {
      for(ResourceLocation resourcelocation : p_192085_1_) {
         Advancement advancement = this.advancements.get(resourcelocation);
         if (advancement == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)resourcelocation);
         } else {
            this.remove(advancement);
         }
      }

   }

   public void add(Map<ResourceLocation, Advancement.Builder> p_192083_1_) {
      Function<ResourceLocation, Advancement> function = Functions.forMap(this.advancements, (Advancement)null);

      while(!p_192083_1_.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, Advancement.Builder>> iterator = p_192083_1_.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Advancement.Builder> entry = iterator.next();
            ResourceLocation resourcelocation = entry.getKey();
            Advancement.Builder advancement$builder = entry.getValue();
            if (advancement$builder.canBuild(function)) {
               Advancement advancement = advancement$builder.build(resourcelocation);
               this.advancements.put(resourcelocation, advancement);
               flag = true;
               iterator.remove();
               if (advancement.getParent() == null) {
                  this.roots.add(advancement);
                  if (this.listener != null) {
                     this.listener.onAddAdvancementRoot(advancement);
                  }
               } else {
                  this.tasks.add(advancement);
                  if (this.listener != null) {
                     this.listener.onAddAdvancementTask(advancement);
                  }
               }
            }
         }

         if (!flag) {
            for(Entry<ResourceLocation, Advancement.Builder> entry1 : p_192083_1_.entrySet()) {
               LOGGER.error("Couldn't load advancement {}: {}", entry1.getKey(), entry1.getValue());
            }
            break;
         }
      }

      net.minecraftforge.common.AdvancementLoadFix.buildSortedTrees(this.roots);
      LOGGER.info("Loaded {} advancements", (int)this.advancements.size());
   }

   @OnlyIn(Dist.CLIENT)
   public void clear() {
      this.advancements.clear();
      this.roots.clear();
      this.tasks.clear();
      if (this.listener != null) {
         this.listener.onAdvancementsCleared();
      }

   }

   public Iterable<Advancement> getRoots() {
      return this.roots;
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.advancements.values();
   }

   @Nullable
   public Advancement get(ResourceLocation p_192084_1_) {
      return this.advancements.get(p_192084_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setListener(@Nullable AdvancementList.IListener p_192086_1_) {
      this.listener = p_192086_1_;
      if (p_192086_1_ != null) {
         for(Advancement advancement : this.roots) {
            p_192086_1_.onAddAdvancementRoot(advancement);
         }

         for(Advancement advancement1 : this.tasks) {
            p_192086_1_.onAddAdvancementTask(advancement1);
         }
      }

   }

   public interface IListener {
      void onAddAdvancementRoot(Advancement p_191931_1_);

      @OnlyIn(Dist.CLIENT)
      void onRemoveAdvancementRoot(Advancement p_191928_1_);

      void onAddAdvancementTask(Advancement p_191932_1_);

      @OnlyIn(Dist.CLIENT)
      void onRemoveAdvancementTask(Advancement p_191929_1_);

      @OnlyIn(Dist.CLIENT)
      void onAdvancementsCleared();
   }
}
