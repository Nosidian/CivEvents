package nos.civevents.CivItems;

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
    public ItemCommands(CivEvents plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("ghost-staff")) {
                giveGhostStaff(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 1");
                return true;
            } else if (args[0].equalsIgnoreCase("hammer")) {
                giveHammer(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 2");
                return true;
            } else if (args[0].equalsIgnoreCase("spear")) {
                giveSpear(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 3");
                return true;
            } else if (args[0].equalsIgnoreCase("dagger")) {
                giveDagger(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 4");
                return true;
            } else if (args[0].equalsIgnoreCase("scythe")) {
                giveScythe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 5");
                return true;
            } else if (args[0].equalsIgnoreCase("mace")) {
                giveMace(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 6");
                return true;
            } else if (args[0].equalsIgnoreCase("vikingspear")) {
                giveVikingSpear(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 7");
                return true;
            } else if (args[0].equalsIgnoreCase("battleaxe")) {
                giveBattleAxe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 8");
                return true;
            } else if (args[0].equalsIgnoreCase("katana")) {
                giveKatana(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 9");
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
            completions.add("vikingspear");
            completions.add("battleaxe");
            completions.add("katana");
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
    private void giveVikingSpear(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createVikingSpear());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the viking spear to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems vikingspear <player>");
        }
    }
    private void giveBattleAxe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createBattleAxe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Battle Axe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems battleaxe <player>");
        }
    }
    private void giveKatana(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createKatana());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Battle Axe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems battleaxe <player>");
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
            meta.setCustomModelData(1);
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
            meta.setCustomModelData(2);
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
            meta.setCustomModelData(3);
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
            meta.setCustomModelData(4);
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
            meta.setCustomModelData(5);
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
            meta.setCustomModelData(6);
            item.setItemMeta(meta);
        }
        return item;
    }
    private ItemStack createVikingSpear() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§lＶＩＫＩＮＧ ＳＰＥＡＲ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m-----------------");
            lore.add("§7RightClick On Player");
            lore.add("§7To Give Bleeding");
            lore.add("§7§m-----------------");
            meta.setLore(lore);
            meta.setCustomModelData(7);
            item.setItemMeta(meta);
        }
        return item;
    }
    public static ItemStack createBattleAxe() {
        ItemStack smashHammer = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = smashHammer.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§lＢＡＴＴＬＥ ＡＸＥ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m----------------",
                    "§7When Right Clicked",
                    "§7Launch And Smash",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(8);
            smashHammer.setItemMeta(meta);
        }
        return smashHammer;
    }
    private ItemStack createKatana() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lＫＡＴＡＮＡ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add("§7§m------------");
            lore.add("§7RightClick To");
            lore.add("§7Dash Forward");
            lore.add("§7§m------------");
            meta.setLore(lore);
            meta.setCustomModelData(9);
            item.setItemMeta(meta);
        }
        return item;
    }
}