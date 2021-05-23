package ilja615.iljatech.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import ilja615.iljatech.blocks.NailsBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

import net.minecraft.item.Item.Properties;

public class IronHammerItem extends TieredItem
{
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    protected static final Map<Block, Block> BLOCK_NAILS_MAP = (new ImmutableMap.Builder<Block, Block>())
            .put(Blocks.OAK_PLANKS, ModBlocks.NAILED_OAK_PLANKS.get())
            .put(Blocks.SPRUCE_PLANKS, ModBlocks.NAILED_SPRUCE_PLANKS.get())
            .put(Blocks.BIRCH_PLANKS, ModBlocks.NAILED_BIRCH_PLANKS.get())
            .put(Blocks.JUNGLE_PLANKS, ModBlocks.NAILED_JUNGLE_PLANKS.get())
            .put(Blocks.ACACIA_PLANKS, ModBlocks.NAILED_ACACIA_PLANKS.get())
            .put(Blocks.DARK_OAK_PLANKS, ModBlocks.NAILED_DARK_OAK_PLANKS.get())
            .put(Blocks.WARPED_PLANKS, ModBlocks.NAILED_WARPED_PLANKS.get())
            .put(Blocks.CRIMSON_PLANKS, ModBlocks.NAILED_CRIMSON_PLANKS.get()).build();

    protected static final Map<Block, Block> BLOCK_CRACKING_MAP = (new ImmutableMap.Builder<Block, Block>())
            .put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS)
            .put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
            .put(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS)
            .put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
            .put(Blocks.STONE, Blocks.COBBLESTONE)
            .put(Blocks.COBBLESTONE, Blocks.GRAVEL).build();

    public IronHammerItem(Properties properties)
    {
        super(ItemTier.IRON, properties);
        this.attackDamage = 5 + ItemTier.IRON.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.6f, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof NailsBlock && blockstate.hasProperty(NailsBlock.STAGE))
        {
            world.playSound(context.getPlayer(), blockpos, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            int stage = blockstate.getValue(NailsBlock.STAGE);
            if (stage < 3)
                world.setBlockAndUpdate(blockpos, blockstate.setValue(NailsBlock.STAGE, stage + 1));
            else
            {
                Block block = BLOCK_NAILS_MAP.get(world.getBlockState(blockpos.relative(blockstate.getValue(BlockStateProperties.FACING))).getBlock());
                if (block != null)
                {
                    world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                    world.setBlockAndUpdate(blockpos.relative(blockstate.getValue(BlockStateProperties.FACING)), block.defaultBlockState());
                }
            }
            return ActionResultType.SUCCESS;
        } else {
            Block block = BLOCK_CRACKING_MAP.get(world.getBlockState(blockpos).getBlock());
            if (block != null)
            {
                world.playSound(context.getPlayer(), blockpos, SoundEvents.STONE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockAndUpdate(blockpos, block.defaultBlockState());
                context.getPlayer().getCooldowns().addCooldown(this, 20);
                return ActionResultType.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (getCooldown(stack) == 0)
        {
            target.addEffect(new EffectInstance(ModEffects.STUNNED.get(), 50, 1));
            stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
            setCooldown(stack, 200);
        }
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
            stack.hurtAndBreak(1, entityLiving, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
            });
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        return material != Material.METAL && material != Material.HEAVY_METAL && material != Material.STONE ? super.getDestroySpeed(stack, state) : ItemTier.IRON.getSpeed();
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        int i = this.getTier().getLevel();
        if (blockIn.getHarvestTool() == net.minecraftforge.common.ToolType.PICKAXE) {
            return i >= blockIn.getHarvestLevel();
        }
        Material material = blockIn.getMaterial();
        return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL;
    }

    public static int getCooldown(ItemStack itemStack) {
        CompoundNBT compoundnbt = itemStack.getTag();
        return compoundnbt == null ? 0 : compoundnbt.getInt("coolDown");
    }

    public static void setCooldown(ItemStack itemStack, int amount) {
        CompoundNBT compoundnbt = itemStack.getOrCreateTag();
        compoundnbt.putInt("coolDown", amount);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int i, boolean b)
    {
        int cooldown = getCooldown(itemStack);
        if (cooldown > 0) {
            setCooldown(itemStack, cooldown - 1);
        }
        super.inventoryTick(itemStack, world, entity, i, b);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return false;
    }
}
