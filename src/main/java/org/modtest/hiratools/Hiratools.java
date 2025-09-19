package org.modtest.hiratools;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Hiratools implements ModInitializer {

    public static final String MOD_ID = "hiratools";

    public static final ComponentType<BlockPos> RECEIVER_POS = Registry.register(Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MOD_ID, "receiver_pos"),
            ComponentType.<BlockPos>builder().codec(BlockPos.CODEC).build());

    public static final Block WIRELESS_TRANSMITTER = registerBlock("wireless_transmitter",
            new WirelessTransmitterBlock(FabricBlockSettings.copyOf(Blocks.DIRT).strength(0.5f)));
    public static final Block WIRELESS_RECEIVER = registerBlock("wireless_receiver",
            new WirelessReceiverBlock(FabricBlockSettings.copyOf(Blocks.DIRT).strength(0.5f)));

    public static final BlockEntityType<WirelessTransmitterBlockEntity> WIRELESS_TRANSMITTER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(MOD_ID, "wireless_transmitter_entity"),
                    FabricBlockEntityTypeBuilder.create(WirelessTransmitterBlockEntity::new, WIRELESS_TRANSMITTER).build());

    public static final Item LINKER = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "linker"),
            new LinkerItem(new Item.Settings().component(RECEIVER_POS, null)));

    public static final Item PORTABLE_WIRELESS_TRANSMITTER = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "portable_wireless_transmitter"),
            new PortableWirelessTransmitterItem(new Item.Settings().component(RECEIVER_POS, null)));

    public static final Item PILOT_LAMP_OFF = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "pilot_lamp_off"),
            new PilotLampOffItem(new Item.Settings().component(RECEIVER_POS, null)));

    public static final Item PILOT_LAMP_ON = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "pilot_lamp_on"),
            new PilotLampOnItem(new Item.Settings().component(RECEIVER_POS, null)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Hiratools.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Hiratools.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(Hiratools.LINKER);
            content.add(Hiratools.WIRELESS_RECEIVER);
            content.add(Hiratools.WIRELESS_TRANSMITTER);
            content.add(Hiratools.PORTABLE_WIRELESS_TRANSMITTER);
            content.add(Hiratools.PILOT_LAMP_OFF);
        });
    }
}
