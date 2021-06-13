package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.tileentities.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, IljaTech.MOD_ID);

    public static final RegistryObject<TileEntityType<CrafterMachineTileEntity>> CRAFTER_MACHINE = TILE_ENTITY_TYPES.register("crafter_machine", () -> TileEntityType.Builder.of(CrafterMachineTileEntity::new, ModBlocks.CRAFTER_MACHINE.get()).build(null));
    public static final RegistryObject<TileEntityType<BurnerTileEntity>> BURNER = TILE_ENTITY_TYPES.register("burner", () -> TileEntityType.Builder.of(BurnerTileEntity::new, ModBlocks.BURNER.get()).build(null));
    public static final RegistryObject<TileEntityType<BellowsTileEntity>> BELLOWS = TILE_ENTITY_TYPES.register("bellows", () -> TileEntityType.Builder.of(BellowsTileEntity::new, ModBlocks.BELLOWS.get()).build(null));
    public static final RegistryObject<TileEntityType<TurbineTileEntity>> TURBINE = TILE_ENTITY_TYPES.register("turbine", () -> TileEntityType.Builder.of(TurbineTileEntity::new, ModBlocks.TURBINE.get()).build(null));
    public static final RegistryObject<TileEntityType<ConveyorBeltTileEntity>> CONVEYOR_BELT = TILE_ENTITY_TYPES.register("conveyor_belt", () -> TileEntityType.Builder.of(ConveyorBeltTileEntity::new, ModBlocks.CONVEYOR_BELT.get()).build(null));
}
