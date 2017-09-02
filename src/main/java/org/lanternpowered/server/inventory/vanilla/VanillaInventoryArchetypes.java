/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.inventory.vanilla;

import static org.lanternpowered.server.plugin.InternalPluginsInfo.Minecraft;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractOrderedSlotsInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.filter.ItemFilter;
import org.lanternpowered.server.inventory.type.LanternCarriedEquipmentInventory;
import org.lanternpowered.server.inventory.type.LanternOrderedSlotsInventory;
import org.lanternpowered.server.inventory.type.slot.LanternEquipmentSlot;
import org.lanternpowered.server.inventory.type.slot.LanternFuelSlot;
import org.lanternpowered.server.inventory.type.slot.LanternInputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternOutputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.inventory.vanilla.block.DispenserInventory;
import org.lanternpowered.server.inventory.vanilla.block.FurnaceInventory;
import org.lanternpowered.server.inventory.vanilla.block.JukeboxInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

public final class VanillaInventoryArchetypes {

    ////////////////////
    /// Default Slot ///
    ////////////////////

    public static final LanternInventoryArchetype<LanternSlot> SLOT = AbstractSlot.builder()
            .typeSupplier(LanternSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "slot");

    //////////////////
    /// Input Slot ///
    //////////////////

    public static final LanternInventoryArchetype<LanternInputSlot> INPUT_SLOT = AbstractSlot.builder()
            .typeSupplier(LanternInputSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "input_slot");

    ///////////////////
    /// Output Slot ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternOutputSlot> OUTPUT_SLOT = AbstractSlot.builder()
            .typeSupplier(LanternOutputSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "output_slot");

    /////////////////
    /// Fuel Slot ///
    /////////////////

    public static final LanternInventoryArchetype<LanternFuelSlot> FUEL_SLOT = AbstractSlot.builder()
            .filter(ItemFilter.ofStackPredicate(stack ->
                    Lantern.getRegistry().getFuelRegistry().findMatching(stack.createSnapshot()).isPresent()))
            .typeSupplier(LanternFuelSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "fuel_slot");

    ///////////////////
    /// Helmet Slot ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> HELMET_SLOT = AbstractSlot.builder()
            .property(EquipmentSlotType.of(EquipmentTypes.HEADWEAR))
            .typeSupplier(LanternEquipmentSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "helmet_slot");

    ///////////////////////
    /// Chestplate Slot ///
    ///////////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> CHESTPLATE_SLOT = AbstractSlot.builder()
            .property(EquipmentSlotType.of(EquipmentTypes.CHESTPLATE))
            .typeSupplier(LanternEquipmentSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "chestplate_slot");

    /////////////////////
    /// Leggings Slot ///
    /////////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> LEGGINGS_SLOT = AbstractSlot.builder()
            .property(EquipmentSlotType.of(EquipmentTypes.LEGGINGS))
            .typeSupplier(LanternEquipmentSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "leggings_slot");

    //////////////////
    /// Boots Slot ///
    //////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> BOOTS_SLOT = AbstractSlot.builder()
            .property(EquipmentSlotType.of(EquipmentTypes.BOOTS))
            .typeSupplier(LanternEquipmentSlot::new)
            .buildArchetype(Minecraft.IDENTIFIER, "boots_slot");

    /////////////
    /// Chest ///
    /////////////

    public static final LanternInventoryArchetype<ChestInventory> CHEST;

    ///////////////////
    /// Shulker Box ///
    ///////////////////

    public static final LanternInventoryArchetype<ChestInventory> SHULKER_BOX;

    ///////////////////
    /// Ender Chest ///
    ///////////////////

    public static final LanternInventoryArchetype<ChestInventory> ENDER_CHEST;

    static {
        final AbstractGridInventory.SlotsBuilder<ChestInventory> builder = AbstractGridInventory.slotsBuilder()
                .typeSupplier(ChestInventory::new);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                builder.slot(x, y, SLOT);
            }
        }
        CHEST = builder.property(new InventoryTitle(t("container.chest")))
                .buildArchetype(Minecraft.IDENTIFIER, "chest");
        SHULKER_BOX = builder.property(new InventoryTitle(t("container.shulkerBox")))
                .buildArchetype(Minecraft.IDENTIFIER, "shulker_box");
        ENDER_CHEST = builder.property(new InventoryTitle(t("container.enderchest")))
                .buildArchetype(Minecraft.IDENTIFIER, "ender_chest");
    }

    ////////////////////
    /// Double Chest ///
    ////////////////////

    public static final LanternInventoryArchetype<ChestInventory> DOUBLE_CHEST = AbstractGridInventory.rowsBuilder()
            .property(new InventoryTitle(t("container.chestDouble")))
            .grid(0, CHEST)
            .grid(3, CHEST)
            .typeSupplier(ChestInventory::new)
            .buildArchetype(Minecraft.IDENTIFIER, "double_chest");

    /////////////////
    /// Dispenser ///
    /////////////////

    public static final LanternInventoryArchetype<DispenserInventory> DISPENSER;

    static {
        final AbstractGridInventory.SlotsBuilder<AbstractGridInventory> builder = AbstractGridInventory.slotsBuilder();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.slot(x, y, SLOT);
            }
        }
        DISPENSER = builder.property(new InventoryTitle(t("container.dispenser")))
                .typeSupplier(DispenserInventory::new)
                .buildArchetype(Minecraft.IDENTIFIER, "dispenser");
    }

    ///////////////
    /// Jukebox ///
    ///////////////

    public static final LanternInventoryArchetype<JukeboxInventory> JUKEBOX = AbstractSlot.builder()
            .typeSupplier(JukeboxInventory::new)
            .buildArchetype(Minecraft.IDENTIFIER, "jukebox");

    ///////////////
    /// Furnace ///
    ///////////////

    public static final LanternInventoryArchetype<FurnaceInventory> FURNACE = LanternOrderedSlotsInventory.builder()
            .property(new InventoryTitle(t("container.furnace")))
            .slot(INPUT_SLOT)
            .slot(FUEL_SLOT)
            .slot(OUTPUT_SLOT)
            .typeSupplier(FurnaceInventory::new)
            .buildArchetype(Minecraft.IDENTIFIER, "furnace");

    ////////////////////////
    /// Entity Equipment ///
    ////////////////////////

    public static final LanternInventoryArchetype<LanternCarriedEquipmentInventory> ARMOR = AbstractOrderedSlotsInventory.builder()
            .slot(HELMET_SLOT)
            .slot(CHESTPLATE_SLOT)
            .slot(LEGGINGS_SLOT)
            .slot(BOOTS_SLOT)
            .typeSupplier(LanternCarriedEquipmentInventory::new)
            .buildArchetype(Minecraft.IDENTIFIER, "armor");

    ////////////////////////
    /// Player Main Grid ///
    ////////////////////////

    public static final LanternInventoryArchetype<AbstractGridInventory> PLAYER_MAIN_GRID;

    static {
        final AbstractGridInventory.SlotsBuilder<AbstractGridInventory> builder = AbstractGridInventory.slotsBuilder();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                builder.slot(x, y, SLOT);
            }
        }
        PLAYER_MAIN_GRID = builder
                .buildArchetype(Minecraft.IDENTIFIER, "player_main_grid");
    }

    /////////////////////
    /// Player Hotbar ///
    /////////////////////

    public static final LanternInventoryArchetype<LanternHotbarInventory> PLAYER_HOTBAR;

    static {
        final AbstractOrderedSlotsInventory.Builder<?> builder = AbstractOrderedSlotsInventory.builder();
        for (int x = 0; x < 9; x++) {
            builder.slot(SLOT);
        }
        PLAYER_HOTBAR = builder
                .typeSupplier(LanternHotbarInventory::new)
                .buildArchetype(Minecraft.IDENTIFIER, "player_hotbar");
    }

    ///////////////////
    /// Player Main ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternMainPlayerInventory> PLAYER_MAIN = AbstractGridInventory.rowsBuilder()
            .grid(0, PLAYER_MAIN_GRID)
            .row(3, PLAYER_HOTBAR, 1050)
            .typeSupplier(LanternMainPlayerInventory::new)
            .buildArchetype(Minecraft.IDENTIFIER, "player_main");

    private VanillaInventoryArchetypes() {
    }
}
