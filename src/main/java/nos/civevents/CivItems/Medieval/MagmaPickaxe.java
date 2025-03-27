package nos.civevents.CivItems.Medieval;

import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("all")
public class MagmaPickaxe implements Listener {
    private static final Map<Material, Material> SMELTABLE_ORES = new HashMap<>();
    private static final String PICKAXE_NAME = "§4§lＭＡＧＭＡ ＰＩＣＫＡＸＥ";
    private final CivEvents plugin;
    public MagmaPickaxe(CivEvents plugin) {
        this.plugin = plugin;
    }
    static {
        SMELTABLE_ORES.put(Material.IRON_ORE, Material.IRON_INGOT);
        SMELTABLE_ORES.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        SMELTABLE_ORES.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        SMELTABLE_ORES.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        SMELTABLE_ORES.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        SMELTABLE_ORES.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
        SMELTABLE_ORES.put(Material.NETHER_GOLD_ORE, Material.GOLD_INGOT);
        SMELTABLE_ORES.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isMagmaPickaxe(player.getInventory().getItemInMainHand()) && SMELTABLE_ORES.containsKey(block.getType())) {
            event.setDropItems(false);
            Material smeltedMaterial = SMELTABLE_ORES.get(block.getType());
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(smeltedMaterial));
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.2, 0.2, 0.2, 0.02);
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1.0f, 1.5f);
            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(Material.AIR);
                }
            }.runTaskLater(plugin, 20L);
        }
    }
    private boolean isMagmaPickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_PICKAXE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && Objects.equals(meta.getDisplayName(), PICKAXE_NAME);
    }
}