package org.modtest.hiratools;

import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class WirelessTransmitterBlockEntity extends BlockEntity {

    private BlockPos receiverPos;
    public static final String RECEIVER_POS_KEY = "receiver_pos";

    public WirelessTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(Hiratools.WIRELESS_TRANSMITTER_BLOCK_ENTITY, pos, state);
    }

    public void setReceiverPos(BlockPos pos) {
        this.receiverPos = pos;
        markDirty();
    }

    public BlockPos getReceiverPos() {
        return this.receiverPos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (this.receiverPos != null) {
            BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.receiverPos)
                    .resultOrPartial(error -> System.err.println("Failed to encode receiver pos: " + error))
                    .ifPresent(encoded -> nbt.put(RECEIVER_POS_KEY, encoded));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(RECEIVER_POS_KEY)) {
            DataResult<BlockPos> result = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get(RECEIVER_POS_KEY));
            Optional<BlockPos> posOptional = result.resultOrPartial(error -> System.err.println("Failed to decode receiver pos: " + error));
            posOptional.ifPresent(pos -> this.receiverPos = pos);
        }
    }
}
