package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STeamsPacket implements IPacket<IClientPlayNetHandler> {
   private String name = "";
   private ITextComponent displayName = StringTextComponent.EMPTY;
   private ITextComponent playerPrefix = StringTextComponent.EMPTY;
   private ITextComponent playerSuffix = StringTextComponent.EMPTY;
   private String nametagVisibility = Team.Visible.ALWAYS.name;
   private String collisionRule = Team.CollisionRule.ALWAYS.name;
   private TextFormatting color = TextFormatting.RESET;
   private final Collection<String> players = Lists.newArrayList();
   private int method;
   private int options;

   public STeamsPacket() {
   }

   public STeamsPacket(ScorePlayerTeam p_i46907_1_, int p_i46907_2_) {
      this.name = p_i46907_1_.getName();
      this.method = p_i46907_2_;
      if (p_i46907_2_ == 0 || p_i46907_2_ == 2) {
         this.displayName = p_i46907_1_.getDisplayName();
         this.options = p_i46907_1_.packOptions();
         this.nametagVisibility = p_i46907_1_.getNameTagVisibility().name;
         this.collisionRule = p_i46907_1_.getCollisionRule().name;
         this.color = p_i46907_1_.getColor();
         this.playerPrefix = p_i46907_1_.getPlayerPrefix();
         this.playerSuffix = p_i46907_1_.getPlayerSuffix();
      }

      if (p_i46907_2_ == 0) {
         this.players.addAll(p_i46907_1_.getPlayers());
      }

   }

   public STeamsPacket(ScorePlayerTeam p_i46908_1_, Collection<String> p_i46908_2_, int p_i46908_3_) {
      if (p_i46908_3_ != 3 && p_i46908_3_ != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (p_i46908_2_ != null && !p_i46908_2_.isEmpty()) {
         this.method = p_i46908_3_;
         this.name = p_i46908_1_.getName();
         this.players.addAll(p_i46908_2_);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.name = p_148837_1_.readUtf(16);
      this.method = p_148837_1_.readByte();
      if (this.method == 0 || this.method == 2) {
         this.displayName = p_148837_1_.readComponent();
         this.options = p_148837_1_.readByte();
         this.nametagVisibility = p_148837_1_.readUtf(40);
         this.collisionRule = p_148837_1_.readUtf(40);
         this.color = p_148837_1_.readEnum(TextFormatting.class);
         this.playerPrefix = p_148837_1_.readComponent();
         this.playerSuffix = p_148837_1_.readComponent();
      }

      if (this.method == 0 || this.method == 3 || this.method == 4) {
         int i = p_148837_1_.readVarInt();

         for(int j = 0; j < i; ++j) {
            this.players.add(p_148837_1_.readUtf(40));
         }
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.name);
      p_148840_1_.writeByte(this.method);
      if (this.method == 0 || this.method == 2) {
         p_148840_1_.writeComponent(this.displayName);
         p_148840_1_.writeByte(this.options);
         p_148840_1_.writeUtf(this.nametagVisibility);
         p_148840_1_.writeUtf(this.collisionRule);
         p_148840_1_.writeEnum(this.color);
         p_148840_1_.writeComponent(this.playerPrefix);
         p_148840_1_.writeComponent(this.playerSuffix);
      }

      if (this.method == 0 || this.method == 3 || this.method == 4) {
         p_148840_1_.writeVarInt(this.players.size());

         for(String s : this.players) {
            p_148840_1_.writeUtf(s);
         }
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetPlayerTeamPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public Collection<String> getPlayers() {
      return this.players;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMethod() {
      return this.method;
   }

   @OnlyIn(Dist.CLIENT)
   public int getOptions() {
      return this.options;
   }

   @OnlyIn(Dist.CLIENT)
   public TextFormatting getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public String getNametagVisibility() {
      return this.nametagVisibility;
   }

   @OnlyIn(Dist.CLIENT)
   public String getCollisionRule() {
      return this.collisionRule;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getPlayerPrefix() {
      return this.playerPrefix;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getPlayerSuffix() {
      return this.playerSuffix;
   }
}
