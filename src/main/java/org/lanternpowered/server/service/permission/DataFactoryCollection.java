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
package org.lanternpowered.server.service.permission;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class DataFactoryCollection extends LanternSubjectCollection {

    private final ConcurrentMap<String, LanternSubject> subjects = new ConcurrentHashMap<>();
    private final Function<String, MemorySubjectData> dataFactory;
    private final Function<String, CommandSource> commandSourceFunction;

    protected DataFactoryCollection(String identifier, LanternPermissionService service, Function<String, MemorySubjectData> dataFactory,
            Function<String, CommandSource> commandSourceFunction) {
        super(identifier, service);
        this.dataFactory = dataFactory;
        this.commandSourceFunction = commandSourceFunction;
    }

    @Override
    public LanternSubject get(String identifier) {
        checkNotNull(identifier, "identifier");
        if (!this.subjects.containsKey(identifier)) {
            this.subjects.putIfAbsent(identifier, new DataFactorySubject(identifier, this.dataFactory.apply(identifier)));
        }
        return this.subjects.get(identifier);
    }

    @Override
    public boolean hasRegistered(String identifier) {
        return this.subjects.containsKey(identifier);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Iterable<Subject> getAllSubjects() {
        return (Iterable) this.subjects.values();
    }

    private class DataFactorySubject extends LanternSubject {

        private final String identifier;
        private final MemorySubjectData data;

        protected DataFactorySubject(String identifier, MemorySubjectData data) {
            this.identifier = identifier;
            this.data = data;
        }

        @Override
        public String getIdentifier() {
            return this.identifier;
        }

        @Override
        public Optional<CommandSource> getCommandSource() {
            return Optional.ofNullable(DataFactoryCollection.this.commandSourceFunction.apply(getIdentifier()));
        }

        @Override
        public SubjectCollection getContainingCollection() {
            return DataFactoryCollection.this;
        }

        @Override
        public MemorySubjectData getSubjectData() {
            return this.data;
        }

        @Override
        public Tristate getPermissionValue(Set<Context> contexts, String permission) {
            Tristate ret = super.getPermissionValue(contexts, permission);
            if (ret == Tristate.UNDEFINED) {
                ret = getDataPermissionValue(DataFactoryCollection.this.getDefaults().getTransientSubjectData(), permission);
            }
            if (ret == Tristate.UNDEFINED) {
                ret = this.getDataPermissionValue(DataFactoryCollection.this.service.getDefaults().getTransientSubjectData(), permission);
            }
            return ret;
        }

        @Override
        public Optional<String> getOption(Set<Context> contexts, String key) {
            return Optional.empty();
        }
    }

}
