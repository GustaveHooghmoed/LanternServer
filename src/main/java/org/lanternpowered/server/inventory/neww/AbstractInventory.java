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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.lanternpowered.server.inventory.neww.type.LanternUnorderedSlotsInventory;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.item.inventory.property.ArmorSlotType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * The base implementation for all the {@link Inventory}s.
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class AbstractInventory implements IInventory {

    static class Name {
        static final Translation INSTANCE = tr("inventory.name"); // The default name
    }

    @Nullable private Translation name;
    @Nullable private AbstractInventory parent;
    @Nullable private AbstractInventory cachedRoot;

    /**
     * Sets the name of this inventory.
     *
     * @param name The name
     */
    void setName(@Nullable Translation name) {
        this.name = name;
    }

    /**
     * Sets the parent {@link Inventory} of this inventory.
     *
     * @param parent The parent inventory
     */
    void setParent(@Nullable AbstractInventory parent) {
        this.parent = parent;
        this.cachedRoot = null;
    }

    /**
     * Sets the parent {@link Inventory} of this inventory safely. This
     * doesn't override the current parent if already set.
     *
     * @param parent The parent inventory
     */
    void setParentSafely(AbstractInventory parent) {
        if (this.parent == null) {
            setParent(parent);
        }
    }

    protected void init() {
    }

    /**
     * Gets the {@link EmptyInventory} that should be used by this
     * inventory when queries fail.
     *
     * @return The empty inventory
     */
    protected abstract EmptyInventory empty();

    /**
     * Gets the {@link EmptyInventory} as a
     * result of type {@link T}.
     *
     * @param <T> The inventory type
     * @return The empty inventory as T
     */
    protected final <T extends Inventory> T genericEmpty() {
        return (T) empty();
    }

    /**
     * Gets all the {@link AbstractSlot}s that could be found in the
     * children inventories. This method may return a empty {@link List}
     * if the subclass doesn't support children, for example slots and
     * empty inventories.
     *
     * @return The slots
     */
    protected abstract List<AbstractSlot> getSlotInventories();

    /**
     * Attempts to offer the specified {@link ItemStack} to this inventory.
     *
     * @param stack The item stack
     * @return The result
     */
    protected abstract FastOfferResult offerFast(ItemStack stack);

    void addViewer(Viewer viewer, LanternContainer container) {
    }

    void removeViewer(Viewer viewer, LanternContainer container) {
    }

    void close() {
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return (Iterable<T>) getSlotInventories();
    }

    // Basic inventory stuff

    @Override
    public PluginContainer getPlugin() {
        // Use the plugin container from the parent if possible
        return this.parent == null ? Lantern.getMinecraftPlugin() : this.parent.getPlugin();
    }

    @Override
    public Translation getName() {
        return this.name == null ? Name.INSTANCE : this.name;
    }

    @Override
    public AbstractInventory parent() {
        return this.parent == null ? this : this.parent;
    }

    @Override
    public AbstractInventory root() {
        if (this.cachedRoot != null) {
            return this.cachedRoot;
        }
        AbstractInventory parent = this;
        AbstractInventory parent1;
        while ((parent1 = parent.parent()) != parent) {
            parent = parent1;
        }
        return this.cachedRoot = parent;
    }

    /**
     * Gets the child {@link AbstractInventory} at
     * the index, if present.
     * <p>
     * INTERNAL USE ONLY, is used for {@link #first()}
     * and {@link #next()} operations.
     *
     * @param index The index
     * @return The child inventory
     */
    @Nullable
    AbstractInventory getChild(int index) {
        return genericEmpty();
    }

    /**
     * Gets the index of the child inventory, or -1
     * if the inventory isn't a child.
     * <p>
     * INTERNAL USE ONLY, is used for {@link #first()}
     * and {@link #next()} operations.
     *
     * @param inventory The child inventory
     * @return The index
     */
    int getChildIndex(AbstractInventory inventory) {
        return -1;
    }

    @Override
    public <T extends Inventory> T first() {
        final Inventory inventory = getChild(0);
        return inventory instanceof EmptyInventory ? (T) this : (T) inventory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T next() {
        final AbstractInventory parent = parent();
        if (parent == this) {
            return (T) empty();
        }
        final int index = parent.getChildIndex(this);
        checkState(index != -1);
        return (T) parent.getChild(index + 1);
    }

    // Queries

    /**
     * Queries for specific {@link AbstractSlot}s.
     *
     * @param predicate The predicate
     * @param <T> The inventory type
     * @return The slots query inventory
     */
    protected <T extends Inventory> T querySlots(Predicate<AbstractSlot> predicate) {
        List<AbstractSlot> slots = getSlotInventories();
        if (slots.isEmpty()) { // Fail fast
            return genericEmpty();
        }
        slots = slots.stream().filter(predicate).collect(ImmutableList.toImmutableList());
        if (slots.isEmpty()) {
            return genericEmpty();
        }
        // Construct the result inventory
        final LanternUnorderedSlotsInventory result = new LanternUnorderedSlotsInventory();
        result.init(slots);
        return (T) result;
    }

    protected abstract <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate);

    @Override
    public <T extends Inventory> T query(ItemType... types) {
        checkNotNull(types, "types");
        return querySlots(slot -> {
            for (ItemType type : types) {
                if (slot.contains(type)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        checkNotNull(types, "types");
        return querySlots(slot -> {
            for (ItemStack type : types) {
                if (slot.contains(type)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T queryAny(ItemStack... types) {
        checkNotNull(types, "types");
        return querySlots(slot -> {
            for (ItemStack type : types) {
                if (slot.containsAny(type)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        checkNotNull(types, "types");
        return queryInventories(inventory -> {
            for (Class<?> type : types) {
                if (type.isInstance(inventory)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        checkNotNull(props, "props");
        return queryInventories(inventory -> {
            for (InventoryProperty<?,?> prop : props) {
                final Optional<InventoryProperty<?,?>> optProperty = inventory.getProperty((Class) prop.getClass(), prop.getKey());
                if (optProperty.isPresent() && prop.matches(optProperty.get())) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T query(Translation... names) {
        checkNotNull(names, "names");
        return queryInventories(inventory -> {
            for (Translation name : names) {
                if (inventory.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        checkNotNull(names, "names");
        return queryInventories(inventory -> {
            final String plainName = inventory.getName().get();
            for (String name : names) {
                if (plainName.equals(name)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public <T extends Inventory> T query(Object... args) {
        checkNotNull(args, "args");
        return queryInventories(inventory -> {
            for (Object arg : args) {
                if (arg instanceof Inventory) {
                    if (inventory.equals(arg)) {
                        return true;
                    }
                } else if (arg instanceof InventoryArchetype) {
                    if (inventory.getArchetype().equals(arg)) {
                        return true;
                    }
                } else if (arg instanceof ItemStack) {
                    if (inventory.contains((ItemStack) arg)) {
                        return true;
                    }
                } else if (arg instanceof Translation) {
                    if (inventory.getName().equals(arg)) {
                        return true;
                    }
                } else if (arg instanceof ItemType) {
                    if (inventory.contains((ItemType) arg)) {
                        return true;
                    }
                } else if (arg instanceof EquipmentType) {
                    for (EquipmentSlotType property : Iterables.concat(
                            inventory.getProperties(EquipmentSlotType.class),
                            inventory.getProperties(ArmorSlotType.class))) {
                        if (((LanternEquipmentType) arg).isChild(property.getValue())) {
                            return true;
                        }
                    }
                } else if (arg instanceof EquipmentSlotType) {
                    for (EquipmentSlotType property : Iterables.concat(
                            inventory.getProperties(EquipmentSlotType.class),
                            inventory.getProperties(ArmorSlotType.class))) {
                        if (((LanternEquipmentType) ((EquipmentSlotType) arg).getValue()).isChild(property.getValue())) {
                            return true;
                        }
                    }
                } else if (arg instanceof InventoryProperty<?,?>) {
                    final InventoryProperty<?,?> prop = (InventoryProperty<?, ?>) arg;
                    final Optional<InventoryProperty<?,?>> optProperty = inventory.getProperty((Class) prop.getClass(), prop.getKey());
                    if (optProperty.isPresent() && prop.matches(optProperty.get())) {
                        return true;
                    }
                } else if (arg instanceof Class<?>) {
                    final Class<?> clazz = (Class<?>) arg;
                    if (InventoryProperty.class.isAssignableFrom(clazz)) {
                        if (inventory.getProperty((Class) clazz, "none").isPresent()) {
                            return true;
                        }
                    } else if (Inventory.class.isAssignableFrom(clazz)) {
                        if (clazz.isInstance(inventory)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    // Peek/poll operations

    @Override
    public Optional<ItemStack> poll() {
        return poll(stack -> true);
    }

    @Override
    public Optional<ItemStack> poll(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return poll(stack -> stack.getType().equals(itemType));
    }

    @Override
    public Optional<ItemStack> poll(int limit) {
        return poll(limit, stack -> true);
    }

    @Override
    public Optional<ItemStack> poll(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return poll(limit, stack -> stack.getType().equals(itemType));
    }

    @Override
    public Optional<ItemStack> peek() {
        return peek(stack -> true);
    }

    @Override
    public Optional<ItemStack> peek(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return peek(stack -> stack.getType().equals(itemType));
    }

    @Override
    public Optional<ItemStack> peek(int limit) {
        return peek(limit, stack -> true);
    }

    @Override
    public Optional<ItemStack> peek(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return peek(limit, stack -> stack.getType().equals(itemType));
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return offerFast(stack).asTransactionResult();
    }

    // Properties

    @Override
    public final <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Class<T> property) {
        return getPropertiesBuilder(property).build();
    }

    /**
     * Constructs a {@link ImmutableList} builder and populates it with
     * the {@link InventoryProperty}s of the provided type.
     *
     * @param property The property type
     * @param <T> The property type
     * @return The immutable list builder
     */
    <T extends InventoryProperty<?, ?>> ImmutableList.Builder<T> getPropertiesBuilder(Class<T> property) {
        checkNotNull(property, "property");
        final AbstractInventory parent = parent();
        final ImmutableList.Builder<T> properties = ImmutableList.builder();
        properties.addAll(tryGetProperties(property));
        if (parent != this) {
            properties.addAll(parent.tryGetProperties(this, property));
        }
        return properties;
    }

    @Override
    public final <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Inventory child, Class<T> property) {
        return getPropertiesBuilder((AbstractInventory) child, property).build();
    }

    /**
     * Constructs a {@link ImmutableList} builder and populates it with
     * the {@link InventoryProperty}s of the provided type for the
     * target child {@link Inventory}.
     *
     * @param child The target child inventory
     * @param property The property type
     * @param <T> The property type
     * @return The immutable list builder
     */
    <T extends InventoryProperty<?, ?>> ImmutableList.Builder<T> getPropertiesBuilder(AbstractInventory child, Class<T> property) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        final ImmutableList.Builder<T> properties = ImmutableList.builder();
        properties.addAll(tryGetProperties(child, property));
        properties.addAll(child.tryGetProperties(property));
        return properties;
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getInventoryProperty(Class<T> property) {
        return getProperty(property, AbstractInventoryProperty.getDefaultKey(property));
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, @Nullable Object key) {
        checkNotNull(property, "property");
        final AbstractInventory parent = parent();
        if (parent != this) {
            final Optional<T> optProperty = parent.tryGetProperty(this, property, key);
            if (optProperty.isPresent()) {
                return optProperty;
            }
        }
        return tryGetProperty(property, key);
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getInventoryProperty(Inventory child, Class<T> property) {
        return getProperty(child, property, AbstractInventoryProperty.getDefaultKey(property));
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, @Nullable Object key) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        Optional<T> optProperty = tryGetProperty(child, property, key);
        if (!optProperty.isPresent()) {
            optProperty = ((AbstractInventory) child).tryGetProperty(property, key);
        }
        return optProperty;
    }

    /**
     * Attempts to get a {@link InventoryProperty} of the given
     * type and optional key from this inventory.
     *
     * @param property The property type
     * @param key The key
     * @param <T> The property type
     * @return The property
     */
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryTitle.class) {
            return Optional.of((T) new InventoryTitle(TextTranslation.toText(getName())));
        } else if (property == InventoryCapacity.class) {
            return Optional.of((T) new InventoryCapacity(capacity()));
        }
        return Optional.empty();
    }

    /**
     * Attempts to get all the {@link InventoryProperty}s of the given
     * type from this inventory.
     *
     * @param property The property type
     * @param <T> The property type
     * @return The properties
     */
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> properties = new ArrayList<>();
        if (property == InventoryTitle.class) {
            properties.add((T) new InventoryTitle(TextTranslation.toText(getName())));
        } else if (property == InventoryCapacity.class) {
            properties.add((T) new InventoryCapacity(capacity()));
        }
        return properties;
    }

    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        return Optional.empty();
    }

    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        return new ArrayList<>();
    }
}
