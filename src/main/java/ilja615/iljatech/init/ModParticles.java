package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import net.minecraft.block.Block;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<BasicParticleType> STEAM_PARTICLE = PARTICLES.register("steam_particle", () -> new BasicParticleType(false));
}
