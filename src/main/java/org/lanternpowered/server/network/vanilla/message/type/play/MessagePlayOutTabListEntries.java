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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public final class MessagePlayOutTabListEntries implements Message {

    private final ImmutableList<Entry> entries;

    public MessagePlayOutTabListEntries(Iterable<Entry> entries) {
        this.entries = ImmutableList.copyOf(entries);
    }

    public ImmutableList<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {

        private final GameProfile gameProfile;
        @Nullable private final GameMode gameMode;
        @Nullable private final Text displayName;
        @Nullable private final Integer ping;

        Entry(GameProfile gameProfile, @Nullable GameMode gameMode, @Nullable Text displayName, @Nullable Integer ping) {
            this.displayName = displayName;
            this.gameProfile = gameProfile;
            this.gameMode = gameMode;
            this.ping = ping;
        }

        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        @Nullable
        public GameMode getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text getDisplayName() {
            return this.displayName;
        }

        @Nullable
        public Integer getPing() {
            return this.ping;
        }

        public static final class Add extends Entry {

            public Add(GameProfile gameProfile, GameMode gameMode, @Nullable Text displayName, int ping) {
                super(gameProfile, gameMode, displayName, ping);
            }
        }

        public static final class UpdateGameMode extends Entry {

            public UpdateGameMode(GameProfile gameProfile, GameMode gameMode) {
                super(gameProfile, gameMode, null, null);
            }
        }

        public static final class UpdateLatency extends Entry {

            public UpdateLatency(GameProfile gameProfile, int ping) {
                super(gameProfile, null, null, ping);
            }
        }

        public static final class UpdateDisplayName extends Entry {

            public UpdateDisplayName(GameProfile gameProfile, @Nullable Text displayName) {
                super(gameProfile, null, displayName, null);
            }
        }

        public static final class Remove extends Entry {

            public Remove(GameProfile gameProfile) {
                super(gameProfile, null, null, null);
            }
        }
    }
}
