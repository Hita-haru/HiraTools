package org.modtest.hiratools;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WirelessTransmitterBlock extends BlockWithEntity {

    public static final MapCodec<WirelessTransmitterBlock> CODEC = createCodec(WirelessTransmitterBlock::new);

    public WirelessTransmitterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessTransmitterBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, net.minecraft.block.Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!world.isClient) {
            boolean isPowered = world.isReceivingRedstonePower(pos);

            if (world.getBlockEntity(pos) instanceof WirelessTransmitterBlockEntity blockEntity) {
                BlockPos receiverPos = blockEntity.getReceiverPos();

                if (receiverPos != null) {
                    BlockState receiverState = world.getBlockState(receiverPos);
                    if (receiverState.getBlock() instanceof WirelessReceiverBlock) {
                        if (receiverState.get(Properties.POWERED) != isPowered) {
                            world.setBlockState(receiverPos, receiverState.with(Properties.POWERED, isPowered), 3);
                        }
                    }
                }
            }
        }
    }
}
