package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.util.CountedIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class ModIngredient
{
    public static void registerIngredient()
    {
        CraftingHelper.register(new ResourceLocation(IljaTech.MOD_ID, "counted_ingredient"), new CountedIngredient.Serializer());
    }
}
