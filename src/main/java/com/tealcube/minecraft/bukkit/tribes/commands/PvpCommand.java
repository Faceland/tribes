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
package com.tealcube.minecraft.bukkit.tribes.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.utils.ScoreboardUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Command;

public class PvpCommand {

    private final TribesPlugin plugin;

    public PvpCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "pvp", permissions = "tribes.commands.pvp", onlyPlayers = true)
    public void baseCommand(Player sender) {
        if (!plugin.getMemberManager().hasMember(sender.getUniqueId())) {
            plugin.getMemberManager().addMember(new Member(sender.getUniqueId()));
        }
        Optional<Member> memberOptional = plugin.getMemberManager().getMember(sender.getUniqueId());
        if (!memberOptional.isPresent()) {
            MessageUtils.sendMessage(sender, "<red>Houston, we have a problem! Alert ToppleTheNun immediately.");
            return;
        }
        Member member = memberOptional.get();
        if (member.getTribe() != null) {
            MessageUtils.sendMessage(sender, "<yellow>You cannot disable PvP mode if you are in a guild.");
            return;
        }
        switch (member.getPvpState()) {
            case ON:
                MessageUtils.sendMessage(sender, "<gray>Your PvP state is <red>ON<gray>.");
                MessageUtils.sendMessage(sender, "<gray>Use <green>\"/pvp off\"<gray> to toggle PvP off.");
                break;
            case OFF:
                MessageUtils.sendMessage(sender, "<gray>Your PvP state is <green>OFF<gray>.");
                MessageUtils.sendMessage(sender, "<gray>Use <red>\"/pvp on\"<gray> to toggle PvP off.");
                break;
        }
    }

    @Command(identifier = "pvp on", permissions = "tribes.commands.pvp", onlyPlayers = true)
    public void onSubcommand(Player sender) {
        if (!plugin.getMemberManager().hasMember(sender.getUniqueId())) {
            plugin.getMemberManager().addMember(new Member(sender.getUniqueId()));
        }
        Optional<Member> memberOptional = plugin.getMemberManager().getMember(sender.getUniqueId());
        if (!memberOptional.isPresent()) {
            MessageUtils.sendMessage(sender, "<red>Houston, we have a problem! Alert ToppleTheNun immediately.");
            return;
        }
        Member member = memberOptional.get();
        if (member.getTribe() != null) {
            MessageUtils.sendMessage(sender, "<gray>PvP mode is always enabled if you're in a guild!");
            return;
        }
        long time = plugin.getPvpManager().getData(sender.getUniqueId()).time();
        if (System.currentTimeMillis() - time < plugin.getSettings().getLong("config.time-since-tag-in-seconds") * 1000) {
            MessageUtils.sendMessage(sender, "<red>You must have been out of PvP for 5 seconds to toggle PvP.");
            return;
        }
        member.setPvpState(Member.PvpState.ON);
        ScoreboardUtils.setPrefix(sender, plugin.getSettings().getString("perm-prefix." + plugin.getPerm()
                .getPrimaryGroup(sender), "") + "<white>");
        ScoreboardUtils.setSuffix(sender, ChatColor.RED + String.valueOf('\u2756'));
        MessageUtils.sendMessage(sender, "<green>You toggled PvP on.");
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
    }

    @Command(identifier = "pvp off", permissions = "tribes.commands.pvp", onlyPlayers = true)
    public void offSubcommand(Player sender) {
        if (!plugin.getMemberManager().hasMember(sender.getUniqueId())) {
            plugin.getMemberManager().addMember(new Member(sender.getUniqueId()));
        }
        Optional<Member> memberOptional = plugin.getMemberManager().getMember(sender.getUniqueId());
        if (!memberOptional.isPresent()) {
            MessageUtils.sendMessage(sender, "<red>Houston, we have a problem! Alert ToppleTheNun immediately.");
            return;
        }
        Member member = memberOptional.get();
        if (member.getTribe() != null) {
            MessageUtils.sendMessage(sender, "<yellow>You cannot disable PvP mode if you are in a guild.");
            return;
        }
        long time = plugin.getPvpManager().getData(sender.getUniqueId()).time();
        if (System.currentTimeMillis() - time < plugin.getSettings().getLong("config.time-since-tag-in-seconds") * 1000) {
            MessageUtils.sendMessage(sender, "<red>You must have been out of PvP for 5 seconds to toggle PvP.");
            return;
        }
        member.setPvpState(Member.PvpState.OFF);
        ScoreboardUtils.setPrefix(sender, plugin.getSettings().getString("perm-prefix." + plugin.getPerm()
                .getPrimaryGroup(sender), "") + "<white>");
        ScoreboardUtils.setSuffix(sender, ChatColor.WHITE + String.valueOf('\u2756'));
        MessageUtils.sendMessage(sender, "<green>You toggled PvP off.");
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
    }

}
