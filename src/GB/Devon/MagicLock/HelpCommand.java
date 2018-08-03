package GB.Devon.MagicLock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;

        if (strings.length < 1 || strings[0].equalsIgnoreCase("help")) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setAuthor("Devon");
            bookMeta.setTitle("MagicLock Help");
            bookMeta.addPage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "MagicLock Help Guide\n\n"
            + ChatColor.BLACK + "" + ChatColor.RESET + "MagicLock enables users to lock their containers - chests and furnaces - from others.\nYou may add friends which can" +
                    " access them, but only the original owner can destroy the container or sign.",
                    "In order to lock a container, place a sign on it and follow the following syntax:\n" +
                    "[lock]\n" +
                    "<your name> " + ChatColor.ITALIC + "(optional)" + ChatColor.RESET + "\n" +
                    "<friend-1> " + ChatColor.ITALIC + "(optional)" + ChatColor.RESET +"\n" +
                    "<friend-2> " + ChatColor.ITALIC + "(optional)" + ChatColor.RESET + "\n\n" +
                    "Congratulations; you should receive confirmation your container is locked.");
            book.setItemMeta(bookMeta);
            player.getInventory().addItem(book);
        }
        return true;
    }
}
