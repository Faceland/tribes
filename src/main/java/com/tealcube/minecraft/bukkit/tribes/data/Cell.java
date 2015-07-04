/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
