package GB.Devon.MagicLock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class LockListener implements Listener {

    /*
        This will handle placing signs down to protect chests/furnaces
     */
    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer() == null) {
            return;
        }
        if (event.getBlock().getType() != Material.SIGN && event.getBlock().getType() != Material.WALL_SIGN) {
            return;
        }
        if (!event.getLine(0).equalsIgnoreCase("[lock]")) {
            return;
        }

        Sign sign = (Sign) event.getBlock().getState();
        org.bukkit.material.Sign sm = (org.bukkit.material.Sign) sign.getData();
        Block attachedBlock = sign.getBlock().getRelative(sm.getAttachedFace());
        Player player = event.getPlayer();

        if (attachedBlock.getType() != Material.FURNACE && attachedBlock.getType() != Material.CHEST) {
            // Check if the sign is being placed on a container
            player.sendMessage(ChatColor.RED + "You must attach a MagicLock sign to a furnace or chest!");
            sign.getBlock().setType(Material.AIR);
            player.getInventory().addItem(new ItemStack(Material.SIGN, 1));
            return;
        }

        if (getLockSign(attachedBlock) != null) {
            // Check if there is another lock sign
            player.sendMessage(ChatColor.RED + "This chest is already locked!");
            sign.getBlock().setType(Material.AIR);
            player.getInventory().addItem(new ItemStack(Material.SIGN, 1));
            return;
        }

        if (isDoubleChest(attachedBlock)) {
            // Check if we are a double chest
            player.sendMessage("Debug doublechest!");
            if (getLockSignDoubleChest(attachedBlock) != null) {
                // Is the other chest locked?
                player.sendMessage(ChatColor.RED + "This chest is already locked!");
                sign.getBlock().setType(Material.AIR);
                player.getInventory().addItem(new ItemStack(Material.SIGN, 1));
                return;
            }
        }

        if (!player.hasPermission("magicLock.canLock")) {
            // Check if the player has permission to make a lock
            player.sendMessage(ChatColor.RED + "You do not have permission to setup locks with MagicLock");
            sign.getBlock().setType(Material.AIR); // Remove the sign
            player.getInventory().addItem(new ItemStack(Material.SIGN, 1));
            return;
        }

        event.setLine(0, ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Lock]"); // Set our lock with colour tag
        event.setLine(1, ChatColor.GOLD + player.getName()); // Set the player as the owner
        player.sendMessage(ChatColor.DARK_PURPLE + "Your chest has been locked successfully.");
    }

    /*
        Handles removing the ability to delete MagicLock signs from non-owners
     */
    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer() == null) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN) {
            return;
        }
        Sign sign = (Sign) block.getState();
        if (!ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[lock]")) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.getName().equals(ChatColor.stripColor(sign.getLine(1))) && !player.hasPermission("magicLock.overrideLock")) {
            event.setCancelled(true); // We cannot break this sign!
            player.sendMessage(ChatColor.DARK_PURPLE + "You do not have permission to remove this MagicLock sign!");
            return;
        }
    }

    /*
        Handles trying to remove containers with signs on them
     */
    @EventHandler
    public void onContainerBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer() == null) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Sign sign;

        if (block.getType() != Material.CHEST && block.getType() != Material.FURNACE) {return;}

        if (!isDoubleChest(block)) {
            sign = getLockSign(block);
        } else {
            sign = getLockSignDoubleChest(block);
        }

        if (sign == null) {return;}

        if (!player.getName().equals(ChatColor.stripColor(sign.getLine(1))) && !player.hasPermission("magicLock.overrideLock")) {
            event.setCancelled(true); // Don't break this container
            player.sendMessage(ChatColor.DARK_PURPLE + "You do not have permission to remove this container!");
            return;
        }
    }

    /*
        Handles trying to access containers that are locked
     */
    @EventHandler
    public void onContainerOpened(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer() == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {return;}
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Sign sign;
        if (!isDoubleChest(block)) {
            sign = getLockSign(block);
        } else {
            sign = getLockSignDoubleChest(block);
        }

        if (sign == null) {
            // If there is no lock sign on this container
            return;
        }

        boolean hasAccess = player.hasPermission("magicLock.overrideLock"); // If they have this perm, then we start at true , false otherwise
        for (String s : sign.getLines()) {
            if (player.getName().equals(ChatColor.stripColor(s))) {
                hasAccess = true;
                break;
            }
        }

        if (!hasAccess) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_PURPLE + "This container is locked!");
        }

    }

    /*
        Handles trying to obstruct containers that are locked
     */
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer() == null) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Block container = block.getRelative(BlockFace.DOWN);
        if (container.getType() != Material.CHEST) {return;}

        Sign sign;
        if (!isDoubleChest(container)) {
            sign = getLockSign(container);
        } else {
            sign = getLockSignDoubleChest(container);
        }

        if (sign == null) {return;}

        if (!player.getName().equals(ChatColor.stripColor(sign.getLine(1))) && !player.hasPermission("magicLock.overrideLock")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_PURPLE + "You cannot obstruct a locked container!");
            return;
        }
    }

    /*
        Gets the lock sign on a doublechest
     */
    private Sign getLockSignDoubleChest(Block block) {
        Sign sign = getLockSign(block);
        if (sign == null) {
            return getLockSign(getOtherChest(block));
        }
        return sign;
    }

    /*
        Checks whether chest is a doublechest
     */
    private boolean isDoubleChest(Block block) {
        if (block.getType() != Material.CHEST) {return false;}
        Chest chest = (Chest) block.getState();
        return (chest.getInventory() instanceof DoubleChestInventory);
    }

    /*
        Gets the other chest of doublechest
     */
    private Block getOtherChest(Block block) {
        BlockFace[] dirs = new BlockFace[] {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
        for (BlockFace dir : dirs) {
            Block b = block.getRelative(dir);
            if (b.getType() == Material.CHEST && isDoubleChest(b)) {
                return b;
            }
        }
        return null;
    }

    /*
        Gets a MagicLock sign instance from a block
     */
    private Sign getLockSign(Block block) {
        ArrayList<Sign> signs = getSigns(block);
        for (Sign b : signs) {
            if (ChatColor.stripColor(b.getLine(0)).equalsIgnoreCase("[lock]")) {
                return b; // We found a lock sign
            }
        }
        return null; // No signs are lock signs
    }

    /*
        Gets signs attached to block
     */
    private ArrayList<Sign> getSigns(Block block) {
        ArrayList<Sign> signs = new ArrayList<>();
        BlockFace[] dirs = new BlockFace[] {BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

        for (BlockFace face : dirs) {
            if (block.getRelative(face).getType() == Material.SIGN || block.getRelative(face).getType() == Material.WALL_SIGN) {
                signs.add((Sign)block.getRelative(face).getState());
            }
        }
        return signs;
    }

}
