package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.*;
import ilja615.iljatech.blocks.RodBlock;
import ilja615.iljatech.blocks.bellows.BellowsBlock;
import ilja615.iljatech.blocks.burner.BurnerBlock;
import ilja615.iljatech.blocks.conveyor_belt.ConveyorBeltBlock;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlock;
import ilja615.iljatech.blocks.crusher.CrusherBlock;
import ilja615.iljatech.blocks.crystals.*;
import ilja615.iljatech.blocks.stretcher.StretcherBlock;
import ilja615.iljatech.blocks.turbine.TurbineBlock;
import ilja615.iljatech.blocks.wire.BarbedWireBlock;
import ilja615.iljatech.blocks.wire.BaseWireBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import ilja615.iljatech.util.ModUtils;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IljaTech.MOD_ID);

    // Other
    public static final RegistryObject<Block> IRON_SCAFFOLDING = BLOCKS.register("iron_scaffolding", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> RESEARCH_TABLE = BLOCKS.register("research_table", () -> new ResearchTableBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion()));

    // Metal parts
    public static final RegistryObject<Block> IRON_PLATE = BLOCKS.register("iron_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_PLATE = BLOCKS.register("gold_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_PLATE = BLOCKS.register("aluminium_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_PLATE = BLOCKS.register("tin_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> COPPER_PLATE = BLOCKS.register("copper_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));

    public static final RegistryObject<Block> IRON_ROD = BLOCKS.register("iron_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_ROD = BLOCKS.register("gold_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> COPPER_ROD = BLOCKS.register("copper_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_ROD = BLOCKS.register("aluminium_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> STEEL_ROD = BLOCKS.register("steel_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> CONSTANTAN_ROD = BLOCKS.register("constantan_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> NICHROME_ROD = BLOCKS.register("nichrome_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_NAILS = BLOCKS.register("iron_nails", () -> new NailsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> COPPER_WIRE = BLOCKS.register("copper_wire", () -> new BaseWireBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> GOLD_WIRE = BLOCKS.register("gold_wire", () -> new BaseWireBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> ALUMINIUM_WIRE = BLOCKS.register("aluminium_wire", () -> new BaseWireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> STEEL_WIRE = BLOCKS.register("steel_wire", () -> new BaseWireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> CONSTANTAN_WIRE = BLOCKS.register("constantan_wire", () -> new BaseWireBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> NICHROME_WIRE = BLOCKS.register("nichrome_wire", () -> new BaseWireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> BARBED_WIRE = BLOCKS.register("barbed_wire", () -> new BarbedWireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));

    // Nailed Planks
    public static final RegistryObject<Block> NAILED_OAK_PLANKS = BLOCKS.register("nailed_oak_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_SPRUCE_PLANKS = BLOCKS.register("nailed_spruce_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> NAILED_BIRCH_PLANKS = BLOCKS.register("nailed_birch_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> NAILED_JUNGLE_PLANKS = BLOCKS.register("nailed_jungle_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> NAILED_DARK_OAK_PLANKS = BLOCKS.register("nailed_dark_oak_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_ACACIA_PLANKS = BLOCKS.register("nailed_acacia_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> NAILED_CRIMSON_PLANKS = BLOCKS.register("nailed_crimson_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> NAILED_WARPED_PLANKS = BLOCKS.register("nailed_warped_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)));

    // Crystals
    public static final RegistryObject<Block> AZURITE_BLOCK = BLOCKS.register("azurite_block", () -> new CrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> RUBY_BLOCK = BLOCKS.register("ruby_block", () -> new CrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> CASSITERITE_BLOCK = BLOCKS.register("cassiterite_block", () -> new CrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> AZURITE_CLUSTER = BLOCKS.register("azurite_cluster", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_CLUSTER_PROPERTY));
    public static final RegistryObject<Block> RUBY_CLUSTER = BLOCKS.register("ruby_cluster", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_CLUSTER_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_CLUSTER = BLOCKS.register("cassiterite_cluster", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_CLUSTER_PROPERTY));
    public static final RegistryObject<Block> AZURITE_SMALL_BUD = BLOCKS.register("azurite_small_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_SMALL_BUD_PROPERTY));
    public static final RegistryObject<Block> RUBY_SMALL_BUD = BLOCKS.register("ruby_small_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_SMALL_BUD_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_SMALL_BUD = BLOCKS.register("cassiterite_small_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_SMALL_BUD_PROPERTY));
    public static final RegistryObject<Block> AZURITE_MEDIUM_BUD = BLOCKS.register("azurite_medium_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_MEDIUM_BUD_PROPERTY));
    public static final RegistryObject<Block> RUBY_MEDIUM_BUD = BLOCKS.register("ruby_medium_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_MEDIUM_BUD_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_MEDIUM_BUD = BLOCKS.register("cassiterite_medium_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_MEDIUM_BUD_PROPERTY));
    public static final RegistryObject<Block> AZURITE_LARGE_BUD = BLOCKS.register("azurite_large_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_LARGE_BUD_PROPERTY));
    public static final RegistryObject<Block> RUBY_LARGE_BUD = BLOCKS.register("ruby_large_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_LARGE_BUD_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_LARGE_BUD = BLOCKS.register("cassiterite_large_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_LARGE_BUD_PROPERTY));
    public static final RegistryObject<Block> BUDDING_AZURITE_BLOCK = BLOCKS.register("budding_azurite_block", () -> new AzuriteBuddingBlock(ModProperties.BUDDING_CRYSTAL_PROPERTY));
    public static final RegistryObject<Block> BUDDING_RUBY_BLOCK = BLOCKS.register("budding_ruby_block", () -> new RubyBuddingBlock(ModProperties.BUDDING_CRYSTAL_PROPERTY));
    public static final RegistryObject<Block> BUDDING_CASSITERITE_BLOCK = BLOCKS.register("budding_cassiterite_block", () -> new CassiteriteBuddingBlock(ModProperties.BUDDING_CRYSTAL_PROPERTY));

    // Technical
    public static final RegistryObject<Block> CRAFTER_MACHINE = BLOCKS.register("crafter_machine", () -> new CrafterMachineBlock(BlockBehaviour.Properties.of(Material.METAL)));

    // Mechanical
    public static final RegistryObject<Block> GEARBOX = BLOCKS.register("gearbox", () -> new GearboxBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CRANK = BLOCKS.register("crank", () -> new CrankBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion().noCollission()));
    public static final RegistryObject<Block> TURBINE = BLOCKS.register("turbine", () -> new TurbineBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CONVEYOR_BELT = BLOCKS.register("conveyor_belt", () -> new ConveyorBeltBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> STRETCHER = BLOCKS.register("stretcher", () -> new StretcherBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> CRUSHER = BLOCKS.register("crusher", () -> new CrusherBlock(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.COPPER).noOcclusion()));

    // Thermal
    public static final RegistryObject<Block> BURNER = BLOCKS.register("burner", () -> new BurnerBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(ModUtils.getLightValueLit(13))));
    public static final RegistryObject<Block> BELLOWS = BLOCKS.register("bellows", () -> new BellowsBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> STOKED_FIRE = BLOCKS.register("stoked_fire", () -> new StokedFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE).noOcclusion().noCollission()));
}