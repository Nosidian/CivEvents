package nos.civevents.CivItems.Medieval;

import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("all")
public class BattleAxe implements Listener{
    private final Random random = new Random();
    private final CivEvents plugin;
    public BattleAxe(CivEvents plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (weapon.getType() == Material.NETHERITE_AXE &&
                weapon.hasItemMeta() &&
                "§c§lＢＡＴＴＬＥ ＡＸＥ".equals(Objects.requireNonNull(weapon.getItemMeta()).getDisplayName())) {
            applyRandomEnchantment(weapon);
            killer.sendMessage("§f§lCivEvents §f| §aYour Battle Axe has gained another enchantment!");
        }
    }
    private void applyRandomEnchantment(ItemStack weapon) {
        List<Enchantment> availableEnchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(weapon)) {
                availableEnchantments.add(enchantment);
            }
        }
        if (availableEnchantments.isEmpty()) return;
        Enchantment selectedEnchantment = availableEnchantments.get(random.nextInt(availableEnchantments.size()));
        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return;
        int currentLevel = meta.hasEnchant(selectedEnchantment) ? meta.getEnchantLevel(selectedEnchantment) : 0;
        meta.addEnchant(selectedEnchantment, currentLevel + 1, true);
        weapon.setItemMeta(meta);
    }
}
