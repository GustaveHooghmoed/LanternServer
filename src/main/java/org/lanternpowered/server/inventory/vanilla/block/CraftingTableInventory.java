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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.event.LanternEventHelper;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.type.LanternCraftingInventory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class CraftingTableInventory extends LanternCraftingInventory implements CarriedInventory<Carrier> {

    private final CarrierReference<Carrier> carrierReference = CarrierReference.of(Carrier.class);

    @Override
    protected void init() {
        super.init();

        addCloseListener(inventory -> getCarrier().ifPresent(carrier -> {
            if (carrier instanceof Locatable) {
                final Cause cause = Cause.source(this).named(NamedCause.owner(carrier)).build();
                final Location<World> location = ((Locatable) carrier).getLocation();
                LanternEventHelper.fireDropItemEventDispense(cause, entities -> getCraftingGrid().slots().forEach(
                        slot -> slot.poll().filter(stack -> !stack.isEmpty()).ifPresent(
                                stack -> entities.add(LanternEventHelper.createDroppedItem(location, stack.createSnapshot())))));
            }
        }));
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
    }

    @Override
    public Optional<Carrier> getCarrier() {
        return this.carrierReference.get();
    }
}
