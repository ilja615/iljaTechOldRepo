package ilja615.iljatech.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import ilja615.iljatech.blocks.NailsBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModEffects;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Map;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.TierSortingRegistry;

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
        super(Tiers.IRON, properties);
        this.attackDamage = 5 + Tiers.IRON.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.6f, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof NailsBlock && blockstate.hasProperty(NailsBlock.STAGE))
        {
            world.playSound(context.getPlayer(), blockpos, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundSource.BLOCKS, 1.0F, 1.0F);
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
            return InteractionResult.SUCCESS;
        } else {
            Block block = BLOCK_CRACKING_MAP.get(world.getBlockState(blockpos).getBlock());
            if (block != null)
            {
                world.playSound(context.getPlayer(), blockpos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.setBlockAndUpdate(blockpos, block.defaultBlockState());
                context.getPlayer().getCooldowns().addCooldown(this, 20);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (getCooldown(stack) == 0)
        {
            target.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 50, 1));
            stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            setCooldown(stack, 200);
        }
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
            stack.hurtAndBreak(1, entityLiving, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        return material != Material.METAL && material != Material.HEAVY_METAL && material != Material.STONE ? super.getDestroySpeed(stack, state) : Tiers.IRON.getSpeed();
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return TierSortingRegistry.isCorrectTierForDrops(Tiers.IRON, state);
        }
        return false;
    }

    public static int getCooldown(ItemStack itemStack) {
        CompoundTag compoundnbt = itemStack.getTag();
        return compoundnbt == null ? 0 : compoundnbt.getInt("coolDown");
    }

    public static void setCooldown(ItemStack itemStack, int amount) {
        CompoundTag compoundnbt = itemStack.getOrCreateTag();
        compoundnbt.putInt("coolDown", amount);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int i, boolean b)
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
