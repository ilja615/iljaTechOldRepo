package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArmorItem extends Item implements IArmorVanishable {
   private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final IDispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         return ArmorItem.dispenseArmor(p_82487_1_, p_82487_2_) ? p_82487_2_ : super.execute(p_82487_1_, p_82487_2_);
      }
   };
   protected final EquipmentSlotType slot;
   private final int defense;
   private final float toughness;
   protected final float knockbackResistance;
   protected final IArmorMaterial material;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public static boolean dispenseArmor(IBlockSource p_226626_0_, ItemStack p_226626_1_) {
      BlockPos blockpos = p_226626_0_.getPos().relative(p_226626_0_.getBlockState().getValue(DispenserBlock.FACING));
      List<LivingEntity> list = p_226626_0_.getLevel().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(blockpos), EntityPredicates.NO_SPECTATORS.and(new EntityPredicates.ArmoredMob(p_226626_1_)));
      if (list.isEmpty()) {
         return false;
      } else {
         LivingEntity livingentity = list.get(0);
         EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(p_226626_1_);
         ItemStack itemstack = p_226626_1_.split(1);
         livingentity.setItemSlot(equipmentslottype, itemstack);
         if (livingentity instanceof MobEntity) {
            ((MobEntity)livingentity).setDropChance(equipmentslottype, 2.0F);
            ((MobEntity)livingentity).setPersistenceRequired();
         }

         return true;
      }
   }

   public ArmorItem(IArmorMaterial p_i48534_1_, EquipmentSlotType p_i48534_2_, Item.Properties p_i48534_3_) {
      super(p_i48534_3_.defaultDurability(p_i48534_1_.getDurabilityForSlot(p_i48534_2_)));
      this.material = p_i48534_1_;
      this.slot = p_i48534_2_;
      this.defense = p_i48534_1_.getDefenseForSlot(p_i48534_2_);
      this.toughness = p_i48534_1_.getToughness();
      this.knockbackResistance = p_i48534_1_.getKnockbackResistance();
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[p_i48534_2_.getIndex()];
      builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
      if (this.knockbackResistance > 0) {
         builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
      }

      this.defaultModifiers = builder.build();
   }

   public EquipmentSlotType getSlot() {
      return this.slot;
   }

   public int getEnchantmentValue() {
      return this.material.getEnchantmentValue();
   }

   public IArmorMaterial getMaterial() {
      return this.material;
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return this.material.getRepairIngredient().test(p_82789_2_) || super.isValidRepairItem(p_82789_1_, p_82789_2_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = p_77659_2_.getItemBySlot(equipmentslottype);
      if (itemstack1.isEmpty()) {
         p_77659_2_.setItemSlot(equipmentslottype, itemstack.copy());
         itemstack.setCount(0);
         return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
      } else {
         return ActionResult.fail(itemstack);
      }
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType p_111205_1_) {
      return p_111205_1_ == this.slot ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_111205_1_);
   }

   public int getDefense() {
      return this.defense;
   }

   public float getToughness() {
      return this.toughness;
   }
}
