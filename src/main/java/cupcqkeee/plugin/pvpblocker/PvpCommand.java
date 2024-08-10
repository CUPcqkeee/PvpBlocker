package cupcqkeee.plugin.pvpblocker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvpCommand implements CommandExecutor {

    private final PvpBlockerCommand plugin;

    public PvpCommand(PvpBlockerCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        if (args.length != 1) {
            player.sendMessage("Usage: /pvpblocker <on/off>");
            return false;
        }

        String action = args[0];

        if (action.equalsIgnoreCase("on")) {
            plugin.addProtectedPlayer(playerName);
            player.sendMessage("PvP protection is now ON.");
        } else if (action.equalsIgnoreCase("off")) {
            plugin.removeProtectedPlayer(playerName);
            player.sendMessage("PvP protection is now OFF.");
        } else {
            player.sendMessage("Usage: /pvpblocker <on/off>");
            return false;
        }
        return true;
    }
}
