package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetName extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ITextComponent name;
   @Nullable
   private final LootContext.EntityTarget resolutionContext;

   private SetName(ILootCondition[] p_i51218_1_, @Nullable ITextComponent p_i51218_2_, @Nullable LootContext.EntityTarget p_i51218_3_) {
      super(p_i51218_1_);
      this.name = p_i51218_2_;
      this.resolutionContext = p_i51218_3_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_NAME;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
   }

   public static UnaryOperator<ITextComponent> createResolver(LootContext p_215936_0_, @Nullable LootContext.EntityTarget p_215936_1_) {
      if (p_215936_1_ != null) {
         Entity entity = p_215936_0_.getParamOrNull(p_215936_1_.getParam());
         if (entity != null) {
            CommandSource commandsource = entity.createCommandSourceStack().withPermission(2);
            return (p_215937_2_) -> {
               try {
                  return TextComponentUtils.updateForEntity(commandsource, p_215937_2_, entity, 0);
               } catch (CommandSyntaxException commandsyntaxexception) {
                  LOGGER.warn("Failed to resolve text component", (Throwable)commandsyntaxexception);
                  return p_215937_2_;
               }
            };
         }
      }

      return (p_215938_0_) -> {
         return p_215938_0_;
      };
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (this.name != null) {
         p_215859_1_.setHoverName(createResolver(p_215859_2_, this.resolutionContext).apply(this.name));
      }

      return p_215859_1_;
   }

   public static class Serializer extends LootFunction.Serializer<SetName> {
      public void serialize(JsonObject p_230424_1_, SetName p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         if (p_230424_2_.name != null) {
            p_230424_1_.add("name", ITextComponent.Serializer.toJsonTree(p_230424_2_.name));
         }

         if (p_230424_2_.resolutionContext != null) {
            p_230424_1_.add("entity", p_230424_3_.serialize(p_230424_2_.resolutionContext));
         }

      }

      public SetName deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_186530_1_.get("name"));
         LootContext.EntityTarget lootcontext$entitytarget = JSONUtils.getAsObject(p_186530_1_, "entity", (LootContext.EntityTarget)null, p_186530_2_, LootContext.EntityTarget.class);
         return new SetName(p_186530_3_, itextcomponent, lootcontext$entitytarget);
      }
   }
}
