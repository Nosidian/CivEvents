package nos.civevents.CivAdmins;

import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

@SuppressWarnings("all")
public class AdminCommands implements CommandExecutor, TabCompleter, Listener {
    private final HashMap<UUID, Entity> grabbedEntities = new HashMap<>();
    private final Set<UUID> grabToggle = new HashSet<>();
    private final CivEvents plugin;
    private final AdminBomb adminBomb;
    public AdminCommands(CivEvents plugin, AdminBomb adminBomb) {
        this.plugin = plugin;
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
                            if (explosionSize > 100) {
                                player.sendMessage("§f§lCivEvents §f| §cExplosion size must be 100 or lower");
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
                }
                if (args[0].equalsIgnoreCase("playerdata")) {
                    if (args[1].equalsIgnoreCase("clearall")) {
                        AdminPlayerData.clearAllPlayerData(sender);
                        return true;
                    } else {
                        String username = args[1];
                        AdminPlayerData.clearPlayerData(sender, username);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("crash")) {
                    if (args.length == 2) {
                        String playerName = args[1];
                        Player target = Bukkit.getPlayer(playerName);
                        if (target != null) {
                            String commandToRun = "particle minecraft:poof ~ ~ ~ ~ ~ ~ 99999999999999999999 999999999 force " + target.getName();
                            player.performCommand(commandToRun);
                            player.sendMessage("§f§lCivEvents §f| §aCrashed " + target.getName());
                        } else {
                            player.sendMessage("§f§lCivEvents §f| §cPlayer not found");
                        }
                        return true;
                    } else {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /ca crash <playername>");
                        return false;
                    }
                }
                if (args[0].equalsIgnoreCase("grab")) {
                    UUID playerUUID = player.getUniqueId();
                    if (grabToggle.contains(playerUUID)) {
                        grabToggle.remove(playerUUID);
                        player.sendMessage("§f§lCivEvents §f| §cGrab and launch has been disabled");
                    } else {
                        grabToggle.add(playerUUID);
                        player.sendMessage("§f§lCivEvents §f| §aGrab and launch has been enabled");
                    }
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
            suggestions.add("playerdata");
            suggestions.add("crash");
            suggestions.add("grab");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("playerdata")) {
            suggestions.add("clearall");
            for (String playerName : getAllPlayerNames()) {
                suggestions.add(playerName);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("airstrike")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("crash")) {
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
    private List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        File playerDataFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    String uuid = file.getName().replace(".dat", "");
                    try {
                        UUID playerUUID = UUID.fromString(uuid);
                        String name = Bukkit.getOfflinePlayer(playerUUID).getName();
                        if (name != null) {
                            playerNames.add(name);
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }
        return playerNames;
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!grabToggle.contains(player.getUniqueId())) return;
        if (player.isSneaking()) {
            Entity target = event.getRightClicked();
            if (!(target instanceof LivingEntity)) return;
            if (!grabbedEntities.containsKey(player.getUniqueId())) {
                grabbedEntities.put(player.getUniqueId(), target);
                player.sendMessage("§f§lCivEvents §f| §aYou grabbed " + target.getName());
                startDragging(player, target);
            }
        }
    }
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!grabToggle.contains(player.getUniqueId())) return;
        if (!player.isSneaking() && grabbedEntities.containsKey(player.getUniqueId())) {
            Entity target = grabbedEntities.remove(player.getUniqueId());
            if (target != null) throwEntity(player, target);
        }
    }
    private void startDragging(Player player, Entity target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !target.isValid() || !grabbedEntities.containsKey(player.getUniqueId())) {
                    grabbedEntities.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                Location playerLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
                target.teleport(playerLocation);
                player.getWorld().spawnParticle(Particle.REDSTONE, target.getLocation().add(0, 1, 0), 1, new Particle.DustOptions(Color.RED, 1.0F));
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    private void throwEntity(Player player, Entity target) {
        Vector direction = player.getLocation().getDirection().normalize().multiply(2).add(new Vector(0, 1, 0));
        target.setVelocity(direction);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 1.0F);
        player.sendMessage("§f§lCivEvents §f| §aYou threw " + target.getName());
    }
}