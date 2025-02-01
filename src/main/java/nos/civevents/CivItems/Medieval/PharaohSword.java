package nos.civevents.CivItems.Medieval;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("all")
public class PharaohSword implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 120;
    private final CivEvents plugin;
    public PharaohSword(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.NETHERITE_SWORD &&
                event.getItem().hasItemMeta() &&
                "§e§lＰＨＡＲＡＯＨ ＳＷＯＲＤ".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            int randomAbility = ThreadLocalRandom.current().nextInt(4);
            switch (randomAbility) {
                case 0 -> teleportToRandomPlayer(player);
                case 1 -> giveStrength(player);
                case 2 -> createQuicksand(player);
                case 3 -> summonWitherSkeletons(player);
            }
        }
    }
    private void teleportToRandomPlayer(Player player) {
        List<Player> nonOpPlayers = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.isOp() && !target.equals(player) && target.getWorld().equals(player.getWorld())) {
                nonOpPlayers.add(target);
            }
        }
        if (!nonOpPlayers.isEmpty()) {
            Player randomPlayer = nonOpPlayers.get(ThreadLocalRandom.current().nextInt(nonOpPlayers.size()));
            player.teleport(randomPlayer.getLocation());
        } else {
            player.sendMessage("§cNo valid players to teleport to in the same world!");
        }
    }
    private void giveStrength(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 60 * 20, 0));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }
    private void createQuicksand(Player player) {
        Location center = player.getLocation();
        List<Location> affectedBlocks = new ArrayList<>();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location blockLoc = center.clone().add(x, 0, z);
                if (blockLoc.getBlock().getType().isSolid()) {
                    affectedBlocks.add(blockLoc);
                    blockLoc.getBlock().setType(Material.COBWEB);
                    blockLoc.clone().add(0, 2, 0).getBlock().setType(Material.SAND);
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : affectedBlocks) {
                    if (loc.getBlock().getType() == Material.COBWEB) {
                        loc.getBlock().setType(Material.SAND);
                    }
                }
            }
        }.runTaskLater(plugin, 500L);
        player.sendMessage("§eThe ground turns into quicksand!");
    }
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton)) return;
        WitherSkeleton skeleton = (WitherSkeleton) event.getEntity();
        if (!skeleton.hasMetadata("summoner")) return;
        UUID summonerUUID = UUID.fromString(skeleton.getMetadata("summoner").get(0).asString());
        if (event.getTarget() instanceof Player && ((Player) event.getTarget()).getUniqueId().equals(summonerUUID)) {
            event.setCancelled(true);
        }
    }
    private void summonWitherSkeletons(Player player) {
        Location location = player.getLocation();
        for (int i = 0; i < 3; i++) {
            WitherSkeleton skeleton = (WitherSkeleton) player.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON);
            skeleton.setCustomName("§e§lPharaoh's Ally");
            skeleton.setCustomNameVisible(true);
            Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40.0);
            skeleton.setHealth(40.0);
            Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(8.0);
            skeleton.setMetadata("summoner", new FixedMetadataValue(plugin, player.getUniqueId()));
        }
        player.getWorld().playSound(location, Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location, 50, 1, 1, 1, 0.1);
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§e§lＰＨＡＲＡＯＨ ＳＷＯＲＤ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§e§lPharaohSword §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§e§lPharaohSword §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}