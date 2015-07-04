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
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import se.ranzdo.bukkit.methodcommand.Wildcard;

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
