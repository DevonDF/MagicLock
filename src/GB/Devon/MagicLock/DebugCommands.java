package GB.Devon.MagicLock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class DebugCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;

        if (!player.isOp()) {
            player.sendMessage("This are DEBUG commands, op needed!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("setowner")) {
            if (strings.length < 1) {
                player.sendMessage("Incorrect usage.");
                return true;
            }
            Block block = player.getTargetBlock(null, 5);
            if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) {
                player.sendMessage("Look at a sign you pillock!");
                return true;
            }
            player.sendMessage(block.getBlockData().toString());
            Sign sign = (Sign) block.getState();
            sign.setLine(1, ChatColor.GOLD + strings[0]);
            sign.update();
        }

        if (command.getName().equalsIgnoreCase("setfriend")) {
            if (strings.length < 1) {
                player.sendMessage("Incorrect usage.");
                return true;
            }
            Block block = player.getTargetBlock(null, 5);
            if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) {
                player.sendMessage("Look at a sign you pillock!");
                return true;
            }
            Sign sign = (Sign) block.getState();
            sign.setLine(2, strings[0]);
            sign.update();
        }
        return true;
    }
}
