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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternHorseColor;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseColors;

public class HorseColorRegistryModule extends PluginCatalogRegistryModule<HorseColor> {

    public HorseColorRegistryModule() {
        super(HorseColors.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternHorseColor("minecraft", "white", 0));
        register(new LanternHorseColor("minecraft", "creamy", 1));
        register(new LanternHorseColor("minecraft", "chestnut", 2));
        register(new LanternHorseColor("minecraft", "brown", 3));
        register(new LanternHorseColor("minecraft", "black", 4));
        register(new LanternHorseColor("minecraft", "gray", 5));
        register(new LanternHorseColor("minecraft", "dark_brown", 6));
    }
}
