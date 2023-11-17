package net.buildtheearth.modules.navigator.commands;

import net.buildtheearth.modules.navigator.menu.MainMenu;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NavigatorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.highlight("You must be a %s to %s this command!", "player", "execute"));
            return true;
        }

        //Opens the navigator
        new MainMenu((Player) sender);
        return true;
    }
}