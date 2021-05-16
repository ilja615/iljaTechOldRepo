package net.minecraft.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.IServerLoginNetHandler;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CJigsawBlockGeneratePacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMarkRecipeSeenPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateRecipeBookStatusPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.network.status.IServerStatusNetHandler;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;

public enum ProtocolType {
   HANDSHAKING(-1, protocol().addFlow(PacketDirection.SERVERBOUND, (new ProtocolType.PacketList<IHandshakeNetHandler>()).addPacket(CHandshakePacket.class, CHandshakePacket::new))),
   PLAY(0, protocol().addFlow(PacketDirection.CLIENTBOUND, (new ProtocolType.PacketList<IClientPlayNetHandler>()).addPacket(SSpawnObjectPacket.class, SSpawnObjectPacket::new).addPacket(SSpawnExperienceOrbPacket.class, SSpawnExperienceOrbPacket::new).addPacket(SSpawnMobPacket.class, SSpawnMobPacket::new).addPacket(SSpawnPaintingPacket.class, SSpawnPaintingPacket::new).addPacket(SSpawnPlayerPacket.class, SSpawnPlayerPacket::new).addPacket(SAnimateHandPacket.class, SAnimateHandPacket::new).addPacket(SStatisticsPacket.class, SStatisticsPacket::new).addPacket(SPlayerDiggingPacket.class, SPlayerDiggingPacket::new).addPacket(SAnimateBlockBreakPacket.class, SAnimateBlockBreakPacket::new).addPacket(SUpdateTileEntityPacket.class, SUpdateTileEntityPacket::new).addPacket(SBlockActionPacket.class, SBlockActionPacket::new).addPacket(SChangeBlockPacket.class, SChangeBlockPacket::new).addPacket(SUpdateBossInfoPacket.class, SUpdateBossInfoPacket::new).addPacket(SServerDifficultyPacket.class, SServerDifficultyPacket::new).addPacket(SChatPacket.class, SChatPacket::new).addPacket(STabCompletePacket.class, STabCompletePacket::new).addPacket(SCommandListPacket.class, SCommandListPacket::new).addPacket(SConfirmTransactionPacket.class, SConfirmTransactionPacket::new).addPacket(SCloseWindowPacket.class, SCloseWindowPacket::new).addPacket(SWindowItemsPacket.class, SWindowItemsPacket::new).addPacket(SWindowPropertyPacket.class, SWindowPropertyPacket::new).addPacket(SSetSlotPacket.class, SSetSlotPacket::new).addPacket(SCooldownPacket.class, SCooldownPacket::new).addPacket(SCustomPayloadPlayPacket.class, SCustomPayloadPlayPacket::new).addPacket(SPlaySoundPacket.class, SPlaySoundPacket::new).addPacket(SDisconnectPacket.class, SDisconnectPacket::new).addPacket(SEntityStatusPacket.class, SEntityStatusPacket::new).addPacket(SExplosionPacket.class, SExplosionPacket::new).addPacket(SUnloadChunkPacket.class, SUnloadChunkPacket::new).addPacket(SChangeGameStatePacket.class, SChangeGameStatePacket::new).addPacket(SOpenHorseWindowPacket.class, SOpenHorseWindowPacket::new).addPacket(SKeepAlivePacket.class, SKeepAlivePacket::new).addPacket(SChunkDataPacket.class, SChunkDataPacket::new).addPacket(SPlaySoundEventPacket.class, SPlaySoundEventPacket::new).addPacket(SSpawnParticlePacket.class, SSpawnParticlePacket::new).addPacket(SUpdateLightPacket.class, SUpdateLightPacket::new).addPacket(SJoinGamePacket.class, SJoinGamePacket::new).addPacket(SMapDataPacket.class, SMapDataPacket::new).addPacket(SMerchantOffersPacket.class, SMerchantOffersPacket::new).addPacket(SEntityPacket.RelativeMovePacket.class, SEntityPacket.RelativeMovePacket::new).addPacket(SEntityPacket.MovePacket.class, SEntityPacket.MovePacket::new).addPacket(SEntityPacket.LookPacket.class, SEntityPacket.LookPacket::new).addPacket(SEntityPacket.class, SEntityPacket::new).addPacket(SMoveVehiclePacket.class, SMoveVehiclePacket::new).addPacket(SOpenBookWindowPacket.class, SOpenBookWindowPacket::new).addPacket(SOpenWindowPacket.class, SOpenWindowPacket::new).addPacket(SOpenSignMenuPacket.class, SOpenSignMenuPacket::new).addPacket(SPlaceGhostRecipePacket.class, SPlaceGhostRecipePacket::new).addPacket(SPlayerAbilitiesPacket.class, SPlayerAbilitiesPacket::new).addPacket(SCombatPacket.class, SCombatPacket::new).addPacket(SPlayerListItemPacket.class, SPlayerListItemPacket::new).addPacket(SPlayerLookPacket.class, SPlayerLookPacket::new).addPacket(SPlayerPositionLookPacket.class, SPlayerPositionLookPacket::new).addPacket(SRecipeBookPacket.class, SRecipeBookPacket::new).addPacket(SDestroyEntitiesPacket.class, SDestroyEntitiesPacket::new).addPacket(SRemoveEntityEffectPacket.class, SRemoveEntityEffectPacket::new).addPacket(SSendResourcePackPacket.class, SSendResourcePackPacket::new).addPacket(SRespawnPacket.class, SRespawnPacket::new).addPacket(SEntityHeadLookPacket.class, SEntityHeadLookPacket::new).addPacket(SMultiBlockChangePacket.class, SMultiBlockChangePacket::new).addPacket(SSelectAdvancementsTabPacket.class, SSelectAdvancementsTabPacket::new).addPacket(SWorldBorderPacket.class, SWorldBorderPacket::new).addPacket(SCameraPacket.class, SCameraPacket::new).addPacket(SHeldItemChangePacket.class, SHeldItemChangePacket::new).addPacket(SUpdateChunkPositionPacket.class, SUpdateChunkPositionPacket::new).addPacket(SUpdateViewDistancePacket.class, SUpdateViewDistancePacket::new).addPacket(SWorldSpawnChangedPacket.class, SWorldSpawnChangedPacket::new).addPacket(SDisplayObjectivePacket.class, SDisplayObjectivePacket::new).addPacket(SEntityMetadataPacket.class, SEntityMetadataPacket::new).addPacket(SMountEntityPacket.class, SMountEntityPacket::new).addPacket(SEntityVelocityPacket.class, SEntityVelocityPacket::new).addPacket(SEntityEquipmentPacket.class, SEntityEquipmentPacket::new).addPacket(SSetExperiencePacket.class, SSetExperiencePacket::new).addPacket(SUpdateHealthPacket.class, SUpdateHealthPacket::new).addPacket(SScoreboardObjectivePacket.class, SScoreboardObjectivePacket::new).addPacket(SSetPassengersPacket.class, SSetPassengersPacket::new).addPacket(STeamsPacket.class, STeamsPacket::new).addPacket(SUpdateScorePacket.class, SUpdateScorePacket::new).addPacket(SUpdateTimePacket.class, SUpdateTimePacket::new).addPacket(STitlePacket.class, STitlePacket::new).addPacket(SSpawnMovingSoundEffectPacket.class, SSpawnMovingSoundEffectPacket::new).addPacket(SPlaySoundEffectPacket.class, SPlaySoundEffectPacket::new).addPacket(SStopSoundPacket.class, SStopSoundPacket::new).addPacket(SPlayerListHeaderFooterPacket.class, SPlayerListHeaderFooterPacket::new).addPacket(SQueryNBTResponsePacket.class, SQueryNBTResponsePacket::new).addPacket(SCollectItemPacket.class, SCollectItemPacket::new).addPacket(SEntityTeleportPacket.class, SEntityTeleportPacket::new).addPacket(SAdvancementInfoPacket.class, SAdvancementInfoPacket::new).addPacket(SEntityPropertiesPacket.class, SEntityPropertiesPacket::new).addPacket(SPlayEntityEffectPacket.class, SPlayEntityEffectPacket::new).addPacket(SUpdateRecipesPacket.class, SUpdateRecipesPacket::new).addPacket(STagsListPacket.class, STagsListPacket::new)).addFlow(PacketDirection.SERVERBOUND, (new ProtocolType.PacketList<IServerPlayNetHandler>()).addPacket(CConfirmTeleportPacket.class, CConfirmTeleportPacket::new).addPacket(CQueryTileEntityNBTPacket.class, CQueryTileEntityNBTPacket::new).addPacket(CSetDifficultyPacket.class, CSetDifficultyPacket::new).addPacket(CChatMessagePacket.class, CChatMessagePacket::new).addPacket(CClientStatusPacket.class, CClientStatusPacket::new).addPacket(CClientSettingsPacket.class, CClientSettingsPacket::new).addPacket(CTabCompletePacket.class, CTabCompletePacket::new).addPacket(CConfirmTransactionPacket.class, CConfirmTransactionPacket::new).addPacket(CEnchantItemPacket.class, CEnchantItemPacket::new).addPacket(CClickWindowPacket.class, CClickWindowPacket::new).addPacket(CCloseWindowPacket.class, CCloseWindowPacket::new).addPacket(CCustomPayloadPacket.class, CCustomPayloadPacket::new).addPacket(CEditBookPacket.class, CEditBookPacket::new).addPacket(CQueryEntityNBTPacket.class, CQueryEntityNBTPacket::new).addPacket(CUseEntityPacket.class, CUseEntityPacket::new).addPacket(CJigsawBlockGeneratePacket.class, CJigsawBlockGeneratePacket::new).addPacket(CKeepAlivePacket.class, CKeepAlivePacket::new).addPacket(CLockDifficultyPacket.class, CLockDifficultyPacket::new).addPacket(CPlayerPacket.PositionPacket.class, CPlayerPacket.PositionPacket::new).addPacket(CPlayerPacket.PositionRotationPacket.class, CPlayerPacket.PositionRotationPacket::new).addPacket(CPlayerPacket.RotationPacket.class, CPlayerPacket.RotationPacket::new).addPacket(CPlayerPacket.class, CPlayerPacket::new).addPacket(CMoveVehiclePacket.class, CMoveVehiclePacket::new).addPacket(CSteerBoatPacket.class, CSteerBoatPacket::new).addPacket(CPickItemPacket.class, CPickItemPacket::new).addPacket(CPlaceRecipePacket.class, CPlaceRecipePacket::new).addPacket(CPlayerAbilitiesPacket.class, CPlayerAbilitiesPacket::new).addPacket(CPlayerDiggingPacket.class, CPlayerDiggingPacket::new).addPacket(CEntityActionPacket.class, CEntityActionPacket::new).addPacket(CInputPacket.class, CInputPacket::new).addPacket(CUpdateRecipeBookStatusPacket.class, CUpdateRecipeBookStatusPacket::new).addPacket(CMarkRecipeSeenPacket.class, CMarkRecipeSeenPacket::new).addPacket(CRenameItemPacket.class, CRenameItemPacket::new).addPacket(CResourcePackStatusPacket.class, CResourcePackStatusPacket::new).addPacket(CSeenAdvancementsPacket.class, CSeenAdvancementsPacket::new).addPacket(CSelectTradePacket.class, CSelectTradePacket::new).addPacket(CUpdateBeaconPacket.class, CUpdateBeaconPacket::new).addPacket(CHeldItemChangePacket.class, CHeldItemChangePacket::new).addPacket(CUpdateCommandBlockPacket.class, CUpdateCommandBlockPacket::new).addPacket(CUpdateMinecartCommandBlockPacket.class, CUpdateMinecartCommandBlockPacket::new).addPacket(CCreativeInventoryActionPacket.class, CCreativeInventoryActionPacket::new).addPacket(CUpdateJigsawBlockPacket.class, CUpdateJigsawBlockPacket::new).addPacket(CUpdateStructureBlockPacket.class, CUpdateStructureBlockPacket::new).addPacket(CUpdateSignPacket.class, CUpdateSignPacket::new).addPacket(CAnimateHandPacket.class, CAnimateHandPacket::new).addPacket(CSpectatePacket.class, CSpectatePacket::new).addPacket(CPlayerTryUseItemOnBlockPacket.class, CPlayerTryUseItemOnBlockPacket::new).addPacket(CPlayerTryUseItemPacket.class, CPlayerTryUseItemPacket::new))),
   STATUS(1, protocol().addFlow(PacketDirection.SERVERBOUND, (new ProtocolType.PacketList<IServerStatusNetHandler>()).addPacket(CServerQueryPacket.class, CServerQueryPacket::new).addPacket(CPingPacket.class, CPingPacket::new)).addFlow(PacketDirection.CLIENTBOUND, (new ProtocolType.PacketList<IClientStatusNetHandler>()).addPacket(SServerInfoPacket.class, SServerInfoPacket::new).addPacket(SPongPacket.class, SPongPacket::new))),
   LOGIN(2, protocol().addFlow(PacketDirection.CLIENTBOUND, (new ProtocolType.PacketList<IClientLoginNetHandler>()).addPacket(SDisconnectLoginPacket.class, SDisconnectLoginPacket::new).addPacket(SEncryptionRequestPacket.class, SEncryptionRequestPacket::new).addPacket(SLoginSuccessPacket.class, SLoginSuccessPacket::new).addPacket(SEnableCompressionPacket.class, SEnableCompressionPacket::new).addPacket(SCustomPayloadLoginPacket.class, SCustomPayloadLoginPacket::new)).addFlow(PacketDirection.SERVERBOUND, (new ProtocolType.PacketList<IServerLoginNetHandler>()).addPacket(CLoginStartPacket.class, CLoginStartPacket::new).addPacket(CEncryptionResponsePacket.class, CEncryptionResponsePacket::new).addPacket(CCustomPayloadLoginPacket.class, CCustomPayloadLoginPacket::new)));

   private static final ProtocolType[] LOOKUP = new ProtocolType[4];
   private static final Map<Class<? extends IPacket<?>>, ProtocolType> PROTOCOL_BY_PACKET = Maps.newHashMap();
   private final int id;
   private final Map<PacketDirection, ? extends ProtocolType.PacketList<?>> flows;

   private static ProtocolType.PacketRegistry protocol() {
      return new ProtocolType.PacketRegistry();
   }

   private ProtocolType(int p_i226083_3_, ProtocolType.PacketRegistry p_i226083_4_) {
      this.id = p_i226083_3_;
      this.flows = p_i226083_4_.flows;
   }

   @Nullable
   public Integer getPacketId(PacketDirection p_179246_1_, IPacket<?> p_179246_2_) {
      return this.flows.get(p_179246_1_).getId(p_179246_2_.getClass());
   }

   @Nullable
   public IPacket<?> createPacket(PacketDirection p_179244_1_, int p_179244_2_) {
      return this.flows.get(p_179244_1_).createPacket(p_179244_2_);
   }

   public int getId() {
      return this.id;
   }

   @Nullable
   public static ProtocolType getById(int p_150760_0_) {
      return p_150760_0_ >= -1 && p_150760_0_ <= 2 ? LOOKUP[p_150760_0_ - -1] : null;
   }

   public static ProtocolType getProtocolForPacket(IPacket<?> p_150752_0_) {
      return PROTOCOL_BY_PACKET.get(p_150752_0_.getClass());
   }

   static {
      for(ProtocolType protocoltype : values()) {
         int i = protocoltype.getId();
         if (i < -1 || i > 2) {
            throw new Error("Invalid protocol ID " + Integer.toString(i));
         }

         LOOKUP[i - -1] = protocoltype;
         protocoltype.flows.forEach((p_229713_1_, p_229713_2_) -> {
            p_229713_2_.getAllPackets().forEach((p_229712_1_) -> {
               if (PROTOCOL_BY_PACKET.containsKey(p_229712_1_) && PROTOCOL_BY_PACKET.get(p_229712_1_) != protocoltype) {
                  throw new IllegalStateException("Packet " + p_229712_1_ + " is already assigned to protocol " + PROTOCOL_BY_PACKET.get(p_229712_1_) + " - can't reassign to " + protocoltype);
               } else {
                  PROTOCOL_BY_PACKET.put(p_229712_1_, protocoltype);
               }
            });
         });
      }

   }

   static class PacketList<T extends INetHandler> {
      private final Object2IntMap<Class<? extends IPacket<T>>> classToId = Util.make(new Object2IntOpenHashMap<>(), (p_229719_0_) -> {
         p_229719_0_.defaultReturnValue(-1);
      });
      private final List<Supplier<? extends IPacket<T>>> idToConstructor = Lists.newArrayList();

      private PacketList() {
      }

      public <P extends IPacket<T>> ProtocolType.PacketList<T> addPacket(Class<P> p_229721_1_, Supplier<P> p_229721_2_) {
         int i = this.idToConstructor.size();
         int j = this.classToId.put(p_229721_1_, i);
         if (j != -1) {
            String s = "Packet " + p_229721_1_ + " is already registered to ID " + j;
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
         } else {
            this.idToConstructor.add(p_229721_2_);
            return this;
         }
      }

      @Nullable
      public Integer getId(Class<?> p_229720_1_) {
         int i = this.classToId.getInt(p_229720_1_);
         return i == -1 ? null : i;
      }

      @Nullable
      public IPacket<?> createPacket(int p_229718_1_) {
         Supplier<? extends IPacket<T>> supplier = this.idToConstructor.get(p_229718_1_);
         return supplier != null ? supplier.get() : null;
      }

      public Iterable<Class<? extends IPacket<?>>> getAllPackets() {
         return Iterables.unmodifiableIterable(this.classToId.keySet());
      }
   }

   static class PacketRegistry {
      private final Map<PacketDirection, ProtocolType.PacketList<?>> flows = Maps.newEnumMap(PacketDirection.class);

      private PacketRegistry() {
      }

      public <T extends INetHandler> ProtocolType.PacketRegistry addFlow(PacketDirection p_229724_1_, ProtocolType.PacketList<T> p_229724_2_) {
         this.flows.put(p_229724_1_, p_229724_2_);
         return this;
      }
   }
}
