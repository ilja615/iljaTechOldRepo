package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial
{
    VINYL(IljaTech.MOD_ID + ":vinyl", 5, new int[] {7, 9, 11, 7}, 10, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0f, () -> {return Ingredient.of(ModBlocks.VINYL_SHEET.get());}, 0.0f);

    private static final int[] MAX_DAMAGE_ARRAY = new int[] {16, 16, 16, 16};
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final LazyLoadedValue<Ingredient> repairMaterial;
    private final float knockbackResistance;

    private ModArmorMaterials(String nameIn, int maxDamageFactorIn, int[] damageReductionArrayIn, int enchantabilityIn, SoundEvent soundEventIn, float toughnessIn, Supplier<Ingredient> repairMaterialIn, float knockbackResistanceIn)
    {
        this.name = nameIn;
        this.maxDamageFactor = maxDamageFactorIn;
        this.damageReductionArray = damageReductionArrayIn;
        this.enchantability = enchantabilityIn;
        this.soundEvent = soundEventIn;
        this.toughness = toughnessIn;
        this.repairMaterial = new LazyLoadedValue<>(repairMaterialIn);
        this.knockbackResistance = knockbackResistanceIn;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot)
    {
        return MAX_DAMAGE_ARRAY[slot.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot)
    {
        return this.damageReductionArray[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue()
    {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound()
    {
        return this.soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return this.repairMaterial.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getName()
    {
        return this.name;
    }

    @Override
    public float getToughness()
    {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance()
    {
        return this.knockbackResistance;
    }
}
