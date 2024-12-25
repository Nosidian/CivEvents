package nos.civevents.commands.utility;

import nos.civevents.CivEvents;
import nos.civevents.configuration.FlagConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class FlagCommands implements CommandExecutor, TabCompleter, Listener {
    private final CivEvents plugin;
    private final FlagConfig flagConfig;

    public FlagCommands() {
        this.plugin = CivEvents.getPlugin();
        this.flagConfig = CivEvents.getPlugin().getFlagConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags <args>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "freeze" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags freeze <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    addFrozenPlayer(target);
                    target.sendMessage("§f§lCivEvents §f| §cYou have been frozen");
                    sender.sendMessage("§f§lCivEvents §f| §a" + target.getName() + " has been frozen");
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §cPlayer not found");
                }
            }
            case "unfreeze" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags unfreeze <player>");
                    return true;
                }
                Player targetUnfreeze = Bukkit.getPlayer(args[1]);
                if (targetUnfreeze != null) {
                    removeFrozenPlayer(targetUnfreeze);
                    targetUnfreeze.sendMessage("§f§lCivEvents §f| §aYou have been unfrozen");
                    sender.sendMessage("§f§lCivEvents §f| §a" + targetUnfreeze.getName() + " has been unfrozen");
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §cPlayer not found");
                }
            }
            case "freeze-all" -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!getFrozenPlayers().contains(p.getName())) {
                        addFrozenPlayer(p);
                        p.sendMessage("§f§lCivEvents §f| §cYou have been frozen");
                    }
                }
                sender.sendMessage("§f§lCivEvents §f| §cAll players have been frozen");
            }
            case "unfreeze-all" -> {
                List<String> frozenPlayers = new ArrayList<>(getFrozenPlayers());
                for (String playerName : frozenPlayers) {
                    Player p = Bukkit.getPlayer(playerName);
                    if (p != null) {
                        p.sendMessage("§f§lCivEvents §f| §aYou have been unfrozen");
                    }
                }
                flagConfig.getConfig().set("frozen-players", null);
                flagConfig.saveConfig();
                sender.sendMessage("§f§lCivEvents §f| §cAll players have been unfrozen");
            }
            case "pvp", "deathban" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags " + args[0] + " <true|false>");
                    return true;
                }
                boolean value;
                try {
                    value = Boolean.parseBoolean(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid value. Use true or false.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("pvp")) {
                    flagConfig.getConfig().set("flags.pvp", value);
                    sender.sendMessage("§f§lCivEvents §f| §aPvP has been " + (value ? "enabled" : "disabled"));
                } else if (args[0].equalsIgnoreCase("deathban")) {
                    flagConfig.getConfig().set("flags.deathban", value);
                    sender.sendMessage("§f§lCivEvents §f| §aDeathban has been " + (value ? "enabled" : "disabled"));
                }
                flagConfig.saveConfig();
            }
            case "invulnerable" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags invulnerable <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.invulnerable", value);
                sender.sendMessage("§f§lCivEvents §f| §aInvulnerability has been " + (value ? "enabled" : "disabled"));
                flagConfig.saveConfig();
            }
            case "disable-mobs" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags disable-mobs <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.disable-mobs", value);
                sender.sendMessage("§f§lCivEvents §f| §aMobs have been " + (value ? "disabled" : "enabled"));
                flagConfig.saveConfig();
            }
            case "no-build" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags no-build <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.no-build", value);
                sender.sendMessage("§f§lCivEvents §f| §aBuilding has been " + (value ? "disabled" : "enabled"));
                flagConfig.saveConfig();
            }
            case "no-break" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags no-break <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.no-break", value);
                sender.sendMessage("§f§lCivEvents §f| §aBlock breaking has been " + (value ? "disabled" : "enabled"));
                flagConfig.saveConfig();
            }
            case "allow-fly" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags allow-fly <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.allow-fly", value);
                sender.sendMessage("§f§lCivEvents §f| §aFlying has been " + (value ? "enabled" : "disabled"));
                flagConfig.saveConfig();
            }
            case "disable-drops" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags disable-drops <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.disable-drops", value);
                sender.sendMessage("§f§lCivEvents §f| §aItem drops have been " + (value ? "disabled" : "enabled"));
                flagConfig.saveConfig();
            }
            case "no-hunger" -> {
                if (args.length < 2) {
                    sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags no-hunger <true|false>");
                    return true;
                }
                boolean value = Boolean.parseBoolean(args[1]);
                flagConfig.getConfig().set("flags.no-hunger", value);
                sender.sendMessage("§f§lCivEvents §f| §aHunger has been " + (value ? "disabled" : "enabled"));
                flagConfig.saveConfig();
            }
            default -> sender.sendMessage("§f§lCivEvents §f| §cUsage: /civflags <args>");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("civflags")) {
            if (args.length == 1) {
                completions.add("pvp");
                completions.add("freeze");
                completions.add("deathban");
                completions.add("unfreeze");
                completions.add("freeze-all");
                completions.add("unfreeze-all");
                completions.add("invulnerable");
                completions.add("disable-mobs");
                completions.add("no-build");
                completions.add("no-break");
                completions.add("allow-fly");
                completions.add("disable-drops");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("pvp") ||
                        args[0].equalsIgnoreCase("deathban") ||
                        args[0].equalsIgnoreCase("invulnerable") ||
                        args[0].equalsIgnoreCase("disable-mobs") ||
                        args[0].equalsIgnoreCase("no-build") ||
                        args[0].equalsIgnoreCase("no-break") ||
                        args[0].equalsIgnoreCase("allow-fly") ||
                        args[0].equalsIgnoreCase("disable-drops")) {
                    completions.add("true");
                    completions.add("false");
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            }
        }
        return completions;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (getFrozenPlayers().contains(player.getName())) {
            event.setCancelled(true);
        }
        if (flagConfig.getConfig().getBoolean("flags.no-hunger")) {
            player.setSaturation(20);
            player.setFoodLevel(20);
        }
    }
    public void addFrozenPlayer(Player player) {
        List<String> frozenPlayers = getFrozenPlayers();
        if (!frozenPlayers.contains(player.getName())) {
            frozenPlayers.add(player.getName());
            flagConfig.getConfig().set("frozen-players", frozenPlayers);
            flagConfig.saveConfig();
        }
    }
    public void removeFrozenPlayer(Player player) {
        List<String> frozenPlayers = getFrozenPlayers();
        if (frozenPlayers.contains(player.getName())) {
            frozenPlayers.remove(player.getName());
            flagConfig.getConfig().set("frozen-players", frozenPlayers);
            flagConfig.saveConfig();
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player) {
            boolean pvpEnabled = flagConfig.getConfig().getBoolean("flags.pvp", true);
            if (!pvpEnabled) {
                event.setCancelled(true);
                attacker.sendMessage("§f§lCivEvents §f| §cPvP is currently disabled");
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        boolean deathBanEnabled = flagConfig.getConfig().getBoolean("flags.deathban", false);
        if (deathBanEnabled) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), "§f§lCivEvents §f- §aThanks For Playing", null, null);
                player.kickPlayer("§f§lCivEvents §f- §aThanks For Playing");
            });
        }
        boolean disableDropsEnabled = flagConfig.getConfig().getBoolean("flags.disable-drops", false);
        if (disableDropsEnabled) {
            event.getDrops().clear();
        }
    }
    public List<String> getFrozenPlayers() {
        return flagConfig.getConfig().getStringList("frozen-players");
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.invulnerable")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.no-break")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.no-build")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.allow-fly")) {
            Player player = event.getPlayer();
            if (player.getAllowFlight()) {
                player.setAllowFlight(true);
            }
        }
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.disable-drops")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.no-hunger")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (flagConfig.getConfig().getBoolean("flags.disable-mobs")) {
            event.setCancelled(true);
        }
    }
}