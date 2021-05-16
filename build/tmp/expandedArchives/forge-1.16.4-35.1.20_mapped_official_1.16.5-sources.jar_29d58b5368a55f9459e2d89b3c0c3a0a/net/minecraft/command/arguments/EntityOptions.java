package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;

public class EntityOptions {
   private static final Map<String, EntityOptions.OptionHandler> OPTIONS = Maps.newHashMap();
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((p_208752_0_) -> {
      return new TranslationTextComponent("argument.entity.options.unknown", p_208752_0_);
   });
   public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((p_208726_0_) -> {
      return new TranslationTextComponent("argument.entity.options.inapplicable", p_208726_0_);
   });
   public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.distance.negative"));
   public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.level.negative"));
   public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.limit.toosmall"));
   public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((p_208749_0_) -> {
      return new TranslationTextComponent("argument.entity.options.sort.irreversible", p_208749_0_);
   });
   public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType((p_208740_0_) -> {
      return new TranslationTextComponent("argument.entity.options.mode.invalid", p_208740_0_);
   });
   public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType((p_208758_0_) -> {
      return new TranslationTextComponent("argument.entity.options.type.invalid", p_208758_0_);
   });

   public static void register(String p_202024_0_, EntityOptions.IFilter p_202024_1_, Predicate<EntitySelectorParser> p_202024_2_, ITextComponent p_202024_3_) {
      OPTIONS.put(p_202024_0_, new EntityOptions.OptionHandler(p_202024_1_, p_202024_2_, p_202024_3_));
   }

   public static void bootStrap() {
      if (OPTIONS.isEmpty()) {
         register("name", (p_197440_0_) -> {
            int i = p_197440_0_.getReader().getCursor();
            boolean flag = p_197440_0_.shouldInvertValue();
            String s = p_197440_0_.getReader().readString();
            if (p_197440_0_.hasNameNotEquals() && !flag) {
               p_197440_0_.getReader().setCursor(i);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_197440_0_.getReader(), "name");
            } else {
               if (flag) {
                  p_197440_0_.setHasNameNotEquals(true);
               } else {
                  p_197440_0_.setHasNameEquals(true);
               }

               p_197440_0_.addPredicate((p_197446_2_) -> {
                  return p_197446_2_.getName().getString().equals(s) != flag;
               });
            }
         }, (p_202016_0_) -> {
            return !p_202016_0_.hasNameEquals();
         }, new TranslationTextComponent("argument.entity.options.name.description"));
         register("distance", (p_197439_0_) -> {
            int i = p_197439_0_.getReader().getCursor();
            MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromReader(p_197439_0_.getReader());
            if ((minmaxbounds$floatbound.getMin() == null || !(minmaxbounds$floatbound.getMin() < 0.0F)) && (minmaxbounds$floatbound.getMax() == null || !(minmaxbounds$floatbound.getMax() < 0.0F))) {
               p_197439_0_.setDistance(minmaxbounds$floatbound);
               p_197439_0_.setWorldLimited();
            } else {
               p_197439_0_.getReader().setCursor(i);
               throw ERROR_RANGE_NEGATIVE.createWithContext(p_197439_0_.getReader());
            }
         }, (p_202020_0_) -> {
            return p_202020_0_.getDistance().isAny();
         }, new TranslationTextComponent("argument.entity.options.distance.description"));
         register("level", (p_197438_0_) -> {
            int i = p_197438_0_.getReader().getCursor();
            MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(p_197438_0_.getReader());
            if ((minmaxbounds$intbound.getMin() == null || minmaxbounds$intbound.getMin() >= 0) && (minmaxbounds$intbound.getMax() == null || minmaxbounds$intbound.getMax() >= 0)) {
               p_197438_0_.setLevel(minmaxbounds$intbound);
               p_197438_0_.setIncludesEntities(false);
            } else {
               p_197438_0_.getReader().setCursor(i);
               throw ERROR_LEVEL_NEGATIVE.createWithContext(p_197438_0_.getReader());
            }
         }, (p_202019_0_) -> {
            return p_202019_0_.getLevel().isAny();
         }, new TranslationTextComponent("argument.entity.options.level.description"));
         register("x", (p_197437_0_) -> {
            p_197437_0_.setWorldLimited();
            p_197437_0_.setX(p_197437_0_.getReader().readDouble());
         }, (p_202022_0_) -> {
            return p_202022_0_.getX() == null;
         }, new TranslationTextComponent("argument.entity.options.x.description"));
         register("y", (p_197442_0_) -> {
            p_197442_0_.setWorldLimited();
            p_197442_0_.setY(p_197442_0_.getReader().readDouble());
         }, (p_202021_0_) -> {
            return p_202021_0_.getY() == null;
         }, new TranslationTextComponent("argument.entity.options.y.description"));
         register("z", (p_197464_0_) -> {
            p_197464_0_.setWorldLimited();
            p_197464_0_.setZ(p_197464_0_.getReader().readDouble());
         }, (p_202029_0_) -> {
            return p_202029_0_.getZ() == null;
         }, new TranslationTextComponent("argument.entity.options.z.description"));
         register("dx", (p_197460_0_) -> {
            p_197460_0_.setWorldLimited();
            p_197460_0_.setDeltaX(p_197460_0_.getReader().readDouble());
         }, (p_202027_0_) -> {
            return p_202027_0_.getDeltaX() == null;
         }, new TranslationTextComponent("argument.entity.options.dx.description"));
         register("dy", (p_197463_0_) -> {
            p_197463_0_.setWorldLimited();
            p_197463_0_.setDeltaY(p_197463_0_.getReader().readDouble());
         }, (p_202026_0_) -> {
            return p_202026_0_.getDeltaY() == null;
         }, new TranslationTextComponent("argument.entity.options.dy.description"));
         register("dz", (p_197458_0_) -> {
            p_197458_0_.setWorldLimited();
            p_197458_0_.setDeltaZ(p_197458_0_.getReader().readDouble());
         }, (p_202030_0_) -> {
            return p_202030_0_.getDeltaZ() == null;
         }, new TranslationTextComponent("argument.entity.options.dz.description"));
         register("x_rotation", (p_197462_0_) -> {
            p_197462_0_.setRotX(MinMaxBoundsWrapped.fromReader(p_197462_0_.getReader(), true, MathHelper::wrapDegrees));
         }, (p_202028_0_) -> {
            return p_202028_0_.getRotX() == MinMaxBoundsWrapped.ANY;
         }, new TranslationTextComponent("argument.entity.options.x_rotation.description"));
         register("y_rotation", (p_197461_0_) -> {
            p_197461_0_.setRotY(MinMaxBoundsWrapped.fromReader(p_197461_0_.getReader(), true, MathHelper::wrapDegrees));
         }, (p_202036_0_) -> {
            return p_202036_0_.getRotY() == MinMaxBoundsWrapped.ANY;
         }, new TranslationTextComponent("argument.entity.options.y_rotation.description"));
         register("limit", (p_197456_0_) -> {
            int i = p_197456_0_.getReader().getCursor();
            int j = p_197456_0_.getReader().readInt();
            if (j < 1) {
               p_197456_0_.getReader().setCursor(i);
               throw ERROR_LIMIT_TOO_SMALL.createWithContext(p_197456_0_.getReader());
            } else {
               p_197456_0_.setMaxResults(j);
               p_197456_0_.setLimited(true);
            }
         }, (p_202035_0_) -> {
            return !p_202035_0_.isCurrentEntity() && !p_202035_0_.isLimited();
         }, new TranslationTextComponent("argument.entity.options.limit.description"));
         register("sort", (p_197455_0_) -> {
            int i = p_197455_0_.getReader().getCursor();
            String s = p_197455_0_.getReader().readUnquotedString();
            p_197455_0_.setSuggestions((p_202056_0_, p_202056_1_) -> {
               return ISuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), p_202056_0_);
            });
            BiConsumer<Vector3d, List<? extends Entity>> biconsumer;
            switch(s) {
            case "nearest":
               biconsumer = EntitySelectorParser.ORDER_NEAREST;
               break;
            case "furthest":
               biconsumer = EntitySelectorParser.ORDER_FURTHEST;
               break;
            case "random":
               biconsumer = EntitySelectorParser.ORDER_RANDOM;
               break;
            case "arbitrary":
               biconsumer = EntitySelectorParser.ORDER_ARBITRARY;
               break;
            default:
               p_197455_0_.getReader().setCursor(i);
               throw ERROR_SORT_UNKNOWN.createWithContext(p_197455_0_.getReader(), s);
            }

            p_197455_0_.setOrder(biconsumer);
            p_197455_0_.setSorted(true);
         }, (p_202043_0_) -> {
            return !p_202043_0_.isCurrentEntity() && !p_202043_0_.isSorted();
         }, new TranslationTextComponent("argument.entity.options.sort.description"));
         register("gamemode", (p_197452_0_) -> {
            p_197452_0_.setSuggestions((p_202018_1_, p_202018_2_) -> {
               String s1 = p_202018_1_.getRemaining().toLowerCase(Locale.ROOT);
               boolean flag1 = !p_197452_0_.hasGamemodeNotEquals();
               boolean flag2 = true;
               if (!s1.isEmpty()) {
                  if (s1.charAt(0) == '!') {
                     flag1 = false;
                     s1 = s1.substring(1);
                  } else {
                     flag2 = false;
                  }
               }

               for(GameType gametype1 : GameType.values()) {
                  if (gametype1 != GameType.NOT_SET && gametype1.getName().toLowerCase(Locale.ROOT).startsWith(s1)) {
                     if (flag2) {
                        p_202018_1_.suggest('!' + gametype1.getName());
                     }

                     if (flag1) {
                        p_202018_1_.suggest(gametype1.getName());
                     }
                  }
               }

               return p_202018_1_.buildFuture();
            });
            int i = p_197452_0_.getReader().getCursor();
            boolean flag = p_197452_0_.shouldInvertValue();
            if (p_197452_0_.hasGamemodeNotEquals() && !flag) {
               p_197452_0_.getReader().setCursor(i);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_197452_0_.getReader(), "gamemode");
            } else {
               String s = p_197452_0_.getReader().readUnquotedString();
               GameType gametype = GameType.byName(s, GameType.NOT_SET);
               if (gametype == GameType.NOT_SET) {
                  p_197452_0_.getReader().setCursor(i);
                  throw ERROR_GAME_MODE_INVALID.createWithContext(p_197452_0_.getReader(), s);
               } else {
                  p_197452_0_.setIncludesEntities(false);
                  p_197452_0_.addPredicate((p_202055_2_) -> {
                     if (!(p_202055_2_ instanceof ServerPlayerEntity)) {
                        return false;
                     } else {
                        GameType gametype1 = ((ServerPlayerEntity)p_202055_2_).gameMode.getGameModeForPlayer();
                        return flag ? gametype1 != gametype : gametype1 == gametype;
                     }
                  });
                  if (flag) {
                     p_197452_0_.setHasGamemodeNotEquals(true);
                  } else {
                     p_197452_0_.setHasGamemodeEquals(true);
                  }

               }
            }
         }, (p_202048_0_) -> {
            return !p_202048_0_.hasGamemodeEquals();
         }, new TranslationTextComponent("argument.entity.options.gamemode.description"));
         register("team", (p_197449_0_) -> {
            boolean flag = p_197449_0_.shouldInvertValue();
            String s = p_197449_0_.getReader().readUnquotedString();
            p_197449_0_.addPredicate((p_197454_2_) -> {
               if (!(p_197454_2_ instanceof LivingEntity)) {
                  return false;
               } else {
                  Team team = p_197454_2_.getTeam();
                  String s1 = team == null ? "" : team.getName();
                  return s1.equals(s) != flag;
               }
            });
            if (flag) {
               p_197449_0_.setHasTeamNotEquals(true);
            } else {
               p_197449_0_.setHasTeamEquals(true);
            }

         }, (p_202038_0_) -> {
            return !p_202038_0_.hasTeamEquals();
         }, new TranslationTextComponent("argument.entity.options.team.description"));
         register("type", (p_197447_0_) -> {
            p_197447_0_.setSuggestions((p_202052_1_, p_202052_2_) -> {
               ISuggestionProvider.suggestResource(Registry.ENTITY_TYPE.keySet(), p_202052_1_, String.valueOf('!'));
               ISuggestionProvider.suggestResource(EntityTypeTags.getAllTags().getAvailableTags(), p_202052_1_, "!#");
               if (!p_197447_0_.isTypeLimitedInversely()) {
                  ISuggestionProvider.suggestResource(Registry.ENTITY_TYPE.keySet(), p_202052_1_);
                  ISuggestionProvider.suggestResource(EntityTypeTags.getAllTags().getAvailableTags(), p_202052_1_, String.valueOf('#'));
               }

               return p_202052_1_.buildFuture();
            });
            int i = p_197447_0_.getReader().getCursor();
            boolean flag = p_197447_0_.shouldInvertValue();
            if (p_197447_0_.isTypeLimitedInversely() && !flag) {
               p_197447_0_.getReader().setCursor(i);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_197447_0_.getReader(), "type");
            } else {
               if (flag) {
                  p_197447_0_.setTypeLimitedInversely();
               }

               if (p_197447_0_.isTag()) {
                  ResourceLocation resourcelocation = ResourceLocation.read(p_197447_0_.getReader());
                  p_197447_0_.addPredicate((p_239573_2_) -> {
                     return p_239573_2_.getServer().getTags().getEntityTypes().getTagOrEmpty(resourcelocation).contains(p_239573_2_.getType()) != flag;
                  });
               } else {
                  ResourceLocation resourcelocation1 = ResourceLocation.read(p_197447_0_.getReader());
                  EntityType<?> entitytype = Registry.ENTITY_TYPE.getOptional(resourcelocation1).orElseThrow(() -> {
                     p_197447_0_.getReader().setCursor(i);
                     return ERROR_ENTITY_TYPE_INVALID.createWithContext(p_197447_0_.getReader(), resourcelocation1.toString());
                  });
                  if (Objects.equals(EntityType.PLAYER, entitytype) && !flag) {
                     p_197447_0_.setIncludesEntities(false);
                  }

                  p_197447_0_.addPredicate((p_202057_2_) -> {
                     return Objects.equals(entitytype, p_202057_2_.getType()) != flag;
                  });
                  if (!flag) {
                     p_197447_0_.limitToType(entitytype);
                  }
               }

            }
         }, (p_202047_0_) -> {
            return !p_202047_0_.isTypeLimited();
         }, new TranslationTextComponent("argument.entity.options.type.description"));
         register("tag", (p_197448_0_) -> {
            boolean flag = p_197448_0_.shouldInvertValue();
            String s = p_197448_0_.getReader().readUnquotedString();
            p_197448_0_.addPredicate((p_197466_2_) -> {
               if ("".equals(s)) {
                  return p_197466_2_.getTags().isEmpty() != flag;
               } else {
                  return p_197466_2_.getTags().contains(s) != flag;
               }
            });
         }, (p_202041_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.tag.description"));
         register("nbt", (p_197450_0_) -> {
            boolean flag = p_197450_0_.shouldInvertValue();
            CompoundNBT compoundnbt = (new JsonToNBT(p_197450_0_.getReader())).readStruct();
            p_197450_0_.addPredicate((p_197443_2_) -> {
               CompoundNBT compoundnbt1 = p_197443_2_.saveWithoutId(new CompoundNBT());
               if (p_197443_2_ instanceof ServerPlayerEntity) {
                  ItemStack itemstack = ((ServerPlayerEntity)p_197443_2_).inventory.getSelected();
                  if (!itemstack.isEmpty()) {
                     compoundnbt1.put("SelectedItem", itemstack.save(new CompoundNBT()));
                  }
               }

               return NBTUtil.compareNbt(compoundnbt, compoundnbt1, true) != flag;
            });
         }, (p_202046_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.nbt.description"));
         register("scores", (p_197457_0_) -> {
            StringReader stringreader = p_197457_0_.getReader();
            Map<String, MinMaxBounds.IntBound> map = Maps.newHashMap();
            stringreader.expect('{');
            stringreader.skipWhitespace();

            while(stringreader.canRead() && stringreader.peek() != '}') {
               stringreader.skipWhitespace();
               String s = stringreader.readUnquotedString();
               stringreader.skipWhitespace();
               stringreader.expect('=');
               stringreader.skipWhitespace();
               MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(stringreader);
               map.put(s, minmaxbounds$intbound);
               stringreader.skipWhitespace();
               if (stringreader.canRead() && stringreader.peek() == ',') {
                  stringreader.skip();
               }
            }

            stringreader.expect('}');
            if (!map.isEmpty()) {
               p_197457_0_.addPredicate((p_197465_1_) -> {
                  Scoreboard scoreboard = p_197465_1_.getServer().getScoreboard();
                  String s1 = p_197465_1_.getScoreboardName();

                  for(Entry<String, MinMaxBounds.IntBound> entry : map.entrySet()) {
                     ScoreObjective scoreobjective = scoreboard.getObjective(entry.getKey());
                     if (scoreobjective == null) {
                        return false;
                     }

                     if (!scoreboard.hasPlayerScore(s1, scoreobjective)) {
                        return false;
                     }

                     Score score = scoreboard.getOrCreatePlayerScore(s1, scoreobjective);
                     int i = score.getScore();
                     if (!entry.getValue().matches(i)) {
                        return false;
                     }
                  }

                  return true;
               });
            }

            p_197457_0_.setHasScores(true);
         }, (p_202033_0_) -> {
            return !p_202033_0_.hasScores();
         }, new TranslationTextComponent("argument.entity.options.scores.description"));
         register("advancements", (p_197453_0_) -> {
            StringReader stringreader = p_197453_0_.getReader();
            Map<ResourceLocation, Predicate<AdvancementProgress>> map = Maps.newHashMap();
            stringreader.expect('{');
            stringreader.skipWhitespace();

            while(stringreader.canRead() && stringreader.peek() != '}') {
               stringreader.skipWhitespace();
               ResourceLocation resourcelocation = ResourceLocation.read(stringreader);
               stringreader.skipWhitespace();
               stringreader.expect('=');
               stringreader.skipWhitespace();
               if (stringreader.canRead() && stringreader.peek() == '{') {
                  Map<String, Predicate<CriterionProgress>> map1 = Maps.newHashMap();
                  stringreader.skipWhitespace();
                  stringreader.expect('{');
                  stringreader.skipWhitespace();

                  while(stringreader.canRead() && stringreader.peek() != '}') {
                     stringreader.skipWhitespace();
                     String s = stringreader.readUnquotedString();
                     stringreader.skipWhitespace();
                     stringreader.expect('=');
                     stringreader.skipWhitespace();
                     boolean flag1 = stringreader.readBoolean();
                     map1.put(s, (p_197444_1_) -> {
                        return p_197444_1_.isDone() == flag1;
                     });
                     stringreader.skipWhitespace();
                     if (stringreader.canRead() && stringreader.peek() == ',') {
                        stringreader.skip();
                     }
                  }

                  stringreader.skipWhitespace();
                  stringreader.expect('}');
                  stringreader.skipWhitespace();
                  map.put(resourcelocation, (p_197435_1_) -> {
                     for(Entry<String, Predicate<CriterionProgress>> entry : map1.entrySet()) {
                        CriterionProgress criterionprogress = p_197435_1_.getCriterion(entry.getKey());
                        if (criterionprogress == null || !entry.getValue().test(criterionprogress)) {
                           return false;
                        }
                     }

                     return true;
                  });
               } else {
                  boolean flag = stringreader.readBoolean();
                  map.put(resourcelocation, (p_197451_1_) -> {
                     return p_197451_1_.isDone() == flag;
                  });
               }

               stringreader.skipWhitespace();
               if (stringreader.canRead() && stringreader.peek() == ',') {
                  stringreader.skip();
               }
            }

            stringreader.expect('}');
            if (!map.isEmpty()) {
               p_197453_0_.addPredicate((p_197441_1_) -> {
                  if (!(p_197441_1_ instanceof ServerPlayerEntity)) {
                     return false;
                  } else {
                     ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_197441_1_;
                     PlayerAdvancements playeradvancements = serverplayerentity.getAdvancements();
                     AdvancementManager advancementmanager = serverplayerentity.getServer().getAdvancements();

                     for(Entry<ResourceLocation, Predicate<AdvancementProgress>> entry : map.entrySet()) {
                        Advancement advancement = advancementmanager.getAdvancement(entry.getKey());
                        if (advancement == null || !entry.getValue().test(playeradvancements.getOrStartProgress(advancement))) {
                           return false;
                        }
                     }

                     return true;
                  }
               });
               p_197453_0_.setIncludesEntities(false);
            }

            p_197453_0_.setHasAdvancements(true);
         }, (p_202032_0_) -> {
            return !p_202032_0_.hasAdvancements();
         }, new TranslationTextComponent("argument.entity.options.advancements.description"));
         register("predicate", (p_229367_0_) -> {
            boolean flag = p_229367_0_.shouldInvertValue();
            ResourceLocation resourcelocation = ResourceLocation.read(p_229367_0_.getReader());
            p_229367_0_.addPredicate((p_229366_2_) -> {
               if (!(p_229366_2_.level instanceof ServerWorld)) {
                  return false;
               } else {
                  ServerWorld serverworld = (ServerWorld)p_229366_2_.level;
                  ILootCondition ilootcondition = serverworld.getServer().getPredicateManager().get(resourcelocation);
                  if (ilootcondition == null) {
                     return false;
                  } else {
                     LootContext lootcontext = (new LootContext.Builder(serverworld)).withParameter(LootParameters.THIS_ENTITY, p_229366_2_).withParameter(LootParameters.ORIGIN, p_229366_2_.position()).create(LootParameterSets.SELECTOR);
                     return flag ^ ilootcondition.test(lootcontext);
                  }
               }
            });
         }, (p_229365_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.predicate.description"));
      }
   }

   public static EntityOptions.IFilter get(EntitySelectorParser p_202017_0_, String p_202017_1_, int p_202017_2_) throws CommandSyntaxException {
      EntityOptions.OptionHandler entityoptions$optionhandler = OPTIONS.get(p_202017_1_);
      if (entityoptions$optionhandler != null) {
         if (entityoptions$optionhandler.predicate.test(p_202017_0_)) {
            return entityoptions$optionhandler.modifier;
         } else {
            throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_202017_0_.getReader(), p_202017_1_);
         }
      } else {
         p_202017_0_.getReader().setCursor(p_202017_2_);
         throw ERROR_UNKNOWN_OPTION.createWithContext(p_202017_0_.getReader(), p_202017_1_);
      }
   }

   public static void suggestNames(EntitySelectorParser p_202049_0_, SuggestionsBuilder p_202049_1_) {
      String s = p_202049_1_.getRemaining().toLowerCase(Locale.ROOT);

      for(Entry<String, EntityOptions.OptionHandler> entry : OPTIONS.entrySet()) {
         if ((entry.getValue()).predicate.test(p_202049_0_) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(s)) {
            p_202049_1_.suggest((String)entry.getKey() + '=', (entry.getValue()).description);
         }
      }

   }

   public interface IFilter {
      void handle(EntitySelectorParser p_handle_1_) throws CommandSyntaxException;
   }

   static class OptionHandler {
      public final EntityOptions.IFilter modifier;
      public final Predicate<EntitySelectorParser> predicate;
      public final ITextComponent description;

      private OptionHandler(EntityOptions.IFilter p_i48717_1_, Predicate<EntitySelectorParser> p_i48717_2_, ITextComponent p_i48717_3_) {
         this.modifier = p_i48717_1_;
         this.predicate = p_i48717_2_;
         this.description = p_i48717_3_;
      }
   }
}
