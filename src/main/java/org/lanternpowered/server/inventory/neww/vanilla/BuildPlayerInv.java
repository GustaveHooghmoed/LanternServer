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
package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.inventory.neww.AbstractGridInventory;
import org.lanternpowered.server.inventory.neww.AbstractOrderedSlotsInventory;
import org.lanternpowered.server.inventory.neww.AbstractSlot;
import org.lanternpowered.server.inventory.neww.DefaultGridInventory;
import org.lanternpowered.server.inventory.neww.LanternInventoryArchetype;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;

public class BuildPlayerInv {

    public static void buildPlayerInventoryArchetype() {
        // Step 1: Construct the hotbar archetype
        final AbstractOrderedSlotsInventory.Builder<?> hotbarBuilder = AbstractOrderedSlotsInventory.builder();
        final LanternInventoryArchetype<AbstractSlot> slotArchetype = AbstractSlot.builder().buildArchetype();
        // In case of a row or column, the index also represents the x or y position
        for (int i = 0; i < 9; i++) {
            hotbarBuilder.slot(slotArchetype);
        }
        final LanternInventoryArchetype<LanternHotbarInventory> hotbarArchetype = hotbarBuilder
                .typeSupplier(LanternHotbarInventory::new).buildArchetype();
        // Step 2: Construct the main inventory without the hotbar
        final AbstractGridInventory.SlotsBuilder<?> mainWithoutHotbarBuilder = AbstractGridInventory.slotsBuilder();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                mainWithoutHotbarBuilder.slot(x, y, slotArchetype);
            }
        }
        final LanternInventoryArchetype<DefaultGridInventory> mainWithoutHotbarArchetype = mainWithoutHotbarBuilder
                .typeSupplier(DefaultGridInventory::new).buildArchetype();
        // Step 3: Construct a grid of the main with hotbar
        final LanternInventoryArchetype<LanternMainPlayerInventory> mainArchetype = AbstractGridInventory.rowsBuilder()
                .grid(0, mainWithoutHotbarArchetype) // The first 3 rows are used by the main grid
                .row(3, hotbarArchetype) // The fourth is the hotbar
                .typeSupplier(LanternMainPlayerInventory::new).buildArchetype();
        // Step 4: Construct the equipment inventory
        final LanternInventoryArchetype<LanternEquipmentInventory> equipmentArchetype = AbstractOrderedSlotsInventory.builder()
                .slot(AbstractSlot.builder().property(EquipmentSlotType.of(EquipmentTypes.HEADWEAR)).buildArchetype())
                .slot(AbstractSlot.builder().property(EquipmentSlotType.of(EquipmentTypes.CHESTPLATE)).buildArchetype())
                .slot(AbstractSlot.builder().property(EquipmentSlotType.of(EquipmentTypes.LEGGINGS)).buildArchetype())
                .slot(AbstractSlot.builder().property(EquipmentSlotType.of(EquipmentTypes.BOOTS)).buildArchetype())
                .typeSupplier(LanternEquipmentInventory::new)
                .buildArchetype();
        // Step 5: Construct the off hand slot
        final LanternInventoryArchetype<AbstractSlot> offhandSlot = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.OFF_HAND)).buildArchetype();
        // TODO: Add crafting grid
        // Step 6: Construct the player inventory
    }
}
