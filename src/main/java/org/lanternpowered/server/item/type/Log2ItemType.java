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
package org.lanternpowered.server.item.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.item.BlockItemType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.item.ItemType;

import java.util.function.Function;

public class Log2ItemType extends BlockItemType {

    public static final Function<BlockType, ItemType> ITEM_TYPE_BUILDER =
            type -> new Log2ItemType(((LanternBlockType) type).getPluginId(), type.getName(), type);

    public Log2ItemType(String pluginId, String identifier, BlockType blockType) {
        super(pluginId, identifier, blockType);
    }

    @Override
    public void registerKeysFor(AbstractValueContainer valueContainer) {
        super.registerKeysFor(valueContainer);
        valueContainer.registerKey(Keys.TREE_TYPE, TreeTypes.ACACIA).nonRemovableAttachedValueProcessor();
    }
}