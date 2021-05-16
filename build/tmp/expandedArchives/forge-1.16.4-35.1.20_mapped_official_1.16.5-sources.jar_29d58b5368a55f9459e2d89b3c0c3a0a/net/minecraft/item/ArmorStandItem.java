package net.minecraft.item;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties p_i48532_1_) {
      super(p_i48532_1_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      Direction direction = p_195939_1_.getClickedFace();
      if (direction == Direction.DOWN) {
         return ActionResultType.FAIL;
      } else {
         World world = p_195939_1_.getLevel();
         BlockItemUseContext blockitemusecontext = new BlockItemUseContext(p_195939_1_);
         BlockPos blockpos = blockitemusecontext.getClickedPos();
         ItemStack itemstack = p_195939_1_.getItemInHand();
         Vector3d vector3d = Vector3d.atBottomCenterOf(blockpos);
         AxisAlignedBB axisalignedbb = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(vector3d.x(), vector3d.y(), vector3d.z());
         if (world.noCollision((Entity)null, axisalignedbb, (p_242390_0_) -> {
            return true;
         }) && world.getEntities((Entity)null, axisalignedbb).isEmpty()) {
            if (world instanceof ServerWorld) {
               ServerWorld serverworld = (ServerWorld)world;
               ArmorStandEntity armorstandentity = EntityType.ARMOR_STAND.create(serverworld, itemstack.getTag(), (ITextComponent)null, p_195939_1_.getPlayer(), blockpos, SpawnReason.SPAWN_EGG, true, true);
               if (armorstandentity == null) {
                  return ActionResultType.FAIL;
               }

               serverworld.addFreshEntityWithPassengers(armorstandentity);
               float f = (float)MathHelper.floor((MathHelper.wrapDegrees(p_195939_1_.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
               armorstandentity.moveTo(armorstandentity.getX(), armorstandentity.getY(), armorstandentity.getZ(), f, 0.0F);
               this.randomizePose(armorstandentity, world.random);
               world.addFreshEntity(armorstandentity);
               world.playSound((PlayerEntity)null, armorstandentity.getX(), armorstandentity.getY(), armorstandentity.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
            }

            itemstack.shrink(1);
            return ActionResultType.sidedSuccess(world.isClientSide);
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   private void randomizePose(ArmorStandEntity p_179221_1_, Random p_179221_2_) {
      Rotations rotations = p_179221_1_.getHeadPose();
      float f = p_179221_2_.nextFloat() * 5.0F;
      float f1 = p_179221_2_.nextFloat() * 20.0F - 10.0F;
      Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
      p_179221_1_.setHeadPose(rotations1);
      rotations = p_179221_1_.getBodyPose();
      f = p_179221_2_.nextFloat() * 10.0F - 5.0F;
      rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      p_179221_1_.setBodyPose(rotations1);
   }
}
