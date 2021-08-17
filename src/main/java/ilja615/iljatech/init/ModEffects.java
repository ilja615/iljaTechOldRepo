package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.effects.StunnedEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IljaTech.MOD_ID);

    public static final RegistryObject<MobEffect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
}
