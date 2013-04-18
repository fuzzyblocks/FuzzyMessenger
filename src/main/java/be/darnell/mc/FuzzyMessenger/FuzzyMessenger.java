/*
 * Copyright (c) 2012 cedeel.
 * All rights reserved.
 * 
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package be.darnell.mc.FuzzyMessenger;

import be.darnell.mc.FuzzyMessenger.commands.*;

import be.darnell.mc.FuzzyLog.FuzzyLog;
import be.darnell.mc.FuzzyLog.LogFacility;

import java.io.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin to handle private messaging
 *
 * @author cedeel
 */
public final class FuzzyMessenger extends JavaPlugin {

    private final FuzzyMessengerListener listener = new FuzzyMessengerListener(this);

    // Map of mutees. String is username in lowercase.
    public MuteManager mm = new MuteManager(getDataFolder());
    protected WordFilter filter = new WordFilter(new File(getDataFolder(), "badwords.txt"), new File(getDataFolder(), "replacements.txt"));
    protected PrivateMessaging pm = new PrivateMessaging(this);
    protected static LogFacility logger;
    protected static boolean useLogger = false;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(listener, this);
        FileConfiguration fc = getConfig();
        useLogger = fc.getBoolean("useLogger");
        if (useLogger) {
            if (getServer().getPluginManager().getPlugin("FuzzyLog") != null) {
                FuzzyLog.addFacility("CHAT");
                logger = FuzzyLog.getFacility("CHAT");
            } else {
                getServer().getLogger().warning("FuzzyMessenger: useLogger set to true, but FuzzyLog not found!");
                useLogger = false;
            }
        }
        registerCommands();
    }

    @Override
    public void onDisable() {
        saveConfig();
        mm.save();
    }

    public static void logMessage(String message) {
        if(useLogger)
            logger.log(message);
        else
            Bukkit.getServer().getLogger().info(message);
    }

    private void registerCommands() {
        getCommand("mute").setExecutor(new MuteCommand(mm));
        getCommand("unmute").setExecutor(new UnmuteCommand(mm));
        getCommand("ismuted").setExecutor(new IsMutedCommand(mm));
        getCommand("mutees").setExecutor(new MuteesCommand(mm));

        getCommand("snoop").setExecutor(new SnoopCommand(pm));
        getCommand("pm").setExecutor(new PMCommand(pm));
        getCommand("reply").setExecutor(new ReplyCommand(pm));
        getCommand("me").setExecutor(new EmoteCommand(mm));
    }
}
