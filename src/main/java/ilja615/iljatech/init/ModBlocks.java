package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import ilja615.iljatech.util.ModUtils;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IljaTech.MOD_ID);

    public static final RegistryObject<Block> IRON_SCAFFOLDING = BLOCKS.register("iron_scaffolding", () -> new Block(AbstractBlock.Properties.of(Material.METAL).noOcclusion()));

    public static final RegistryObject<Block> IRON_PLATE = BLOCKS.register("iron_plate", () -> new PlateBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_PLATE = BLOCKS.register("gold_plate", () -> new PlateBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_PLATE = BLOCKS.register("aluminium_plate", () -> new PlateBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_PLATE = BLOCKS.register("tin_plate", () -> new PlateBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_ROD = BLOCKS.register("iron_rod", () -> new RodBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_NAILS = BLOCKS.register("iron_nails", () -> new NailsBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> NAILED_OAK_PLANKS = BLOCKS.register("nailed_oak_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_SPRUCE_PLANKS = BLOCKS.register("nailed_spruce_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> NAILED_BIRCH_PLANKS = BLOCKS.register("nailed_birch_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> NAILED_JUNGLE_PLANKS = BLOCKS.register("nailed_jungle_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> NAILED_DARK_OAK_PLANKS = BLOCKS.register("nailed_dark_oak_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_ACACIA_PLANKS = BLOCKS.register("nailed_acacia_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> NAILED_CRIMSON_PLANKS = BLOCKS.register("nailed_crimson_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> NAILED_WARPED_PLANKS = BLOCKS.register("nailed_warped_planks", () -> new Block(AbstractBlock.Properties.copy(Blocks.WARPED_PLANKS)));

    public static final RegistryObject<Block> CRAFTER_MACHINE = BLOCKS.register("crafter_machine", () -> new CrafterMachineBlock(AbstractBlock.Properties.of(Material.METAL)));

    // Mechanical
    public static final RegistryObject<Block> GEARBOX = BLOCKS.register("gearbox", () -> new GearboxBlock(AbstractBlock.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CRANK = BLOCKS.register("crank", () -> new CrankBlock(AbstractBlock.Properties.of(Material.WOOD).noOcclusion().noCollission()));
    public static final RegistryObject<Block> TURBINE = BLOCKS.register("turbine", () -> new TurbineBlock(AbstractBlock.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CONVEYOR_BELT = BLOCKS.register("conveyor_belt", () -> new ConveyorBeltBlock(AbstractBlock.Properties.of(Material.METAL).noOcclusion()));

    // Thermal
    public static final RegistryObject<Block> BURNER = BLOCKS.register("burner", () -> new BurnerBlock(AbstractBlock.Properties.copy(Blocks.BRICKS).lightLevel(ModUtils.getLightValueLit(13))));
    public static final RegistryObject<Block> BELLOWS = BLOCKS.register("bellows", () -> new BellowsBlock(AbstractBlock.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> STOKED_FIRE = BLOCKS.register("stoked_fire", () -> new StokedFireBlock(AbstractBlock.Properties.copy(Blocks.FIRE).noOcclusion().noCollission()));
}