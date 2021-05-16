package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.BeeNestDestroyedTrigger;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.RightClickBlockWithItemTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN, EntityType.STRIDER};
   private static final Item[] FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
   private static final Item[] FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
   private static final Item[] EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE};

   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement advancement = Advancement.Builder.advancement().display(Blocks.HAY_BLOCK, new TranslationTextComponent("advancements.husbandry.root.title"), new TranslationTextComponent("advancements.husbandry.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", ConsumeItemTrigger.Instance.usedItem()).save(p_accept_1_, "husbandry/root");
      Advancement advancement1 = Advancement.Builder.advancement().parent(advancement).display(Items.WHEAT, new TranslationTextComponent("advancements.husbandry.plant_seed.title"), new TranslationTextComponent("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(IRequirementsStrategy.OR).addCriterion("wheat", PlacedBlockTrigger.Instance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", PlacedBlockTrigger.Instance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", PlacedBlockTrigger.Instance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", PlacedBlockTrigger.Instance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", PlacedBlockTrigger.Instance.placedBlock(Blocks.NETHER_WART)).save(p_accept_1_, "husbandry/plant_seed");
      Advancement advancement2 = Advancement.Builder.advancement().parent(advancement).display(Items.WHEAT, new TranslationTextComponent("advancements.husbandry.breed_an_animal.title"), new TranslationTextComponent("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(IRequirementsStrategy.OR).addCriterion("bred", BredAnimalsTrigger.Instance.bredAnimals()).save(p_accept_1_, "husbandry/breed_an_animal");
      this.addFood(Advancement.Builder.advancement()).parent(advancement1).display(Items.APPLE, new TranslationTextComponent("advancements.husbandry.balanced_diet.title"), new TranslationTextComponent("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(p_accept_1_, "husbandry/balanced_diet");
      Advancement.Builder.advancement().parent(advancement1).display(Items.NETHERITE_HOE, new TranslationTextComponent("advancements.husbandry.netherite_hoe.title"), new TranslationTextComponent("advancements.husbandry.netherite_hoe.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", InventoryChangeTrigger.Instance.hasItems(Items.NETHERITE_HOE)).save(p_accept_1_, "husbandry/obtain_netherite_hoe");
      Advancement advancement3 = Advancement.Builder.advancement().parent(advancement).display(Items.LEAD, new TranslationTextComponent("advancements.husbandry.tame_an_animal.title"), new TranslationTextComponent("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", TameAnimalTrigger.Instance.tamedAnimal()).save(p_accept_1_, "husbandry/tame_an_animal");
      this.addBreedable(Advancement.Builder.advancement()).parent(advancement2).display(Items.GOLDEN_CARROT, new TranslationTextComponent("advancements.husbandry.breed_all_animals.title"), new TranslationTextComponent("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(p_accept_1_, "husbandry/bred_all_animals");
      Advancement advancement4 = this.addFish(Advancement.Builder.advancement()).parent(advancement).requirements(IRequirementsStrategy.OR).display(Items.FISHING_ROD, new TranslationTextComponent("advancements.husbandry.fishy_business.title"), new TranslationTextComponent("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_accept_1_, "husbandry/fishy_business");
      this.addFishBuckets(Advancement.Builder.advancement()).parent(advancement4).requirements(IRequirementsStrategy.OR).display(Items.PUFFERFISH_BUCKET, new TranslationTextComponent("advancements.husbandry.tactical_fishing.title"), new TranslationTextComponent("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_accept_1_, "husbandry/tactical_fishing");
      this.addCatVariants(Advancement.Builder.advancement()).parent(advancement3).display(Items.COD, new TranslationTextComponent("advancements.husbandry.complete_catalogue.title"), new TranslationTextComponent("advancements.husbandry.complete_catalogue.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(p_accept_1_, "husbandry/complete_catalogue");
      Advancement.Builder.advancement().parent(advancement).addCriterion("safely_harvest_honey", RightClickBlockWithItemTrigger.Instance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.BEEHIVES).build()).setSmokey(true), ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE))).display(Items.HONEY_BOTTLE, new TranslationTextComponent("advancements.husbandry.safely_harvest_honey.title"), new TranslationTextComponent("advancements.husbandry.safely_harvest_honey.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_accept_1_, "husbandry/safely_harvest_honey");
      Advancement.Builder.advancement().parent(advancement).addCriterion("silk_touch_nest", BeeNestDestroyedTrigger.Instance.destroyedBeeNest(Blocks.BEE_NEST, ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))), MinMaxBounds.IntBound.exactly(3))).display(Blocks.BEE_NEST, new TranslationTextComponent("advancements.husbandry.silk_touch_nest.title"), new TranslationTextComponent("advancements.husbandry.silk_touch_nest.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_accept_1_, "husbandry/silk_touch_nest");
   }

   private Advancement.Builder addFood(Advancement.Builder p_204288_1_) {
      for(Item item : EDIBLE_ITEMS) {
         p_204288_1_.addCriterion(Registry.ITEM.getKey(item).getPath(), ConsumeItemTrigger.Instance.usedItem(item));
      }

      return p_204288_1_;
   }

   private Advancement.Builder addBreedable(Advancement.Builder p_204289_1_) {
      for(EntityType<?> entitytype : BREEDABLE_ANIMALS) {
         p_204289_1_.addCriterion(EntityType.getKey(entitytype).toString(), BredAnimalsTrigger.Instance.bredAnimals(EntityPredicate.Builder.entity().of(entitytype)));
      }

      p_204289_1_.addCriterion(EntityType.getKey(EntityType.TURTLE).toString(), BredAnimalsTrigger.Instance.bredAnimals(EntityPredicate.Builder.entity().of(EntityType.TURTLE).build(), EntityPredicate.Builder.entity().of(EntityType.TURTLE).build(), EntityPredicate.ANY));
      return p_204289_1_;
   }

   private Advancement.Builder addFishBuckets(Advancement.Builder p_204865_1_) {
      for(Item item : FISH_BUCKETS) {
         p_204865_1_.addCriterion(Registry.ITEM.getKey(item).getPath(), FilledBucketTrigger.Instance.filledBucket(ItemPredicate.Builder.item().of(item).build()));
      }

      return p_204865_1_;
   }

   private Advancement.Builder addFish(Advancement.Builder p_204864_1_) {
      for(Item item : FISH) {
         p_204864_1_.addCriterion(Registry.ITEM.getKey(item).getPath(), FishingRodHookedTrigger.Instance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.item().of(item).build()));
      }

      return p_204864_1_;
   }

   private Advancement.Builder addCatVariants(Advancement.Builder p_218460_1_) {
      CatEntity.TEXTURE_BY_TYPE.forEach((p_218461_1_, p_218461_2_) -> {
         p_218460_1_.addCriterion(p_218461_2_.getPath(), TameAnimalTrigger.Instance.tamedAnimal(EntityPredicate.Builder.entity().of(p_218461_2_).build()));
      });
      return p_218460_1_;
   }
}
