package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.Function;

public class ModMultiBlocks
{
    public static void registerMultiBlocks()
    {
        registerMultiBlock("foundry_multiblock", ModMultiBlocks::foundry);
    }

    private static void registerMultiBlock(String name, Function<PatchouliAPI.IPatchouliAPI, IMultiblock> factory)
    {
        final PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
        ResourceLocation rl = new ResourceLocation(IljaTech.MOD_ID, name);
        api.registerMultiblock(rl, factory.apply(api));
    }

    private static IMultiblock foundry(PatchouliAPI.IPatchouliAPI api)
    {
        return api.makeMultiblock(new String[][] {
                        {"BBB", "BCB", "BBB"},
                        {"BBB", "B B", "BBB"},
                        {"BBB", "B B", "BBB"},
                        {"BBB", "F0B", "BBB"}
                },
                'F', api.looseBlockMatcher(ModBlocks.BRICK_FOUNDRY.get()),
                ' ', api.airMatcher(),
                'B', api.predicateMatcher(Blocks.BRICKS, state -> state.is(Blocks.BRICKS) || state.is(ModBlocks.BRICK_FOUNDRY_CHANNEL.get())),
                'C', api.looseBlockMatcher(ModBlocks.BRICK_FOUNDRY_CHANNEL.get()),
                '0', api.predicateMatcher(Blocks.BRICKS, state -> state.is(Blocks.BRICKS) || state.is(ModBlocks.BRICK_FOUNDRY_CHANNEL.get()))
        );
    }
}