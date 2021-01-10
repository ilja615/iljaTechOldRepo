package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.*;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import net.minecraft.block.Block;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IljaTech.MOD_ID);

    public static final RegistryObject<Block> IRON_PLATE = BLOCKS.register("iron_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_PLATE = BLOCKS.register("gold_plate", () -> new PlateBlock(Block.Properties.from(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_PLATE = BLOCKS.register("aluminium_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> NICKEL_PLATE = BLOCKS.register("nickel_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_PLATE = BLOCKS.register("tin_plate", () -> new PlateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_ROD = BLOCKS.register("iron_rod", () -> new RodBlock(Block.Properties.from(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_NAILS = BLOCKS.register("iron_nails", () -> new NailsBlock(Block.Properties.from(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> NAILED_OAK_PLANKS = BLOCKS.register("nailed_oak_planks", () -> new Block(Block.Properties.from(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_SPRUCE_PLANKS = BLOCKS.register("nailed_spruce_planks", () -> new Block(Block.Properties.from(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> NAILED_BIRCH_PLANKS = BLOCKS.register("nailed_birch_planks", () -> new Block(Block.Properties.from(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> NAILED_JUNGLE_PLANKS = BLOCKS.register("nailed_jungle_planks", () -> new Block(Block.Properties.from(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> NAILED_DARK_OAK_PLANKS = BLOCKS.register("nailed_dark_oak_planks", () -> new Block(Block.Properties.from(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_ACACIA_PLANKS = BLOCKS.register("nailed_acacia_planks", () -> new Block(Block.Properties.from(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> NAILED_CRIMSON_PLANKS = BLOCKS.register("nailed_crimson_planks", () -> new Block(Block.Properties.from(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> NAILED_WARPED_PLANKS = BLOCKS.register("nailed_warped_planks", () -> new Block(Block.Properties.from(Blocks.WARPED_PLANKS)));

    public static final RegistryObject<Block> CRAFTER_MACHINE = BLOCKS.register("crafter_machine", () -> new CrafterMachineBlock(Block.Properties.create(Material.IRON)));

    public static final RegistryObject<Block> GEARBOX = BLOCKS.register("gearbox", () -> new GearboxBlock(Block.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> CRANK = BLOCKS.register("crank", () -> new CrankBlock(Block.Properties.create(Material.WOOD)));
}
