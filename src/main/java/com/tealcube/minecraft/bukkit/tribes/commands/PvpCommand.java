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
package com.tealcube.minecraft.bukkit.tribes.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.utils.ScoreboardUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
            MessageUtils.sendMessage(sender, "<gray>Your PvP state is permanently <red>ON<gray>.");
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
            case DUEL:
                MessageUtils.sendMessage(sender, "<gray>You are in a duel.");
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
            MessageUtils.sendMessage(sender, "<red>You cannot toggle PvP if you're in a tribe.");
            return;
        }
        long time = plugin.getPvpManager().getData(sender.getUniqueId()).time();
        if (System.currentTimeMillis() - time < plugin.getSettings().getLong("config.time-since-tag-in-seconds") * 1000) {
            MessageUtils.sendMessage(sender, "<red>You must have been out of PvP for 5 seconds to toggle PvP.");
            return;
        }
        member.setPvpState(Member.PvpState.ON);
        ScoreboardUtils.setPrefix(sender, ChatColor.RED + String.valueOf('\u2726') + ChatColor.WHITE);
        ScoreboardUtils.setSuffix(sender, ChatColor.RED + String.valueOf('\u2726'));
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
            MessageUtils.sendMessage(sender, "<red>You cannot toggle PvP if you're in a tribe.");
            return;
        }
        long time = plugin.getPvpManager().getData(sender.getUniqueId()).time();
        if (System.currentTimeMillis() - time < plugin.getSettings().getLong("config.time-since-tag-in-seconds") * 1000) {
            MessageUtils.sendMessage(sender, "<red>You must have been out of PvP for 5 seconds to toggle PvP.");
            return;
        }
        member.setPvpState(Member.PvpState.OFF);
        ScoreboardUtils.setPrefix(sender, ChatColor.WHITE + String.valueOf('\u2726') + ChatColor.WHITE);
        ScoreboardUtils.setSuffix(sender, ChatColor.WHITE + String.valueOf('\u2726'));
        MessageUtils.sendMessage(sender, "<green>You toggled PvP off.");
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
    }

}
