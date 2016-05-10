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
package org.lanternpowered.server.game.registry.type.world;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.WorldCreationSettingsTypes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ GameModeRegistryModule.class, GeneratorTypeRegistryModule.class, DifficultyRegistryModule.class,
        DimensionTypeRegistryModule.class, SerializationBehaviorRegistryModule.class, GeneratorModifierRegistryModule.class })
public final class WorldCreationSettingsRegistryModule implements AdditionalCatalogRegistryModule<WorldCreationSettings>,
        AlternateCatalogRegistryModule<WorldCreationSettings> {

    @RegisterCatalog(WorldCreationSettingsTypes.class)
    private final Map<String, WorldCreationSettings> worldCreationSettingsMap = new HashMap<>();

    @Override
    public Map<String, WorldCreationSettings> provideCatalogMap() {
        Map<String, WorldCreationSettings> provided = new HashMap<>();
        for (Map.Entry<String, WorldCreationSettings> entry : this.worldCreationSettingsMap.entrySet()) {
            provided.put(entry.getKey().replace("minecraft:", "").replace("sponge:", ""), entry.getValue());
        }
        return provided;
    }

    @Override
    public void registerDefaults() {
        final WorldCreationSettings overworld = WorldCreationSettings.builder()
                .enabled(true)
                .loadsOnStartup(true)
                .keepsSpawnLoaded(true)
                .generateSpawnOnLoad(true)
                .commandsAllowed(true)
                .gameMode(GameModes.SURVIVAL)
                .generator(GeneratorTypes.DEFAULT)
                .dimension(DimensionTypes.OVERWORLD)
                .difficulty(Difficulties.NORMAL)
                .usesMapFeatures(true)
                .hardcore(false)
                .pvp(true)
                .generateBonusChest(false)
                .serializationBehavior(SerializationBehaviors.AUTOMATIC)
                .build("minecraft:overworld", "Overworld");
        this.worldCreationSettingsMap.put("minecraft:overworld", overworld);
        this.worldCreationSettingsMap.put("minecraft:the_nether", WorldCreationSettings.builder()
                .from(overworld)
                .generator(GeneratorTypes.NETHER)
                .dimension(DimensionTypes.NETHER)
                .build("minecraft:the_nether", "The Nether"));
        this.worldCreationSettingsMap.put("minecraft:the_end", WorldCreationSettings.builder()
                .from(overworld)
                .generator(GeneratorTypes.THE_END)
                .dimension(DimensionTypes.THE_END)
                .build("minecraft:the_end", "The End"));
        this.worldCreationSettingsMap.put("sponge:skylands", WorldCreationSettings.builder()
                .from(overworld)
                .generatorModifiers(WorldGeneratorModifiers.SKYLANDS)
                .build("sponge:the_skylands", "The Skylands"));
    }

    @Override
    public void registerAdditionalCatalog(WorldCreationSettings extraCatalog) {
        checkNotNull(extraCatalog, "WorldCreationSettings cannot be null!");
        final String id = extraCatalog.getId().toLowerCase(Locale.ENGLISH);
        checkArgument(!id.isEmpty(), "Id may not be empty!");
        checkArgument(!id.startsWith("minecraft:"), "Plugin trying to register a fake minecraft generation settings!");
        checkArgument(!id.startsWith("sponge:"), "Plugin trying to register a fake sponge generation settings!");
        this.worldCreationSettingsMap.put(id, extraCatalog);
    }

    @Override
    public Optional<WorldCreationSettings> getById(String id) {
        return Optional.ofNullable(this.worldCreationSettingsMap.get(checkNotNull(id, "id").toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<WorldCreationSettings> getAll() {
        return Collections.unmodifiableCollection(this.worldCreationSettingsMap.values());
    }
}
