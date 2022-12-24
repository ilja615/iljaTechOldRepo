package ilja615.iljatech.init;

import com.mojang.math.Vector3f;
import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.fluids.BaseFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation OIL_STILL_RL = new ResourceLocation(IljaTech.MOD_ID, "block/oil_still");
    public static final ResourceLocation OIL_FLOWING_RL = new ResourceLocation(IljaTech.MOD_ID, "block/oil_flowing");
    public static final ResourceLocation OIL_OVERLAY_RL = new ResourceLocation(IljaTech.MOD_ID, "misc/in_oil");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<FluidType> OIL_FLUID_TYPE = register("oil_fluid",
            FluidType.Properties.create().density(15).viscosity(5).sound(SoundAction.get("drink"),
                    SoundEvents.HONEY_DRINK));

    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(OIL_STILL_RL, OIL_FLOWING_RL, OIL_OVERLAY_RL,
                0xFFFFFFFF, new Vector3f(0.0f, 0.0f, 0.0f), properties));
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}