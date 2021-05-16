package net.minecraft.resources;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ResourcePackList implements AutoCloseable {
   private final Set<IPackFinder> sources;
   private Map<String, ResourcePackInfo> available = ImmutableMap.of();
   private List<ResourcePackInfo> selected = ImmutableList.of();
   private final ResourcePackInfo.IFactory constructor;

   public ResourcePackList(ResourcePackInfo.IFactory p_i231423_1_, IPackFinder... p_i231423_2_) {
      this.constructor = p_i231423_1_;
      this.sources = new java.util.HashSet<>(java.util.Arrays.asList(p_i231423_2_));
   }

   public ResourcePackList(IPackFinder... p_i241886_1_) {
      this(ResourcePackInfo::new, p_i241886_1_);
   }

   public void reload() {
      List<String> list = this.selected.stream().map(ResourcePackInfo::getId).collect(ImmutableList.toImmutableList());
      this.close();
      this.available = this.discoverAvailable();
      this.selected = this.rebuildSelected(list);
   }

   private Map<String, ResourcePackInfo> discoverAvailable() {
      Map<String, ResourcePackInfo> map = Maps.newTreeMap();

      for(IPackFinder ipackfinder : this.sources) {
         ipackfinder.loadPacks((p_232615_1_) -> {
            ResourcePackInfo resourcepackinfo = map.put(p_232615_1_.getId(), p_232615_1_);
         }, this.constructor);
      }

      return ImmutableMap.copyOf(map);
   }

   public void setSelected(Collection<String> p_198985_1_) {
      this.selected = this.rebuildSelected(p_198985_1_);
   }

   private List<ResourcePackInfo> rebuildSelected(Collection<String> p_232618_1_) {
      List<ResourcePackInfo> list = this.getAvailablePacks(p_232618_1_).collect(Collectors.toList());

      for(ResourcePackInfo resourcepackinfo : this.available.values()) {
         if (resourcepackinfo.isRequired() && !list.contains(resourcepackinfo)) {
            resourcepackinfo.getDefaultPosition().insert(list, resourcepackinfo, Functions.identity(), false);
         }
      }

      return ImmutableList.copyOf(list);
   }

   private Stream<ResourcePackInfo> getAvailablePacks(Collection<String> p_232620_1_) {
      return p_232620_1_.stream().map(this.available::get).filter(Objects::nonNull);
   }

   public Collection<String> getAvailableIds() {
      return this.available.keySet();
   }

   public Collection<ResourcePackInfo> getAvailablePacks() {
      return this.available.values();
   }

   public Collection<String> getSelectedIds() {
      return this.selected.stream().map(ResourcePackInfo::getId).collect(ImmutableSet.toImmutableSet());
   }

   public Collection<ResourcePackInfo> getSelectedPacks() {
      return this.selected;
   }

   @Nullable
   public ResourcePackInfo getPack(String p_198981_1_) {
      return this.available.get(p_198981_1_);
   }

   public void addPackFinder(IPackFinder packFinder) {
      this.sources.add(packFinder);
   }

   public void close() {
      this.available.values().forEach(ResourcePackInfo::close);
   }

   public boolean isAvailable(String p_232617_1_) {
      return this.available.containsKey(p_232617_1_);
   }

   public List<IResourcePack> openAllSelected() {
      return this.selected.stream().map(ResourcePackInfo::open).collect(ImmutableList.toImmutableList());
   }
}
