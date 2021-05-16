package net.minecraft.command.arguments;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

public class EntitySelectorParser {
   public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.invalid"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((p_208703_0_) -> {
      return new TranslationTextComponent("argument.entity.selector.unknown", p_208703_0_);
   });
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.selector.not_allowed"));
   public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.selector.missing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.unterminated"));
   public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((p_208711_0_) -> {
      return new TranslationTextComponent("argument.entity.options.valueless", p_208711_0_);
   });
   public static final BiConsumer<Vector3d, List<? extends Entity>> ORDER_ARBITRARY = (p_197402_0_, p_197402_1_) -> {
   };
   public static final BiConsumer<Vector3d, List<? extends Entity>> ORDER_NEAREST = (p_197392_0_, p_197392_1_) -> {
      p_197392_1_.sort((p_197393_1_, p_197393_2_) -> {
         return Doubles.compare(p_197393_1_.distanceToSqr(p_197392_0_), p_197393_2_.distanceToSqr(p_197392_0_));
      });
   };
   public static final BiConsumer<Vector3d, List<? extends Entity>> ORDER_FURTHEST = (p_197383_0_, p_197383_1_) -> {
      p_197383_1_.sort((p_197369_1_, p_197369_2_) -> {
         return Doubles.compare(p_197369_2_.distanceToSqr(p_197383_0_), p_197369_1_.distanceToSqr(p_197383_0_));
      });
   };
   public static final BiConsumer<Vector3d, List<? extends Entity>> ORDER_RANDOM = (p_197368_0_, p_197368_1_) -> {
      Collections.shuffle(p_197368_1_);
   };
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_201342_0_, p_201342_1_) -> {
      return p_201342_0_.buildFuture();
   };
   private final StringReader reader;
   private final boolean allowSelectors;
   private int maxResults;
   private boolean includesEntities;
   private boolean worldLimited;
   private MinMaxBounds.FloatBound distance = MinMaxBounds.FloatBound.ANY;
   private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.ANY;
   @Nullable
   private Double x;
   @Nullable
   private Double y;
   @Nullable
   private Double z;
   @Nullable
   private Double deltaX;
   @Nullable
   private Double deltaY;
   @Nullable
   private Double deltaZ;
   private MinMaxBoundsWrapped rotX = MinMaxBoundsWrapped.ANY;
   private MinMaxBoundsWrapped rotY = MinMaxBoundsWrapped.ANY;
   private Predicate<Entity> predicate = (p_197375_0_) -> {
      return true;
   };
   private BiConsumer<Vector3d, List<? extends Entity>> order = ORDER_ARBITRARY;
   private boolean currentEntity;
   @Nullable
   private String playerName;
   private int startPosition;
   @Nullable
   private UUID entityUUID;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
   private boolean hasNameEquals;
   private boolean hasNameNotEquals;
   private boolean isLimited;
   private boolean isSorted;
   private boolean hasGamemodeEquals;
   private boolean hasGamemodeNotEquals;
   private boolean hasTeamEquals;
   private boolean hasTeamNotEquals;
   @Nullable
   private EntityType<?> type;
   private boolean typeInverse;
   private boolean hasScores;
   private boolean hasAdvancements;
   private boolean usesSelectors;

   public EntitySelectorParser(StringReader p_i47958_1_) {
      this(p_i47958_1_, true);
   }

   public EntitySelectorParser(StringReader p_i49550_1_, boolean p_i49550_2_) {
      this.reader = p_i49550_1_;
      this.allowSelectors = p_i49550_2_;
   }

   public EntitySelector getSelector() {
      AxisAlignedBB axisalignedbb;
      if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
         if (this.distance.getMax() != null) {
            float f = this.distance.getMax();
            axisalignedbb = new AxisAlignedBB((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
         } else {
            axisalignedbb = null;
         }
      } else {
         axisalignedbb = this.createAabb(this.deltaX == null ? 0.0D : this.deltaX, this.deltaY == null ? 0.0D : this.deltaY, this.deltaZ == null ? 0.0D : this.deltaZ);
      }

      Function<Vector3d, Vector3d> function;
      if (this.x == null && this.y == null && this.z == null) {
         function = (p_197379_0_) -> {
            return p_197379_0_;
         };
      } else {
         function = (p_197367_1_) -> {
            return new Vector3d(this.x == null ? p_197367_1_.x : this.x, this.y == null ? p_197367_1_.y : this.y, this.z == null ? p_197367_1_.z : this.z);
         };
      }

      return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, function, axisalignedbb, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
   }

   private AxisAlignedBB createAabb(double p_197390_1_, double p_197390_3_, double p_197390_5_) {
      boolean flag = p_197390_1_ < 0.0D;
      boolean flag1 = p_197390_3_ < 0.0D;
      boolean flag2 = p_197390_5_ < 0.0D;
      double d0 = flag ? p_197390_1_ : 0.0D;
      double d1 = flag1 ? p_197390_3_ : 0.0D;
      double d2 = flag2 ? p_197390_5_ : 0.0D;
      double d3 = (flag ? 0.0D : p_197390_1_) + 1.0D;
      double d4 = (flag1 ? 0.0D : p_197390_3_) + 1.0D;
      double d5 = (flag2 ? 0.0D : p_197390_5_) + 1.0D;
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public void finalizePredicates() {
      if (this.rotX != MinMaxBoundsWrapped.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, (p_197386_0_) -> {
            return (double)p_197386_0_.xRot;
         }));
      }

      if (this.rotY != MinMaxBoundsWrapped.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, (p_197385_0_) -> {
            return (double)p_197385_0_.yRot;
         }));
      }

      if (!this.level.isAny()) {
         this.predicate = this.predicate.and((p_197371_1_) -> {
            return !(p_197371_1_ instanceof ServerPlayerEntity) ? false : this.level.matches(((ServerPlayerEntity)p_197371_1_).experienceLevel);
         });
      }

   }

   private Predicate<Entity> createRotationPredicate(MinMaxBoundsWrapped p_197366_1_, ToDoubleFunction<Entity> p_197366_2_) {
      double d0 = (double)MathHelper.wrapDegrees(p_197366_1_.getMin() == null ? 0.0F : p_197366_1_.getMin());
      double d1 = (double)MathHelper.wrapDegrees(p_197366_1_.getMax() == null ? 359.0F : p_197366_1_.getMax());
      return (p_197374_5_) -> {
         double d2 = MathHelper.wrapDegrees(p_197366_2_.applyAsDouble(p_197374_5_));
         if (d0 > d1) {
            return d2 >= d0 || d2 <= d1;
         } else {
            return d2 >= d0 && d2 <= d1;
         }
      };
   }

   protected void parseSelector() throws CommandSyntaxException {
      this.usesSelectors = true;
      this.suggestions = this::suggestSelector;
      if (!this.reader.canRead()) {
         throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
      } else {
         int i = this.reader.getCursor();
         char c0 = this.reader.read();
         if (c0 == 'p') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
         } else if (c0 == 'a') {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = false;
            this.order = ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
         } else if (c0 == 'r') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
         } else if (c0 == 's') {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
         } else {
            if (c0 != 'e') {
               this.reader.setCursor(i);
               throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, '@' + String.valueOf(c0));
            }

            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = true;
            this.order = ORDER_ARBITRARY;
            this.predicate = Entity::isAlive;
         }

         this.suggestions = this::suggestOpenOptions;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
         }

      }
   }

   protected void parseNameOrUUID() throws CommandSyntaxException {
      if (this.reader.canRead()) {
         this.suggestions = this::suggestName;
      }

      int i = this.reader.getCursor();
      String s = this.reader.readString();

      try {
         this.entityUUID = UUID.fromString(s);
         this.includesEntities = true;
      } catch (IllegalArgumentException illegalargumentexception) {
         if (s.isEmpty() || s.length() > 16) {
            this.reader.setCursor(i);
            throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
         }

         this.includesEntities = false;
         this.playerName = s;
      }

      this.maxResults = 1;
   }

   public void parseOptions() throws CommandSyntaxException {
      this.suggestions = this::suggestOptionsKey;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            EntityOptions.IFilter entityoptions$ifilter = EntityOptions.get(this, s, i);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(i);
               throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            entityoptions$ifilter.handle(this);
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsNextOrClose;
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestOptionsKey;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            this.suggestions = SUGGEST_NOTHING;
            return;
         }

         throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      }
   }

   public boolean shouldInvertValue() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '!') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public boolean isTag() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   public void addPredicate(Predicate<Entity> p_197401_1_) {
      this.predicate = this.predicate.and(p_197401_1_);
   }

   public void setWorldLimited() {
      this.worldLimited = true;
   }

   public MinMaxBounds.FloatBound getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.FloatBound p_197397_1_) {
      this.distance = p_197397_1_;
   }

   public MinMaxBounds.IntBound getLevel() {
      return this.level;
   }

   public void setLevel(MinMaxBounds.IntBound p_197399_1_) {
      this.level = p_197399_1_;
   }

   public MinMaxBoundsWrapped getRotX() {
      return this.rotX;
   }

   public void setRotX(MinMaxBoundsWrapped p_197389_1_) {
      this.rotX = p_197389_1_;
   }

   public MinMaxBoundsWrapped getRotY() {
      return this.rotY;
   }

   public void setRotY(MinMaxBoundsWrapped p_197387_1_) {
      this.rotY = p_197387_1_;
   }

   @Nullable
   public Double getX() {
      return this.x;
   }

   @Nullable
   public Double getY() {
      return this.y;
   }

   @Nullable
   public Double getZ() {
      return this.z;
   }

   public void setX(double p_197384_1_) {
      this.x = p_197384_1_;
   }

   public void setY(double p_197395_1_) {
      this.y = p_197395_1_;
   }

   public void setZ(double p_197372_1_) {
      this.z = p_197372_1_;
   }

   public void setDeltaX(double p_197377_1_) {
      this.deltaX = p_197377_1_;
   }

   public void setDeltaY(double p_197391_1_) {
      this.deltaY = p_197391_1_;
   }

   public void setDeltaZ(double p_197405_1_) {
      this.deltaZ = p_197405_1_;
   }

   @Nullable
   public Double getDeltaX() {
      return this.deltaX;
   }

   @Nullable
   public Double getDeltaY() {
      return this.deltaY;
   }

   @Nullable
   public Double getDeltaZ() {
      return this.deltaZ;
   }

   public void setMaxResults(int p_197388_1_) {
      this.maxResults = p_197388_1_;
   }

   public void setIncludesEntities(boolean p_197373_1_) {
      this.includesEntities = p_197373_1_;
   }

   public void setOrder(BiConsumer<Vector3d, List<? extends Entity>> p_197376_1_) {
      this.order = p_197376_1_;
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestNameOrSelector;
      if (this.reader.canRead() && this.reader.peek() == '@') {
         if (!this.allowSelectors) {
            throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.reader.skip();
         EntitySelector forgeSelector = net.minecraftforge.common.command.EntitySelectorManager.parseSelector(this);
         if (forgeSelector != null)
            return forgeSelector;
         this.parseSelector();
      } else {
         this.parseNameOrUUID();
      }

      this.finalizePredicates();
      return this.getSelector();
   }

   private static void fillSelectorSuggestions(SuggestionsBuilder p_210326_0_) {
      p_210326_0_.suggest("@p", new TranslationTextComponent("argument.entity.selector.nearestPlayer"));
      p_210326_0_.suggest("@a", new TranslationTextComponent("argument.entity.selector.allPlayers"));
      p_210326_0_.suggest("@r", new TranslationTextComponent("argument.entity.selector.randomPlayer"));
      p_210326_0_.suggest("@s", new TranslationTextComponent("argument.entity.selector.self"));
      p_210326_0_.suggest("@e", new TranslationTextComponent("argument.entity.selector.allEntities"));
      net.minecraftforge.common.command.EntitySelectorManager.fillSelectorSuggestions(p_210326_0_);
   }

   private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder p_201981_1_, Consumer<SuggestionsBuilder> p_201981_2_) {
      p_201981_2_.accept(p_201981_1_);
      if (this.allowSelectors) {
         fillSelectorSuggestions(p_201981_1_);
      }

      return p_201981_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder p_201974_1_, Consumer<SuggestionsBuilder> p_201974_2_) {
      SuggestionsBuilder suggestionsbuilder = p_201974_1_.createOffset(this.startPosition);
      p_201974_2_.accept(suggestionsbuilder);
      return p_201974_1_.add(suggestionsbuilder).buildFuture();
   }

   private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder p_201959_1_, Consumer<SuggestionsBuilder> p_201959_2_) {
      SuggestionsBuilder suggestionsbuilder = p_201959_1_.createOffset(p_201959_1_.getStart() - 1);
      fillSelectorSuggestions(suggestionsbuilder);
      p_201959_1_.add(suggestionsbuilder);
      return p_201959_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder p_201989_1_, Consumer<SuggestionsBuilder> p_201989_2_) {
      p_201989_1_.suggest(String.valueOf('['));
      return p_201989_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder p_201996_1_, Consumer<SuggestionsBuilder> p_201996_2_) {
      p_201996_1_.suggest(String.valueOf(']'));
      EntityOptions.suggestNames(this, p_201996_1_);
      return p_201996_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder p_201994_1_, Consumer<SuggestionsBuilder> p_201994_2_) {
      EntityOptions.suggestNames(this, p_201994_1_);
      return p_201994_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder p_201969_1_, Consumer<SuggestionsBuilder> p_201969_2_) {
      p_201969_1_.suggest(String.valueOf(','));
      p_201969_1_.suggest(String.valueOf(']'));
      return p_201969_1_.buildFuture();
   }

   public boolean isCurrentEntity() {
      return this.currentEntity;
   }

   public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> p_201978_1_) {
      this.suggestions = p_201978_1_;
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_201993_1_, Consumer<SuggestionsBuilder> p_201993_2_) {
      return this.suggestions.apply(p_201993_1_.createOffset(this.reader.getCursor()), p_201993_2_);
   }

   public boolean hasNameEquals() {
      return this.hasNameEquals;
   }

   public void setHasNameEquals(boolean p_201990_1_) {
      this.hasNameEquals = p_201990_1_;
   }

   public boolean hasNameNotEquals() {
      return this.hasNameNotEquals;
   }

   public void setHasNameNotEquals(boolean p_201998_1_) {
      this.hasNameNotEquals = p_201998_1_;
   }

   public boolean isLimited() {
      return this.isLimited;
   }

   public void setLimited(boolean p_201979_1_) {
      this.isLimited = p_201979_1_;
   }

   public boolean isSorted() {
      return this.isSorted;
   }

   public void setSorted(boolean p_201986_1_) {
      this.isSorted = p_201986_1_;
   }

   public boolean hasGamemodeEquals() {
      return this.hasGamemodeEquals;
   }

   public void setHasGamemodeEquals(boolean p_201988_1_) {
      this.hasGamemodeEquals = p_201988_1_;
   }

   public boolean hasGamemodeNotEquals() {
      return this.hasGamemodeNotEquals;
   }

   public void setHasGamemodeNotEquals(boolean p_201973_1_) {
      this.hasGamemodeNotEquals = p_201973_1_;
   }

   public boolean hasTeamEquals() {
      return this.hasTeamEquals;
   }

   public void setHasTeamEquals(boolean p_201975_1_) {
      this.hasTeamEquals = p_201975_1_;
   }

   public void setHasTeamNotEquals(boolean p_201958_1_) {
      this.hasTeamNotEquals = p_201958_1_;
   }

   public void limitToType(EntityType<?> p_218114_1_) {
      this.type = p_218114_1_;
   }

   public void setTypeLimitedInversely() {
      this.typeInverse = true;
   }

   public boolean isTypeLimited() {
      return this.type != null;
   }

   public boolean isTypeLimitedInversely() {
      return this.typeInverse;
   }

   public boolean hasScores() {
      return this.hasScores;
   }

   public void setHasScores(boolean p_201970_1_) {
      this.hasScores = p_201970_1_;
   }

   public boolean hasAdvancements() {
      return this.hasAdvancements;
   }

   public void setHasAdvancements(boolean p_201992_1_) {
      this.hasAdvancements = p_201992_1_;
   }
}
