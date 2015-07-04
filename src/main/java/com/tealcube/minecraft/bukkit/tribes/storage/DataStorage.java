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
package com.tealcube.minecraft.bukkit.tribes.storage;

import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import com.tealcube.minecraft.bukkit.tribes.math.Vec2;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataStorage {

    void initialize();

    void shutdown();

    Set<Cell> loadCells();

    Set<Cell> loadCells(Iterable<Vec2> vec2s);

    Set<Cell> loadCells(Vec2... vec2s);

    void saveCells(Iterable<Cell> cellIterable);

    List<Member> loadMembers();

    List<Member> loadMembers(Iterable<UUID> uuids);

    List<Member> loadMembers(UUID... uuids);

    void saveMembers(Iterable<Member> memberIterable);

    List<Tribe> loadTribes();

    List<Tribe> loadTribes(Iterable<UUID> uuids);

    List<Tribe> loadTribes(UUID... uuids);

    void saveTribes(Iterable<Tribe> tribeIterable);

}
