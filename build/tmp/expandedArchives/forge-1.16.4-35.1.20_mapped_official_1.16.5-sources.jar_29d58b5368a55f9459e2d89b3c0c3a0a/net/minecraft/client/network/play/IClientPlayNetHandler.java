package net.minecraft.client.network.play;

import net.minecraft.network.INetHandler;
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

public interface IClientPlayNetHandler extends INetHandler {
   void handleAddEntity(SSpawnObjectPacket p_147235_1_);

   void handleAddExperienceOrb(SSpawnExperienceOrbPacket p_147286_1_);

   void handleAddMob(SSpawnMobPacket p_147281_1_);

   void handleAddObjective(SScoreboardObjectivePacket p_147291_1_);

   void handleAddPainting(SSpawnPaintingPacket p_147288_1_);

   void handleAddPlayer(SSpawnPlayerPacket p_147237_1_);

   void handleAnimate(SAnimateHandPacket p_147279_1_);

   void handleAwardStats(SStatisticsPacket p_147293_1_);

   void handleAddOrRemoveRecipes(SRecipeBookPacket p_191980_1_);

   void handleBlockDestruction(SAnimateBlockBreakPacket p_147294_1_);

   void handleOpenSignEditor(SOpenSignMenuPacket p_147268_1_);

   void handleBlockEntityData(SUpdateTileEntityPacket p_147273_1_);

   void handleBlockEvent(SBlockActionPacket p_147261_1_);

   void handleBlockUpdate(SChangeBlockPacket p_147234_1_);

   void handleChat(SChatPacket p_147251_1_);

   void handleChunkBlocksUpdate(SMultiBlockChangePacket p_147287_1_);

   void handleMapItemData(SMapDataPacket p_147264_1_);

   void handleContainerAck(SConfirmTransactionPacket p_147239_1_);

   void handleContainerClose(SCloseWindowPacket p_147276_1_);

   void handleContainerContent(SWindowItemsPacket p_147241_1_);

   void handleHorseScreenOpen(SOpenHorseWindowPacket p_217271_1_);

   void handleContainerSetData(SWindowPropertyPacket p_147245_1_);

   void handleContainerSetSlot(SSetSlotPacket p_147266_1_);

   void handleCustomPayload(SCustomPayloadPlayPacket p_147240_1_);

   void handleDisconnect(SDisconnectPacket p_147253_1_);

   void handleEntityEvent(SEntityStatusPacket p_147236_1_);

   void handleEntityLinkPacket(SMountEntityPacket p_147243_1_);

   void handleSetEntityPassengersPacket(SSetPassengersPacket p_184328_1_);

   void handleExplosion(SExplosionPacket p_147283_1_);

   void handleGameEvent(SChangeGameStatePacket p_147252_1_);

   void handleKeepAlive(SKeepAlivePacket p_147272_1_);

   void handleLevelChunk(SChunkDataPacket p_147263_1_);

   void handleForgetLevelChunk(SUnloadChunkPacket p_184326_1_);

   void handleLevelEvent(SPlaySoundEventPacket p_147277_1_);

   void handleLogin(SJoinGamePacket p_147282_1_);

   void handleMoveEntity(SEntityPacket p_147259_1_);

   void handleMovePlayer(SPlayerPositionLookPacket p_184330_1_);

   void handleParticleEvent(SSpawnParticlePacket p_147289_1_);

   void handlePlayerAbilities(SPlayerAbilitiesPacket p_147270_1_);

   void handlePlayerInfo(SPlayerListItemPacket p_147256_1_);

   void handleRemoveEntity(SDestroyEntitiesPacket p_147238_1_);

   void handleRemoveMobEffect(SRemoveEntityEffectPacket p_147262_1_);

   void handleRespawn(SRespawnPacket p_147280_1_);

   void handleRotateMob(SEntityHeadLookPacket p_147267_1_);

   void handleSetCarriedItem(SHeldItemChangePacket p_147257_1_);

   void handleSetDisplayObjective(SDisplayObjectivePacket p_147254_1_);

   void handleSetEntityData(SEntityMetadataPacket p_147284_1_);

   void handleSetEntityMotion(SEntityVelocityPacket p_147244_1_);

   void handleSetEquipment(SEntityEquipmentPacket p_147242_1_);

   void handleSetExperience(SSetExperiencePacket p_147295_1_);

   void handleSetHealth(SUpdateHealthPacket p_147249_1_);

   void handleSetPlayerTeamPacket(STeamsPacket p_147247_1_);

   void handleSetScore(SUpdateScorePacket p_147250_1_);

   void handleSetSpawn(SWorldSpawnChangedPacket p_230488_1_);

   void handleSetTime(SUpdateTimePacket p_147285_1_);

   void handleSoundEvent(SPlaySoundEffectPacket p_184327_1_);

   void handleSoundEntityEvent(SSpawnMovingSoundEffectPacket p_217266_1_);

   void handleCustomSoundEvent(SPlaySoundPacket p_184329_1_);

   void handleTakeItemEntity(SCollectItemPacket p_147246_1_);

   void handleTeleportEntity(SEntityTeleportPacket p_147275_1_);

   void handleUpdateAttributes(SEntityPropertiesPacket p_147290_1_);

   void handleUpdateMobEffect(SPlayEntityEffectPacket p_147260_1_);

   void handleUpdateTags(STagsListPacket p_199723_1_);

   void handlePlayerCombat(SCombatPacket p_175098_1_);

   void handleChangeDifficulty(SServerDifficultyPacket p_175101_1_);

   void handleSetCamera(SCameraPacket p_175094_1_);

   void handleSetBorder(SWorldBorderPacket p_175093_1_);

   void handleSetTitles(STitlePacket p_175099_1_);

   void handleTabListCustomisation(SPlayerListHeaderFooterPacket p_175096_1_);

   void handleResourcePack(SSendResourcePackPacket p_175095_1_);

   void handleBossUpdate(SUpdateBossInfoPacket p_184325_1_);

   void handleItemCooldown(SCooldownPacket p_184324_1_);

   void handleMoveVehicle(SMoveVehiclePacket p_184323_1_);

   void handleUpdateAdvancementsPacket(SAdvancementInfoPacket p_191981_1_);

   void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket p_194022_1_);

   void handlePlaceRecipe(SPlaceGhostRecipePacket p_194307_1_);

   void handleCommands(SCommandListPacket p_195511_1_);

   void handleStopSoundEvent(SStopSoundPacket p_195512_1_);

   void handleCommandSuggestions(STabCompletePacket p_195510_1_);

   void handleUpdateRecipes(SUpdateRecipesPacket p_199525_1_);

   void handleLookAt(SPlayerLookPacket p_200232_1_);

   void handleTagQueryPacket(SQueryNBTResponsePacket p_211522_1_);

   void handleLightUpdatePacked(SUpdateLightPacket p_217269_1_);

   void handleOpenBook(SOpenBookWindowPacket p_217268_1_);

   void handleOpenScreen(SOpenWindowPacket p_217272_1_);

   void handleMerchantOffers(SMerchantOffersPacket p_217273_1_);

   void handleSetChunkCacheRadius(SUpdateViewDistancePacket p_217270_1_);

   void handleSetChunkCacheCenter(SUpdateChunkPositionPacket p_217267_1_);

   void handleBlockBreakAck(SPlayerDiggingPacket p_225312_1_);
}
