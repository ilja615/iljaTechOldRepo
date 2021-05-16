package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STabCompletePacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private Suggestions suggestions;

   public STabCompletePacket() {
   }

   public STabCompletePacket(int p_i47941_1_, Suggestions p_i47941_2_) {
      this.id = p_i47941_1_;
      this.suggestions = p_i47941_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      int i = p_148837_1_.readVarInt();
      int j = p_148837_1_.readVarInt();
      StringRange stringrange = StringRange.between(i, i + j);
      int k = p_148837_1_.readVarInt();
      List<Suggestion> list = Lists.newArrayListWithCapacity(k);

      for(int l = 0; l < k; ++l) {
         String s = p_148837_1_.readUtf(32767);
         ITextComponent itextcomponent = p_148837_1_.readBoolean() ? p_148837_1_.readComponent() : null;
         list.add(new Suggestion(stringrange, s, itextcomponent));
      }

      this.suggestions = new Suggestions(stringrange, list);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeVarInt(this.suggestions.getRange().getStart());
      p_148840_1_.writeVarInt(this.suggestions.getRange().getLength());
      p_148840_1_.writeVarInt(this.suggestions.getList().size());

      for(Suggestion suggestion : this.suggestions.getList()) {
         p_148840_1_.writeUtf(suggestion.getText());
         p_148840_1_.writeBoolean(suggestion.getTooltip() != null);
         if (suggestion.getTooltip() != null) {
            p_148840_1_.writeComponent(TextComponentUtils.fromMessage(suggestion.getTooltip()));
         }
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCommandSuggestions(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public Suggestions getSuggestions() {
      return this.suggestions;
   }
}
