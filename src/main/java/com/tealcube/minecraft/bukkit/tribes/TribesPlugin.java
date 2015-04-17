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
package com.tealcube.minecraft.bukkit.tribes;

import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.facecore.shade.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.facecore.shade.config.VersionedSmartConfiguration;
import com.tealcube.minecraft.bukkit.facecore.shade.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.kern.methodcommand.CommandHandler;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Optional;
import com.tealcube.minecraft.bukkit.tribes.commands.GCommand;
import com.tealcube.minecraft.bukkit.tribes.commands.PvpCommand;
import com.tealcube.minecraft.bukkit.tribes.commands.TribeCommand;
import com.tealcube.minecraft.bukkit.tribes.data.Cell;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import com.tealcube.minecraft.bukkit.tribes.listeners.PlayerListener;
import com.tealcube.minecraft.bukkit.tribes.managers.CellManager;
import com.tealcube.minecraft.bukkit.tribes.managers.MemberManager;
import com.tealcube.minecraft.bukkit.tribes.managers.PvpManager;
import com.tealcube.minecraft.bukkit.tribes.managers.TribeManager;
import com.tealcube.minecraft.bukkit.tribes.storage.DataStorage;
import com.tealcube.minecraft.bukkit.tribes.storage.SqliteDataStorage;
import com.tealcube.minecraft.bukkit.tribes.tasks.DataCleanTask;
import com.tealcube.minecraft.bukkit.tribes.tasks.DataSaveTask;
import info.faceland.q.QPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;

public class TribesPlugin extends FacePlugin {

    private static TribesPlugin INSTANCE;
    private DataStorage dataStorage;
    private CellManager cellManager;
    private TribeManager tribeManager;
    private MemberManager memberManager;
    private PvpManager pvpManager;
    private PluginLogger debugPrinter;
    private MasterConfiguration settings;
    private QPlugin qPlugin;
    private HighNoonPlugin highNoonPlugin;
    private Economy economy;

    public static TribesPlugin getInstance() {
        return INSTANCE;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    @Override
    public void enable() {
        INSTANCE = this;
        debugPrinter = new PluginLogger(this);
        debug("Enabling v" + getDescription().getVersion());

        VersionedSmartYamlConfiguration configYAML = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            debug("Updating config.yml");
        }
        VersionedSmartYamlConfiguration dbYAML = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "db.yml"), getResource("db.yml"),
                VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (dbYAML.update()) {
            debug("Updating db.yml");
        }

        settings = MasterConfiguration.loadFromFiles(configYAML, dbYAML);

        dataStorage = new SqliteDataStorage(this);
        dataStorage.initialize();

        cellManager = new CellManager();
        memberManager = new MemberManager();
        tribeManager = new TribeManager();
        pvpManager = new PvpManager();

        loadData();

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new TribeCommand(this));
        commandHandler.registerCommands(new PvpCommand(this));
        commandHandler.registerCommands(new GCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        qPlugin = (QPlugin) getServer().getPluginManager().getPlugin("Q");
        highNoonPlugin = (HighNoonPlugin) getServer().getPluginManager().getPlugin("HighNoon");

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        new DataSaveTask(this).runTaskTimer(this, 0L, 20L * 600);
        new DataCleanTask(this).runTaskTimer(this, 0L, 20L * 600);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        getDataStorage().saveTribes(getTribeManager().getTribes());
        getDataStorage().saveCells(getCellManager().getCells());
        getDataStorage().saveMembers(getMemberManager().getMembers());
        dataStorage.shutdown();
    }

    public void debug(String... messages) {
        for (String message : messages) {
            debugPrinter.log(message);
        }
    }

    private void loadData() {
        for (Cell cell : dataStorage.loadCells()) {
            cellManager.placeCell(cell.getLocation(), cell);
        }
        for (Member member : dataStorage.loadMembers()) {
            if (memberManager.hasMember(member)) {
                memberManager.removeMember(member);
            }
            memberManager.addMember(member);
        }
        for (Tribe tribe : dataStorage.loadTribes()) {
            if (tribeManager.hasTribe(tribe)) {
                tribeManager.removeTribe(tribe);
            }
            tribeManager.addTribe(tribe);
        }
        debug("cells loaded: " + cellManager.getCells().size(),
                "members loaded: " + memberManager.getMembers().size(),
                "tribes loaded: " + tribeManager.getTribes().size());
        for (Member member : memberManager.getMembers()) {
            if (member.getTribe() == null) {
                continue;
            }
            Optional<Tribe> tribeOptional = tribeManager.getTribe(member.getTribe());
            if (tribeOptional.isPresent()) {
                Tribe tribe = tribeOptional.get();
                tribeOptional.get().setRank(member.getUniqueId(), member.getRank());
                getTribeManager().removeTribe(tribe);
                getTribeManager().addTribe(tribe);
            } else {
                member.setRank(Tribe.Rank.GUEST);
                member.setTribe(null);
            }
        }
    }

    public TribeManager getTribeManager() {
        return tribeManager;
    }

    public CellManager getCellManager() {
        return cellManager;
    }

    public MemberManager getMemberManager() {
        return memberManager;
    }

    public MasterConfiguration getSettings() {
        return settings;
    }

    public PvpManager getPvpManager() {
        return pvpManager;
    }

    public QPlugin getQPlugin() {
        return qPlugin;
    }

    public Economy getEconomy() {
        return economy;
    }

    public HighNoonPlugin getHighNoonPlugin() {
        return highNoonPlugin;
    }

}
