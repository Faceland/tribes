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
package com.tealcube.minecraft.bukkit.tribes.managers;

import com.tealcube.minecraft.bukkit.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CellManager {

    private final Map<Vec2, Cell> cellMap;

    public CellManager() {
        this.cellMap = new HashMap<>();
    }

    public Optional<Cell> getCell(Vec2 vec2) {
        Preconditions.checkNotNull(vec2, "vec2 cannot be null");
        return !cellMap.containsKey(vec2) ? Optional.<Cell>absent() : Optional.of(cellMap.get(vec2));
    }

    public void placeCell(Vec2 vec2, Cell cell) {
        Preconditions.checkNotNull(vec2, "vec2 cannot be null");
        if (cell == null) {
            cellMap.remove(vec2);
        } else {
            cellMap.put(vec2, cell);
        }
    }

    public Set<Cell> getCells() {
        return new HashSet<>(cellMap.values());
    }

    public Set<Cell> getCellsWithOwner(UUID owner) {
        Preconditions.checkNotNull(owner);
        Set<Cell> cells = new HashSet<>();
        for (Cell cell : getCells()) {
            if (cell.getOwner() != null && cell.getOwner().equals(owner)) {
                cells.add(cell);
            }
        }
        return cells;
    }

}
