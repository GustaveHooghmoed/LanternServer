package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.translation.Translation;

public final class LanternEntityType extends SimpleLanternCatalogType implements EntityType {

    private final Class<? extends Entity> entityClass;
    private final Translation name;

    public LanternEntityType(String identifier, Class<? extends Entity> entityClass,
            Translation name) {
        super(identifier);

        this.entityClass = checkNotNull(entityClass, "entityClass");
        this.name = checkNotNull(name, "name");
    }

    @Override
    public Translation getTranslation() {
        return this.name;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }
}