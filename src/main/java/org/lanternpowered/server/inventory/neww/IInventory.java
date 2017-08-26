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
package org.lanternpowered.server.inventory.neww;

import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public interface IInventory extends Inventory {

    @Override
    IInventory parent();

    /**
     * Adds a {@link SlotChangeListener} to
     * this {@link Inventory}.
     *
     * @param listener The listener
     */
    void addChangeListener(SlotChangeListener listener);

    /**
     * Adds a {@link ContainerViewListener} to this {@link Inventory}.
     *
     * @param listener The listener
     */
    void addViewListener(ContainerViewListener listener);

    /**
     * Adds a {@link InventoryCloseListener} to this {@link Inventory}.
     *
     * @param listener The listener
     */
    void addCloseListener(InventoryCloseListener listener);

    Optional<ItemStack> poll(ItemType itemType);

    Optional<ItemStack> poll(Predicate<ItemStack> matcher);

    Optional<ItemStack> poll(int limit, ItemType itemType);

    Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher);

    Optional<ItemStack> peek(ItemType itemType);

    Optional<ItemStack> peek(Predicate<ItemStack> matcher);

    Optional<ItemStack> peek(int limit, ItemType itemType);

    Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher);

    /**
     * Check whether the supplied item can be inserted into this one of the children of the
     * inventory. Returning false from this method implies that {@link #offer} <b>would
     * always return false</b> for this item.
     *
     * @param stack ItemStack to check
     * @return True if the stack is valid for at least one of the children of this inventory
     */
    boolean isValidItem(ItemStack stack);

    @Override
    IInventory intersect(Inventory inventory);

    @Override
    IInventory union(Inventory inventory);
}