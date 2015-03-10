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
package com.tealcube.minecraft.bukkit.tribes.utils;

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public final class ScoreboardUtils {

    private ScoreboardUtils() {
        // do nothing
    }

    public static void updateMightDisplay(Member member) {
        Preconditions.checkNotNull(member);
        Player player = Bukkit.getPlayer(member.getUniqueId());
        Preconditions.checkNotNull(player);
        Objective objective = player.getScoreboard().getObjective("mightBottom");
        if (objective != null) {
            objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.WHITE + "Might");
            Score score = objective.getScore(player.getName());
            score.setScore(member.getScore());
        } else {
            objective = player.getScoreboard().registerNewObjective("mightBottom", "dummy");
            objective.setDisplayName(ChatColor.WHITE + "Might");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            Score score = objective.getScore(player.getName());
            score.setScore(member.getScore());
        }
    }

}
