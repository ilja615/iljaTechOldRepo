package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BannerTileEntity extends TileEntity implements INameable {
   @Nullable
   private ITextComponent name;
   @Nullable
   private DyeColor baseColor = DyeColor.WHITE;
   @Nullable
   private ListNBT itemPatterns;
   private boolean receivedData;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> patterns;

   public BannerTileEntity() {
      super(TileEntityType.BANNER);
   }

   public BannerTileEntity(DyeColor p_i47731_1_) {
      this();
      this.baseColor = p_i47731_1_;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static ListNBT getItemPatterns(ItemStack p_230139_0_) {
      ListNBT listnbt = null;
      CompoundNBT compoundnbt = p_230139_0_.getTagElement("BlockEntityTag");
      if (compoundnbt != null && compoundnbt.contains("Patterns", 9)) {
         listnbt = compoundnbt.getList("Patterns", 10).copy();
      }

      return listnbt;
   }

   @OnlyIn(Dist.CLIENT)
   public void fromItem(ItemStack p_195534_1_, DyeColor p_195534_2_) {
      this.itemPatterns = getItemPatterns(p_195534_1_);
      this.baseColor = p_195534_2_;
      this.patterns = null;
      this.receivedData = true;
      this.name = p_195534_1_.hasCustomHoverName() ? p_195534_1_.getHoverName() : null;
   }

   public ITextComponent getName() {
      return (ITextComponent)(this.name != null ? this.name : new TranslationTextComponent("block.minecraft.banner"));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.name;
   }

   public void setCustomName(ITextComponent p_213136_1_) {
      this.name = p_213136_1_;
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (this.itemPatterns != null) {
         p_189515_1_.put("Patterns", this.itemPatterns);
      }

      if (this.name != null) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      return p_189515_1_;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      if (p_230337_2_.contains("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(p_230337_2_.getString("CustomName"));
      }

      if (this.hasLevel()) {
         this.baseColor = ((AbstractBannerBlock)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.itemPatterns = p_230337_2_.getList("Patterns", 10);
      this.patterns = null;
      this.receivedData = true;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 6, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   public static int getPatternCount(ItemStack p_175113_0_) {
      CompoundNBT compoundnbt = p_175113_0_.getTagElement("BlockEntityTag");
      return compoundnbt != null && compoundnbt.contains("Patterns") ? compoundnbt.getList("Patterns", 10).size() : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Pair<BannerPattern, DyeColor>> getPatterns() {
      if (this.patterns == null && this.receivedData) {
         this.patterns = createPatterns(this.getBaseColor(this::getBlockState), this.itemPatterns);
      }

      return this.patterns;
   }

   @OnlyIn(Dist.CLIENT)
   public static List<Pair<BannerPattern, DyeColor>> createPatterns(DyeColor p_230138_0_, @Nullable ListNBT p_230138_1_) {
      List<Pair<BannerPattern, DyeColor>> list = Lists.newArrayList();
      list.add(Pair.of(BannerPattern.BASE, p_230138_0_));
      if (p_230138_1_ != null) {
         for(int i = 0; i < p_230138_1_.size(); ++i) {
            CompoundNBT compoundnbt = p_230138_1_.getCompound(i);
            BannerPattern bannerpattern = BannerPattern.byHash(compoundnbt.getString("Pattern"));
            if (bannerpattern != null) {
               int j = compoundnbt.getInt("Color");
               list.add(Pair.of(bannerpattern, DyeColor.byId(j)));
            }
         }
      }

      return list;
   }

   public static void removeLastPattern(ItemStack p_175117_0_) {
      CompoundNBT compoundnbt = p_175117_0_.getTagElement("BlockEntityTag");
      if (compoundnbt != null && compoundnbt.contains("Patterns", 9)) {
         ListNBT listnbt = compoundnbt.getList("Patterns", 10);
         if (!listnbt.isEmpty()) {
            listnbt.remove(listnbt.size() - 1);
            if (listnbt.isEmpty()) {
               p_175117_0_.removeTagKey("BlockEntityTag");
            }

         }
      }
   }

   public ItemStack getItem(BlockState p_190615_1_) {
      ItemStack itemstack = new ItemStack(BannerBlock.byColor(this.getBaseColor(() -> {
         return p_190615_1_;
      })));
      if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
         itemstack.getOrCreateTagElement("BlockEntityTag").put("Patterns", this.itemPatterns.copy());
      }

      if (this.name != null) {
         itemstack.setHoverName(this.name);
      }

      return itemstack;
   }

   public DyeColor getBaseColor(Supplier<BlockState> p_195533_1_) {
      if (this.baseColor == null) {
         this.baseColor = ((AbstractBannerBlock)p_195533_1_.get().getBlock()).getColor();
      }

      return this.baseColor;
   }
}
