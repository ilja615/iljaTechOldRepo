package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.battery_box.BatteryBoxBlockEntity;
import ilja615.iljatech.blocks.bellows.BellowsBlockEntity;
import ilja615.iljatech.blocks.burner.BurnerBlockEntity;
import ilja615.iljatech.blocks.conveyor_belt.ConveyorBeltBlockEntity;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlockEntity;
import ilja615.iljatech.blocks.crusher.CrusherBlockEntity;
import ilja615.iljatech.blocks.dynamo.DynamoBlockEntity;
import ilja615.iljatech.blocks.foundry.FoundryBlockEntity;
import ilja615.iljatech.blocks.foundry.ChuteBlockEntity;
import ilja615.iljatech.blocks.stretcher.StretcherBlockEntity;
import ilja615.iljatech.blocks.stretcher.StretcherSpecialRenderer;
import ilja615.iljatech.blocks.turbine.TurbineBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrafterMachineBlockEntity>> CRAFTER_MACHINE = BLOCK_ENTITY_TYPES.register("crafter_machine", () -> BlockEntityType.Builder.of(CrafterMachineBlockEntity::new, ModBlocks.CRAFTER_MACHINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BurnerBlockEntity>> BURNER = BLOCK_ENTITY_TYPES.register("burner", () -> BlockEntityType.Builder.of(BurnerBlockEntity::new, ModBlocks.BURNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BellowsBlockEntity>> BELLOWS = BLOCK_ENTITY_TYPES.register("bellows", () -> BlockEntityType.Builder.of(BellowsBlockEntity::new, ModBlocks.BELLOWS.get()).build(null));
    public static final RegistryObject<BlockEntityType<TurbineBlockEntity>> TURBINE = BLOCK_ENTITY_TYPES.register("turbine", () -> BlockEntityType.Builder.of(TurbineBlockEntity::new, ModBlocks.TURBINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ConveyorBeltBlockEntity>> CONVEYOR_BELT = BLOCK_ENTITY_TYPES.register("conveyor_belt", () -> BlockEntityType.Builder.of(ConveyorBeltBlockEntity::new, ModBlocks.CONVEYOR_BELT.get()).build(null));
    public static final RegistryObject<BlockEntityType<StretcherBlockEntity>> STRETCHER = BLOCK_ENTITY_TYPES.register("stretcher", () -> BlockEntityType.Builder.of(StretcherBlockEntity::new, ModBlocks.STRETCHER.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER = BLOCK_ENTITY_TYPES.register("crusher", () -> BlockEntityType.Builder.of(CrusherBlockEntity::new, ModBlocks.CRUSHER.get()).build(null));
    public static final RegistryObject<BlockEntityType<FoundryBlockEntity>> FOUNDRY = BLOCK_ENTITY_TYPES.register("foundry", () -> BlockEntityType.Builder.of(FoundryBlockEntity::new, ModBlocks.BRICK_FOUNDRY.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChuteBlockEntity>> CHUTE = BLOCK_ENTITY_TYPES.register("chute", () -> BlockEntityType.Builder.of(ChuteBlockEntity::new, ModBlocks.BRICK_CHUTE.get()).build(null));
    public static final RegistryObject<BlockEntityType<DynamoBlockEntity>> DYNAMO = BLOCK_ENTITY_TYPES.register("dynamo", () -> BlockEntityType.Builder.of(DynamoBlockEntity::new, ModBlocks.DYNAMO.get()).build(null));
    public static final RegistryObject<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX = BLOCK_ENTITY_TYPES.register("battery_box", () -> BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX.get()).build(null));


    public static void registerBlockEntityRenderers()
    {
        BlockEntityRenderers.register(ModBlockEntityTypes.STRETCHER.get(), StretcherSpecialRenderer::new);
    }
}
