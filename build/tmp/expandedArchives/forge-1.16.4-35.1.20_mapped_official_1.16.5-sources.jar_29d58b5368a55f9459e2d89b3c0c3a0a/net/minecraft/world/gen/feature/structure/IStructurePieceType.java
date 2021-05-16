package net.minecraft.world.gen.feature.structure;

import java.util.Locale;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface IStructurePieceType {
   IStructurePieceType MINE_SHAFT_CORRIDOR = setPieceId(MineshaftPieces.Corridor::new, "MSCorridor");
   IStructurePieceType MINE_SHAFT_CROSSING = setPieceId(MineshaftPieces.Cross::new, "MSCrossing");
   IStructurePieceType MINE_SHAFT_ROOM = setPieceId(MineshaftPieces.Room::new, "MSRoom");
   IStructurePieceType MINE_SHAFT_STAIRS = setPieceId(MineshaftPieces.Stairs::new, "MSStairs");
   IStructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = setPieceId(FortressPieces.Crossing3::new, "NeBCr");
   IStructurePieceType NETHER_FORTRESS_BRIDGE_END_FILLER = setPieceId(FortressPieces.End::new, "NeBEF");
   IStructurePieceType NETHER_FORTRESS_BRIDGE_STRAIGHT = setPieceId(FortressPieces.Straight::new, "NeBS");
   IStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS = setPieceId(FortressPieces.Corridor3::new, "NeCCS");
   IStructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY = setPieceId(FortressPieces.Corridor4::new, "NeCTB");
   IStructurePieceType NETHER_FORTRESS_CASTLE_ENTRANCE = setPieceId(FortressPieces.Entrance::new, "NeCE");
   IStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING = setPieceId(FortressPieces.Crossing2::new, "NeSCSC");
   IStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN = setPieceId(FortressPieces.Corridor::new, "NeSCLT");
   IStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR = setPieceId(FortressPieces.Corridor5::new, "NeSC");
   IStructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = setPieceId(FortressPieces.Corridor2::new, "NeSCRT");
   IStructurePieceType NETHER_FORTRESS_CASTLE_STALK_ROOM = setPieceId(FortressPieces.NetherStalkRoom::new, "NeCSR");
   IStructurePieceType NETHER_FORTRESS_MONSTER_THRONE = setPieceId(FortressPieces.Throne::new, "NeMT");
   IStructurePieceType NETHER_FORTRESS_ROOM_CROSSING = setPieceId(FortressPieces.Crossing::new, "NeRC");
   IStructurePieceType NETHER_FORTRESS_STAIRS_ROOM = setPieceId(FortressPieces.Stairs::new, "NeSR");
   IStructurePieceType NETHER_FORTRESS_START = setPieceId(FortressPieces.Start::new, "NeStart");
   IStructurePieceType STRONGHOLD_CHEST_CORRIDOR = setPieceId(StrongholdPieces.ChestCorridor::new, "SHCC");
   IStructurePieceType STRONGHOLD_FILLER_CORRIDOR = setPieceId(StrongholdPieces.Corridor::new, "SHFC");
   IStructurePieceType STRONGHOLD_FIVE_CROSSING = setPieceId(StrongholdPieces.Crossing::new, "SH5C");
   IStructurePieceType STRONGHOLD_LEFT_TURN = setPieceId(StrongholdPieces.LeftTurn::new, "SHLT");
   IStructurePieceType STRONGHOLD_LIBRARY = setPieceId(StrongholdPieces.Library::new, "SHLi");
   IStructurePieceType STRONGHOLD_PORTAL_ROOM = setPieceId(StrongholdPieces.PortalRoom::new, "SHPR");
   IStructurePieceType STRONGHOLD_PRISON_HALL = setPieceId(StrongholdPieces.Prison::new, "SHPH");
   IStructurePieceType STRONGHOLD_RIGHT_TURN = setPieceId(StrongholdPieces.RightTurn::new, "SHRT");
   IStructurePieceType STRONGHOLD_ROOM_CROSSING = setPieceId(StrongholdPieces.RoomCrossing::new, "SHRC");
   IStructurePieceType STRONGHOLD_STAIRS_DOWN = setPieceId(StrongholdPieces.Stairs::new, "SHSD");
   IStructurePieceType STRONGHOLD_START = setPieceId(StrongholdPieces.Stairs2::new, "SHStart");
   IStructurePieceType STRONGHOLD_STRAIGHT = setPieceId(StrongholdPieces.Straight::new, "SHS");
   IStructurePieceType STRONGHOLD_STRAIGHT_STAIRS_DOWN = setPieceId(StrongholdPieces.StairsStraight::new, "SHSSD");
   IStructurePieceType JUNGLE_PYRAMID_PIECE = setPieceId(JunglePyramidPiece::new, "TeJP");
   IStructurePieceType OCEAN_RUIN = setPieceId(OceanRuinPieces.Piece::new, "ORP");
   IStructurePieceType IGLOO = setPieceId(IglooPieces.Piece::new, "Iglu");
   IStructurePieceType RUINED_PORTAL = setPieceId(RuinedPortalPiece::new, "RUPO");
   IStructurePieceType SWAMPLAND_HUT = setPieceId(SwampHutPiece::new, "TeSH");
   IStructurePieceType DESERT_PYRAMID_PIECE = setPieceId(DesertPyramidPiece::new, "TeDP");
   IStructurePieceType OCEAN_MONUMENT_BUILDING = setPieceId(OceanMonumentPieces.MonumentBuilding::new, "OMB");
   IStructurePieceType OCEAN_MONUMENT_CORE_ROOM = setPieceId(OceanMonumentPieces.MonumentCoreRoom::new, "OMCR");
   IStructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = setPieceId(OceanMonumentPieces.DoubleXRoom::new, "OMDXR");
   IStructurePieceType OCEAN_MONUMENT_DOUBLE_XY_ROOM = setPieceId(OceanMonumentPieces.DoubleXYRoom::new, "OMDXYR");
   IStructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = setPieceId(OceanMonumentPieces.DoubleYRoom::new, "OMDYR");
   IStructurePieceType OCEAN_MONUMENT_DOUBLE_YZ_ROOM = setPieceId(OceanMonumentPieces.DoubleYZRoom::new, "OMDYZR");
   IStructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = setPieceId(OceanMonumentPieces.DoubleZRoom::new, "OMDZR");
   IStructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = setPieceId(OceanMonumentPieces.EntryRoom::new, "OMEntry");
   IStructurePieceType OCEAN_MONUMENT_PENTHOUSE = setPieceId(OceanMonumentPieces.Penthouse::new, "OMPenthouse");
   IStructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = setPieceId(OceanMonumentPieces.SimpleRoom::new, "OMSimple");
   IStructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = setPieceId(OceanMonumentPieces.SimpleTopRoom::new, "OMSimpleT");
   IStructurePieceType OCEAN_MONUMENT_WING_ROOM = setPieceId(OceanMonumentPieces.WingRoom::new, "OMWR");
   IStructurePieceType END_CITY_PIECE = setPieceId(EndCityPieces.CityTemplate::new, "ECP");
   IStructurePieceType WOODLAND_MANSION_PIECE = setPieceId(WoodlandMansionPieces.MansionTemplate::new, "WMP");
   IStructurePieceType BURIED_TREASURE_PIECE = setPieceId(BuriedTreasure.Piece::new, "BTP");
   IStructurePieceType SHIPWRECK_PIECE = setPieceId(ShipwreckPieces.Piece::new, "Shipwreck");
   IStructurePieceType NETHER_FOSSIL = setPieceId(NetherFossilStructures.Piece::new, "NeFos");
   IStructurePieceType JIGSAW = setPieceId(AbstractVillagePiece::new, "jigsaw");

   StructurePiece load(TemplateManager p_load_1_, CompoundNBT p_load_2_);

   static IStructurePieceType setPieceId(IStructurePieceType p_214750_0_, String p_214750_1_) {
      return Registry.register(Registry.STRUCTURE_PIECE, p_214750_1_.toLowerCase(Locale.ROOT), p_214750_0_);
   }
}
