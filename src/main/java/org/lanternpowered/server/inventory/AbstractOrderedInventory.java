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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.TopContainerPart;
import org.lanternpowered.server.inventory.type.LanternOrderedInventory;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractOrderedInventory extends AbstractChildrenInventory implements IOrderedInventory {

    public static Builder<LanternOrderedInventory> builder() {
        return new Builder<>().type(LanternOrderedInventory.class);
    }

    public static ViewBuilder<LanternOrderedInventory> viewBuilder() {
        return new ViewBuilder<>().type(LanternOrderedInventory.class);
    }

    /**
     * Represents a invalid slot index.
     */
    static final int INVALID_INDEX = -1;

    @Nullable private List<AbstractMutableInventory> children;
    @Nullable private List<AbstractSlot> slots;
    @Nullable private List<AbstractSlot> prioritizedSlots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;

    void initWithSlots(List<AbstractMutableInventory> children, List<? extends AbstractSlot> slots,
            @Nullable List<? extends AbstractSlot> prioritizedSlots) {
        this.children = children;
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        int index = 0;
        for (AbstractSlot slot : slots) {
            slotsToIndex.put(slot, index++);
        }
        this.slots = ImmutableList.copyOf(slots);
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        this.prioritizedSlots = prioritizedSlots != null ? ImmutableList.copyOf(prioritizedSlots) : null;
        init();
    }

    void initWithChildren(List<AbstractMutableInventory> children,
            @Nullable List<AbstractMutableInventory> prioritizedChildren) {
        this.children = prioritizedChildren == null ? children : prioritizedChildren;
        final ImmutableList.Builder<AbstractSlot> slotsBuilder = ImmutableList.builder();
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        int index = 0;
        for (AbstractMutableInventory inventory : children) {
            if (inventory instanceof AbstractSlot) {
                final AbstractSlot slot = (AbstractSlot) inventory;
                slotsBuilder.add(slot);
                slotsToIndex.put(slot, index++);
            } else if (inventory instanceof AbstractOrderedInventory) {
                final AbstractOrderedInventory childrenInventory = (AbstractOrderedInventory) inventory;
                for (AbstractSlot slot : childrenInventory.getIndexedSlotInventories()) {
                    slotsBuilder.add(slot);
                    slotsToIndex.put(slot, index++);
                }
            } else {
                throw new IllegalArgumentException("All the children inventories must be ordered.");
            }
        }
        if (prioritizedChildren != null) {
            final ImmutableList.Builder<AbstractSlot> prioritizedSlotsBuilder = ImmutableList.builder();
            for (AbstractMutableInventory inventory : prioritizedChildren) {
                if (inventory instanceof AbstractSlot) {
                    prioritizedSlotsBuilder.add((AbstractSlot) inventory);
                } else {
                    prioritizedSlotsBuilder.addAll(inventory.getSlotInventories());
                }
            }
            this.prioritizedSlots = prioritizedSlotsBuilder.build();
        }
        this.slots = slotsBuilder.build();
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        init();
    }

    @Override
    protected List<AbstractSlot> getIndexedSlotInventories() {
        return this.slots == null ? Collections.emptyList() : this.slots;
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return this.prioritizedSlots != null ? this.prioritizedSlots : getIndexedSlotInventories();
    }

    @Override
    protected List<AbstractMutableInventory> getChildren() {
        return this.children == null ? Collections.emptyList() : this.children;
    }

    protected Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }

    // Supply the slot indexes for the children

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        if (property == SlotIndex.class && child instanceof Slot) {
            final int index = getSlotsToIndexMap().getInt(child);
            return index == INVALID_INDEX ? Optional.empty() : Optional.of(property.cast(SlotIndex.of(index)));
        }
        return super.tryGetProperty(child, property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (property == SlotIndex.class && child instanceof Slot) {
            final int index = getSlotsToIndexMap().getInt(child);
            if (index != INVALID_INDEX) {
                properties.add(property.cast(SlotIndex.of(index)));
            }
        }
        return properties;
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        return getSlot(index).flatMap(Inventory::poll);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
        return getSlot(index).flatMap(slot -> slot.poll(limit));
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        return getSlot(index).flatMap(Inventory::peek);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        return getSlot(index).flatMap(slot -> slot.peek(limit));
    }

    @Override
    public InventoryTransactionResult set(SlotIndex index, ItemStack stack) {
        return getSlot(index).map(slot -> slot.set(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    @Override
    public Optional<Slot> getSlot(SlotIndex slotIndex) {
        checkNotNull(slotIndex, "slotIndex");
        if (!(slotIndex.getOperator() == Property.Operator.EQUAL ||
                slotIndex.getOperator() == Property.Operator.DELEGATE) || slotIndex.getValue() == null) {
            return Optional.empty();
        }
        return (Optional) getSlot(slotIndex.getValue());
    }

    @Override
    public Optional<ISlot> getSlot(int index) {
        final List<AbstractSlot> slots = getIndexedSlotInventories();
        return index < 0 || index >= slots.size() ? Optional.empty() : Optional.ofNullable(slots.get(index));
    }

    @Override
    public int getSlotIndex(Slot slot) {
        return getSlotsToIndexMap().getInt(slot);
    }

    @Override
    protected void initClientContainer(ClientContainer clientContainer) {
        super.initClientContainer(clientContainer);

        // Bind all the slots of this inventory
        final TopContainerPart part = clientContainer.getTop();
        getSlotsToIndexMap().object2IntEntrySet()
                .forEach(entry -> part.bindSlot(entry.getIntValue(), entry.getKey().transform()));
    }

    public static final class Builder<T extends AbstractOrderedInventory>
            extends AbstractArchetypeBuilder<T, AbstractOrderedInventory, Builder<T>>  {

        private final List<PrioritizedObject<LanternInventoryArchetype<? extends AbstractMutableInventory>>> inventories = new ArrayList<>();
        private int expandedSlots = 0;

        private Builder() {
        }

        void expand(int size) {
            this.expandedSlots += size;
        }

        @Override
        public <N extends AbstractOrderedInventory> Builder<N> type(Class<N> inventoryType) {
            return (Builder<N>) super.type(inventoryType);
        }

        /**
         * Adds the {@link InventoryArchetype} with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> addFirst(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype, int priority) {
            return add(0, inventoryArchetype, priority);
        }

        /**
         * Adds the {@link InventoryArchetype} with the default priority.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> addFirst(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            return add(0, inventoryArchetype, DEFAULT_PRIORITY);
        }

        /**
         * Adds the {@link InventoryArchetype} with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> addLast(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype, int priority) {
            return add(this.inventories.size(), inventoryArchetype, priority);
        }

        /**
         * Adds the {@link InventoryArchetype} with the default priority.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> addLast(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            return add(this.inventories.size(), inventoryArchetype, DEFAULT_PRIORITY);
        }

        /**
         * Adds the {@link InventoryArchetype} with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> add(int index, LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype, int priority) {
            this.inventories.add(index, new PrioritizedObject<>(inventoryArchetype, priority));
            if (inventoryArchetype.getBuilder() instanceof AbstractInventorySlot.Builder) {
                this.slots++;
                if (this.expandedSlots < this.slots) {
                    this.expandedSlots = this.slots;
                }
            } else {
                this.slots += inventoryArchetype.getBuilder().slots;
                if (this.expandedSlots < this.slots) {
                    this.expandedSlots = this.slots;
                }
            }
            return this;
        }

        /**
         * Adds the {@link InventoryArchetype} with the default priority.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> add(int index, LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            return add(index, inventoryArchetype, DEFAULT_PRIORITY);
        }

        @Override
        protected void build(AbstractOrderedInventory inventory) {
            checkState(this.expandedSlots <= this.slots, "The builder got expanded to %s slots, "
                    + "but only %s slots could be found.", this.expandedSlots, this.slots);
            final ImmutableList<PrioritizedObject<? extends AbstractMutableInventory>> prioritizedChildrenObjects = this.inventories.stream()
                    .map(e -> {
                        final AbstractMutableInventory inventory1 = e.object.build();
                        inventory1.setParentSafely(inventory);
                        return new PrioritizedObject<>(inventory1, e.priority);
                    })
                    .collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractMutableInventory> children = prioritizedChildrenObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractMutableInventory> prioritizedChildren = prioritizedChildrenObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.initWithChildren(children, prioritizedChildren);
        }

        @Override
        protected void copyTo(Builder<T> copy) {
            super.copyTo(copy);
            copy.inventories.addAll(this.inventories);
            copy.expandedSlots = this.expandedSlots;
        }

        @Override
        protected Builder<T> newBuilder() {
            return new Builder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            return (List) this.inventories;
        }
    }

    public static final class ViewBuilder<T extends AbstractOrderedInventory>
            extends AbstractViewBuilder<T, AbstractOrderedInventory, ViewBuilder<T>>  {

        private final List<PrioritizedObject<AbstractMutableInventory>> inventories = new ArrayList<>();

        private ViewBuilder() {
        }

        @Override
        public <N extends AbstractOrderedInventory> ViewBuilder<N> type(Class<N> inventoryType) {
            return (ViewBuilder<N>) super.type(inventoryType);
        }

        /**
         * Adds the {@link AbstractMutableInventory} with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventory The inventory
         * @param priority The priority
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventory(Inventory inventory, int priority) {
            this.inventories.add(new PrioritizedObject<>((AbstractMutableInventory) inventory, priority));
            return this;
        }

        /**
         * Adds the {@link AbstractMutableInventory} with the default priority.
         *
         * @param inventory The inventory
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventory(Inventory inventory) {
            return inventory(inventory, DEFAULT_PRIORITY);
        }

        /**
         * Adds the {@link AbstractMutableInventory}s with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventories The inventories
         * @param priority The priority
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventories(Iterable<? extends Inventory> inventories, int priority) {
            inventories.forEach(inventory -> inventory(inventory, priority));
            return this;
        }

        /**
         * Adds the {@link AbstractMutableInventory} with the default priority.
         *
         * @param inventories The inventories
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventories(Iterable<? extends Inventory> inventories) {
            inventories.forEach(this::inventory);
            return this;
        }

        @Override
        protected void build(AbstractOrderedInventory inventory) {
            final ImmutableList<AbstractMutableInventory> children = this.inventories.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractMutableInventory> prioritizedChildren = this.inventories.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.initWithChildren(children, prioritizedChildren);
        }
    }
}
