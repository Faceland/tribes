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
