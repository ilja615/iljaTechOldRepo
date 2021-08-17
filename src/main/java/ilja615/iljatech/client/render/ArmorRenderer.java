package ilja615.iljatech.client.render;

import ilja615.iljatech.client.ModEntityRenderRegistry;
import ilja615.iljatech.client.models.ArmorBaseModel;
import ilja615.iljatech.client.models.PetrolymerHelmetModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;

public class ArmorRenderer extends ArmorItem implements IItemRenderProperties
{
    public ArmorBaseModel armorModel;

    public ArmorRenderer(ArmorMaterial material, EquipmentSlot equipmentSlot, Item.Properties properties)
    {
        super(material, equipmentSlot, properties);
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
    {
        return this.armorModel.getTexture();
    }

    @Override
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default)
    {
        return (A)this.armorModel;
    }
}