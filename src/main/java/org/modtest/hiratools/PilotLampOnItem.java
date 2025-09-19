package org.modtest.hiratools;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PilotLampOnItem extends Item {

    public PilotLampOnItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos clickedPos = context.getBlockPos();
        BlockState clickedState = world.getBlockState(clickedPos);
        ItemStack stack = context.getStack();

        if (clickedState.getBlock() instanceof WirelessTransmitterBlock || clickedState.getBlock() instanceof WirelessReceiverBlock) {
            if (!world.isClient) {
                stack.set(Hiratools.RECEIVER_POS, clickedPos);
                if (context.getPlayer() != null) {
                    context.getPlayer().sendMessage(Text.literal("ランプをリンクしました: " + clickedPos.toShortString()), false);
                }
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;

        BlockPos linkedPos = stack.get(Hiratools.RECEIVER_POS);
        if (linkedPos == null) return;

        if (world.getTime() % 20 == 0) {
            BlockState linkedState = world.getBlockState(linkedPos);
            boolean isPowered = false;

            if (linkedState.getBlock() instanceof WirelessTransmitterBlock) {
                isPowered = world.isReceivingRedstonePower(linkedPos);
            } else if (linkedState.getBlock() instanceof WirelessReceiverBlock) {
                isPowered = linkedState.get(WirelessReceiverBlock.POWERED);
            }

            if (!isPowered) {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    ItemStack newStack = new ItemStack(Hiratools.PILOT_LAMP_OFF);
                    newStack.set(Hiratools.RECEIVER_POS, linkedPos);
                    player.getInventory().setStack(slot, newStack);
                }
            }
        }
    }
}
