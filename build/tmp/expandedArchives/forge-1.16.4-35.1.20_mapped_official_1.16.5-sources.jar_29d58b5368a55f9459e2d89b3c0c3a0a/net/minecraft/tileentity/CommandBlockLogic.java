package net.minecraft.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CommandBlockLogic implements ICommandSource {
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private static final ITextComponent DEFAULT_NAME = new StringTextComponent("@");
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   @Nullable
   private ITextComponent lastOutput;
   private String command = "";
   private ITextComponent name = DEFAULT_NAME;

   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int p_184167_1_) {
      this.successCount = p_184167_1_;
   }

   public ITextComponent getLastOutput() {
      return this.lastOutput == null ? StringTextComponent.EMPTY : this.lastOutput;
   }

   public CompoundNBT save(CompoundNBT p_189510_1_) {
      p_189510_1_.putString("Command", this.command);
      p_189510_1_.putInt("SuccessCount", this.successCount);
      p_189510_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
      p_189510_1_.putBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         p_189510_1_.putString("LastOutput", ITextComponent.Serializer.toJson(this.lastOutput));
      }

      p_189510_1_.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution > 0L) {
         p_189510_1_.putLong("LastExecution", this.lastExecution);
      }

      return p_189510_1_;
   }

   public void load(CompoundNBT p_145759_1_) {
      this.command = p_145759_1_.getString("Command");
      this.successCount = p_145759_1_.getInt("SuccessCount");
      if (p_145759_1_.contains("CustomName", 8)) {
         this.setName(ITextComponent.Serializer.fromJson(p_145759_1_.getString("CustomName")));
      }

      if (p_145759_1_.contains("TrackOutput", 1)) {
         this.trackOutput = p_145759_1_.getBoolean("TrackOutput");
      }

      if (p_145759_1_.contains("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = ITextComponent.Serializer.fromJson(p_145759_1_.getString("LastOutput"));
         } catch (Throwable throwable) {
            this.lastOutput = new StringTextComponent(throwable.getMessage());
         }
      } else {
         this.lastOutput = null;
      }

      if (p_145759_1_.contains("UpdateLastExecution")) {
         this.updateLastExecution = p_145759_1_.getBoolean("UpdateLastExecution");
      }

      if (this.updateLastExecution && p_145759_1_.contains("LastExecution")) {
         this.lastExecution = p_145759_1_.getLong("LastExecution");
      } else {
         this.lastExecution = -1L;
      }

   }

   public void setCommand(String p_145752_1_) {
      this.command = p_145752_1_;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean performCommand(World p_145755_1_) {
      if (!p_145755_1_.isClientSide && p_145755_1_.getGameTime() != this.lastExecution) {
         if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = new StringTextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
         } else {
            this.successCount = 0;
            MinecraftServer minecraftserver = this.getLevel().getServer();
            if (minecraftserver.isCommandBlockEnabled() && !StringUtils.isNullOrEmpty(this.command)) {
               try {
                  this.lastOutput = null;
                  CommandSource commandsource = this.createCommandSourceStack().withCallback((p_209527_1_, p_209527_2_, p_209527_3_) -> {
                     if (p_209527_2_) {
                        ++this.successCount;
                     }

                  });
                  minecraftserver.getCommands().performCommand(commandsource, this.command);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command block");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                  crashreportcategory.setDetail("Command", this::getCommand);
                  crashreportcategory.setDetail("Name", () -> {
                     return this.getName().getString();
                  });
                  throw new ReportedException(crashreport);
               }
            }

            if (this.updateLastExecution) {
               this.lastExecution = p_145755_1_.getGameTime();
            } else {
               this.lastExecution = -1L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public ITextComponent getName() {
      return this.name;
   }

   public void setName(@Nullable ITextComponent p_207405_1_) {
      if (p_207405_1_ != null) {
         this.name = p_207405_1_;
      } else {
         this.name = DEFAULT_NAME;
      }

   }

   public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      if (this.trackOutput) {
         this.lastOutput = (new StringTextComponent("[" + TIME_FORMAT.format(new Date()) + "] ")).append(p_145747_1_);
         this.onUpdated();
      }

   }

   public abstract ServerWorld getLevel();

   public abstract void onUpdated();

   public void setLastOutput(@Nullable ITextComponent p_145750_1_) {
      this.lastOutput = p_145750_1_;
   }

   public void setTrackOutput(boolean p_175573_1_) {
      this.trackOutput = p_175573_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public ActionResultType usedBy(PlayerEntity p_175574_1_) {
      if (!p_175574_1_.canUseGameMasterBlocks()) {
         return ActionResultType.PASS;
      } else {
         if (p_175574_1_.getCommandSenderWorld().isClientSide) {
            p_175574_1_.openMinecartCommandBlock(this);
         }

         return ActionResultType.sidedSuccess(p_175574_1_.level.isClientSide);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract Vector3d getPosition();

   public abstract CommandSource createCommandSourceStack();

   public boolean acceptsSuccess() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
   }

   public boolean acceptsFailure() {
      return this.trackOutput;
   }

   public boolean shouldInformAdmins() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
   }
}
