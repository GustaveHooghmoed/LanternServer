/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.plugin;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class AbstractPluginContainer implements PluginContainer {

    protected AbstractPluginContainer() {
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper("Plugin")
                .omitNullValues()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("version", this.getVersion().orElse(null))
                .add("description", this.getDescription().orElse(null))
                .add("assets", getAssetDirectory().orElse(null))
                .add("url", this.getUrl().orElse(null))
                .add("authors", this.getAuthors().isEmpty() ? null : this.getAuthors())
                .add("source", this.getSource().orElse(null));
    }

    @Override
    public final String toString() {
        return this.toStringHelper().toString();
    }
}
