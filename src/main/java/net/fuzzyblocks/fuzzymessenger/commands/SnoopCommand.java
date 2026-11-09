/*
 * Copyright (c) 2012 - 2026 Chris Darnell (cedeel).
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
package net.fuzzyblocks.fuzzymessenger.commands;

import net.fuzzyblocks.fuzzymessenger.PrivateMessaging;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
                                    Component.text("PM snooping enabled.")
                                    .color(NamedTextColor.GREEN)
                            );
                        } else {
                            player.sendMessage(
                                    Component.text("PM snooping already enabled.")
                                    .color(NamedTextColor.YELLOW)
                            );
                        }
                    } else if (args[0].equalsIgnoreCase("off")) {
                        if (pm.isSnooper(player)) {
                            pm.removeSnooper(player);
                            sender.sendMessage(
                                Component.text("PM snooping disabled.")
                                .color(NamedTextColor.GREEN)
                            );
                        } else {
                            player.sendMessage(
                                Component.text("PM snooping not enabled.")
                                .color(NamedTextColor.YELLOW)
                            );
                        }
                    }
                    return true;
                }
                return false;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("No snooping for console.");
            return true;
        }
        return false;
    }
}
