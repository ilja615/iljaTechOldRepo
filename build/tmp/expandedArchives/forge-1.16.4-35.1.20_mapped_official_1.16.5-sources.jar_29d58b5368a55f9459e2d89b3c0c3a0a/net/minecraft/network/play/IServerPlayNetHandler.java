package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
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

public interface IServerPlayNetHandler extends INetHandler {
   void handleAnimate(CAnimateHandPacket p_175087_1_);

   void handleChat(CChatMessagePacket p_147354_1_);

   void handleClientCommand(CClientStatusPacket p_147342_1_);

   void handleClientInformation(CClientSettingsPacket p_147352_1_);

   void handleContainerAck(CConfirmTransactionPacket p_147339_1_);

   void handleContainerButtonClick(CEnchantItemPacket p_147338_1_);

   void handleContainerClick(CClickWindowPacket p_147351_1_);

   void handlePlaceRecipe(CPlaceRecipePacket p_194308_1_);

   void handleContainerClose(CCloseWindowPacket p_147356_1_);

   void handleCustomPayload(CCustomPayloadPacket p_147349_1_);

   void handleInteract(CUseEntityPacket p_147340_1_);

   void handleKeepAlive(CKeepAlivePacket p_147353_1_);

   void handleMovePlayer(CPlayerPacket p_147347_1_);

   void handlePlayerAbilities(CPlayerAbilitiesPacket p_147348_1_);

   void handlePlayerAction(CPlayerDiggingPacket p_147345_1_);

   void handlePlayerCommand(CEntityActionPacket p_147357_1_);

   void handlePlayerInput(CInputPacket p_147358_1_);

   void handleSetCarriedItem(CHeldItemChangePacket p_147355_1_);

   void handleSetCreativeModeSlot(CCreativeInventoryActionPacket p_147344_1_);

   void handleSignUpdate(CUpdateSignPacket p_147343_1_);

   void handleUseItemOn(CPlayerTryUseItemOnBlockPacket p_184337_1_);

   void handleUseItem(CPlayerTryUseItemPacket p_147346_1_);

   void handleTeleportToEntityPacket(CSpectatePacket p_175088_1_);

   void handleResourcePackResponse(CResourcePackStatusPacket p_175086_1_);

   void handlePaddleBoat(CSteerBoatPacket p_184340_1_);

   void handleMoveVehicle(CMoveVehiclePacket p_184338_1_);

   void handleAcceptTeleportPacket(CConfirmTeleportPacket p_184339_1_);

   void handleRecipeBookSeenRecipePacket(CMarkRecipeSeenPacket p_191984_1_);

   void handleRecipeBookChangeSettingsPacket(CUpdateRecipeBookStatusPacket p_241831_1_);

   void handleSeenAdvancements(CSeenAdvancementsPacket p_194027_1_);

   void handleCustomCommandSuggestions(CTabCompletePacket p_195518_1_);

   void handleSetCommandBlock(CUpdateCommandBlockPacket p_210153_1_);

   void handleSetCommandMinecart(CUpdateMinecartCommandBlockPacket p_210158_1_);

   void handlePickItem(CPickItemPacket p_210152_1_);

   void handleRenameItem(CRenameItemPacket p_210155_1_);

   void handleSetBeaconPacket(CUpdateBeaconPacket p_210154_1_);

   void handleSetStructureBlock(CUpdateStructureBlockPacket p_210157_1_);

   void handleSelectTrade(CSelectTradePacket p_210159_1_);

   void handleEditBook(CEditBookPacket p_210156_1_);

   void handleEntityTagQuery(CQueryEntityNBTPacket p_211526_1_);

   void handleBlockEntityTagQuery(CQueryTileEntityNBTPacket p_211525_1_);

   void handleSetJigsawBlock(CUpdateJigsawBlockPacket p_217262_1_);

   void handleJigsawGenerate(CJigsawBlockGeneratePacket p_230549_1_);

   void handleChangeDifficulty(CSetDifficultyPacket p_217263_1_);

   void handleLockDifficulty(CLockDifficultyPacket p_217261_1_);
}
