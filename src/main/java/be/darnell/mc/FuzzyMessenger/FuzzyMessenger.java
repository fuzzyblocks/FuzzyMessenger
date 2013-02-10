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

import be.darnell.mc.FuzzyLog.FuzzyLog;
import be.darnell.mc.FuzzyLog.LogFacility;
import java.io.File;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin to handle private messaging
 *
 * @author cedeel
 */
public class FuzzyMessenger extends JavaPlugin {

    private final FuzzyMessengerListener listener = new FuzzyMessengerListener(this);
    protected HashSet<String> mutees = new HashSet<String>();
    protected WordFilter filter = new WordFilter(new File(getDataFolder(), "badwords.txt"), new File(getDataFolder(), "replacements.txt"));
    protected PrivateMessaging pm = new PrivateMessaging(this);
    protected LogFacility logger;
    protected boolean useLogger = false;

    @Override
    public void onEnable() {
        PluginManager pmgr = getServer().getPluginManager();
        pmgr.registerEvents(listener, this);
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
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();


        // switch (command.getName().toLowerCase()) {
        if (cmd.equals("mute")) {
            if (args.length == 1) {
                return mute(sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.GOLD + "Usage: /mute <player>");
            }
        } else if (cmd.equals("unmute")) {
            if (args.length == 1) {
                return unmute(sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.GOLD + "Usage: /unmute <player>");
            }
        } else if (cmd.equals("ismuted")) {
            if (args.length == 1) {
                Player player = getServer().getPlayer(args[0]);
                if (isMuted(args[0])) {
                    sender.sendMessage(ChatColor.RED + player.getName()
                            + ChatColor.GOLD + " is muted.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + player.getName()
                            + ChatColor.GOLD + " is not muted.");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.GOLD
                        + "Usage: /ismuted <player>");
                return false;
            }
        } else if (cmd.equals("mutees")) {
            int page = 1;
            if (args.length >= 1) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                }
            }
            sendMutees(sender, page);
            return true;
        }
        if (cmd.equals("pm")) {
            if (args.length > 1) {
                String message = constructMessage(args, 1);
                return pm.sendMessage(sender, args[0], message);
            }
            sender.sendMessage(ChatColor.GOLD
                    + "Usage: /pm <recipient> <message>");
        }

        if (cmd.equals("r")) {
            if (args.length >= 1) {
                return pm.replyMessage(sender, constructMessage(args, 0));
            }
        }
        if (cmd.equals("snoop")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("fuzzymessenger.pm.snoop")) {
                        if (args[0].equalsIgnoreCase("on")) {
                            if (!pm.isSnooper(player)) {
                                pm.addSnooper(player);
                                sender.sendMessage(
                                        ChatColor.GREEN
                                        + "PM snooping enabled.");
                            } else {
                                player.sendMessage(
                                        ChatColor.YELLOW
                                        + "PM snooping already enabled.");
                            }
                        } else if (args[0].equalsIgnoreCase("off")) {
                            if (pm.isSnooper(player)) {
                                pm.removeSnooper(player);
                                sender.sendMessage(
                                        ChatColor.GREEN
                                        + "PM snooping disabled.");
                            } else {
                                player.sendMessage(
                                        ChatColor.YELLOW
                                        + "PM snooping not enabled.");
                            }
                        }
                    }
                } else if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("No snooping for console :P");
                }
            } else {
                sender.sendMessage(ChatColor.GOLD
                        + "Usage: /snoop [on,off]");
            }
        }
        if (cmd.equals("me")) {
            emote(sender, constructMessage(args, 0));
            return true;

        }

        return false;
    }

    private boolean mute(CommandSender sender, String player) {
        if (sender.hasPermission("fuzzymessenger.mute")) {
            try {
                String mutee = getServer().getPlayer(player).getName();
                mutees.add(mutee);
                getServer().broadcastMessage(ChatColor.DARK_RED + mutee
                        + ChatColor.AQUA + " has been muted by "
                        + ChatColor.GOLD + sender.getName());
            } catch (NullPointerException e) {
                sender.sendMessage("Player not found.");
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
        return true;
    }

    private boolean unmute(CommandSender sender, String player) {
        if (sender.hasPermission("fuzzymessenger.mute")) {
            // OfflinePlayer mutee = getServer().getOfflinePlayer(player);
            try {
                mutees.remove(player);
                getServer().broadcastMessage(ChatColor.DARK_RED + player
                        + ChatColor.AQUA + " has been unmuted by "
                        + ChatColor.GOLD + sender.getName());
            } catch (Exception e) {
                sender.sendMessage(player + " not found or not muted");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
        return true;
    }

    private String constructMessage(String[] message, int offset) {
        String m = "";
        for (int i = offset; i < message.length; i++) {
            m = m + " " + message[i];
        }
        return m.trim();
    }

    private void emote(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fuzzymessenger.me")) {
                if (mutees.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You can't use emotes while muted.");
                } else {
                    this.getServer().broadcastMessage(ChatColor.DARK_GRAY + "* "
                            + ChatColor.WHITE + player.getDisplayName()
                            + " " + message + ChatColor.DARK_GRAY + " *");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to use emotes.");
            }
        } else {
            this.getServer().broadcastMessage(ChatColor.DARK_GRAY + "* "
                    + ChatColor.WHITE + "Console "
                    + message
                    + ChatColor.DARK_GRAY + " *");
        }
    }

    private void sendMutees(CommandSender sender, int page) {
        if (sender.hasPermission("fuzzymessenger.mute")) {
            sender.sendMessage(ChatColor.GOLD + "Muted players:");
            StringBuilder sb = new StringBuilder(48);
            sb.append(ChatColor.DARK_RED);
            try {
                for (String p : mutees) {
                    sb.append(p).append(", ");
                }
            } catch (NullPointerException e) {
            }
            sender.sendMessage(sb.toString());
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
    }

    private boolean isMuted(String playerName) {
        String player = getServer().getPlayer(playerName).getName();
        return mutees.contains(player);
    }
}
