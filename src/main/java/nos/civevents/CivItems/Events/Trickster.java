package nos.civevents.CivItems.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("all")
public class Trickster implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 300;
    private final CivEvents plugin;
    public Trickster(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.DIAMOND_SWORD &&
                event.getItem().hasItemMeta() &&
                "§c§lＴＲＩＣＫＳＴＥＲ᾽Ｓ ＳＷＯＲＤ".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
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
                case 2 -> switchWeapon(player);
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
    private void switchWeapon(Player player) {
        clearTricksterSword(player);
        List<Runnable> actions = Arrays.asList(
                () -> giveEventSpear(player),
                () -> giveEventScythe(player),
                () -> giveEventGoldMace(player),
                () -> giveEventHeroSword(player),
                () -> giveEventTricksterSword(player),
                () -> giveEventIceStaff(player),
                () -> giveEventSilverSword(player),
                () -> giveEventDwarfAxe(player),
                () -> giveEventLegueStaff(player)
        );
        int randomIndex = ThreadLocalRandom.current().nextInt(actions.size());
        actions.get(randomIndex).run();
        player.sendMessage("§aA random legendary weapon has been chosen for you!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 1f);
    }
    private void clearTricksterSword(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta() != null) {
                if ("§c§lＴＲＩＣＫＳＴＥＲ ＳＷＯＲＤ".equals(item.getItemMeta().getDisplayName())) {
                    player.getInventory().remove(item);
                    break;
                }
            }
        }
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
            skeleton.setCustomName("§c§lTrickster's Ally");
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
                        "§c§lＴＲＩＣＫＳＴＥＲ᾽Ｓ ＳＷＯＲＤ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§c§lTrickster §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§c§lTrickster §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    public void giveEventSpear(Player player) {
        player.getInventory().addItem(createEventSpear());
    }
    private void giveEventScythe(Player player) {
        player.getInventory().addItem(createEventScythe());
    }
    private void giveEventGoldMace(Player player) {
        player.getInventory().addItem(createEventGoldMace());
    }
    private void giveEventHeroSword(Player player) {
        player.getInventory().addItem(createEventHeroSword());
    }
    private void giveEventTricksterSword(Player player) {
        player.getInventory().addItem(createEventTricksterSword());
    }
    private void giveEventIceStaff(Player player) {
        player.getInventory().addItem(createEventIceStaff());
    }
    private void giveEventSilverSword(Player player) {
        player.getInventory().addItem(createEventSilverSword());
    }
    private void giveEventDwarfAxe(Player player) {
        player.getInventory().addItem(createEventDwarfAxe());
    }
    private void giveEventLegueStaff(Player player) {
        player.getInventory().addItem(createEventLegueStaff());
    }
    private static ItemStack createEventSpear() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§lＳＰＥＡＲ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m-----------------");
            lore.add("§7RightClick On Player");
            lore.add("§7To Give Bleeding");
            lore.add("§7§m-----------------");
            meta.setLore(lore);
            meta.setCustomModelData(1);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack createEventScythe() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lＳＣＹＴＨＥ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m------------");
            lore.add("§7RightClick To");
            lore.add("§7Dash Forward");
            lore.add("§7§m------------");
            meta.setLore(lore);
            meta.setCustomModelData(2);
            item.setItemMeta(meta);
        }
        return item;
    }
    public static ItemStack createEventGoldMace() {
        ItemStack smashHammer = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = smashHammer.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lＧＯＬＤＭＡＣＥ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m----------------",
                    "§7When Right Clicked",
                    "§7Launch And Smash",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(3);
            smashHammer.setItemMeta(meta);
        }
        return smashHammer;
    }
    public static ItemStack createEventHeroSword() {
        ItemStack heroSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = heroSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lＨＥＲＯ᾽Ｓ ＳＷＯＲＤ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m-----------------",
                    "§7When You Die While",
                    "§7Holding This Sword",
                    "§7You Will Be Revived",
                    "§7But The Sword Will",
                    "§7Be Destroyed",
                    "§7§m-----------------"
            ));
            meta.setCustomModelData(4);
            heroSword.setItemMeta(meta);
        }
        return heroSword;
    }
    public static ItemStack createEventTricksterSword() {
        ItemStack tricksterSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = tricksterSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§lＴＲＩＣＫＳＴＥＲ᾽Ｓ ＳＷＯＲＤ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m-------------------------",
                    "§7You Have 1/4 Chance",
                    "§71: Switch Your Legendary",
                    "§72: Tp To Random Player",
                    "§73: Get Strength 5 Min",
                    "§74: Get Wither Allies",
                    "§7§m-------------------------"
            ));
            meta.setCustomModelData(5);
            tricksterSword.setItemMeta(meta);
        }
        return tricksterSword;
    }
    public static ItemStack createEventIceStaff() {
        ItemStack iceStaff = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = iceStaff.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§lＩＣＥ ＳＴＡＦＦ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m-------------------",
                    "§7When Right Clicked",
                    "§7Shoot Snowball That",
                    "§7Gives Target Slowness",
                    "§7§m-------------------"
            ));
            meta.setCustomModelData(6);
            iceStaff.setItemMeta(meta);
        }
        return iceStaff;
    }
    public static ItemStack createEventSilverSword() {
        ItemStack silverSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = silverSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lＳＩＬＶＥＲ ＳＷＯＲＤ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m-------------------",
                    "§7When Target Is Hit",
                    "§7Break All His Iron",
                    "§7Armor Every 5 Min",
                    "§7§m-------------------"
            ));
            meta.setCustomModelData(7);
            silverSword.setItemMeta(meta);
        }
        return silverSword;
    }
    public static ItemStack createEventDwarfAxe() {
        ItemStack dwarfAxe = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = dwarfAxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§8§lＤＷＡＲＦ ＡＸＥ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m--------------",
                    "§7On Player Kill",
                    "§7Get A Random",
                    "§7Enchantment",
                    "§7§m--------------"
            ));
            meta.setCustomModelData(8);
            dwarfAxe.setItemMeta(meta);
        }
        return dwarfAxe;
    }
    public static ItemStack createEventLegueStaff() {
        ItemStack legueStaff = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = legueStaff.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§2§lＬＥＧＵＥ ＳＴＡＦＦ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m-----------------",
                    "§7When Right Clicked",
                    "§7Your Offhand Item",
                    "§7Will Either Double",
                    "§7Or Get Removed",
                    "§7§m-----------------"
            ));
            meta.setCustomModelData(9);
            legueStaff.setItemMeta(meta);
        }
        return legueStaff;
    }
}