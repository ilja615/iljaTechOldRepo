package ilja615.iljatech.init;

import ilja615.iljatech.blocks.wire.WireShape;
import ilja615.iljatech.mechanicalpower.MechanicalPower;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;

public class ModProperties
{
    //food
    public static final FoodProperties BOILED_EGG = new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).build();

    //items properties
    public static final Item.Properties ITEM_PROPERTY = new Item.Properties();
    public static final Item.Properties ITEM_PROPERTY_NOT_STACKABLE = new Item.Properties().stacksTo(1);
    public static final Item.Properties BOILED_EGG_PROPERTY = new Item.Properties().food(BOILED_EGG);

    //block properties
    public static final Block.Properties CRYSTAL_CLUSTER_PROPERTY = BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel(emission -> 5);
    public static final Block.Properties CRYSTAL_SMALL_BUD_PROPERTY = BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel(emission -> 4);
    public static final Block.Properties CRYSTAL_MEDIUM_BUD_PROPERTY = BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel(emission -> 2);
    public static final Block.Properties CRYSTAL_LARGE_BUD_PROPERTY = BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel(emission -> 1);
    public static final Block.Properties BUDDING_CRYSTAL_PROPERTY = BlockBehaviour.Properties.of(Material.AMETHYST).randomTicks().strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops();
    public static final Block.Properties BARBED_WIRE_PROPERTY = BlockBehaviour.Properties.of(Material.METAL).noCollission().requiresCorrectToolForDrops().strength(4.0F);
    public static final Block.Properties ORE_PROPERTY = BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F);
    public static final Block.Properties DEEPSLATE_ORE_PROPERTY = BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.5F, 3.0F);
    public static final Block.Properties SALT_PROPERTY = BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 2.0F).sound(SoundType.CALCITE);
    public static final Block.Properties SALT_LAMP_PROPERTY = BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 2.0F).sound(SoundType.CALCITE). lightLevel((blockState) -> 3);
    public static final Block.Properties SALT_POWDER_PROPERTY = BlockBehaviour.Properties.of(Material.SAND).requiresCorrectToolForDrops().strength(2.0F, 2.0F).sound(SoundType.SAND);
    // TODO : add a custom sound for salt :)

    //state properties
    public static final EnumProperty MECHANICAL_POWER = EnumProperty.create("mechanical_power", MechanicalPower.class);
    public static final EnumProperty<WireShape> WIRE_SHAPE = EnumProperty.create("wire_shape", WireShape.class);
}
