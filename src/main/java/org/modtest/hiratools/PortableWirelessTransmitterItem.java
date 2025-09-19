package org.modtest.hiratools;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class PortableWirelessTransmitterItem extends Item {

    public PortableWirelessTransmitterItem(Settings settings) {
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

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        BlockPos receiverPos = stack.get(Hiratools.RECEIVER_POS);

        if (user.isSneaking()) {
            if (receiverPos != null) {
                if (!world.isClient) {
                    stack.remove(Hiratools.RECEIVER_POS);
                    user.sendMessage(Text.literal("記録を消去しました。"), false);
                }
                return TypedActionResult.success(stack);
            }
        }

        if (receiverPos != null) {
            if (!world.isClient) {
                BlockState receiverState = world.getBlockState(receiverPos);
                if (receiverState.getBlock() instanceof WirelessReceiverBlock) {
                    world.setBlockState(receiverPos, receiverState.cycle(Properties.POWERED), 3);
                }
            }
            return TypedActionResult.success(stack);
        } else {
            if (!world.isClient && user != null) {
                user.sendMessage(Text.literal("先に受信機を右クリックして記録してください。"), false);
            }
            return TypedActionResult.fail(stack);
        }
    }
}
