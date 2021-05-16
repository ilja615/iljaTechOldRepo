package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> implements net.minecraftforge.common.extensions.IForgeItemStack {
   public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create((p_234698_0_) -> {
      return p_234698_0_.group(Registry.ITEM.fieldOf("id").forGetter((p_234706_0_) -> {
         return p_234706_0_.item;
      }), Codec.INT.fieldOf("Count").forGetter((p_234705_0_) -> {
         return p_234705_0_.count;
      }), CompoundNBT.CODEC.optionalFieldOf("tag").forGetter((p_234704_0_) -> {
         return Optional.ofNullable(p_234704_0_.tag);
      })).apply(p_234698_0_, ItemStack::new);
   });
   private net.minecraftforge.registries.IRegistryDelegate<Item> delegate;
   private CompoundNBT capNBT;

   private static final Logger LOGGER = LogManager.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Item)null);
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), (p_234699_0_) -> {
      p_234699_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private static final Style LORE_STYLE = Style.EMPTY.withColor(TextFormatting.DARK_PURPLE).withItalic(true);
   private int count;
   private int popTime;
   @Deprecated
   private final Item item;
   private CompoundNBT tag;
   private boolean emptyCacheFlag;
   private Entity entityRepresentation;
   private CachedBlockInfo cachedBreakBlock;
   private boolean cachedBreakBlockResult;
   private CachedBlockInfo cachedPlaceBlock;
   private boolean cachedPlaceBlockResult;

   public ItemStack(IItemProvider p_i48203_1_) {
      this(p_i48203_1_, 1);
   }

   private ItemStack(IItemProvider p_i231596_1_, int p_i231596_2_, Optional<CompoundNBT> p_i231596_3_) {
      this(p_i231596_1_, p_i231596_2_);
      p_i231596_3_.ifPresent(this::setTag);
   }

   public ItemStack(IItemProvider p_i48204_1_, int p_i48204_2_) { this(p_i48204_1_, p_i48204_2_, (CompoundNBT) null); }
   public ItemStack(IItemProvider p_i48204_1_, int p_i48204_2_, @Nullable CompoundNBT capNBT) {
      super(ItemStack.class);
      this.capNBT = capNBT;
      this.item = p_i48204_1_ == null ? null : p_i48204_1_.asItem();
      this.count = p_i48204_2_;
      if (this.item != null && this.item.isDamageable(this)) {
         this.setDamageValue(this.getDamageValue());
      }

      this.updateEmptyCacheFlag();
      this.forgeInit();
   }

   private void updateEmptyCacheFlag() {
      this.emptyCacheFlag = false;
      this.emptyCacheFlag = this.isEmpty();
   }

   private ItemStack(CompoundNBT p_i47263_1_) {
      super(ItemStack.class);
      this.capNBT = p_i47263_1_.contains("ForgeCaps") ? p_i47263_1_.getCompound("ForgeCaps") : null;
      this.item = Registry.ITEM.get(new ResourceLocation(p_i47263_1_.getString("id")));
      this.count = p_i47263_1_.getByte("Count");
      if (p_i47263_1_.contains("tag", 10)) {
         this.tag = p_i47263_1_.getCompound("tag");
         this.getItem().verifyTagAfterLoad(p_i47263_1_);
      }

      if (this.getItem().isDamageable(this)) {
         this.setDamageValue(this.getDamageValue());
      }

      this.updateEmptyCacheFlag();
      this.forgeInit();
   }

   public static ItemStack of(CompoundNBT p_199557_0_) {
      try {
         return new ItemStack(p_199557_0_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.debug("Tried to load invalid item: {}", p_199557_0_, runtimeexception);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      if (this == EMPTY) {
         return true;
      } else if (this.getItemRaw() != null && this.getItemRaw() != Items.AIR) {
         return this.count <= 0;
      } else {
         return true;
      }
   }

   public ItemStack split(int p_77979_1_) {
      int i = Math.min(p_77979_1_, this.count);
      ItemStack itemstack = this.copy();
      itemstack.setCount(i);
      this.shrink(i);
      return itemstack;
   }

   public Item getItem() {
      return this.emptyCacheFlag || this.delegate == null ? Items.AIR : this.delegate.get();
   }

   public ActionResultType useOn(ItemUseContext p_196084_1_) {
      if (!p_196084_1_.getLevel().isClientSide) return net.minecraftforge.common.ForgeHooks.onPlaceItemIntoWorld(p_196084_1_);
      return onItemUse(p_196084_1_, (c) -> getItem().useOn(p_196084_1_));
   }

   public ActionResultType onItemUseFirst(ItemUseContext context) {
      return onItemUse(context, (c) -> getItem().onItemUseFirst(this, context));
   }

   private ActionResultType onItemUse(ItemUseContext p_196084_1_, java.util.function.Function<ItemUseContext, ActionResultType> callback) {
      PlayerEntity playerentity = p_196084_1_.getPlayer();
      BlockPos blockpos = p_196084_1_.getClickedPos();
      CachedBlockInfo cachedblockinfo = new CachedBlockInfo(p_196084_1_.getLevel(), blockpos, false);
      if (playerentity != null && !playerentity.abilities.mayBuild && !this.hasAdventureModePlaceTagForBlock(p_196084_1_.getLevel().getTagManager(), cachedblockinfo)) {
         return ActionResultType.PASS;
      } else {
         Item item = this.getItem();
         ActionResultType actionresulttype = callback.apply(p_196084_1_);
         if (playerentity != null && actionresulttype.consumesAction()) {
            playerentity.awardStat(Stats.ITEM_USED.get(item));
         }

         return actionresulttype;
      }
   }

   public float getDestroySpeed(BlockState p_150997_1_) {
      return this.getItem().getDestroySpeed(this, p_150997_1_);
   }

   public ActionResult<ItemStack> use(World p_77957_1_, PlayerEntity p_77957_2_, Hand p_77957_3_) {
      return this.getItem().use(p_77957_1_, p_77957_2_, p_77957_3_);
   }

   public ItemStack finishUsingItem(World p_77950_1_, LivingEntity p_77950_2_) {
      return this.getItem().finishUsingItem(this, p_77950_1_, p_77950_2_);
   }

   public CompoundNBT save(CompoundNBT p_77955_1_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.getItem());
      p_77955_1_.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      p_77955_1_.putByte("Count", (byte)this.count);
      if (this.tag != null) {
         p_77955_1_.put("tag", this.tag.copy());
      }
      CompoundNBT cnbt = this.serializeCaps();
      if (cnbt != null && !cnbt.isEmpty()) {
         p_77955_1_.put("ForgeCaps", cnbt);
      }
      return p_77955_1_;
   }

   public int getMaxStackSize() {
      return this.getItem().getItemStackLimit(this);
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
   }

   public boolean isDamageableItem() {
      if (!this.emptyCacheFlag && this.getItem().isDamageable(this)) {
         CompoundNBT compoundnbt = this.getTag();
         return compoundnbt == null || !compoundnbt.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageableItem() && getItem().isDamaged(this);
   }

   public int getDamageValue() {
      return this.getItem().getDamage(this);
   }

   public void setDamageValue(int p_196085_1_) {
      this.getItem().setDamage(this, p_196085_1_);
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage(this);
   }

   public boolean hurt(int p_96631_1_, Random p_96631_2_, @Nullable ServerPlayerEntity p_96631_3_) {
      if (!this.isDamageableItem()) {
         return false;
      } else {
         if (p_96631_1_ > 0) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int j = 0;

            for(int k = 0; i > 0 && k < p_96631_1_; ++k) {
               if (UnbreakingEnchantment.shouldIgnoreDurabilityDrop(this, i, p_96631_2_)) {
                  ++j;
               }
            }

            p_96631_1_ -= j;
            if (p_96631_1_ <= 0) {
               return false;
            }
         }

         if (p_96631_3_ != null && p_96631_1_ != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(p_96631_3_, this, this.getDamageValue() + p_96631_1_);
         }

         int l = this.getDamageValue() + p_96631_1_;
         this.setDamageValue(l);
         return l >= this.getMaxDamage();
      }
   }

   public <T extends LivingEntity> void hurtAndBreak(int p_222118_1_, T p_222118_2_, Consumer<T> p_222118_3_) {
      if (!p_222118_2_.level.isClientSide && (!(p_222118_2_ instanceof PlayerEntity) || !((PlayerEntity)p_222118_2_).abilities.instabuild)) {
         if (this.isDamageableItem()) {
            p_222118_1_ = this.getItem().damageItem(this, p_222118_1_, p_222118_2_, p_222118_3_);
            if (this.hurt(p_222118_1_, p_222118_2_.getRandom(), p_222118_2_ instanceof ServerPlayerEntity ? (ServerPlayerEntity)p_222118_2_ : null)) {
               p_222118_3_.accept(p_222118_2_);
               Item item = this.getItem();
               this.shrink(1);
               if (p_222118_2_ instanceof PlayerEntity) {
                  ((PlayerEntity)p_222118_2_).awardStat(Stats.ITEM_BROKEN.get(item));
               }

               this.setDamageValue(0);
            }

         }
      }
   }

   public void hurtEnemy(LivingEntity p_77961_1_, PlayerEntity p_77961_2_) {
      Item item = this.getItem();
      if (item.hurtEnemy(this, p_77961_1_, p_77961_2_)) {
         p_77961_2_.awardStat(Stats.ITEM_USED.get(item));
      }

   }

   public void mineBlock(World p_179548_1_, BlockState p_179548_2_, BlockPos p_179548_3_, PlayerEntity p_179548_4_) {
      Item item = this.getItem();
      if (item.mineBlock(this, p_179548_1_, p_179548_2_, p_179548_3_, p_179548_4_)) {
         p_179548_4_.awardStat(Stats.ITEM_USED.get(item));
      }

   }

   public boolean isCorrectToolForDrops(BlockState p_150998_1_) {
      return this.getItem().canHarvestBlock(this, p_150998_1_);
   }

   public ActionResultType interactLivingEntity(PlayerEntity p_111282_1_, LivingEntity p_111282_2_, Hand p_111282_3_) {
      return this.getItem().interactLivingEntity(this, p_111282_1_, p_111282_2_, p_111282_3_);
   }

   public ItemStack copy() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack itemstack = new ItemStack(this.getItem(), this.count, this.serializeCaps());
         itemstack.setPopTime(this.getPopTime());
         if (this.tag != null) {
            itemstack.tag = this.tag.copy();
         }

         return itemstack;
      }
   }

   public static boolean tagMatches(ItemStack p_77970_0_, ItemStack p_77970_1_) {
      if (p_77970_0_.isEmpty() && p_77970_1_.isEmpty()) {
         return true;
      } else if (!p_77970_0_.isEmpty() && !p_77970_1_.isEmpty()) {
         if (p_77970_0_.tag == null && p_77970_1_.tag != null) {
            return false;
         } else {
            return (p_77970_0_.tag == null || p_77970_0_.tag.equals(p_77970_1_.tag)) && p_77970_0_.areCapsCompatible(p_77970_1_);
         }
      } else {
         return false;
      }
   }

   public static boolean matches(ItemStack p_77989_0_, ItemStack p_77989_1_) {
      if (p_77989_0_.isEmpty() && p_77989_1_.isEmpty()) {
         return true;
      } else {
         return !p_77989_0_.isEmpty() && !p_77989_1_.isEmpty() ? p_77989_0_.matches(p_77989_1_) : false;
      }
   }

   private boolean matches(ItemStack p_77959_1_) {
      if (this.count != p_77959_1_.count) {
         return false;
      } else if (this.getItem() != p_77959_1_.getItem()) {
         return false;
      } else if (this.tag == null && p_77959_1_.tag != null) {
         return false;
      } else {
         return (this.tag == null || this.tag.equals(p_77959_1_.tag)) && this.areCapsCompatible(p_77959_1_);
      }
   }

   public static boolean isSame(ItemStack p_179545_0_, ItemStack p_179545_1_) {
      if (p_179545_0_ == p_179545_1_) {
         return true;
      } else {
         return !p_179545_0_.isEmpty() && !p_179545_1_.isEmpty() ? p_179545_0_.sameItem(p_179545_1_) : false;
      }
   }

   public static boolean isSameIgnoreDurability(ItemStack p_185132_0_, ItemStack p_185132_1_) {
      if (p_185132_0_ == p_185132_1_) {
         return true;
      } else {
         return !p_185132_0_.isEmpty() && !p_185132_1_.isEmpty() ? p_185132_0_.sameItemStackIgnoreDurability(p_185132_1_) : false;
      }
   }

   public boolean sameItem(ItemStack p_77969_1_) {
      return !p_77969_1_.isEmpty() && this.getItem() == p_77969_1_.getItem();
   }

   public boolean sameItemStackIgnoreDurability(ItemStack p_185136_1_) {
      if (!this.isDamageableItem()) {
         return this.sameItem(p_185136_1_);
      } else {
         return !p_185136_1_.isEmpty() && this.getItem() == p_185136_1_.getItem();
      }
   }

   public String getDescriptionId() {
      return this.getItem().getDescriptionId(this);
   }

   public String toString() {
      return this.count + " " + this.getItem();
   }

   public void inventoryTick(World p_77945_1_, Entity p_77945_2_, int p_77945_3_, boolean p_77945_4_) {
      if (this.popTime > 0) {
         --this.popTime;
      }

      if (this.getItem() != null) {
         this.getItem().inventoryTick(this, p_77945_1_, p_77945_2_, p_77945_3_, p_77945_4_);
      }

   }

   public void onCraftedBy(World p_77980_1_, PlayerEntity p_77980_2_, int p_77980_3_) {
      p_77980_2_.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), p_77980_3_);
      this.getItem().onCraftedBy(this, p_77980_1_, p_77980_2_);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public UseAction getUseAnimation() {
      return this.getItem().getUseAnimation(this);
   }

   public void releaseUsing(World p_77974_1_, LivingEntity p_77974_2_, int p_77974_3_) {
      this.getItem().releaseUsing(this, p_77974_1_, p_77974_2_, p_77974_3_);
   }

   public boolean useOnRelease() {
      return this.getItem().useOnRelease(this);
   }

   public boolean hasTag() {
      return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public CompoundNBT getTag() {
      return this.tag;
   }

   public CompoundNBT getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new CompoundNBT());
      }

      return this.tag;
   }

   public CompoundNBT getOrCreateTagElement(String p_190925_1_) {
      if (this.tag != null && this.tag.contains(p_190925_1_, 10)) {
         return this.tag.getCompound(p_190925_1_);
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.addTagElement(p_190925_1_, compoundnbt);
         return compoundnbt;
      }
   }

   @Nullable
   public CompoundNBT getTagElement(String p_179543_1_) {
      return this.tag != null && this.tag.contains(p_179543_1_, 10) ? this.tag.getCompound(p_179543_1_) : null;
   }

   public void removeTagKey(String p_196083_1_) {
      if (this.tag != null && this.tag.contains(p_196083_1_)) {
         this.tag.remove(p_196083_1_);
         if (this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public ListNBT getEnchantmentTags() {
      return this.tag != null ? this.tag.getList("Enchantments", 10) : new ListNBT();
   }

   public void setTag(@Nullable CompoundNBT p_77982_1_) {
      this.tag = p_77982_1_;
      if (this.getItem().isDamageable(this)) {
         this.setDamageValue(this.getDamageValue());
      }

   }

   public ITextComponent getHoverName() {
      CompoundNBT compoundnbt = this.getTagElement("display");
      if (compoundnbt != null && compoundnbt.contains("Name", 8)) {
         try {
            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("Name"));
            if (itextcomponent != null) {
               return itextcomponent;
            }

            compoundnbt.remove("Name");
         } catch (JsonParseException jsonparseexception) {
            compoundnbt.remove("Name");
         }
      }

      return this.getItem().getName(this);
   }

   public ItemStack setHoverName(@Nullable ITextComponent p_200302_1_) {
      CompoundNBT compoundnbt = this.getOrCreateTagElement("display");
      if (p_200302_1_ != null) {
         compoundnbt.putString("Name", ITextComponent.Serializer.toJson(p_200302_1_));
      } else {
         compoundnbt.remove("Name");
      }

      return this;
   }

   public void resetHoverName() {
      CompoundNBT compoundnbt = this.getTagElement("display");
      if (compoundnbt != null) {
         compoundnbt.remove("Name");
         if (compoundnbt.isEmpty()) {
            this.removeTagKey("display");
         }
      }

      if (this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasCustomHoverName() {
      CompoundNBT compoundnbt = this.getTagElement("display");
      return compoundnbt != null && compoundnbt.contains("Name", 8);
   }

   @OnlyIn(Dist.CLIENT)
   public List<ITextComponent> getTooltipLines(@Nullable PlayerEntity p_82840_1_, ITooltipFlag p_82840_2_) {
      List<ITextComponent> list = Lists.newArrayList();
      IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("")).append(this.getHoverName()).withStyle(this.getRarity().color);
      if (this.hasCustomHoverName()) {
         iformattabletextcomponent.withStyle(TextFormatting.ITALIC);
      }

      list.add(iformattabletextcomponent);
      if (!p_82840_2_.isAdvanced() && !this.hasCustomHoverName() && this.getItem() == Items.FILLED_MAP) {
         list.add((new StringTextComponent("#" + FilledMapItem.getMapId(this))).withStyle(TextFormatting.GRAY));
      }

      int i = this.getHideFlags();
      if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.ADDITIONAL)) {
         this.getItem().appendHoverText(this, p_82840_1_ == null ? null : p_82840_1_.level, list, p_82840_2_);
      }

      if (this.hasTag()) {
         if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
            appendEnchantmentNames(list, this.getEnchantmentTags());
         }

         if (this.tag.contains("display", 10)) {
            CompoundNBT compoundnbt = this.tag.getCompound("display");
            if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.DYE) && compoundnbt.contains("color", 99)) {
               if (p_82840_2_.isAdvanced()) {
                  list.add((new TranslationTextComponent("item.color", String.format("#%06X", compoundnbt.getInt("color")))).withStyle(TextFormatting.GRAY));
               } else {
                  list.add((new TranslationTextComponent("item.dyed")).withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
               }
            }

            if (compoundnbt.getTagType("Lore") == 9) {
               ListNBT listnbt = compoundnbt.getList("Lore", 8);

               for(int j = 0; j < listnbt.size(); ++j) {
                  String s = listnbt.getString(j);

                  try {
                     IFormattableTextComponent iformattabletextcomponent1 = ITextComponent.Serializer.fromJson(s);
                     if (iformattabletextcomponent1 != null) {
                        list.add(TextComponentUtils.mergeStyles(iformattabletextcomponent1, LORE_STYLE));
                     }
                  } catch (JsonParseException jsonparseexception) {
                     compoundnbt.remove("Lore");
                  }
               }
            }
         }
      }

      if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.MODIFIERS)) {
         for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            Multimap<Attribute, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslottype);
            if (!multimap.isEmpty()) {
               list.add(StringTextComponent.EMPTY);
               list.add((new TranslationTextComponent("item.modifiers." + equipmentslottype.getName())).withStyle(TextFormatting.GRAY));

               for(Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                  AttributeModifier attributemodifier = entry.getValue();
                  double d0 = attributemodifier.getAmount();
                  boolean flag = false;
                  if (p_82840_1_ != null) {
                     if (attributemodifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                        d0 = d0 + p_82840_1_.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                        d0 = d0 + (double)EnchantmentHelper.getDamageBonus(this, CreatureAttribute.UNDEFINED);
                        flag = true;
                     } else if (attributemodifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                        d0 += p_82840_1_.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                        flag = true;
                     }
                  }

                  double d1;
                  if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                     if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                        d1 = d0 * 10.0D;
                     } else {
                        d1 = d0;
                     }
                  } else {
                     d1 = d0 * 100.0D;
                  }

                  if (flag) {
                     list.add((new StringTextComponent(" ")).append(new TranslationTextComponent("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.DARK_GREEN));
                  } else if (d0 > 0.0D) {
                     list.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.BLUE));
                  } else if (d0 < 0.0D) {
                     d1 = d1 * -1.0D;
                     list.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.RED));
                  }
               }
            }
         }
      }

      if (this.hasTag()) {
         if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
            list.add((new TranslationTextComponent("item.unbreakable")).withStyle(TextFormatting.BLUE));
         }

         if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
            ListNBT listnbt1 = this.tag.getList("CanDestroy", 8);
            if (!listnbt1.isEmpty()) {
               list.add(StringTextComponent.EMPTY);
               list.add((new TranslationTextComponent("item.canBreak")).withStyle(TextFormatting.GRAY));

               for(int k = 0; k < listnbt1.size(); ++k) {
                  list.addAll(expandBlockState(listnbt1.getString(k)));
               }
            }
         }

         if (shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
            ListNBT listnbt2 = this.tag.getList("CanPlaceOn", 8);
            if (!listnbt2.isEmpty()) {
               list.add(StringTextComponent.EMPTY);
               list.add((new TranslationTextComponent("item.canPlace")).withStyle(TextFormatting.GRAY));

               for(int l = 0; l < listnbt2.size(); ++l) {
                  list.addAll(expandBlockState(listnbt2.getString(l)));
               }
            }
         }
      }

      if (p_82840_2_.isAdvanced()) {
         if (this.isDamaged()) {
            list.add(new TranslationTextComponent("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
         }

         list.add((new StringTextComponent(Registry.ITEM.getKey(this.getItem()).toString())).withStyle(TextFormatting.DARK_GRAY));
         if (this.hasTag()) {
            list.add((new TranslationTextComponent("item.nbt_tags", this.tag.getAllKeys().size())).withStyle(TextFormatting.DARK_GRAY));
         }
      }

      net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this, p_82840_1_, list, p_82840_2_);
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   private static boolean shouldShowInTooltip(int p_242394_0_, ItemStack.TooltipDisplayFlags p_242394_1_) {
      return (p_242394_0_ & p_242394_1_.getMask()) == 0;
   }

   @OnlyIn(Dist.CLIENT)
   private int getHideFlags() {
      return this.hasTag() && this.tag.contains("HideFlags", 99) ? this.tag.getInt("HideFlags") : 0;
   }

   public void hideTooltipPart(ItemStack.TooltipDisplayFlags p_242395_1_) {
      CompoundNBT compoundnbt = this.getOrCreateTag();
      compoundnbt.putInt("HideFlags", compoundnbt.getInt("HideFlags") | p_242395_1_.getMask());
   }

   @OnlyIn(Dist.CLIENT)
   public static void appendEnchantmentNames(List<ITextComponent> p_222120_0_, ListNBT p_222120_1_) {
      for(int i = 0; i < p_222120_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_222120_1_.getCompound(i);
         Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(compoundnbt.getString("id"))).ifPresent((p_222123_2_) -> {
            p_222120_0_.add(p_222123_2_.getFullname(compoundnbt.getInt("lvl")));
         });
      }

   }

   @OnlyIn(Dist.CLIENT)
   private static Collection<ITextComponent> expandBlockState(String p_206845_0_) {
      try {
         BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(p_206845_0_), true)).parse(true);
         BlockState blockstate = blockstateparser.getState();
         ResourceLocation resourcelocation = blockstateparser.getTag();
         boolean flag = blockstate != null;
         boolean flag1 = resourcelocation != null;
         if (flag || flag1) {
            if (flag) {
               return Lists.newArrayList(blockstate.getBlock().getName().withStyle(TextFormatting.DARK_GRAY));
            }

            ITag<Block> itag = BlockTags.getAllTags().getTag(resourcelocation);
            if (itag != null) {
               Collection<Block> collection = itag.getValues();
               if (!collection.isEmpty()) {
                  return collection.stream().map(Block::getName).map((p_222119_0_) -> {
                     return p_222119_0_.withStyle(TextFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return Lists.newArrayList((new StringTextComponent("missingno")).withStyle(TextFormatting.DARK_GRAY));
   }

   public boolean hasFoil() {
      return this.getItem().isFoil(this);
   }

   public Rarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.isEnchanted();
      }
   }

   public void enchant(Enchantment p_77966_1_, int p_77966_2_) {
      this.getOrCreateTag();
      if (!this.tag.contains("Enchantments", 9)) {
         this.tag.put("Enchantments", new ListNBT());
      }

      ListNBT listnbt = this.tag.getList("Enchantments", 10);
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("id", String.valueOf((Object)Registry.ENCHANTMENT.getKey(p_77966_1_)));
      compoundnbt.putShort("lvl", (short)((byte)p_77966_2_));
      listnbt.add(compoundnbt);
   }

   public boolean isEnchanted() {
      if (this.tag != null && this.tag.contains("Enchantments", 9)) {
         return !this.tag.getList("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void addTagElement(String p_77983_1_, INBT p_77983_2_) {
      this.getOrCreateTag().put(p_77983_1_, p_77983_2_);
   }

   public boolean isFramed() {
      return this.entityRepresentation instanceof ItemFrameEntity;
   }

   public void setEntityRepresentation(@Nullable Entity p_234695_1_) {
      this.entityRepresentation = p_234695_1_;
   }

   @Nullable
   public ItemFrameEntity getFrame() {
      return this.entityRepresentation instanceof ItemFrameEntity ? (ItemFrameEntity)this.getEntityRepresentation() : null;
   }

   @Nullable
   public Entity getEntityRepresentation() {
      return !this.emptyCacheFlag ? this.entityRepresentation : null;
   }

   public int getBaseRepairCost() {
      return this.hasTag() && this.tag.contains("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
   }

   public void setRepairCost(int p_82841_1_) {
      this.getOrCreateTag().putInt("RepairCost", p_82841_1_);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111283_1_) {
      Multimap<Attribute, AttributeModifier> multimap;
      if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         ListNBT listnbt = this.tag.getList("AttributeModifiers", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            if (!compoundnbt.contains("Slot", 8) || compoundnbt.getString("Slot").equals(p_111283_1_.getName())) {
               Optional<Attribute> optional = Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundnbt.getString("AttributeName")));
               if (optional.isPresent()) {
                  AttributeModifier attributemodifier = AttributeModifier.load(compoundnbt);
                  if (attributemodifier != null && attributemodifier.getId().getLeastSignificantBits() != 0L && attributemodifier.getId().getMostSignificantBits() != 0L) {
                     multimap.put(optional.get(), attributemodifier);
                  }
               }
            }
         }
      } else {
         multimap = this.getItem().getAttributeModifiers(p_111283_1_, this);
      }

      multimap = net.minecraftforge.common.ForgeHooks.getAttributeModifiers(this, p_111283_1_, multimap);
      return multimap;
   }

   public void addAttributeModifier(Attribute p_185129_1_, AttributeModifier p_185129_2_, @Nullable EquipmentSlotType p_185129_3_) {
      this.getOrCreateTag();
      if (!this.tag.contains("AttributeModifiers", 9)) {
         this.tag.put("AttributeModifiers", new ListNBT());
      }

      ListNBT listnbt = this.tag.getList("AttributeModifiers", 10);
      CompoundNBT compoundnbt = p_185129_2_.save();
      compoundnbt.putString("AttributeName", Registry.ATTRIBUTE.getKey(p_185129_1_).toString());
      if (p_185129_3_ != null) {
         compoundnbt.putString("Slot", p_185129_3_.getName());
      }

      listnbt.add(compoundnbt);
   }

   public ITextComponent getDisplayName() {
      IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("")).append(this.getHoverName());
      if (this.hasCustomHoverName()) {
         iformattabletextcomponent.withStyle(TextFormatting.ITALIC);
      }

      IFormattableTextComponent iformattabletextcomponent1 = TextComponentUtils.wrapInSquareBrackets(iformattabletextcomponent);
      if (!this.emptyCacheFlag) {
         iformattabletextcomponent1.withStyle(this.getRarity().color).withStyle((p_234702_1_) -> {
            return p_234702_1_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(this)));
         });
      }

      return iformattabletextcomponent1;
   }

   private static boolean areSameBlocks(CachedBlockInfo p_206846_0_, @Nullable CachedBlockInfo p_206846_1_) {
      if (p_206846_1_ != null && p_206846_0_.getState() == p_206846_1_.getState()) {
         if (p_206846_0_.getEntity() == null && p_206846_1_.getEntity() == null) {
            return true;
         } else {
            return p_206846_0_.getEntity() != null && p_206846_1_.getEntity() != null ? Objects.equals(p_206846_0_.getEntity().save(new CompoundNBT()), p_206846_1_.getEntity().save(new CompoundNBT())) : false;
         }
      } else {
         return false;
      }
   }

   public boolean hasAdventureModeBreakTagForBlock(ITagCollectionSupplier p_206848_1_, CachedBlockInfo p_206848_2_) {
      if (areSameBlocks(p_206848_2_, this.cachedBreakBlock)) {
         return this.cachedBreakBlockResult;
      } else {
         this.cachedBreakBlock = p_206848_2_;
         if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListNBT listnbt = this.tag.getList("CanDestroy", 8);

            for(int i = 0; i < listnbt.size(); ++i) {
               String s = listnbt.getString(i);

               try {
                  Predicate<CachedBlockInfo> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(s)).create(p_206848_1_);
                  if (predicate.test(p_206848_2_)) {
                     this.cachedBreakBlockResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException commandsyntaxexception) {
               }
            }
         }

         this.cachedBreakBlockResult = false;
         return false;
      }
   }

   public boolean hasAdventureModePlaceTagForBlock(ITagCollectionSupplier p_206847_1_, CachedBlockInfo p_206847_2_) {
      if (areSameBlocks(p_206847_2_, this.cachedPlaceBlock)) {
         return this.cachedPlaceBlockResult;
      } else {
         this.cachedPlaceBlock = p_206847_2_;
         if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListNBT listnbt = this.tag.getList("CanPlaceOn", 8);

            for(int i = 0; i < listnbt.size(); ++i) {
               String s = listnbt.getString(i);

               try {
                  Predicate<CachedBlockInfo> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(s)).create(p_206847_1_);
                  if (predicate.test(p_206847_2_)) {
                     this.cachedPlaceBlockResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException commandsyntaxexception) {
               }
            }
         }

         this.cachedPlaceBlockResult = false;
         return false;
      }
   }

   public int getPopTime() {
      return this.popTime;
   }

   public void setPopTime(int p_190915_1_) {
      this.popTime = p_190915_1_;
   }

   public int getCount() {
      return this.emptyCacheFlag ? 0 : this.count;
   }

   public void setCount(int p_190920_1_) {
      this.count = p_190920_1_;
      this.updateEmptyCacheFlag();
   }

   public void grow(int p_190917_1_) {
      this.setCount(this.count + p_190917_1_);
   }

   public void shrink(int p_190918_1_) {
      this.grow(-p_190918_1_);
   }

   public void onUseTick(World p_222121_1_, LivingEntity p_222121_2_, int p_222121_3_) {
      this.getItem().onUseTick(p_222121_1_, p_222121_2_, this, p_222121_3_);
   }

   public boolean isEdible() {
      return this.getItem().isEdible();
   }

   // FORGE START
   public void deserializeNBT(CompoundNBT nbt) {
      final ItemStack itemStack = ItemStack.of(nbt);
      getStack().setTag(itemStack.getTag());
      if (itemStack.capNBT != null) deserializeCaps(itemStack.capNBT);
   }

   /**
    * Set up forge's ItemStack additions.
    */
   private void forgeInit() {
      Item item = getItemRaw();
      if (item != null) {
         this.delegate = item.delegate;
         net.minecraftforge.common.capabilities.ICapabilityProvider provider = item.initCapabilities(this, this.capNBT);
         this.gatherCapabilities(provider);
         if (this.capNBT != null) deserializeCaps(this.capNBT);
      }
   }

   /**
    * Internal call to get the actual item, not the delegate.
    * In all other methods, FML replaces calls to this.item with the item delegate.
    */
   @Nullable
   private Item getItemRaw() {
       return this.item;
   }

   public SoundEvent getDrinkingSound() {
      return this.getItem().getDrinkingSound();
   }

   public SoundEvent getEatingSound() {
      return this.getItem().getEatingSound();
   }

   public static enum TooltipDisplayFlags {
      ENCHANTMENTS,
      MODIFIERS,
      UNBREAKABLE,
      CAN_DESTROY,
      CAN_PLACE,
      ADDITIONAL,
      DYE;

      private int mask = 1 << this.ordinal();

      public int getMask() {
         return this.mask;
      }
   }
}
