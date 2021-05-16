package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;

public class EndAdvancements implements Consumer<Consumer<Advancement>> {
   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement advancement = Advancement.Builder.advancement().display(Blocks.END_STONE, new TranslationTextComponent("advancements.end.root.title"), new TranslationTextComponent("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).addCriterion("entered_end", ChangeDimensionTrigger.Instance.changedDimensionTo(World.END)).save(p_accept_1_, "end/root");
      Advancement advancement1 = Advancement.Builder.advancement().parent(advancement).display(Blocks.DRAGON_HEAD, new TranslationTextComponent("advancements.end.kill_dragon.title"), new TranslationTextComponent("advancements.end.kill_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("killed_dragon", KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(p_accept_1_, "end/kill_dragon");
      Advancement advancement2 = Advancement.Builder.advancement().parent(advancement1).display(Items.ENDER_PEARL, new TranslationTextComponent("advancements.end.enter_end_gateway.title"), new TranslationTextComponent("advancements.end.enter_end_gateway.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end_gateway", EnterBlockTrigger.Instance.entersBlock(Blocks.END_GATEWAY)).save(p_accept_1_, "end/enter_end_gateway");
      Advancement.Builder.advancement().parent(advancement1).display(Items.END_CRYSTAL, new TranslationTextComponent("advancements.end.respawn_dragon.title"), new TranslationTextComponent("advancements.end.respawn_dragon.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_dragon", SummonedEntityTrigger.Instance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(p_accept_1_, "end/respawn_dragon");
      Advancement advancement3 = Advancement.Builder.advancement().parent(advancement2).display(Blocks.PURPUR_BLOCK, new TranslationTextComponent("advancements.end.find_end_city.title"), new TranslationTextComponent("advancements.end.find_end_city.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_city", PositionTrigger.Instance.located(LocationPredicate.inFeature(Structure.END_CITY))).save(p_accept_1_, "end/find_end_city");
      Advancement.Builder.advancement().parent(advancement1).display(Items.DRAGON_BREATH, new TranslationTextComponent("advancements.end.dragon_breath.title"), new TranslationTextComponent("advancements.end.dragon_breath.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_breath", InventoryChangeTrigger.Instance.hasItems(Items.DRAGON_BREATH)).save(p_accept_1_, "end/dragon_breath");
      Advancement.Builder.advancement().parent(advancement3).display(Items.SHULKER_SHELL, new TranslationTextComponent("advancements.end.levitate.title"), new TranslationTextComponent("advancements.end.levitate.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("levitated", LevitationTrigger.Instance.levitated(DistancePredicate.vertical(MinMaxBounds.FloatBound.atLeast(50.0F)))).save(p_accept_1_, "end/levitate");
      Advancement.Builder.advancement().parent(advancement3).display(Items.ELYTRA, new TranslationTextComponent("advancements.end.elytra.title"), new TranslationTextComponent("advancements.end.elytra.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("elytra", InventoryChangeTrigger.Instance.hasItems(Items.ELYTRA)).save(p_accept_1_, "end/elytra");
      Advancement.Builder.advancement().parent(advancement1).display(Blocks.DRAGON_EGG, new TranslationTextComponent("advancements.end.dragon_egg.title"), new TranslationTextComponent("advancements.end.dragon_egg.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_egg", InventoryChangeTrigger.Instance.hasItems(Blocks.DRAGON_EGG)).save(p_accept_1_, "end/dragon_egg");
   }
}
