package ilja615.iljatech.util;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.init.ModParticles;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public enum GassType implements IStringSerializable {
    STEAM("steam", ModParticles.STEAM_PARTICLE.get());

    private final String name;
    private final ParticleType particleType;

    private GassType(String nameIn, ParticleType particleType) {
        this.name = nameIn;
        this.particleType = particleType;
    }

    @Override
    public String getString() { return this.name; }
}