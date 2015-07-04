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

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvpManager {

    private final Map<UUID, PvpData> tagData;

    public PvpManager() {
        tagData = new HashMap<>();
    }

    public PvpData getData(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return tagData.containsKey(uuid) ? tagData.get(uuid) : new PvpData(0, null);
    }

    public void setData(UUID uuid, PvpData data) {
        Preconditions.checkNotNull(uuid);
        tagData.put(uuid, data);
    }

    public void clearTime(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        tagData.remove(uuid);
    }

    public class PvpData {
        private final long time;
        private final UUID tagger;

        private PvpData(long time, UUID tagger) {
            this.time = time;
            this.tagger = tagger;
        }

        public long time() {
            return time;
        }

        public UUID tagger() {
            return tagger;
        }

        public PvpData withTime(long time) {
            return new PvpData(time, tagger);
        }

        public PvpData withTagger(UUID tagger) {
            return new PvpData(time, tagger);
        }
    }

}
