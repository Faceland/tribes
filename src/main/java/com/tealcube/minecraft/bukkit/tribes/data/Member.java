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

import java.util.UUID;

public final class Member {

    private final UUID uniqueId;
    private UUID tribe;
    private int score = 100;
    private Tribe.Rank rank = Tribe.Rank.GUEST;
    private PvpState pvpState = PvpState.OFF;

    public Member(UUID uuid) {
        this.uniqueId = uuid;
    }

    public PvpState getPvpState() {
        return pvpState;
    }

    public void setPvpState(PvpState pvpState) {
        this.pvpState = pvpState;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getTribe() {
        return tribe;
    }

    public void setTribe(UUID tribe) {
        this.tribe = tribe;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Tribe.Rank getRank() {
        return rank;
    }

    public void setRank(Tribe.Rank rank) {
        this.rank = rank;
    }

    public enum PvpState {
        ON,
        OFF
    }

}
