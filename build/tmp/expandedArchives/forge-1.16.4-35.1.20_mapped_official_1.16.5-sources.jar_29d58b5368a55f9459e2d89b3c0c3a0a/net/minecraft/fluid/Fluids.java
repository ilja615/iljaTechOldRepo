package net.minecraft.fluid;

import net.minecraft.util.registry.Registry;

public class Fluids {
   public static final Fluid EMPTY = register("empty", new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = register("flowing_water", new WaterFluid.Flowing());
   public static final FlowingFluid WATER = register("water", new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = register("flowing_lava", new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = register("lava", new LavaFluid.Source());

   private static <T extends Fluid> T register(String p_215710_0_, T p_215710_1_) {
      return Registry.register(Registry.FLUID, p_215710_0_, p_215710_1_);
   }

   static {
      for(Fluid fluid : Registry.FLUID) {
         for(FluidState fluidstate : fluid.getStateDefinition().getPossibleStates()) {
            Fluid.FLUID_STATE_REGISTRY.add(fluidstate);
         }
      }

   }
}
