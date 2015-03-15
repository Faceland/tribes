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
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public final class ScoreboardUtils {

    private static final String BOARD_KEY = "tribesboard";
    private static final Scoreboard EMPTY_BOARD;

    static {
        EMPTY_BOARD = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    private ScoreboardUtils() {
        // do nothing
    }

    public static void setPrefix(Player player, String prefix) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(prefix);
        Scoreboard scoreboard = EMPTY_BOARD;
        for (Team t : scoreboard.getTeams()) {
            t.removePlayer(player);
        }
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }
        team.setPrefix(prefix);
        team.addPlayer(player);
        player.setScoreboard(scoreboard);
    }

    public static void setSuffix(Player player, String suffix) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(suffix);
        Scoreboard scoreboard = EMPTY_BOARD;
        for (Team t : scoreboard.getTeams()) {
            t.removePlayer(player);
        }
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }
        team.setPrefix(suffix);
        team.addPlayer(player);
        player.setScoreboard(scoreboard);
    }

    public static void setDisplayBelowName(Player player, String display) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(display);
        Scoreboard scoreboard = EMPTY_BOARD;
        Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
        if (objective == null) {
            objective = scoreboard.registerNewObjective("tribesdisplay", "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        objective.setDisplayName(display);
    }

    public static void setDisplayBelowScore(Player player, int number) {
        Preconditions.checkNotNull(player);
        Scoreboard scoreboard = EMPTY_BOARD;
        Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
        if (objective == null) {
            objective = scoreboard.registerNewObjective("tribesdisplay", "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        Score score = objective.getScore(player.getName());
        score.setScore(number);
    }

    public static void updateMightDisplay(Member member) {
        Preconditions.checkNotNull(member);
        Player player = Bukkit.getPlayer(member.getUniqueId());
        Preconditions.checkNotNull(player);
        setDisplayBelowName(player, "Might");
        setDisplayBelowScore(player, member.getScore());
    }

}
