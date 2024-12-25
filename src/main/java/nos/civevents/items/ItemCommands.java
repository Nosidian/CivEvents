package nos.civevents.items;

import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class ItemCommands implements CommandExecutor, TabCompleter {
    private final CivEvents plugin;
    public ItemCommands() {
        this.plugin = CivEvents.getPlugin();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("ghost-staff")) {
                giveGhostStaff(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 4");
                return true;
            } else if (args[0].equalsIgnoreCase("hammer")) {
                giveHammer(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 5");
                return true;
            } else if (args[0].equalsIgnoreCase("spear")) {
                giveSpear(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 6");
                return true;
            } else if (args[0].equalsIgnoreCase("dagger")) {
                giveDagger(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 7");
                return true;
            } else if (args[0].equalsIgnoreCase("scythe")) {
                giveScythe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 8");
                return true;
            } else if (args[0].equalsIgnoreCase("mace")) {
                giveMace(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 9");
                return true;
            } else if (args[0].equalsIgnoreCase("event-spear")) {
                giveEventSpear(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 1");
                return true;
            } else if (args[0].equalsIgnoreCase("event-scythe")) {
                giveEventScythe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 2");
                return true;
            } else if (args[0].equalsIgnoreCase("event-goldmace")) {
                giveEventGoldMace(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 3");
                return true;
            }
            sender.sendMessage("§f§lCivEvents §f| §cInvalid item name");
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems <item> <player>");
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("ghost-staff");
            completions.add("hammer");
            completions.add("spear");
            completions.add("dagger");
            completions.add("scythe");
            completions.add("mace");
            completions.add("event-spear");
            completions.add("event-scythe");
            completions.add("event-goldmace");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
    private void giveGhostStaff(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createGhostStaff());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Ghost Staff to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems ghost-staff <player>");
        }
    }
    private void giveHammer(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createHammer());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Hammer to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems hammer <player>");
        }
    }
    private void giveSpear(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createSpear());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Spear to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems spear <player>");
        }
    }
    private void giveDagger(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createDagger());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Dagger to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems dagger <player>");
        }
    }
    private void giveScythe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createScythe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Scythe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems scythe <player>");
        }
    }
    private void giveMace(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createMace());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Mace to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems mace <player>");
        }
    }
    private void giveEventSpear(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventSpear());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Spear to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems event-spear <player>");
        }
    }
    private void giveEventScythe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventScythe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Scythe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems event-scythe <player>");
        }
    }
    private void giveEventGoldMace(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventGoldMace());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the GoldMace to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems event-goldmace <player>");
        }
    }
    public static ItemStack createGhostStaff() {
        ItemStack ghostStaff = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = ghostStaff.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lＧＨＯＳＴ ＳＴＡＦＦ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§d=-=-=-=-=-=-=-=-=",
                    "§dWhen Target Is Hit",
                    "§dStarts Ghost Combo",
                    "§d=-=-=-=-=-=-=-=-="
            ));
            meta.setCustomModelData(4);
            ghostStaff.setItemMeta(meta);
        }
        return ghostStaff;
    }
    public static ItemStack createHammer() {
        ItemStack smashHammer = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = smashHammer.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lHammer");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m----------------",
                    "§7When Right Clicked",
                    "§7Launch And Smash",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(5);
            smashHammer.setItemMeta(meta);
        }
        return smashHammer;
    }
    private ItemStack createSpear() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lSpear");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m-----------------");
            lore.add("§7RightClick On Player");
            lore.add("§7To Give Bleeding");
            lore.add("§7§m-----------------");
            meta.setLore(lore);
            meta.setCustomModelData(6);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack createDagger() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lDagger");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m------------");
            lore.add("§7RightClick To");
            lore.add("§7Summon Wolfs");
            lore.add("§7§m------------");
            meta.setLore(lore);
            meta.setCustomModelData(7);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack createScythe() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lScythe");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m------------");
            lore.add("§7RightClick To");
            lore.add("§7Dash Forward");
            lore.add("§7§m------------");
            meta.setLore(lore);
            meta.setCustomModelData(8);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack createMace() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lMace");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m--------------");
            lore.add("§7RightClick To");
            lore.add("§7Launch Players");
            lore.add("§7§m--------------");
            meta.setLore(lore);
            meta.setCustomModelData(9);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack createEventSpear() {
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
}