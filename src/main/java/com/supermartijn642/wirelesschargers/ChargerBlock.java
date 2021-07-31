package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlock extends BaseBlock implements IWaterLoggable {

    public final ChargerType type;

    public ChargerBlock(ChargerType type){
        super(type.getRegistryName(), true, Properties.of(Material.METAL, DyeColor.GRAY).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(2f));
        this.type = type;

        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context){
        return this.type.modelType.outlineShape.getUnderlying();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_){
        return this.type.modelType.collisionShape.getUnderlying();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context){
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state){
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean hasTileEntity(BlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return this.type.createTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder){
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult){
        if(world.isClientSide)
            ClientProxy.openChargerScreen(TextComponents.block(this).get(), pos);
        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> list, ITooltipFlag flag){
        ITextComponent range = TextComponents.number(this.type.range.get() * 2 + 1).color(TextFormatting.GOLD).get();

        // blocks
        if(this.type.canChargeBlocks){
            list.add(TextComponents.translation("wirelesschargers.charger.info.blocks", range).color(TextFormatting.YELLOW).get());

            ITextComponent transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(TextFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_blocks", transferRate).color(TextFormatting.GRAY).get());
        }

        // players
        if(this.type.canChargePlayers){
            list.add(TextComponents.translation("wirelesschargers.charger.info.players", range).color(TextFormatting.YELLOW).get());

            ITextComponent transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(TextFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_players", transferRate).color(TextFormatting.GRAY).get());
        }

        // stored energy
        int energy = stack.getTag() == null ? 0 : stack.getTag().getCompound("tileData").getInt("energy");
        if(energy > 0){
            ITextComponent energyText = TextComponents.string(EnergyFormat.formatEnergy(energy)).color(TextFormatting.GOLD).get();
            ITextComponent capacity = TextComponents.string(EnergyFormat.formatEnergy(this.type.capacity.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(TextFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.stored_energy", energyText, capacity).color(TextFormatting.GRAY).get());
        }

        // redstone mode
        int redstoneMode = stack.getTag() == null ? 2 : stack.getTag().getCompound("tileData").contains("redstoneMode") ? stack.getTag().getCompound("tileData").getInt("redstoneMode") : 2;
        if(redstoneMode != 2){
            ChargerBlockEntity.RedstoneMode mode = ChargerBlockEntity.RedstoneMode.fromIndex(redstoneMode);
            ITextComponent value = TextComponents.translation("wirelesschargers.screen.redstone_" + mode.name().toLowerCase(Locale.ROOT)).color(TextFormatting.GOLD).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.redstone_mode", value).color(TextFormatting.GRAY).get());
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side){
        return side != Direction.UP;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean p_220069_6_){
        TileEntity entity = world.getBlockEntity(pos);
        if(entity instanceof ChargerBlockEntity)
            ((ChargerBlockEntity)entity).setRedstonePowered(world.hasNeighborSignal(pos));
    }
}
