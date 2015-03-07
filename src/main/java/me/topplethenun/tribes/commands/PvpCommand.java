package me.topplethenun.tribes.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import me.topplethenun.tribes.TribesPlugin;
import me.topplethenun.tribes.data.Member;
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
        long time = plugin.getPvpManager().getTime(sender.getUniqueId());
        if (System.currentTimeMillis() - time < plugin.getSettings().getLong("config.time-since-tag-in-seconds") * 1000) {
            MessageUtils.sendMessage(sender, "<red>You must have been out of PvP for 5 seconds to toggle PvP.");
            return;
        }
        member.setPvpState(Member.PvpState.ON);
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
        long time = plugin.getPvpManager().getTime(sender.getUniqueId());
        if (System.currentTimeMillis() - time < plugin.getSettings().getLong("config.time-since-tag-in-seconds") * 1000) {
            MessageUtils.sendMessage(sender, "<red>You must have been out of PvP for 5 seconds to toggle PvP.");
            return;
        }
        member.setPvpState(Member.PvpState.OFF);
        MessageUtils.sendMessage(sender, "<green>You toggled PvP off.");
        plugin.getMemberManager().removeMember(member);
        plugin.getMemberManager().addMember(member);
    }

}
