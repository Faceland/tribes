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
package com.tealcube.minecraft.bukkit.tribes.utils;

import com.tealcube.minecraft.bukkit.shade.google.common.base.Preconditions;
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
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }
        team.setSuffix(suffix);
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
