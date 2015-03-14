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
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Arg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.utils.Formatter;
import com.tealcube.minecraft.bukkit.tribes.utils.ScoreboardUtils;
import info.faceland.q.actions.options.Option;
import info.faceland.q.actions.questions.Question;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelCommand {

    private final TribesPlugin plugin;

    public DuelCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "duel", permissions = "tribes.command.duel", onlyPlayers = true)
    public void duelCommand(final Player sender, @Arg(name = "target") final Player target) {
        if (sender.equals(target)) {
            MessageUtils.sendMessage(sender, "<red>You cannot duel yourself, dingus.");
            return;
        }
        final Member senderMember = plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(senderMember)) {
            plugin.getMemberManager().addMember(senderMember);
        }
        final Member targetMember = plugin.getMemberManager().getMember(target.getUniqueId()).or(new Member(target.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(targetMember)) {
            plugin.getMemberManager().addMember(targetMember);
        }
        if (senderMember.getPvpState() == Member.PvpState.DUEL || senderMember.getDuelPartner() != null) {
            MessageUtils.sendMessage(sender, "<red>You are already in a duel!");
            return;
        }
        if (targetMember.getPvpState() == Member.PvpState.DUEL || targetMember.getDuelPartner() != null) {
            MessageUtils.sendMessage(sender, "<red>This player is dueling somebody else.");
            return;
        }
        if (sender.getLocation().distance(target.getLocation()) > 50) {
            MessageUtils.sendMessage(target, "<red>This player is too far away to duel.");
            return;
        }
        List<Option> options = new ArrayList<>();
        options.add(new Option("accept", new Runnable() {
            @Override
            public void run() {
                if (senderMember.getPvpState() == Member.PvpState.DUEL || senderMember.getDuelPartner() != null) {
                    MessageUtils.sendMessage(target, "<red>This player is dueling somebody else.");
                    return;
                }
                if (targetMember.getPvpState() == Member.PvpState.DUEL || targetMember.getDuelPartner() != null) {
                    MessageUtils.sendMessage(target, "<red>You are already in a duel!");
                    return;
                }
                if (sender.getLocation().distance(target.getLocation()) > 50) {
                    MessageUtils.sendMessage(target, "<red>This player is too far away to duel.");
                    return;
                }
                senderMember.setPvpState(Member.PvpState.DUEL);
                targetMember.setPvpState(Member.PvpState.DUEL);
                senderMember.setDuelPartner(targetMember.getUniqueId());
                targetMember.setDuelPartner(senderMember.getUniqueId());
                MessageUtils.sendMessage(sender, "<white>%name% <dark purple>accepted your duel request! Fight!", new String[][]{{"%name%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<dark purple>You accepted <white>%name%<dark purple>'s duel request! Fight!", new String[][]{{"%name%", target.getDisplayName()}});
                plugin.getMemberManager().removeMember(targetMember);
                plugin.getMemberManager().removeMember(senderMember);
                plugin.getMemberManager().addMember(targetMember);
                plugin.getMemberManager().addMember(senderMember);
                ScoreboardUtils.setPrefix(target, ChatColor.LIGHT_PURPLE + String.valueOf('\u2726') + ChatColor.WHITE);
                ScoreboardUtils.setPrefix(sender, ChatColor.LIGHT_PURPLE + String.valueOf('\u2726') + ChatColor.WHITE);
                ScoreboardUtils.setSuffix(target, ChatColor.LIGHT_PURPLE + String.valueOf('\u2726'));
                ScoreboardUtils.setSuffix(sender, ChatColor.LIGHT_PURPLE + String.valueOf('\u2726'));
            }
        }));
        options.add(new Option("deny", new Runnable() {
            @Override
            public void run() {
                MessageUtils.sendMessage(sender, "<white>%name%<red> has declined your duel request.", new String[][]{{"%name%", target.getDisplayName()}});
                MessageUtils.sendMessage(target, "<red>You declined <white>%name%<red>'s duel request.", new String[][]{{"%name%", target.getDisplayName()}});
            }
        }));
        Question question = new Question(target.getUniqueId(), TextUtils.color(TextUtils.args(
                "<dark purple>[!] <white>%sender%<dark purple> has challened you to a duel!",
                new String[][]{{"%sender%", sender.getDisplayName()}})), options);
        plugin.getQPlugin().getQuestionManager().appendQuestion(question);
        List<String> messages = Formatter.format(question);
        for (String m : messages) {
            target.sendMessage(m);
        }
        MessageUtils.sendMessage(sender, "<dark purple>You have challened <white>%player%<dark purple> to a duel!",
                new String[][]{{"%player%", target.getDisplayName()}});
    }

}
