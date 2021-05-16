package net.minecraft.util.text.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoverEvent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final HoverEvent.Action<?> action;
   private final Object value;

   public <T> HoverEvent(HoverEvent.Action<T> p_i232564_1_, T p_i232564_2_) {
      this.action = p_i232564_1_;
      this.value = p_i232564_2_;
   }

   public HoverEvent.Action<?> getAction() {
      return this.action;
   }

   @Nullable
   public <T> T getValue(HoverEvent.Action<T> p_240662_1_) {
      return (T)(this.action == p_240662_1_ ? p_240662_1_.cast(this.value) : null);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         HoverEvent hoverevent = (HoverEvent)p_equals_1_;
         return this.action == hoverevent.action && Objects.equals(this.value, hoverevent.value);
      } else {
         return false;
      }
   }

   public String toString() {
      return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   public int hashCode() {
      int i = this.action.hashCode();
      return 31 * i + (this.value != null ? this.value.hashCode() : 0);
   }

   @Nullable
   public static HoverEvent deserialize(JsonObject p_240661_0_) {
      String s = JSONUtils.getAsString(p_240661_0_, "action", (String)null);
      if (s == null) {
         return null;
      } else {
         HoverEvent.Action<?> action = HoverEvent.Action.getByName(s);
         if (action == null) {
            return null;
         } else {
            JsonElement jsonelement = p_240661_0_.get("contents");
            if (jsonelement != null) {
               return action.deserialize(jsonelement);
            } else {
               ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_240661_0_.get("value"));
               return itextcomponent != null ? action.deserializeFromLegacy(itextcomponent) : null;
            }
         }
      }
   }

   public JsonObject serialize() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("action", this.action.getName());
      jsonobject.add("contents", this.action.serializeArg(this.value));
      return jsonobject;
   }

   public static class Action<T> {
      public static final HoverEvent.Action<ITextComponent> SHOW_TEXT = new HoverEvent.Action<>("show_text", true, ITextComponent.Serializer::fromJson, ITextComponent.Serializer::toJsonTree, Function.identity());
      public static final HoverEvent.Action<HoverEvent.ItemHover> SHOW_ITEM = new HoverEvent.Action<>("show_item", true, (p_240673_0_) -> {
         return HoverEvent.ItemHover.create(p_240673_0_);
      }, (p_240676_0_) -> {
         return p_240676_0_.serialize();
      }, (p_240675_0_) -> {
         return HoverEvent.ItemHover.create(p_240675_0_);
      });
      public static final HoverEvent.Action<HoverEvent.EntityHover> SHOW_ENTITY = new HoverEvent.Action<>("show_entity", true, HoverEvent.EntityHover::create, HoverEvent.EntityHover::serialize, HoverEvent.EntityHover::create);
      private static final Map<String, HoverEvent.Action> LOOKUP = Stream.of(SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(HoverEvent.Action::getName, (p_240671_0_) -> {
         return p_240671_0_;
      }));
      private final String name;
      private final boolean allowFromServer;
      private final Function<JsonElement, T> argDeserializer;
      private final Function<T, JsonElement> argSerializer;
      private final Function<ITextComponent, T> legacyArgDeserializer;

      public Action(String p_i232565_1_, boolean p_i232565_2_, Function<JsonElement, T> p_i232565_3_, Function<T, JsonElement> p_i232565_4_, Function<ITextComponent, T> p_i232565_5_) {
         this.name = p_i232565_1_;
         this.allowFromServer = p_i232565_2_;
         this.argDeserializer = p_i232565_3_;
         this.argSerializer = p_i232565_4_;
         this.legacyArgDeserializer = p_i232565_5_;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static HoverEvent.Action getByName(String p_150684_0_) {
         return LOOKUP.get(p_150684_0_);
      }

      private T cast(Object p_240674_1_) {
         return (T)p_240674_1_;
      }

      @Nullable
      public HoverEvent deserialize(JsonElement p_240668_1_) {
         T t = this.argDeserializer.apply(p_240668_1_);
         return t == null ? null : new HoverEvent(this, t);
      }

      @Nullable
      public HoverEvent deserializeFromLegacy(ITextComponent p_240670_1_) {
         T t = this.legacyArgDeserializer.apply(p_240670_1_);
         return t == null ? null : new HoverEvent(this, t);
      }

      public JsonElement serializeArg(Object p_240669_1_) {
         return this.argSerializer.apply(this.cast(p_240669_1_));
      }

      public String toString() {
         return "<action " + this.name + ">";
      }
   }

   public static class EntityHover {
      public final EntityType<?> type;
      public final UUID id;
      @Nullable
      public final ITextComponent name;
      @Nullable
      @OnlyIn(Dist.CLIENT)
      private List<ITextComponent> linesCache;

      public EntityHover(EntityType<?> p_i232566_1_, UUID p_i232566_2_, @Nullable ITextComponent p_i232566_3_) {
         this.type = p_i232566_1_;
         this.id = p_i232566_2_;
         this.name = p_i232566_3_;
      }

      @Nullable
      public static HoverEvent.EntityHover create(JsonElement p_240682_0_) {
         if (!p_240682_0_.isJsonObject()) {
            return null;
         } else {
            JsonObject jsonobject = p_240682_0_.getAsJsonObject();
            EntityType<?> entitytype = Registry.ENTITY_TYPE.get(new ResourceLocation(JSONUtils.getAsString(jsonobject, "type")));
            UUID uuid = UUID.fromString(JSONUtils.getAsString(jsonobject, "id"));
            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(jsonobject.get("name"));
            return new HoverEvent.EntityHover(entitytype, uuid, itextcomponent);
         }
      }

      @Nullable
      public static HoverEvent.EntityHover create(ITextComponent p_240683_0_) {
         try {
            CompoundNBT compoundnbt = JsonToNBT.parseTag(p_240683_0_.getString());
            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("name"));
            EntityType<?> entitytype = Registry.ENTITY_TYPE.get(new ResourceLocation(compoundnbt.getString("type")));
            UUID uuid = UUID.fromString(compoundnbt.getString("id"));
            return new HoverEvent.EntityHover(entitytype, uuid, itextcomponent);
         } catch (CommandSyntaxException | JsonSyntaxException jsonsyntaxexception) {
            return null;
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("type", Registry.ENTITY_TYPE.getKey(this.type).toString());
         jsonobject.addProperty("id", this.id.toString());
         if (this.name != null) {
            jsonobject.add("name", ITextComponent.Serializer.toJsonTree(this.name));
         }

         return jsonobject;
      }

      @OnlyIn(Dist.CLIENT)
      public List<ITextComponent> getTooltipLines() {
         if (this.linesCache == null) {
            this.linesCache = Lists.newArrayList();
            if (this.name != null) {
               this.linesCache.add(this.name);
            }

            this.linesCache.add(new TranslationTextComponent("gui.entity_tooltip.type", this.type.getDescription()));
            this.linesCache.add(new StringTextComponent(this.id.toString()));
         }

         return this.linesCache;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            HoverEvent.EntityHover hoverevent$entityhover = (HoverEvent.EntityHover)p_equals_1_;
            return this.type.equals(hoverevent$entityhover.type) && this.id.equals(hoverevent$entityhover.id) && Objects.equals(this.name, hoverevent$entityhover.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.type.hashCode();
         i = 31 * i + this.id.hashCode();
         return 31 * i + (this.name != null ? this.name.hashCode() : 0);
      }
   }

   public static class ItemHover {
      private final Item item;
      private final int count;
      @Nullable
      private final CompoundNBT tag;
      @Nullable
      @OnlyIn(Dist.CLIENT)
      private ItemStack itemStack;

      ItemHover(Item p_i232567_1_, int p_i232567_2_, @Nullable CompoundNBT p_i232567_3_) {
         this.item = p_i232567_1_;
         this.count = p_i232567_2_;
         this.tag = p_i232567_3_;
      }

      public ItemHover(ItemStack p_i232568_1_) {
         this(p_i232568_1_.getItem(), p_i232568_1_.getCount(), p_i232568_1_.getTag() != null ? p_i232568_1_.getTag().copy() : null);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            HoverEvent.ItemHover hoverevent$itemhover = (HoverEvent.ItemHover)p_equals_1_;
            return this.count == hoverevent$itemhover.count && this.item.equals(hoverevent$itemhover.item) && Objects.equals(this.tag, hoverevent$itemhover.tag);
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.item.hashCode();
         i = 31 * i + this.count;
         return 31 * i + (this.tag != null ? this.tag.hashCode() : 0);
      }

      @OnlyIn(Dist.CLIENT)
      public ItemStack getItemStack() {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(this.item, this.count);
            if (this.tag != null) {
               this.itemStack.setTag(this.tag);
            }
         }

         return this.itemStack;
      }

      private static HoverEvent.ItemHover create(JsonElement p_240694_0_) {
         if (p_240694_0_.isJsonPrimitive()) {
            return new HoverEvent.ItemHover(Registry.ITEM.get(new ResourceLocation(p_240694_0_.getAsString())), 1, (CompoundNBT)null);
         } else {
            JsonObject jsonobject = JSONUtils.convertToJsonObject(p_240694_0_, "item");
            Item item = Registry.ITEM.get(new ResourceLocation(JSONUtils.getAsString(jsonobject, "id")));
            int i = JSONUtils.getAsInt(jsonobject, "count", 1);
            if (jsonobject.has("tag")) {
               String s = JSONUtils.getAsString(jsonobject, "tag");

               try {
                  CompoundNBT compoundnbt = JsonToNBT.parseTag(s);
                  return new HoverEvent.ItemHover(item, i, compoundnbt);
               } catch (CommandSyntaxException commandsyntaxexception) {
                  HoverEvent.LOGGER.warn("Failed to parse tag: {}", s, commandsyntaxexception);
               }
            }

            return new HoverEvent.ItemHover(item, i, (CompoundNBT)null);
         }
      }

      @Nullable
      private static HoverEvent.ItemHover create(ITextComponent p_240695_0_) {
         try {
            CompoundNBT compoundnbt = JsonToNBT.parseTag(p_240695_0_.getString());
            return new HoverEvent.ItemHover(ItemStack.of(compoundnbt));
         } catch (CommandSyntaxException commandsyntaxexception) {
            HoverEvent.LOGGER.warn("Failed to parse item tag: {}", p_240695_0_, commandsyntaxexception);
            return null;
         }
      }

      private JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("id", Registry.ITEM.getKey(this.item).toString());
         if (this.count != 1) {
            jsonobject.addProperty("count", this.count);
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", this.tag.toString());
         }

         return jsonobject;
      }
   }
}
