package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.*;
import ilja615.iljatech.blocks.RodBlock;
import ilja615.iljatech.blocks.bellows.BellowsBlock;
import ilja615.iljatech.blocks.burner.BurnerBlock;
import ilja615.iljatech.blocks.conveyor_belt.ConveyorBeltBlock;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlock;
import ilja615.iljatech.blocks.elongating_mill.ElongatingMillBlock;
import ilja615.iljatech.blocks.turbine.TurbineBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import ilja615.iljatech.util.ModUtils;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IljaTech.MOD_ID);

    public static final RegistryObject<Block> IRON_SCAFFOLDING = BLOCKS.register("iron_scaffolding", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));

    public static final RegistryObject<Block> IRON_PLATE = BLOCKS.register("iron_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_PLATE = BLOCKS.register("gold_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_PLATE = BLOCKS.register("aluminium_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_PLATE = BLOCKS.register("tin_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> COPPER_PLATE = BLOCKS.register("copper_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));

    public static final RegistryObject<Block> IRON_ROD = BLOCKS.register("iron_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_NAILS = BLOCKS.register("iron_nails", () -> new NailsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> NAILED_OAK_PLANKS = BLOCKS.register("nailed_oak_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_SPRUCE_PLANKS = BLOCKS.register("nailed_spruce_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> NAILED_BIRCH_PLANKS = BLOCKS.register("nailed_birch_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> NAILED_JUNGLE_PLANKS = BLOCKS.register("nailed_jungle_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> NAILED_DARK_OAK_PLANKS = BLOCKS.register("nailed_dark_oak_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_ACACIA_PLANKS = BLOCKS.register("nailed_acacia_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> NAILED_CRIMSON_PLANKS = BLOCKS.register("nailed_crimson_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> NAILED_WARPED_PLANKS = BLOCKS.register("nailed_warped_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)));

    public static final RegistryObject<Block> RESEARCH_TABLE = BLOCKS.register("research_table", () -> new ResearchTableBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion()));

    public static final RegistryObject<Block> CRAFTER_MACHINE = BLOCKS.register("crafter_machine", () -> new CrafterMachineBlock(BlockBehaviour.Properties.of(Material.METAL)));

    // Mechanical
    public static final RegistryObject<Block> GEARBOX = BLOCKS.register("gearbox", () -> new GearboxBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CRANK = BLOCKS.register("crank", () -> new CrankBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion().noCollission()));
    public static final RegistryObject<Block> TURBINE = BLOCKS.register("turbine", () -> new TurbineBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CONVEYOR_BELT = BLOCKS.register("conveyor_belt", () -> new ConveyorBeltBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> ELONGATING_MILL = BLOCKS.register("elongating_mill", () -> new ElongatingMillBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));

    // Thermal
    public static final RegistryObject<Block> BURNER = BLOCKS.register("burner", () -> new BurnerBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(ModUtils.getLightValueLit(13))));
    public static final RegistryObject<Block> BELLOWS = BLOCKS.register("bellows", () -> new BellowsBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> STOKED_FIRE = BLOCKS.register("stoked_fire", () -> new StokedFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE).noOcclusion().noCollission()));
}