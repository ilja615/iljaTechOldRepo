package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.effects.StunnedEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects
{
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, IljaTech.MOD_ID);

    public static final RegistryObject<Effect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
}
