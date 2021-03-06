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
package org.lanternpowered.server.text.format;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.text.format.TextStyle;

import javax.annotation.Nullable;

public class LanternTextStyle extends TextStyle.Base implements SimpleCatalogType {

    private final String identifier;

    public LanternTextStyle(String identifier, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underline,
            @Nullable Boolean strikethrough, @Nullable Boolean obfuscated) {
        super(bold, italic, underline, strikethrough, obfuscated);
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    public static final class Formatting extends LanternTextStyle implements FormattingCodeHolder {

        private final char code;

        public Formatting(String identifier, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underline,
                @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, char code) {
            super(identifier, bold, italic, underline, strikethrough, obfuscated);
            this.code = code;
        }

        @Override
        public char getCode() {
            return this.code;
        }
    }
}
