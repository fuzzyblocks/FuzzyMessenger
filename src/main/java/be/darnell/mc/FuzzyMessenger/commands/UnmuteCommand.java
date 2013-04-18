/*
 * Copyright (c) 2013 cedeel.
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

import be.darnell.mc.FuzzyMessenger.FuzzyMessenger;
import be.darnell.mc.FuzzyMessenger.MuteManager;
import be.darnell.mc.FuzzyMessenger.Mutee;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

    private MuteManager manager;

    public UnmuteCommand(MuteManager mm) {
        manager = mm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            Mutee mutee = manager.get(args[0]);
            if(mutee != null) {
                return unmute(sender, mutee);
            }
        }
        sender.sendMessage(ChatColor.GOLD + "Usage: /unmute <player>");
        return false;
    }

    private boolean unmute(CommandSender sender, Mutee mutee) {
        try {
            if (manager.remove(mutee.playerName)) {
                FuzzyMessenger.logMessage(mutee.playerName + " was unmuted by " + sender.getName());
                String muter = sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName();
                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + mutee.displayName
                        + ChatColor.GOLD + " has been unmuted by "
                        + ChatColor.GRAY + muter);
            } else {
                sender.sendMessage(mutee.displayName + " was not muted.");
            }
        } catch (Exception e) {
            sender.sendMessage(mutee.displayName + " not found or not muted.");
        }
        return true;
    }
}
