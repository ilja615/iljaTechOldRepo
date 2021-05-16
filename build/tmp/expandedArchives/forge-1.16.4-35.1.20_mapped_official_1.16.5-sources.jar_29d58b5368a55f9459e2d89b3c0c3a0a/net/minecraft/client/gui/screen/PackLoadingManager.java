package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PackLoadingManager {
   private final ResourcePackList repository;
   private final List<ResourcePackInfo> selected;
   private final List<ResourcePackInfo> unselected;
   private final Function<ResourcePackInfo, ResourceLocation> iconGetter;
   private final Runnable onListChanged;
   private final Consumer<ResourcePackList> output;

   public PackLoadingManager(Runnable p_i242059_1_, Function<ResourcePackInfo, ResourceLocation> p_i242059_2_, ResourcePackList p_i242059_3_, Consumer<ResourcePackList> p_i242059_4_) {
      this.onListChanged = p_i242059_1_;
      this.iconGetter = p_i242059_2_;
      this.repository = p_i242059_3_;
      this.selected = Lists.newArrayList(p_i242059_3_.getSelectedPacks());
      Collections.reverse(this.selected);
      this.unselected = Lists.newArrayList(p_i242059_3_.getAvailablePacks());
      this.unselected.removeAll(this.selected);
      this.output = p_i242059_4_;
   }

   public Stream<PackLoadingManager.IPack> getUnselected() {
      return this.unselected.stream().map((p_238870_1_) -> {
         return new PackLoadingManager.DisabledPack(p_238870_1_);
      });
   }

   public Stream<PackLoadingManager.IPack> getSelected() {
      return this.selected.stream().map((p_238866_1_) -> {
         return new PackLoadingManager.EnabledPack(p_238866_1_);
      });
   }

   public void commit() {
      this.repository.setSelected(Lists.reverse(this.selected).stream().map(ResourcePackInfo::getId).collect(ImmutableList.toImmutableList()));
      this.output.accept(this.repository);
   }

   public void findNewPacks() {
      this.repository.reload();
      this.selected.retainAll(this.repository.getAvailablePacks());
      this.unselected.clear();
      this.unselected.addAll(this.repository.getAvailablePacks());
      this.unselected.removeAll(this.selected);
   }

   @OnlyIn(Dist.CLIENT)
   abstract class AbstractPack implements PackLoadingManager.IPack {
      private final ResourcePackInfo pack;

      public AbstractPack(ResourcePackInfo p_i232297_2_) {
         this.pack = p_i232297_2_;
      }

      protected abstract List<ResourcePackInfo> getSelfList();

      protected abstract List<ResourcePackInfo> getOtherList();

      public ResourceLocation getIconTexture() {
         return PackLoadingManager.this.iconGetter.apply(this.pack);
      }

      public PackCompatibility getCompatibility() {
         return this.pack.getCompatibility();
      }

      public ITextComponent getTitle() {
         return this.pack.getTitle();
      }

      public ITextComponent getDescription() {
         return this.pack.getDescription();
      }

      public IPackNameDecorator getPackSource() {
         return this.pack.getPackSource();
      }

      public boolean isFixedPosition() {
         return this.pack.isFixedPosition();
      }

      public boolean isRequired() {
         return this.pack.isRequired();
      }

      protected void toggleSelection() {
         this.getSelfList().remove(this.pack);
         this.pack.getDefaultPosition().insert(this.getOtherList(), this.pack, Function.identity(), true);
         PackLoadingManager.this.onListChanged.run();
      }

      protected void move(int p_238879_1_) {
         List<ResourcePackInfo> list = this.getSelfList();
         int i = list.indexOf(this.pack);
         list.remove(i);
         list.add(i + p_238879_1_, this.pack);
         PackLoadingManager.this.onListChanged.run();
      }

      public boolean canMoveUp() {
         List<ResourcePackInfo> list = this.getSelfList();
         int i = list.indexOf(this.pack);
         return i > 0 && !list.get(i - 1).isFixedPosition();
      }

      public void moveUp() {
         this.move(-1);
      }

      public boolean canMoveDown() {
         List<ResourcePackInfo> list = this.getSelfList();
         int i = list.indexOf(this.pack);
         return i >= 0 && i < list.size() - 1 && !list.get(i + 1).isFixedPosition();
      }

      public void moveDown() {
         this.move(1);
      }

      @Override
      public boolean notHidden() {
          return !pack.isHidden();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class DisabledPack extends PackLoadingManager.AbstractPack {
      public DisabledPack(ResourcePackInfo p_i232299_2_) {
         super(p_i232299_2_);
      }

      protected List<ResourcePackInfo> getSelfList() {
         return PackLoadingManager.this.unselected;
      }

      protected List<ResourcePackInfo> getOtherList() {
         return PackLoadingManager.this.selected;
      }

      public boolean isSelected() {
         return false;
      }

      public void select() {
         this.toggleSelection();
      }

      public void unselect() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   class EnabledPack extends PackLoadingManager.AbstractPack {
      public EnabledPack(ResourcePackInfo p_i232298_2_) {
         super(p_i232298_2_);
      }

      protected List<ResourcePackInfo> getSelfList() {
         return PackLoadingManager.this.selected;
      }

      protected List<ResourcePackInfo> getOtherList() {
         return PackLoadingManager.this.unselected;
      }

      public boolean isSelected() {
         return true;
      }

      public void select() {
      }

      public void unselect() {
         this.toggleSelection();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IPack {
      ResourceLocation getIconTexture();

      PackCompatibility getCompatibility();

      ITextComponent getTitle();

      ITextComponent getDescription();

      IPackNameDecorator getPackSource();

      default ITextComponent getExtendedDescription() {
         return this.getPackSource().decorate(this.getDescription());
      }

      boolean isFixedPosition();

      boolean isRequired();

      void select();

      void unselect();

      void moveUp();

      void moveDown();

      boolean isSelected();

      default boolean canSelect() {
         return !this.isSelected();
      }

      default boolean canUnselect() {
         return this.isSelected() && !this.isRequired();
      }

      boolean canMoveUp();

      boolean canMoveDown();

      default boolean notHidden() { return true; }
   }
}
