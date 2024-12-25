package nos.civevents.commands.admin;

import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class AdminCommands implements CommandExecutor, TabCompleter, Listener {
    private final Map<Player, Integer> playerPageMap = new HashMap<>();
    private static final int GREEN_GLASS_PANE_SLOT = 53;
    private static final int BACK_GLASS_PANE_SLOT = 45;
    private static final int GUI_SIZE = 54;

    private CivEvents plugin;
    private final AdminBomb adminBomb;

    public AdminCommands(AdminBomb adminBomb) {
        this.plugin = CivEvents.getPlugin();
        this.adminBomb = adminBomb;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("airstrike")) {
                    if (args.length == 3) {
                        String targetName = args[1];
                        int explosionSize;
                        try {
                            explosionSize = Integer.parseInt(args[2]);
                            if (explosionSize <= 0) {
                                player.sendMessage("§f§lCivEvents §f| §cExplosion size must be greater than 0");
                                return false;
                            }
                            if (explosionSize > 200) {
                                player.sendMessage("§f§lCivEvents §f| §cExplosion size must be 200 or lower");
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage("§f§lCivEvents §f| §cInvalid explosion size");
                            return false;
                        }
                        Player target = Bukkit.getPlayer(targetName);
                        if (target != null) {
                            Location targetLocation = target.getLocation().add(0, 10, 0);
                            adminBomb.spawnMissile(targetLocation, explosionSize);
                            player.sendMessage("§f§lCivEvents §f| §aAirstrike launched at " + target.getName());
                        } else {
                            player.sendMessage("§f§lCivEvents §f| §cPlayer not found");
                        }
                        return true;
                    } else {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civadmin airstrike <player> <explosion-size>");
                        return false;
                    }
                } else if (args[0].equalsIgnoreCase("airstrike-gui")) {
                    openAirstrikeGUI(player, 1);
                    player.sendMessage("§f§lCivEvents §f| §cNot finished");
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("airstrike");
            //suggestions.add("airstrike-gui");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("airstrike")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("airstrike")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add("<explostion-size>");
            }
        }
        return suggestions;
    }
    private void executeAirstrike(Player target, int explosionSize) {
        World world = target.getWorld();
        double targetX = target.getLocation().getX();
        double targetY = target.getLocation().getY();
        double targetZ = target.getLocation().getZ();
        final int particleHeight = 100;
        final int updateInterval = 1;
        final int duration = 100;
        final int totalSteps = duration / updateInterval;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int step = 0; step < totalSteps; step++) {
                int currentHeight = particleHeight - (step * particleHeight / totalSteps);
                int finalStep = step;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (int i = 0; i < 10; i++) {
                        double offsetX = Math.random() * 0.2 - 0.1;
                        double offsetY = Math.random() * 0.2 - 0.1;
                        double offsetZ = Math.random() * 0.2 - 0.1;
                        world.spawnParticle(org.bukkit.Particle.REDSTONE, targetX, targetY + currentHeight, targetZ, 1, offsetX, offsetY, offsetZ, 1);
                    }
                    if (finalStep == totalSteps - 1) {
                        createExplosion(target, explosionSize);
                    }
                }, step * updateInterval);
            }
        });
    }
    private void createExplosion(Player target, int explosionSize) {
        World world = target.getWorld();
        double targetX = target.getLocation().getX();
        double targetY = target.getLocation().getY();
        double targetZ = target.getLocation().getZ();
        int radius = explosionSize;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.sqrt(x * x + y * y + z * z) <= radius) {
                        world.getBlockAt((int) targetX + x, (int) targetY - 1, (int) targetZ + z).setType(Material.FIRE);
                    }
                }
            }
        }
        world.createExplosion(targetX, targetY - 1, targetZ, explosionSize, false, true);
    }
    private void openAirstrikeGUI(Player player, int page) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, "§c§lAirstrike §c- §fPage " + page);
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, blackGlass);
            gui.setItem(GUI_SIZE - 9 + i, blackGlass);
        }
        ItemStack greenGlass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        if (page > 1) {
            gui.setItem(BACK_GLASS_PANE_SLOT, greenGlass);
        }
        if (page * 45 < getPlayerSkulls().size()) {
            gui.setItem(GREEN_GLASS_PANE_SLOT, greenGlass);
        }
        List<ItemStack> skulls = getPlayerSkulls();
        int start = (page - 1) * 45;
        int end = Math.min(start + 45, skulls.size());
        for (int i = start; i < end; i++) {
            gui.setItem(i - start + 9, skulls.get(i));
        }
        player.openInventory(gui);
        playerPageMap.put(player, page);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("§c§lAirstrike")) {
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE) {
                event.setCancelled(true);
                int page = playerPageMap.getOrDefault(player, 1);
                if (event.getSlot() == GREEN_GLASS_PANE_SLOT) {
                    openAirstrikeGUI(player, page + 1);
                } else if (event.getSlot() == BACK_GLASS_PANE_SLOT && page > 1) {
                    openAirstrikeGUI(player, page - 1);
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        playerPageMap.remove(event.getPlayer());
    }
    private List<ItemStack> getPlayerSkulls() {
        List<ItemStack> skulls = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            if (skullMeta != null) {
                skullMeta.setOwningPlayer(onlinePlayer);
                skull.setItemMeta(skullMeta);
                skulls.add(skull);
            }
        }
        return skulls;
    }
}