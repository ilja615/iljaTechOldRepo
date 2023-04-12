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
                        {"BBB", "BPB", "BBB"},
                        {"ccc", "c c", "ccc"},
                        {"ppp", "0 B", "ppp"},
                        {"BBB", "ccB", "BBB"}
                },
                '0', api.looseBlockMatcher(ModBlocks.BRICK_FOUNDRY.get()),
                ' ', api.airMatcher(),
                'B', api.looseBlockMatcher(Blocks.BRICKS),
                'p', api.predicateMatcher(Blocks.BRICKS, state -> state.is(Blocks.BRICKS) || state.is(ModBlocks.BRICK_FOUNDRY_PIPING.get())),
                'c', api.predicateMatcher(Blocks.BRICKS, state -> state.is(Blocks.BRICKS) || state.is(ModBlocks.BRICK_CHUTE.get())),
                'P', api.looseBlockMatcher(ModBlocks.BRICK_FOUNDRY_PIPING.get())
        );
    }
}