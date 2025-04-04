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
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 9");
                return true;
            } else if (args[0].equalsIgnoreCase("spear")) {
                giveEventSpear(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 1");
                return true;
            } else if (args[0].equalsIgnoreCase("scythe")) {
                giveEventScythe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 2");
                return true;
            } else if (args[0].equalsIgnoreCase("goldmace")) {
                giveEventGoldMace(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 3");
                return true;
            } else if (args[0].equalsIgnoreCase("herosword")) {
                giveEventHeroSword(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 4");
                return true;
            } else if (args[0].equalsIgnoreCase("trickster")) {
                giveEventTricksterSword(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 5");
                return true;
            } else if (args[0].equalsIgnoreCase("icestaff")) {
                giveEventIceStaff(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 6");
                return true;
            } else if (args[0].equalsIgnoreCase("swordsilver")) {
                giveEventSilverSword(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 7");
                return true;
            } else if (args[0].equalsIgnoreCase("dwarfaxe")) {
                giveEventDwarfAxe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 8");
                return true;
            } else if (args[0].equalsIgnoreCase("leguestaff")) {
                giveEventLegueStaff(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 9");
                return true;
            } else if (args[0].equalsIgnoreCase("magicwand")) {
                giveMagicWand(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 10");
                return true;
            } else if (args[0].equalsIgnoreCase("spellhammer")) {
                giveSpellHammer(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 11");
                return true;
            } else if (args[0].equalsIgnoreCase("magicblade")) {
                giveMagicBlade(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 12");
                return true;
            } else if (args[0].equalsIgnoreCase("silversword")) {
                giveSilverSword(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 13");
                return true;
            } else if (args[0].equalsIgnoreCase("silverspear")) {
                giveSilverSpear(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 14");
                return true;
            } else if (args[0].equalsIgnoreCase("silverscythe")) {
                giveSilverScythe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 15");
                return true;
            } else if (args[0].equalsIgnoreCase("battleaxe")) {
                giveBattleAxe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 16");
                return true;
            } else if (args[0].equalsIgnoreCase("warhammer")) {
                giveWarHammer(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 17");
                return true;
            } else if (args[0].equalsIgnoreCase("pharaoh")) {
                givePharaohSword(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 18");
                return true;
            } else if (args[0].equalsIgnoreCase("magmapick")) {
                giveMagmaPickaxe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 19");
                return true;
            } else if (args[0].equalsIgnoreCase("moltenblade")) {
                giveMoltenBlade(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 20");
                return true;
            } else if (args[0].equalsIgnoreCase("arcticcrusher")) {
                giveArcticCrusher(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 21");
                return true;
            } else if (args[0].equalsIgnoreCase("dragonscythe")) {
                giveDragonScythe(sender, args);
                sender.sendMessage("§f§lCivEvents §f| §aUsing model data 22");
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
            completions.add("spear");
            completions.add("scythe");
            completions.add("goldmace");
            completions.add("herosword");
            completions.add("trickster");
            completions.add("icestaff");
            completions.add("swordsilver");
            completions.add("dwarfaxe");
            completions.add("leguestaff");
            completions.add("magicwand");
            completions.add("spellhammer");
            completions.add("magicblade");
            completions.add("silversword");
            completions.add("silverspear");
            completions.add("silverscythe");
            completions.add("battleaxe");
            completions.add("warhammer");
            completions.add("pharaoh");
            completions.add("magmapick");
            completions.add("moltenblade");
            completions.add("arcticcrusher");
            completions.add("dragonscythe");
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
    public static void giveEventSpear(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventSpear());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Spear to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems spear <player>");
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
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems scythe <player>");
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
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems goldmace <player>");
        }
    }
    private void giveEventHeroSword(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventHeroSword());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Hero's Sword to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems herosword <player>");
        }
    }
    private void giveEventTricksterSword(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventTricksterSword());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Trickster's Sword to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems trickster <player>");
        }
    }
    private void giveEventIceStaff(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventIceStaff());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Ice Staff to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems icestaff <player>");
        }
    }
    private void giveEventSilverSword(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventSilverSword());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Silver Sword to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems silversword <player>");
        }
    }
    private void giveEventDwarfAxe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventDwarfAxe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Dwarf Axe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems dwarfaxe <player>");
        }
    }
    private void giveEventLegueStaff(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createEventLegueStaff());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Legue Staff to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems leguestaff <player>");
        }
    }
    private void giveMagicWand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createMagicWand());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Magic Wand to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems magicwand <player>");
        }
    }
    private void giveSpellHammer(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createSpellHammer());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Spell Hammer to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems spellhammer <player>");
        }
    }
    private void giveMagicBlade(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createMagicBlade());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Magic Blade to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems magicblade <player>");
        }
    }
    private void giveSilverSword(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createSilerSword());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Silver Sword to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems silversword <player>");
        }
    }
    private void giveSilverSpear(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createSilerSpear());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Silver Spear to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems silverspear <player>");
        }
    }
    private void giveSilverScythe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createSilerScythe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Silver Scythe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems silverscythe <player>");
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
    private void giveWarHammer(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createWarHammer());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the War Hammer to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems warhammer <player>");
        }
    }
    private void givePharaohSword(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createPharaohSword());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Pharaoh Sword to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems pharaoh <player>");
        }
    }
    private void giveMagmaPickaxe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createMagmaPickaxe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Magma Pickaxe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems magmapick <player>");
        }
    }
    private void giveMoltenBlade(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createMoltenBlade());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Molten Blade to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems moltenblade <player>");
        }
    }
    private void giveArcticCrusher(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createArcticCrusher());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Arctic Crusher to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems arcticcrusher <player>");
        }
    }
    private void giveDragonScythe(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                targetPlayer.getInventory().addItem(createDragonScythe());
                sender.sendMessage("§f§lCivEvents §f| §aYou have given the Dragon Scythe to " + targetPlayer.getName());
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found!");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civitems dragonscythe <player>");
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
            meta.setCustomModelData(9);
            ghostStaff.setItemMeta(meta);
        }
        return ghostStaff;
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
                    "§7In Your Main Hand",
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
                    "§7When Right Clicked",
                    "§7You Have 1/4 Chance",
                    "§71: Switch Your Legendary",
                    "§72: Tp To Random Player",
                    "§73: Get Strength For 5 Min",
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
                    "§7§m------------------",
                    "§7When Target Is Hit",
                    "§7Break All His Iron",
                    "§7Armor Every 5 Min",
                    "§7§m------------------"
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
            meta.setLore(Arrays.asList(
                    "§7§m-------------",
                    "§7On Player Kill",
                    "§7Get A Random",
                    "§7Enchantment",
                    "§7§m-------------"
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
                    "§7§m----------------",
                    "§7When Right Clicked",
                    "§7Your Offhand Item",
                    "§7Will Either Double",
                    "§7Or Get Removed",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(9);
            legueStaff.setItemMeta(meta);
        }
        return legueStaff;
    }
    public static ItemStack createMagicWand() {
        ItemStack magicWand = new ItemStack(Material.STICK);
        ItemMeta meta = magicWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§lＭＡＧＩＣ ＷＡＮＤ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m----------------",
                    "§7When Right Clicked",
                    "§7Shoots A Particle",
                    "§7Line Dealing Lots",
                    "§7Of Heart Damage",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(10);
            magicWand.setItemMeta(meta);
        }
        return magicWand;
    }
    public static ItemStack createSpellHammer() {
        ItemStack spellHammer = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = spellHammer.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lＳＰＥＬＬ ＨＡＭＭＥＲ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m----------------",
                    "§7RightClick + Sneak",
                    "§7To Launch Yourself",
                    "§7And Knock Players",
                    "§7On Ground Impact",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(11);
            spellHammer.setItemMeta(meta);
        }
        return spellHammer;
    }
    public static ItemStack createMagicBlade() {
        ItemStack magicBlade = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = magicBlade.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lＭＡＧＩＣ ＢＬＡＤＥ");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Arrays.asList(
                    "§7§m----------------",
                    "§7When Right Clicked",
                    "§7You Gain Speed 3",
                    "§7For 5 Seconds",
                    "§7§m----------------"
            ));
            meta.setCustomModelData(12);
            magicBlade.setItemMeta(meta);
        }
        return magicBlade;
    }
    public static ItemStack createSilerSword() {
        ItemStack medievalSword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = medievalSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§lＳＩＬＶＥＲ ＳＷＯＲＤ");
            meta.setCustomModelData(13);
            medievalSword.setItemMeta(meta);
        }
        return medievalSword;
    }
    public static ItemStack createSilerSpear() {
        ItemStack medievalSpear = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = medievalSpear.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§5§lＳＩＬＶＥＲ ＳＰＥＡＲ");
            meta.setCustomModelData(14);
            medievalSpear.setItemMeta(meta);
        }
        return medievalSpear;
    }
    public static ItemStack createSilerScythe() {
        ItemStack medievalScythe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = medievalScythe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lＳＩＬＶＥＲ ＳＣＹＴＨＥ");
            meta.setCustomModelData(15);
            medievalScythe.setItemMeta(meta);
        }
        return medievalScythe;
    }
    public static ItemStack createBattleAxe() {
        ItemStack medievalBattleAxe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = medievalBattleAxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§lＢＡＴＴＬＥ ＡＸＥ");
            meta.setCustomModelData(16);
            medievalBattleAxe.setItemMeta(meta);
        }
        return medievalBattleAxe;
    }
    public static ItemStack createWarHammer() {
        ItemStack medievalWarHammer = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = medievalWarHammer.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lＷＡＲ ＨＡＭＭＥＲ");
            meta.setCustomModelData(17);
            medievalWarHammer.setItemMeta(meta);
        }
        return medievalWarHammer;
    }
    public static ItemStack createPharaohSword() {
        ItemStack medievalPharaohSword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = medievalPharaohSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§lＰＨＡＲＡＯＨ ＳＷＯＲＤ");
            meta.setCustomModelData(18);
            medievalPharaohSword.setItemMeta(meta);
        }
        return medievalPharaohSword;
    }
    public static ItemStack createMagmaPickaxe() {
        ItemStack medievalMagmaPickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = medievalMagmaPickaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§4§lＭＡＧＭＡ ＰＩＣＫＡＸＥ");
            meta.setCustomModelData(19);
            medievalMagmaPickaxe.setItemMeta(meta);
        }
        return medievalMagmaPickaxe;
    }
    public static ItemStack createMoltenBlade() {
        ItemStack moltenBlade = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = moltenBlade.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c§lＭＯＬＴＥＮ ＢＬＡＤＥ");
            meta.setLore(Arrays.asList(
                    "",
                    "§8§l> §cOn RightClick §7summons a wave of fire",
                    "",
                    "§8§l> §cFire Resistance §7while holding this weapon",
                    "",
                    "§8§l> §7Cooldown: 2min"
            ));
            meta.setCustomModelData(20);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
            moltenBlade.setItemMeta(meta);
        }
        return moltenBlade;
    }
    public static ItemStack createArcticCrusher() {
        ItemStack arcticCrusher = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = arcticCrusher.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§lＡＲＣＴＩＣ ＣＲＵＳＨＥＲ");
            meta.setLore(Arrays.asList(
                    "",
                    "§8§l> §bOn RightClick §7freeze target player",
                    "",
                    "§8§l> §bResistance §7while holding this weapon",
                    "",
                    "§8§l> §7Cooldown: 2min"
            ));
            meta.setCustomModelData(21);
            arcticCrusher.setItemMeta(meta);
        }
        return arcticCrusher;
    }
    public static ItemStack createDragonScythe() {
        ItemStack dragonScythe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = dragonScythe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lＤＲＡＧＯＮ ＳＣＹＴＨＥ");
            meta.setLore(Arrays.asList(
                    "",
                    "§8§l> §dOn RightClick §7long dash forward",
                    "",
                    "§8§l> §dslow falling §7while holding this weapon",
                    "",
                    "§8§l> §7Cooldown: 2min"
            ));
            meta.setCustomModelData(22);
            dragonScythe.setItemMeta(meta);
        }
        return dragonScythe;
    }
}