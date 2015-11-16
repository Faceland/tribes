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
package com.tealcube.minecraft.bukkit.tribes;

import com.tealcube.minecraft.bukkit.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.highnoon.HighNoonPlugin;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Optional;
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
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

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
    private Economy economy;
    private Permission perm;

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
                VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            debug("Updating config.yml");
        }
        VersionedSmartYamlConfiguration dbYAML = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "db.yml"), getResource("db.yml"),
                VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
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

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net
                .milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(net
                .milkbowl.vault.permission.Permission.class);
        if (permProvider != null) {
            perm = permProvider.getProvider();
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

    public Permission getPerm() {
        return perm;
    }

}
