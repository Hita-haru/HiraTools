package org.modtest.hiratools;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LinkerItem extends Item {

    public LinkerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getBlockPos();
        BlockState clickedState = world.getBlockState(clickedPos);
        ItemStack stack = context.getStack();

        if (clickedState.getBlock() instanceof WirelessReceiverBlock) {
            stack.set(Hiratools.RECEIVER_POS, clickedPos);
            if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(Text.literal("受信機を記録しました: " + clickedPos.toShortString()), false);
            }
            return ActionResult.SUCCESS;
        }

        if (clickedState.getBlock() instanceof WirelessTransmitterBlock) {
            BlockPos receiverPos = stack.get(Hiratools.RECEIVER_POS);

            if (receiverPos != null) {
                BlockEntity blockEntity = world.getBlockEntity(clickedPos);

                if (blockEntity instanceof WirelessTransmitterBlockEntity transmitterEntity) {
                    transmitterEntity.setReceiverPos(receiverPos);

                    stack.remove(Hiratools.RECEIVER_POS);

                    if (context.getPlayer() != null) {
                        context.getPlayer().sendMessage(Text.literal("ペアリング完了！ 送信機: " + clickedPos.toShortString() + " -> 受信機: " + receiverPos.toShortString()), false);
                    }
                    return ActionResult.SUCCESS;
                }
            } else {
                if (context.getPlayer() != null) {
                    context.getPlayer().sendMessage(Text.literal("先に受信機を右クリックしてください。"), false);
                }
                return ActionResult.FAIL;
            }
        }

        return super.useOnBlock(context);
    }
}