package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.CrafterMachineBlock;
import ilja615.iljatech.blocks.PlateBlock;
import ilja615.iljatech.blocks.RodBlock;
import net.minecraft.block.Block;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IljaTech.MOD_ID);

    public static final RegistryObject<Block> IRON_PLATE = BLOCKS.register("iron_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_PLATE = BLOCKS.register("gold_plate", () -> new PlateBlock(Block.Properties.from(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_PLATE = BLOCKS.register("aluminium_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> CHROME_PLATE = BLOCKS.register("chrome_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> NICKEL_PLATE = BLOCKS.register("nickel_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_PLATE = BLOCKS.register("tin_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_ROD = BLOCKS.register("iron_rod", () -> new RodBlock(Block.Properties.from(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> CRAFTER_MACHINE = BLOCKS.register("crafter_machine", () -> new CrafterMachineBlock(Block.Properties.create(Material.ROCK)));

}
