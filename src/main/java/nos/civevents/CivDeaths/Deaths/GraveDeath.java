package nos.civevents.CivDeaths.Deaths;

import nos.civevents.CivDeaths.DeathConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class GraveDeath implements Listener {
    private final Plugin plugin;
    private final DeathConfig deathConfig;
    public GraveDeath(Plugin plugin, DeathConfig deathConfig) {
        this.plugin = plugin;
        this.deathConfig = deathConfig;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        if (deathConfig.getConfig().getBoolean("grave.enabled")) {
            spawnGrave(deathLocation, player);
            event.getDrops().clear();
            Objects.requireNonNull(deathLocation.getWorld()).playSound(deathLocation, Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
        }
    }
    public void spawnGrave(Location location, Player player) {
        Material baseMaterial = Material.COBBLESTONE;
        Material slabMaterial = Material.COBBLESTONE_SLAB;
        Material wallMaterial = Material.COBBLESTONE_WALL;
        Material baseMaterial2 = Material.MOSSY_COBBLESTONE;
        Material slabMaterial2 = Material.MOSSY_COBBLESTONE_SLAB;
        Material wallMaterial2 = Material.MOSSY_COBBLESTONE_WALL;
        Material chestMaterial = Material.CHEST;
        Location graveLocation = location.clone().add(0, -1, 0);
        Block chestBlock1 = graveLocation.getBlock();
        Block chestBlock2 = graveLocation.clone().add(1, 0, 0).getBlock();
        chestBlock1.setType(chestMaterial);
        chestBlock2.setType(chestMaterial);
        Chest chest1 = (Chest) chestBlock1.getState();
        Chest chest2 = (Chest) chestBlock2.getState();
        Inventory playerInventory = player.getInventory();
        Inventory chest1Inventory = chest1.getBlockInventory();
        Inventory chest2Inventory = chest2.getBlockInventory();
        for (ItemStack item : playerInventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                if (chest1Inventory.firstEmpty() != -1) {
                    chest1Inventory.addItem(item);
                } else if (chest2Inventory.firstEmpty() != -1) {
                    chest2Inventory.addItem(item);
                }
            }
        }
        playerInventory.clear();
        Block slab1 = graveLocation.clone().add(0, 1, 0).getBlock();
        Block slab2 = graveLocation.clone().add(1, 1, 0).getBlock();
        slab1.setType(slabMaterial);
        slab2.setType(slabMaterial2);
        Block sideBlock1 = graveLocation.clone().add(2, 0, 0).getBlock();
        Block sideBlock2 = graveLocation.clone().add(2, 1, 0).getBlock();
        sideBlock1.setType(baseMaterial);
        sideBlock2.setType(baseMaterial2);
        Block wallBlock = graveLocation.clone().add(2, 2, 0).getBlock();
        wallBlock.setType(wallMaterial2);
        Block topWall1 = graveLocation.clone().add(2, 3, 0).getBlock();
        Block topWall2 = graveLocation.clone().add(2, 4, 0).getBlock();
        Block sideWall1 = graveLocation.clone().add(2, 3, -1).getBlock();
        Block sideWall2 = graveLocation.clone().add(2, 3, 1).getBlock();
        topWall1.setType(wallMaterial);
        topWall2.setType(wallMaterial);
        sideWall1.setType(wallMaterial2);
        sideWall2.setType(wallMaterial);
    }
}
