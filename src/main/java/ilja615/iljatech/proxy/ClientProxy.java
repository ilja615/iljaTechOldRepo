package ilja615.iljatech.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;

public class ClientProxy implements IProxy
{
    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }

    @Override
    public void init()
    {
        //Block colors

//        Minecraft.getInstance().getBlockColors().register((x, reader, pos, u)
//                -> reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos)
//                : GrassColors.get(0.5D, 1.0D), ModBlocks.JUNGLE_ROCK_OVERGROWN.get());


    // Item colors/*GrassColors.get(0.5D, 1.0D)*/

//        Minecraft.getInstance().getItemColors().register((x, u)
//                -> GrassColors.get(0.5D, 1.0D), ModBlocks.JUNGLE_ROCK_OVERGROWN.get());


        // Cutout s render layer;

//            RenderTypeLookup.setRenderLayer(ModBlocks.MOSS.get(), RenderType.getCutout());

    }
}
