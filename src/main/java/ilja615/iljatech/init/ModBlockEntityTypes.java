package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.tileentities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntityTypes
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, IljaTech.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrafterMachineBlockEntity>> CRAFTER_MACHINE = BLOCK_ENTITY_TYPES.register("crafter_machine", () -> BlockEntityType.Builder.of(CrafterMachineBlockEntity::new, ModBlocks.CRAFTER_MACHINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BurnerBlockEntity>> BURNER = BLOCK_ENTITY_TYPES.register("burner", () -> BlockEntityType.Builder.of(BurnerBlockEntity::new, ModBlocks.BURNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BellowsBlockEntity>> BELLOWS = BLOCK_ENTITY_TYPES.register("bellows", () -> BlockEntityType.Builder.of(BellowsBlockEntity::new, ModBlocks.BELLOWS.get()).build(null));
    public static final RegistryObject<BlockEntityType<TurbineBlockEntity>> TURBINE = BLOCK_ENTITY_TYPES.register("turbine", () -> BlockEntityType.Builder.of(TurbineBlockEntity::new, ModBlocks.TURBINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ConveyorBeltBlockEntity>> CONVEYOR_BELT = BLOCK_ENTITY_TYPES.register("conveyor_belt", () -> BlockEntityType.Builder.of(ConveyorBeltBlockEntity::new, ModBlocks.CONVEYOR_BELT.get()).build(null));
}
