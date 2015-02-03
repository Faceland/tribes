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
package me.topplethenun.tribes.storage;

import me.topplethenun.tribes.data.Cell;
import me.topplethenun.tribes.data.Member;
import me.topplethenun.tribes.data.Tribe;
import me.topplethenun.tribes.math.Vec2;

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
