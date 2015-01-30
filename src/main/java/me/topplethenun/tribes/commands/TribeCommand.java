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
package me.topplethenun.tribes.commands;

import me.topplethenun.tribes.TribesPlugin;
import me.topplethenun.tribes.data.Member;
import me.topplethenun.tribes.data.Tribe;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.nunnerycode.facecore.utilities.MessageUtils;
import org.nunnerycode.kern.apache.commons.lang3.text.WordUtils;
import org.nunnerycode.kern.methodcommand.Command;

public class TribeCommand {

    private final TribesPlugin plugin;

    public TribeCommand(TribesPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "tribe", description = "base tribe command", onlyPlayers = false)
    public void baseCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "<red>Only players can execute the base command.");
            return;
        }
        Player player = (Player) sender;
        Member member = plugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!plugin.getMemberManager().hasMember(member)) {
            plugin.getMemberManager().addMember(member);
        }
        MessageUtils.sendMessage(player, "<green><====||====||====||====>");
        MessageUtils.sendMessage(player, "<white>%player%", new String[][]{{"%player%", player.getDisplayName()}});
        if (member.getTribe() == null || !plugin.getTribeManager().getTribe(member.getTribe()).isPresent()) {
            MessageUtils.sendMessage(player, "<red>Not a member of any Tribe");
        } else {
            MessageUtils.sendMessage(player, "<white>%rank%<dark green> of <white>%tribe%", new String[][]{
                    {"%rank%", WordUtils.capitalizeFully(member.getRank().name())},
                    {"%tribe%", plugin.getTribeManager().getTribe(member.getTribe()).get().getName()}
            });
            for (Tribe.Permission permission : Tribe.Permission.values()) {
                if (member.getRank().getPermissions().contains(permission)) {
                    MessageUtils.sendMessage(player, "<gray>You <green>CAN <gray> " + permission.name().toLowerCase());
                } else {
                    MessageUtils.sendMessage(player, "<gray>You <red>CAN'T <gray> " + permission.name()
                            .toLowerCase());
                }
            }
        }
        MessageUtils.sendMessage(player, "<dark green>Score: <white>%score%", new String[][]{{"%score%", member
                .getScore() + ""}});
        MessageUtils.sendMessage(player, "<green><====||====||====||====>");
    }

}
