package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.*;
import ilja615.iljatech.blocks.RodBlock;
import ilja615.iljatech.blocks.battery_box.BatteryBoxBlock;
import ilja615.iljatech.blocks.bellows.BellowsBlock;
import ilja615.iljatech.blocks.burner.BurnerBlock;
import ilja615.iljatech.blocks.conveyor_belt.ConveyorBeltBlock;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlock;
import ilja615.iljatech.blocks.crusher.CrusherBlock;
import ilja615.iljatech.blocks.crystals.*;
import ilja615.iljatech.blocks.dynamo.DynamoBlock;
import ilja615.iljatech.blocks.foundry.FoundryBlock;
import ilja615.iljatech.blocks.foundry.ChuteBlock;
import ilja615.iljatech.blocks.stretcher.StretcherBlock;
import ilja615.iljatech.blocks.turbine.TurbineBlock;
import ilja615.iljatech.blocks.wire.BarbedWireBlock;
import ilja615.iljatech.blocks.wire.ElectricalWireBlock;
import ilja615.iljatech.blocks.wire.WireBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import ilja615.iljatech.util.ModUtils;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IljaTech.MOD_ID);

    // Other
    public static final RegistryObject<Block> IRON_SCAFFOLDING = registerBlockWithItem("iron_scaffolding", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> RESEARCH_TABLE = registerBlockWithItem("research_table", () -> new ResearchTableBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion()));

    // Metal parts
    public static final RegistryObject<Block> IRON_PLATE = registerBlockWithItem("iron_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_PLATE = registerBlockWithItem("gold_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_PLATE = registerBlockWithItem("aluminium_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_PLATE = registerBlockWithItem("tin_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> COPPER_PLATE = registerBlockWithItem("copper_plate", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> VINYL_SHEET = registerBlockWithItem("vinyl_sheet", () -> new PlateBlock(BlockBehaviour.Properties.copy(Blocks.HONEYCOMB_BLOCK)));

    public static final RegistryObject<Block> IRON_ROD = registerBlockWithItem("iron_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> GOLD_ROD = registerBlockWithItem("gold_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> COPPER_ROD = registerBlockWithItem("copper_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> ALUMINIUM_ROD = registerBlockWithItem("aluminium_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> STEEL_ROD = registerBlockWithItem("steel_rod", () -> new RodBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> IRON_NAILS = registerBlockWithItem("iron_nails", () -> new NailsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> GOLD_WIRE = registerBlockWithItem("gold_wire", () -> new WireBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> STEEL_WIRE = registerBlockWithItem("steel_wire", () -> new WireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> BARBED_WIRE = registerBlockWithItem("barbed_wire", () -> new BarbedWireBlock(ModProperties.BARBED_WIRE_PROPERTY));
    public static final RegistryObject<Block> COPPER_WIRE = registerBlockWithItem("copper_wire", () -> new ElectricalWireBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> ALUMINIUM_WIRE = registerBlockWithItem("aluminium_wire", () -> new ElectricalWireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> CONSTANTAN_WIRE = registerBlockWithItem("constantan_wire", () -> new ElectricalWireBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> NICHROME_WIRE = registerBlockWithItem("nichrome_wire", () -> new ElectricalWireBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().noCollission()));

    // Nailed Planks
    public static final RegistryObject<Block> NAILED_OAK_PLANKS = registerBlockWithItem("nailed_oak_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_SPRUCE_PLANKS = registerBlockWithItem("nailed_spruce_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> NAILED_BIRCH_PLANKS = registerBlockWithItem("nailed_birch_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> NAILED_JUNGLE_PLANKS = registerBlockWithItem("nailed_jungle_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> NAILED_DARK_OAK_PLANKS = registerBlockWithItem("nailed_dark_oak_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> NAILED_ACACIA_PLANKS = registerBlockWithItem("nailed_acacia_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> NAILED_CRIMSON_PLANKS = registerBlockWithItem("nailed_crimson_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> NAILED_WARPED_PLANKS = registerBlockWithItem("nailed_warped_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS)));

    // Ores
    public static final RegistryObject<Block> ALUMINIUM_ORE = registerBlockWithItem("aluminium_ore", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> TIN_ORE = registerBlockWithItem("tin_ore", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> CHROME_ORE = registerBlockWithItem("chrome_ore", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> NICKEL_ORE = registerBlockWithItem("nickel_ore", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> ALUMINIUM_ORE_DEEPSLATE = registerBlockWithItem("aluminium_ore_deepslate", () -> new Block(ModProperties.DEEPSLATE_ORE_PROPERTY));
    public static final RegistryObject<Block> TIN_ORE_DEEPSLATE = registerBlockWithItem("tin_ore_deepslate", () -> new Block(ModProperties.DEEPSLATE_ORE_PROPERTY));
    public static final RegistryObject<Block> CHROME_ORE_DEEPSLATE = registerBlockWithItem("chrome_ore_deepslate", () -> new Block(ModProperties.DEEPSLATE_ORE_PROPERTY));
    public static final RegistryObject<Block> NICKEL_ORE_DEEPSLATE = registerBlockWithItem("nickel_ore_deepslate", () -> new Block(ModProperties.DEEPSLATE_ORE_PROPERTY));
    public static final RegistryObject<Block> RAW_ALUMINIUM_ORE_BLOCK = registerBlockWithItem("raw_aluminium_ore_block", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> RAW_TIN_ORE_BLOCK = registerBlockWithItem("raw_tin_ore_block", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> RAW_CHROME_ORE_BLOCK = registerBlockWithItem("raw_chrome_ore_block", () -> new Block(ModProperties.ORE_PROPERTY));
    public static final RegistryObject<Block> RAW_NICKEL_ORE_BLOCK = registerBlockWithItem("raw_nickel_ore_block", () -> new Block(ModProperties.ORE_PROPERTY));

    // Crystals
    public static final RegistryObject<Block> AZURITE_BLOCK = registerBlockWithItem("azurite_block", () -> new CrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> RUBY_BLOCK = registerBlockWithItem("ruby_block", () -> new CrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> CASSITERITE_BLOCK = registerBlockWithItem("cassiterite_block", () -> new CrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> AZURITE_CLUSTER = registerBlockWithItem("azurite_cluster", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_CLUSTER_PROPERTY));
    public static final RegistryObject<Block> RUBY_CLUSTER = registerBlockWithItem("ruby_cluster", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_CLUSTER_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_CLUSTER = registerBlockWithItem("cassiterite_cluster", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_CLUSTER_PROPERTY));
    public static final RegistryObject<Block> AZURITE_SMALL_BUD = registerBlockWithItem("azurite_small_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_SMALL_BUD_PROPERTY));
    public static final RegistryObject<Block> RUBY_SMALL_BUD = registerBlockWithItem("ruby_small_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_SMALL_BUD_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_SMALL_BUD = registerBlockWithItem("cassiterite_small_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_SMALL_BUD_PROPERTY));
    public static final RegistryObject<Block> AZURITE_MEDIUM_BUD = registerBlockWithItem("azurite_medium_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_MEDIUM_BUD_PROPERTY));
    public static final RegistryObject<Block> RUBY_MEDIUM_BUD = registerBlockWithItem("ruby_medium_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_MEDIUM_BUD_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_MEDIUM_BUD = registerBlockWithItem("cassiterite_medium_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_MEDIUM_BUD_PROPERTY));
    public static final RegistryObject<Block> AZURITE_LARGE_BUD = registerBlockWithItem("azurite_large_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_LARGE_BUD_PROPERTY));
    public static final RegistryObject<Block> RUBY_LARGE_BUD = registerBlockWithItem("ruby_large_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_LARGE_BUD_PROPERTY));
    public static final RegistryObject<Block> CASSITERITE_LARGE_BUD = registerBlockWithItem("cassiterite_large_bud", () -> new CrystalClusterBlock(7, 3, ModProperties.CRYSTAL_LARGE_BUD_PROPERTY));
    public static final RegistryObject<Block> BUDDING_AZURITE_BLOCK = registerBlockWithItem("budding_azurite_block", () -> new AzuriteBuddingBlock(ModProperties.BUDDING_CRYSTAL_PROPERTY));
    public static final RegistryObject<Block> BUDDING_RUBY_BLOCK = registerBlockWithItem("budding_ruby_block", () -> new RubyBuddingBlock(ModProperties.BUDDING_CRYSTAL_PROPERTY));
    public static final RegistryObject<Block> BUDDING_CASSITERITE_BLOCK = registerBlockWithItem("budding_cassiterite_block", () -> new CassiteriteBuddingBlock(ModProperties.BUDDING_CRYSTAL_PROPERTY));

    // Salt
    public static final RegistryObject<Block> SALT_CRYSTAL_BLOCK = registerBlockWithItem("salt_crystal_block", () -> new Block(ModProperties.SALT_PROPERTY));
    public static final RegistryObject<Block> SALT_LAMP = registerBlockWithItem("salt_lamp", () -> new Block(ModProperties.SALT_LAMP_PROPERTY));
    public static final RegistryObject<Block> SALT_POWDER_BLOCK = registerBlockWithItem("salt_powder_block", () -> new SaltPowderBlock(ModProperties.SALT_LAMP_PROPERTY));

    // Fluids
    public static final RegistryObject<LiquidBlock> OIL_BLOCK = BLOCKS.register("oil_block",
            () -> new LiquidBlock(ModFluids.SOURCE_OIL, BlockBehaviour.Properties.copy(Blocks.WATER)));

    // Technical
    public static final RegistryObject<Block> CRAFTER_MACHINE = registerBlockWithItem("crafter_machine", () -> new CrafterMachineBlock(BlockBehaviour.Properties.of(Material.METAL)));

    // Thermal
    public static final RegistryObject<Block> BURNER = registerBlockWithItem("burner", () -> new BurnerBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS).lightLevel(ModUtils.getLightValueLit(13))));
    public static final RegistryObject<Block> BELLOWS = registerBlockWithItem("bellows", () -> new BellowsBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> STOKED_FIRE = ModBlocks.BLOCKS.register("stoked_fire", () -> new StokedFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE).noOcclusion().noCollission()));
    public static final RegistryObject<Block> BRICK_FOUNDRY = registerBlockWithItem("brick_foundry", () -> new FoundryBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> BRICK_CHUTE = registerBlockWithItem("brick_chute", () -> new ChuteBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> BRICK_FOUNDRY_PIPING = registerBlockWithItem("brick_foundry_piping", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BRICKS)));

    // Mechanical
    public static final RegistryObject<Block> GEARBOX = registerBlockWithItem("gearbox", () -> new GearboxBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CRANK = registerBlockWithItem("crank", () -> new CrankBlock(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion().noCollission()));
    public static final RegistryObject<Block> TURBINE = registerBlockWithItem("turbine", () -> new TurbineBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> CONVEYOR_BELT = registerBlockWithItem("conveyor_belt", () -> new ConveyorBeltBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> STRETCHER = registerBlockWithItem("stretcher", () -> new StretcherBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> CRUSHER = registerBlockWithItem("crusher", () -> new CrusherBlock(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.COPPER).noOcclusion()));

    // Electrical
    public static final RegistryObject<Block> DYNAMO = registerBlockWithItem("dynamo", () -> new DynamoBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BATTERY_BOX = registerBlockWithItem("battery_box", () -> new BatteryBoxBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static <BLOCK extends Block> RegistryObject<BLOCK> registerBlockWithItem(String name, Supplier<BLOCK> blockSupplier, Item.Properties properties)
    {
        RegistryObject<BLOCK> block = ModBlocks.BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    public static <BLOCK extends Block> RegistryObject<BLOCK> registerBlockWithItem(String name, Supplier<BLOCK> blockSupplier)
    {
        RegistryObject<BLOCK> block = ModBlocks.BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), ModProperties.ITEM_PROPERTY));
        return block;
    }
}