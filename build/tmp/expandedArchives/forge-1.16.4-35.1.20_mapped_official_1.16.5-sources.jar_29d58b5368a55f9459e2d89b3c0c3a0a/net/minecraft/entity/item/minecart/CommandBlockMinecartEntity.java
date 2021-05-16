package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
   private static final DataParameter<String> DATA_ID_COMMAND_NAME = EntityDataManager.defineId(CommandBlockMinecartEntity.class, DataSerializers.STRING);
   private static final DataParameter<ITextComponent> DATA_ID_LAST_OUTPUT = EntityDataManager.defineId(CommandBlockMinecartEntity.class, DataSerializers.COMPONENT);
   private final CommandBlockLogic commandBlock = new CommandBlockMinecartEntity.MinecartCommandLogic();
   private int lastActivated;

   public CommandBlockMinecartEntity(EntityType<? extends CommandBlockMinecartEntity> p_i50123_1_, World p_i50123_2_) {
      super(p_i50123_1_, p_i50123_2_);
   }

   public CommandBlockMinecartEntity(World p_i46755_1_, double p_i46755_2_, double p_i46755_4_, double p_i46755_6_) {
      super(EntityType.COMMAND_BLOCK_MINECART, p_i46755_1_, p_i46755_2_, p_i46755_4_, p_i46755_6_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_ID_COMMAND_NAME, "");
      this.getEntityData().define(DATA_ID_LAST_OUTPUT, StringTextComponent.EMPTY);
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.commandBlock.load(p_70037_1_);
      this.getEntityData().set(DATA_ID_COMMAND_NAME, this.getCommandBlock().getCommand());
      this.getEntityData().set(DATA_ID_LAST_OUTPUT, this.getCommandBlock().getLastOutput());
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      this.commandBlock.save(p_213281_1_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.COMMAND_BLOCK;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.COMMAND_BLOCK.defaultBlockState();
   }

   public CommandBlockLogic getCommandBlock() {
      return this.commandBlock;
   }

   public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_ && this.tickCount - this.lastActivated >= 4) {
         this.getCommandBlock().performCommand(this.level);
         this.lastActivated = this.tickCount;
      }

   }

   public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      ActionResultType ret = super.interact(p_184230_1_, p_184230_2_);
      if (ret.consumesAction()) return ret;
      return this.commandBlock.usedBy(p_184230_1_);
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      super.onSyncedDataUpdated(p_184206_1_);
      if (DATA_ID_LAST_OUTPUT.equals(p_184206_1_)) {
         try {
            this.commandBlock.setLastOutput(this.getEntityData().get(DATA_ID_LAST_OUTPUT));
         } catch (Throwable throwable) {
         }
      } else if (DATA_ID_COMMAND_NAME.equals(p_184206_1_)) {
         this.commandBlock.setCommand(this.getEntityData().get(DATA_ID_COMMAND_NAME));
      }

   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public class MinecartCommandLogic extends CommandBlockLogic {
      public ServerWorld getLevel() {
         return (ServerWorld)CommandBlockMinecartEntity.this.level;
      }

      public void onUpdated() {
         CommandBlockMinecartEntity.this.getEntityData().set(CommandBlockMinecartEntity.DATA_ID_COMMAND_NAME, this.getCommand());
         CommandBlockMinecartEntity.this.getEntityData().set(CommandBlockMinecartEntity.DATA_ID_LAST_OUTPUT, this.getLastOutput());
      }

      @OnlyIn(Dist.CLIENT)
      public Vector3d getPosition() {
         return CommandBlockMinecartEntity.this.position();
      }

      @OnlyIn(Dist.CLIENT)
      public CommandBlockMinecartEntity getMinecart() {
         return CommandBlockMinecartEntity.this;
      }

      public CommandSource createCommandSourceStack() {
         return new CommandSource(this, CommandBlockMinecartEntity.this.position(), CommandBlockMinecartEntity.this.getRotationVector(), this.getLevel(), 2, this.getName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), this.getLevel().getServer(), CommandBlockMinecartEntity.this);
      }
   }
}
