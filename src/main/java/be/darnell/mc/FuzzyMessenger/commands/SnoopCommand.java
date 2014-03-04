/*
 * Copyright (c) 2012 - 2014 Chris Darnell (cedeel).
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
package be.darnell.mc.FuzzyMessenger.commands;

import be.darnell.mc.FuzzyMessenger.PrivateMessaging;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SnoopCommand implements CommandExecutor {

    private PrivateMessaging pm;

    public SnoopCommand(PrivateMessaging privateMessaging) {
        pm = privateMessaging;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fuzzymessenger.pm.snoop")) {
                if (args.length == 1) {
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
                    return true;
                }
                return false;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("No snooping for console :P");
            return true;
        }
        return false;
    }
}
