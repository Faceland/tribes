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
import com.tealcube.minecraft.bukkit.kern.methodcommand.Arg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Wildcard;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GCommand {

    private final TribesPlugin plugin;

    public GCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "g", permissions = "tribes.command.g", onlyPlayers = true)
    public void gCommand(Player sender, @Wildcard @Arg(name = "message") String message) {
        Member member = plugin.getMemberManager().getMember(sender.getUniqueId()).or(new Member(sender.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        if (member.getTribe() == null) {
            MessageUtils.sendMessage(sender, "<red>You must be in a guild to use guild chat.");
            return;
        }
        Optional<Tribe> tribeOptional = plugin.getTribeManager().getTribe(member.getTribe());
        if (!tribeOptional.isPresent()) {
            MessageUtils.sendMessage(sender, "<red>You must be in a guild to use guild chat.");
            return;
        }
        Tribe tribe = tribeOptional.get();
        for (Member m : plugin.getMemberManager().getMembersWithTribe(tribe.getUniqueId())) {
            Player player = Bukkit.getPlayer(m.getUniqueId());
            if (player != null && player.isOnline()) {
                MessageUtils.sendMessage(player, "<green>[G] %name%: %message%",
                        new String[][]{{"%name%", sender.getDisplayName()}, {"%message%", message}});
            }
        }
        MessageUtils.sendMessage(Bukkit.getConsoleSender(), "%name% -> %tribe%: %message%",
                new String[][]{{"%name%", sender.getDisplayName()}, {"%tribe%", tribe.getName()},
                        {"%message%", message}});
    }

}
