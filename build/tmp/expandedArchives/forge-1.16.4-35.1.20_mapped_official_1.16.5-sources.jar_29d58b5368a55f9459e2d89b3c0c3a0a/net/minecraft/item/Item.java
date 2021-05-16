package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Item extends net.minecraftforge.registries.ForgeRegistryEntry<Item> implements IItemProvider, net.minecraftforge.common.extensions.IForgeItem {
   public static final Map<Block, Item> BY_BLOCK = net.minecraftforge.registries.GameData.getBlockItemMap();
   protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   protected static final Random random = new Random();
   protected final ItemGroup category;
   private final Rarity rarity;
   private final int maxStackSize;
   private final int maxDamage;
   private final boolean isFireResistant;
   private final Item craftingRemainingItem;
   @Nullable
   private String descriptionId;
   @Nullable
   private final Food foodProperties;

   public static int getId(Item p_150891_0_) {
      return p_150891_0_ == null ? 0 : Registry.ITEM.getId(p_150891_0_);
   }

   public static Item byId(int p_150899_0_) {
      return Registry.ITEM.byId(p_150899_0_);
   }

   @Deprecated
   public static Item byBlock(Block p_150898_0_) {
      return BY_BLOCK.getOrDefault(p_150898_0_, Items.AIR);
   }

   public Item(Item.Properties p_i48487_1_) {
      this.category = p_i48487_1_.category;
      this.rarity = p_i48487_1_.rarity;
      this.craftingRemainingItem = p_i48487_1_.craftingRemainingItem;
      this.maxDamage = p_i48487_1_.maxDamage;
      this.maxStackSize = p_i48487_1_.maxStackSize;
      this.foodProperties = p_i48487_1_.foodProperties;
      this.isFireResistant = p_i48487_1_.isFireResistant;
      this.canRepair = p_i48487_1_.canRepair;
      this.toolClasses.putAll(p_i48487_1_.toolClasses);
      Object tmp = p_i48487_1_.ister == null ? null : net.minecraftforge.fml.DistExecutor.callWhenOn(Dist.CLIENT, p_i48487_1_.ister);
      this.ister = tmp == null ? null : () -> (net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer) tmp;
   }

   public void onUseTick(World p_219972_1_, LivingEntity p_219972_2_, ItemStack p_219972_3_, int p_219972_4_) {
   }

   public boolean verifyTagAfterLoad(CompoundNBT p_179215_1_) {
      return false;
   }

   public boolean canAttackBlock(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      return ActionResultType.PASS;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      return 1.0F;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      if (this.isEdible()) {
         ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
         if (p_77659_2_.canEat(this.getFoodProperties().canAlwaysEat())) {
            p_77659_2_.startUsingItem(p_77659_3_);
            return ActionResult.consume(itemstack);
         } else {
            return ActionResult.fail(itemstack);
         }
      } else {
         return ActionResult.pass(p_77659_2_.getItemInHand(p_77659_3_));
      }
   }

   public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      return this.isEdible() ? p_77654_3_.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
   }

   @Deprecated // Use ItemStack sensitive version.
   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   @Deprecated // Use ItemStack sensitive version.
   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean canBeDepleted() {
      return this.maxDamage > 0;
   }

   public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      return false;
   }

   public boolean mineBlock(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      return false;
   }

   public boolean isCorrectToolForDrops(BlockState p_150897_1_) {
      return false;
   }

   public ActionResultType interactLivingEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      return ActionResultType.PASS;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDescription() {
      return new TranslationTextComponent(this.getDescriptionId());
   }

   public String toString() {
      return Registry.ITEM.getKey(this).getPath();
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("item", Registry.ITEM.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public String getDescriptionId(ItemStack p_77667_1_) {
      return this.getDescriptionId();
   }

   public boolean shouldOverrideMultiplayerNbt() {
      return true;
   }

   @Nullable
   @Deprecated // Use ItemStack sensitive version.
   public final Item getCraftingRemainingItem() {
      return this.craftingRemainingItem;
   }

   @Deprecated // Use ItemStack sensitive version.
   public boolean hasCraftingRemainingItem() {
      return this.craftingRemainingItem != null;
   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
   }

   public void onCraftedBy(ItemStack p_77622_1_, World p_77622_2_, PlayerEntity p_77622_3_) {
   }

   public boolean isComplex() {
      return false;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return p_77661_1_.getItem().isEdible() ? UseAction.EAT : UseAction.NONE;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      if (p_77626_1_.getItem().isEdible()) {
         return this.getFoodProperties().isFastFood() ? 16 : 32;
      } else {
         return 0;
      }
   }

   public void releaseUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
   }

   public ITextComponent getName(ItemStack p_200295_1_) {
      return new TranslationTextComponent(this.getDescriptionId(p_200295_1_));
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return p_77636_1_.isEnchanted();
   }

   public Rarity getRarity(ItemStack p_77613_1_) {
      if (!p_77613_1_.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return Rarity.RARE;
         case RARE:
            return Rarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   public boolean isEnchantable(ItemStack p_77616_1_) {
      return this.getItemStackLimit(p_77616_1_) == 1 && this.isDamageable(p_77616_1_);
   }

   protected static BlockRayTraceResult getPlayerPOVHitResult(World p_219968_0_, PlayerEntity p_219968_1_, RayTraceContext.FluidMode p_219968_2_) {
      float f = p_219968_1_.xRot;
      float f1 = p_219968_1_.yRot;
      Vector3d vector3d = p_219968_1_.getEyePosition(1.0F);
      float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d0 = p_219968_1_.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();;
      Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
      return p_219968_0_.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, p_219968_2_, p_219968_1_));
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public void fillItemCategory(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.allowdedIn(p_150895_1_)) {
         p_150895_2_.add(new ItemStack(this));
      }

   }

   protected boolean allowdedIn(ItemGroup p_194125_1_) {
      if (getCreativeTabs().stream().anyMatch(tab -> tab == p_194125_1_)) return true;
      ItemGroup itemgroup = this.getItemCategory();
      return itemgroup != null && (p_194125_1_ == ItemGroup.TAB_SEARCH || p_194125_1_ == itemgroup);
   }

   @Nullable
   public final ItemGroup getItemCategory() {
      return this.category;
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return false;
   }

   @Deprecated // Use ItemStack sensitive version.
   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType p_111205_1_) {
      return ImmutableMultimap.of();
   }

   @Nullable
   private final java.util.function.Supplier<net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer> ister;
   private final java.util.Map<net.minecraftforge.common.ToolType, Integer> toolClasses = Maps.newHashMap();
   private final net.minecraftforge.common.util.ReverseTagWrapper<Item> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, net.minecraft.tags.ItemTags::getAllTags);
   protected final boolean canRepair;

   @Override
   public boolean isRepairable(ItemStack stack) {
      return canRepair && isDamageable(stack);
   }

   @Override
   public java.util.Set<net.minecraftforge.common.ToolType> getToolTypes(ItemStack stack) {
      return toolClasses.keySet();
   }

   @Override
   public int getHarvestLevel(ItemStack stack, net.minecraftforge.common.ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
      return toolClasses.getOrDefault(tool, -1);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public final net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer getItemStackTileEntityRenderer() {
      net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer renderer = ister != null ? ister.get() : null;
      return renderer != null ? renderer : net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer.instance;
   }

   @Override
   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   public boolean useOnRelease(ItemStack p_219970_1_) {
      return p_219970_1_.getItem() == Items.CROSSBOW;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean is(ITag<Item> p_206844_1_) {
      return p_206844_1_.contains(this);
   }

   public boolean isEdible() {
      return this.foodProperties != null;
   }

   @Nullable
   public Food getFoodProperties() {
      return this.foodProperties;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.GENERIC_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.GENERIC_EAT;
   }

   public boolean isFireResistant() {
      return this.isFireResistant;
   }

   public boolean canBeHurtBy(DamageSource p_234685_1_) {
      return !this.isFireResistant || !p_234685_1_.isFire();
   }

   public static class Properties {
      private int maxStackSize = 64;
      private int maxDamage;
      private Item craftingRemainingItem;
      private ItemGroup category;
      private Rarity rarity = Rarity.COMMON;
      private Food foodProperties;
      private boolean isFireResistant;
      private boolean canRepair = true;
      private java.util.Map<net.minecraftforge.common.ToolType, Integer> toolClasses = Maps.newHashMap();
      private java.util.function.Supplier<java.util.concurrent.Callable<net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer>> ister;

      public Item.Properties food(Food p_221540_1_) {
         this.foodProperties = p_221540_1_;
         return this;
      }

      public Item.Properties stacksTo(int p_200917_1_) {
         if (this.maxDamage > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.maxStackSize = p_200917_1_;
            return this;
         }
      }

      public Item.Properties defaultDurability(int p_200915_1_) {
         return this.maxDamage == 0 ? this.durability(p_200915_1_) : this;
      }

      public Item.Properties durability(int p_200918_1_) {
         this.maxDamage = p_200918_1_;
         this.maxStackSize = 1;
         return this;
      }

      public Item.Properties craftRemainder(Item p_200919_1_) {
         this.craftingRemainingItem = p_200919_1_;
         return this;
      }

      public Item.Properties tab(ItemGroup p_200916_1_) {
         this.category = p_200916_1_;
         return this;
      }

      public Item.Properties rarity(Rarity p_208103_1_) {
         this.rarity = p_208103_1_;
         return this;
      }

      public Item.Properties fireResistant() {
         this.isFireResistant = true;
         return this;
      }

      public Item.Properties setNoRepair() {
         canRepair = false;
         return this;
      }

      public Item.Properties addToolType(net.minecraftforge.common.ToolType type, int level) {
         toolClasses.put(type, level);
         return this;
      }

      public Item.Properties setISTER(java.util.function.Supplier<java.util.concurrent.Callable<net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer>> ister) {
         this.ister = ister;
         return this;
      }
   }
}
