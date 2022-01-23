package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.particles.StarParticle;
import ilja615.iljatech.particles.SteamParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<SimpleParticleType> STEAM_PARTICLE = PARTICLES.register("steam_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> STAR_PARTICLE = PARTICLES.register("star_particle", () -> new SimpleParticleType(false));

    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticles.STEAM_PARTICLE.get(), SteamParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.STAR_PARTICLE.get(), StarParticle.Factory::new);
    }
}
