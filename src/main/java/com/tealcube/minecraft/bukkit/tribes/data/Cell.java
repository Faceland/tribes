/*
 * This file is part of Tribes, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.tribes.data;

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;

import java.util.UUID;

/**
 * Cell represents a piece of claimable land.
 */
public final class Cell {

    private final Vec2 location;
    private UUID owner;

    /**
     * Constructs a new Cell based on a given Vec2.
     * @param location Chunk that is claimable
     */
    public Cell(Vec2 location) {
        this(location, null);
    }

    /**
     * Constructs a new Cell based on a given Vec2 and owner.
     * @param location Chunk that is claimable
     * @param owner owner of this Cell
     */
    public Cell(Vec2 location, UUID owner) {
        Preconditions.checkNotNull(location, "location cannot be null");
        this.location = location;
        this.owner = owner;
    }

    /**
     * Get and return the location of this Cell.
     * @return location of this Cell.
     */
    public Vec2 getLocation() {
        return location;
    }

    /**
     * Gets and returns the owner of this Cell.
     * @return owner of this Cell
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this Cell.
     * @param owner owner of this Cell
     */
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cell)) {
            return false;
        }

        Cell cell = (Cell) o;

        return !(location != null ? !location.equals(cell.location) : cell.location != null) &&
                !(owner != null ? !owner.equals(cell.owner) : cell.owner != null);
    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getLocation().toString() + ":" + (owner == null ? "" : owner.toString());
    }
}
