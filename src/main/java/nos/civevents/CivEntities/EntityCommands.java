package nos.civevents.CivEntities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("all")
public class EntityCommands implements CommandExecutor, TabCompleter, Listener {
    private final EntityConfig entityConfig;
    private final Plugin plugin;
    public EntityCommands(Plugin plugin, EntityConfig entityConfig) {
        this.entityConfig = entityConfig;
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length < 1) {
            return false;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /civentities create <customname> <entityType>");
                return true;
            }
            String customName = args[1];
            String entityType = args[2];
            try {
                EntityType.valueOf(entityType.toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§f§lCivEvents §f| §cInvalid entity type: " + entityType);
                return true;
            }
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection == null) {
                entitiesSection = entityConfig.getConfig().createSection("entities");
            }
            ConfigurationSection customEntitySection = entitiesSection.createSection(customName);
            customEntitySection.set("entityType", entityType.toUpperCase());
            customEntitySection.set("customName", customName);
            ConfigurationSection bodyPartsSection = customEntitySection.createSection("bodyparts");
            List.of("head", "chest", "legs", "feet", "mainhand", "offhand").forEach(bodyPart -> {
                ConfigurationSection bodyPartSection = bodyPartsSection.createSection(bodyPart);
                bodyPartSection.set("type", "AIR");
                if (bodyPart.equals("head")) {
                    bodyPartSection.set("skull", "NONE");
                }
                bodyPartSection.set("trimType", "NONE");
                bodyPartSection.set("trimMaterial", "NONE");
                if (bodyPart.equals("mainhand") || bodyPart.equals("offhand")) {
                    bodyPartSection.set("metadata", "NONE");
                }
            });
            entityConfig.saveConfig();
            sender.sendMessage("§f§lCivEvents §f| §aCustom entity created with name " + customName);
            return true;
        }
        if (args[0].equalsIgnoreCase("wand")) {
            if (args.length != 3) {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /civentities wand <customname> <playername>");
                return true;
            }
            String customName = args[1];
            String playerName = args[2];
            String entityType = entityConfig.getConfig().getString("entities." + customName + ".entityType");
            if (entityType == null) {
                sender.sendMessage("§f§lCivEvents §f| §cNo entity found with name " + customName);
                return true;
            }
            Player targetPlayer = plugin.getServer().getPlayerExact(playerName);
            if (targetPlayer == null) {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found: " + playerName);
                return true;
            }
            ItemStack item = new ItemStack(Material.BLAZE_ROD);
            ItemMeta meta = item.getItemMeta();
            Objects.requireNonNull(meta).setDisplayName(customName);
            item.setItemMeta(meta);
            targetPlayer.getInventory().addItem(item);
            targetPlayer.sendMessage("§f§lCivEvents §f| §aYou have received " + customName + " wand");
            sender.sendMessage("§f§lCivEvents §f| §aGave a wand named " + customName + " to " + playerName);
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /civentities delete <customname>");
                return true;
            }
            String customName = args[1];
            if (entityConfig.getConfig().contains("entities." + customName)) {
                entityConfig.getConfig().set("entities." + customName, null);
                entityConfig.saveConfig();
                sender.sendMessage("§f§lCivEvents §f| §aDeleted custom entity with name " + customName);
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cNo entity found with name " + customName);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length < 4) {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /civentities edit <customname> <bodypart> <itemType> [trimType] [trimMaterial]");
                return true;
            }
            String customName = args[1];
            String bodyPart = args[2].toLowerCase();
            String itemTypeStr = args[3].toUpperCase();
            String trimType = args.length > 4 ? args[4].toUpperCase() : null;
            String trimMaterial = args.length > 5 ? args[5].toUpperCase() : null;
            if (!isValidBodyPart(bodyPart) || !isValidItemType(itemTypeStr)) {
                sender.sendMessage("§f§lCivEvents §f| §cInvalid body part or item type");
                return true;
            }
            ItemStack itemStack = new ItemStack(Material.valueOf(itemTypeStr));
            if (itemTypeStr.equals("PLAYER_HEAD") && bodyPart.equals("head")) {
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                if (skullMeta != null) {
                    String playerName = args.length > 4 ? args[4] : "";
                    if (!playerName.isEmpty()) {
                        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
                        itemStack.setItemMeta(skullMeta);
                        sender.sendMessage("§f§lCivEvents §f| §aSetting skull owner to " + playerName);
                    } else {
                        skullMeta.setOwningPlayer(null);
                        itemStack.setItemMeta(skullMeta);
                        sender.sendMessage("§f§lCivEvents §f| §cNo skull owner found or set to NONE");
                    }
                }
            } else {
                if (itemStack.getItemMeta() instanceof ArmorMeta armorMeta) {
                    if (trimType != null && !trimType.equalsIgnoreCase("NONE") && trimMaterial != null && !trimMaterial.equalsIgnoreCase("NONE")) {
                        TrimMaterial trimMaterialEnum = getTrimMaterial(trimMaterial);
                        TrimPattern trimPatternEnum = getTrimPattern(trimType);
                        if (trimMaterialEnum != null && trimPatternEnum != null) {
                            armorMeta.setTrim(new ArmorTrim(trimMaterialEnum, trimPatternEnum));
                        }
                    }
                    itemStack.setItemMeta(armorMeta);
                }
            }
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection == null) {
                sender.sendMessage("§f§lCivEvents §f| §cNo entities found to edit");
                return true;
            }
            ConfigurationSection customEntitySection = entitiesSection.getConfigurationSection(customName);
            if (customEntitySection == null) {
                sender.sendMessage("§f§lCivEvents §f| §cNo custom entity found with name " + customName);
                return true;
            }
            ConfigurationSection bodyPartsSection = customEntitySection.getConfigurationSection("bodyparts");
            if (bodyPartsSection != null) {
                ConfigurationSection bodyPartSection = bodyPartsSection.getConfigurationSection(bodyPart);
                if (bodyPartSection == null) {
                    bodyPartSection = bodyPartsSection.createSection(bodyPart);
                }
                bodyPartSection.set("type", itemStack.getType().name());
                if (itemTypeStr.equals("PLAYER_HEAD") && bodyPart.equals("head")) {
                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    if (skullMeta != null) {
                        OfflinePlayer owner = skullMeta.getOwningPlayer();
                        if (owner != null && !Objects.requireNonNull(owner.getName()).equalsIgnoreCase("NONE")) {
                            bodyPartSection.set("skull", owner.getName());
                        } else {
                            bodyPartSection.set("skull", "NONE");
                        }
                    } else {
                        bodyPartSection.set("skull", "NONE");
                    }
                } else {
                    if (itemStack.getItemMeta() instanceof ArmorMeta armorMeta) {
                        if (trimType != null && !trimType.equalsIgnoreCase("NONE")) {
                            bodyPartSection.set("trimType", trimType);
                        } else {
                            bodyPartSection.set("trimType", "NONE");
                        }
                        if (trimMaterial != null && !trimMaterial.equalsIgnoreCase("NONE")) {
                            bodyPartSection.set("trimMaterial", trimMaterial);
                        } else {
                            bodyPartSection.set("trimMaterial", "NONE");
                        }
                    }
                }
                if (bodyPart.equals("mainhand") || bodyPart.equals("offhand")) {
                    String metadataStr = args.length > 4 ? args[4] : "";
                    if (metadataStr != null) {
                        try {
                            int metadata = Integer.parseInt(metadataStr);
                            bodyPartSection.set("metadata", metadata);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§f§lCivEvents §f| §cInvalid metadata number");
                            return true;
                        }
                    } else {
                        bodyPartSection.set("metadata", "NONE");
                    }
                }
                entityConfig.saveConfig();
                sender.sendMessage("§f§lCivEvents §f| §aUpdated the " + bodyPart);
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cNo body parts section found");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("spawner")) {
            if (args.length != 2) {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /civentities spawner <customname>");
                return true;
            }
            String customName = args[1];
            String entityTypeString = entityConfig.getConfig().getString("entities." + customName + ".entityType");
            if (entityTypeString == null) {
                sender.sendMessage("§f§lCivEvents §f| §cNo entity found with name " + customName);
                return true;
            }
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(entityTypeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§f§lCivEvents §f| §cInvalid entity type: " + entityTypeString);
                return true;
            }
            Location location = player.getLocation();
            int x = (int) Math.round(location.getX());
            int y = (int) Math.round(location.getY());
            int z = (int) Math.round(location.getZ());
            String command2 = "setblock " + x + " " + y + " " + z + " spawner{SpawnData:{entity:{id:"
                    + entityTypeString.toLowerCase()
                    + ",PersistenceRequired:1,CustomName:\""
                    + customName
                    + "2\"}},Delay:200} replace";
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command2);
            sender.sendMessage("§f§lCivEvents §f| §aPlaced a custom spawner for " + customName);
            return true;
        }
        return false;
    }
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            String customName = livingEntity.getCustomName();
            if (customName != null && customName.endsWith("2")) {
                String originalName = customName.substring(0, customName.length() - 1).trim();
                String entityTypeStr = entityConfig.getConfig().getString("entities." + originalName + ".entityType");
                if (entityTypeStr != null) {
                    try {
                        EntityType entityType = EntityType.valueOf(entityTypeStr.toUpperCase());
                        World world = livingEntity.getWorld();
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Location location = livingEntity.getLocation();
                            livingEntity.remove();
                            Entity newEntity = world.spawnEntity(location, entityType);
                            if (newEntity instanceof LivingEntity) {
                                LivingEntity newLivingEntity = (LivingEntity) newEntity;
                                newLivingEntity.setCustomName(originalName);
                                newLivingEntity.setCustomNameVisible(true);
                                applyItemsToEntity(newLivingEntity, originalName);
                            }
                        }, 1L);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid entity type in config: " + entityTypeStr);
                    }
                } else {
                    plugin.getLogger().warning("No entity type found for custom name: " + originalName);
                }
            }
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("create");
            completions.add("wand");
            completions.add("delete");
            completions.add("edit");
            completions.add("spawner");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("wand")) {
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection != null) {
                completions.addAll(entitiesSection.getKeys(false));
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            completions.add("{New-Name}");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection != null) {
                completions.addAll(entitiesSection.getKeys(false));
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection != null) {
                completions.addAll(entitiesSection.getKeys(false));
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spawner")) {
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection != null) {
                completions.addAll(entitiesSection.getKeys(false));
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("wand")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            for (EntityType entityType : EntityType.values()) {
                completions.add(entityType.name());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
            completions.add("head");
            completions.add("chest");
            completions.add("legs");
            completions.add("feet");
            completions.add("mainhand");
            completions.add("offhand");
        } else if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
            String bodyPart = args[2].toLowerCase();
            switch (bodyPart) {
                case "head" -> {
                    completions.add("PLAYER_HEAD");
                    completions.add("LEATHER_HELMET");
                    completions.add("GOLDEN_HELMET");
                    completions.add("CHAINMAIL_HELMET");
                    completions.add("IRON_HELMET");
                    completions.add("DIAMOND_HELMET");
                    completions.add("NETHERITE_HELMET");
                }
                case "chest" -> {
                    completions.add("LEATHER_CHESTPLATE");
                    completions.add("GOLDEN_CHESTPLATE");
                    completions.add("CHAINMAIL_CHESTPLATE");
                    completions.add("IRON_CHESTPLATE");
                    completions.add("DIAMOND_CHESTPLATE");
                    completions.add("NETHERITE_CHESTPLATE");
                }
                case "legs" -> {
                    completions.add("LEATHER_LEGGINGS");
                    completions.add("GOLDEN_LEGGINGS");
                    completions.add("CHAINMAIL_LEGGINGS");
                    completions.add("IRON_LEGGINGS");
                    completions.add("DIAMOND_LEGGINGS");
                    completions.add("NETHERITE_LEGGINGS");
                }
                case "feet" -> {
                    completions.add("LEATHER_BOOTS");
                    completions.add("GOLDEN_BOOTS");
                    completions.add("CHAINMAIL_BOOTS");
                    completions.add("IRON_BOOTS");
                    completions.add("DIAMOND_BOOTS");
                    completions.add("NETHERITE_BOOTS");
                }
                case "mainhand", "offhand" -> {
                    for (Material material : Material.values()) {
                        if (material.isItem()) {
                            completions.add(material.name());
                        }
                    }
                }
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("head") && args[3].equalsIgnoreCase("PLAYER_HEAD")) {
            String bodyPart = args[2].toLowerCase();
            String itemType = args[3].toUpperCase();
            if (bodyPart.equals("head") && itemType.equals("PLAYER_HEAD")) {
                completions.add("{SkullOwner}");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("mainhand") || args[2].equalsIgnoreCase("offhand")) {
            String bodyPart = args[2].toLowerCase();
            String itemType = args[3].toUpperCase();
            if (bodyPart.equals("mainhand") || bodyPart.equals("offhand")) {
                completions.add("{MetaDataNumber}");
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("edit")) {
            String bodyPart = args[2].toLowerCase();
            String itemType = args[3].toUpperCase();
            if (bodyPart.equals("chest") || bodyPart.equals("head") || bodyPart.equals("legs") || bodyPart.equals("feet")) {
                if (itemType.endsWith("_CHESTPLATE") || itemType.endsWith("_HELMET") || itemType.endsWith("_LEGGINGS") || itemType.endsWith("_BOOTS")) {
                    completions.add("COAST");
                    completions.add("DUNE");
                    completions.add("EYE");
                    completions.add("HOST");
                    completions.add("RAISER");
                    completions.add("RIB");
                    completions.add("SILENCE");
                    completions.add("SENTRY");
                    completions.add("SHAPER");
                    completions.add("SNOUT");
                    completions.add("TIDE");
                    completions.add("VEX");
                    completions.add("WARD");
                    completions.add("WILD");
                    completions.add("WAYFINDER");
                }
            }
        } else if (args.length == 6 && args[0].equalsIgnoreCase("edit")) {
            String bodyPart = args[2].toLowerCase();
            String itemType = args[3].toUpperCase();
            if (bodyPart.equals("chest") || bodyPart.equals("head") || bodyPart.equals("legs") || bodyPart.equals("feet")) {
                if (itemType.endsWith("_CHESTPLATE") || itemType.endsWith("_HELMET") || itemType.endsWith("_LEGGINGS") || itemType.endsWith("_BOOTS")) {
                    completions.add("NETHERITE");
                    completions.add("DIAMOND");
                    completions.add("EMERALD");
                    completions.add("IRON");
                    completions.add("GOLD");
                    completions.add("COPPER");
                    completions.add("AMETHYST");
                    completions.add("REDSTONE");
                    completions.add("LAPIS");
                    completions.add("QUARTZ");
                }
            }
        }
        return completions;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.BLAZE_ROD) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String customName = meta.getDisplayName();
                    String entityTypeStr = entityConfig.getConfig().getString("entities." + customName + ".entityType");
                    if (entityTypeStr != null) {
                        try {
                            EntityType entityType = EntityType.valueOf(entityTypeStr.toUpperCase());
                            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                Block clickedBlock = event.getClickedBlock();
                                if (clickedBlock != null) {
                                    Location spawnLocation = clickedBlock.getLocation().add(0.5, 1, 0.5);
                                    Entity entity = clickedBlock.getWorld().spawnEntity(spawnLocation, entityType);
                                    if (entity instanceof LivingEntity) {
                                        LivingEntity livingEntity = (LivingEntity) entity;
                                        livingEntity.setCustomName(customName);
                                        livingEntity.setCustomNameVisible(true);
                                        applyItemsToEntity(livingEntity, customName);
                                    }
                                }
                            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                                Location eyeLocation = event.getPlayer().getEyeLocation();
                                Vector direction = eyeLocation.getDirection().normalize();
                                RayTraceResult rayTraceResult = Objects.requireNonNull(eyeLocation.getWorld()).rayTraceBlocks(eyeLocation, direction, 100);
                                Location targetLocation;
                                if (rayTraceResult != null) {
                                    targetLocation = rayTraceResult.getHitPosition().toLocation(eyeLocation.getWorld());
                                } else {
                                    targetLocation = eyeLocation.add(direction.multiply(100));
                                }
                                targetLocation.setY(Objects.requireNonNull(targetLocation.getWorld()).getHighestBlockYAt(targetLocation.getBlockX(), targetLocation.getBlockZ()) + 1);
                                Entity entity = targetLocation.getWorld().spawnEntity(targetLocation, entityType);
                                if (entity instanceof LivingEntity) {
                                    LivingEntity livingEntity = (LivingEntity) entity;
                                    livingEntity.setCustomName(customName);
                                    livingEntity.setCustomNameVisible(true);
                                    applyItemsToEntity(livingEntity, customName);
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid entity type in config: " + entityTypeStr);
                        }
                    } else {
                        plugin.getLogger().warning("No entity type found for custom name: " + customName);
                    }
                } else {
                    plugin.getLogger().warning("Item meta or display name is missing.");
                }
            }
        }
    }
    private boolean isValidBodyPart(String bodyPart) {
        return List.of("head", "chest", "legs", "feet", "mainhand", "offhand").contains(bodyPart);
    }
    private boolean isValidItemType(String itemType) {
        try {
            Material.valueOf(itemType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    private void applyItemsToEntity(LivingEntity entity, String customName) {
        ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
        if (entitiesSection == null) return;
        ConfigurationSection customEntitySection = entitiesSection.getConfigurationSection(customName);
        if (customEntitySection == null) return;
        ConfigurationSection bodyPartsSection = customEntitySection.getConfigurationSection("bodyparts");
        if (bodyPartsSection == null) return;
        applyItemToBodyPart(entity, bodyPartsSection, "head", itemStack -> {
            if (entity.getEquipment() != null) {
                entity.getEquipment().setHelmet(itemStack);
            }
        });
        applyItemToBodyPart(entity, bodyPartsSection, "chest", itemStack -> {
            if (entity.getEquipment() != null) {
                entity.getEquipment().setChestplate(itemStack);
            }
        });
        applyItemToBodyPart(entity, bodyPartsSection, "legs", itemStack -> {
            if (entity.getEquipment() != null) {
                entity.getEquipment().setLeggings(itemStack);
            }
        });
        applyItemToBodyPart(entity, bodyPartsSection, "feet", itemStack -> {
            if (entity.getEquipment() != null) {
                entity.getEquipment().setBoots(itemStack);
            }
        });
        applyItemToBodyPart(entity, bodyPartsSection, "mainhand", itemStack -> {
            if (entity.getEquipment() != null) {
                entity.getEquipment().setItemInMainHand(itemStack);
            }
        });
        applyItemToBodyPart(entity, bodyPartsSection, "offhand", itemStack -> {
            if (entity.getEquipment() != null) {
                entity.getEquipment().setItemInOffHand(itemStack);
            }
        });
    }
    private void applyItemToBodyPart(LivingEntity entity, ConfigurationSection bodyPartsSection, String bodyPart, Consumer<ItemStack> equipmentSetter) {
        ConfigurationSection bodyPartSection = bodyPartsSection.getConfigurationSection(bodyPart);
        if (bodyPartSection == null) return;
        String itemTypeStr = bodyPartSection.getString("type");
        String trimType = bodyPartSection.getString("trimType");
        String trimMaterial = bodyPartSection.getString("trimMaterial");
        String skullOwner = bodyPartSection.getString("skull", "NONE");
        int metadata = bodyPartSection.getInt("metadata", -1);
        if (itemTypeStr != null) {
            try {
                Material material = Material.valueOf(itemTypeStr);
                ItemStack itemStack = new ItemStack(material);
                if (metadata >= 0) {
                    itemStack = new ItemStack(material, 1, (short) metadata);
                } else {
                    itemStack = new ItemStack(material);
                }
                if (material == Material.PLAYER_HEAD && bodyPart.equalsIgnoreCase("head")) {
                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    if (skullMeta != null) {
                        if (!"NONE".equalsIgnoreCase(skullOwner)) {
                            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner));
                            itemStack.setItemMeta(skullMeta);
                        } else {
                            skullMeta.setOwningPlayer(null);
                            itemStack.setItemMeta(skullMeta);
                        }
                    }
                } else if (itemStack.getItemMeta() instanceof ArmorMeta armorMeta) {
                    if (trimType != null && trimMaterial != null) {
                        TrimMaterial trimMaterialEnum = getTrimMaterial(trimMaterial);
                        TrimPattern trimPatternEnum = getTrimPattern(trimType);
                        if (trimMaterialEnum != null && trimPatternEnum != null) {
                            armorMeta.setTrim(new ArmorTrim(trimMaterialEnum, trimPatternEnum));
                        }
                    }
                    itemStack.setItemMeta(armorMeta);
                }
                equipmentSetter.accept(itemStack);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("§f§lCivEvents §f| §cInvalid material type specified: " + itemTypeStr);
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        String customName = entity.getCustomName();
        if (customName != null) {
            customName = customName.trim();
            ConfigurationSection entitiesSection = entityConfig.getConfig().getConfigurationSection("entities");
            if (entitiesSection != null) {
                for (String entityKey : entitiesSection.getKeys(false)) {
                    String configCustomName = entitiesSection.getString(entityKey + ".customName");
                    if (configCustomName != null && customName.equalsIgnoreCase(configCustomName)) {
                        event.getDrops().clear();
                        break;
                    }
                }
            }
        }
    }
    static TrimMaterial getTrimMaterial(String trimColor) {
        return switch (trimColor.toUpperCase()) {
            case "NETHERITE" -> TrimMaterial.NETHERITE;
            case "DIAMOND" -> TrimMaterial.DIAMOND;
            case "EMERALD" -> TrimMaterial.EMERALD;
            case "IRON" -> TrimMaterial.IRON;
            case "GOLD" -> TrimMaterial.GOLD;
            case "COPPER" -> TrimMaterial.COPPER;
            case "AMETHYST" -> TrimMaterial.AMETHYST;
            case "REDSTONE" -> TrimMaterial.REDSTONE;
            case "LAPIS" -> TrimMaterial.LAPIS;
            case "QUARTZ" -> TrimMaterial.QUARTZ;
            default -> null;
        };
    }
    static TrimPattern getTrimPattern(String trimType) {
        return switch (trimType.toUpperCase()) {
            case "COAST" -> TrimPattern.COAST;
            case "DUNE" -> TrimPattern.DUNE;
            case "EYE" -> TrimPattern.EYE;
            case "HOST" -> TrimPattern.HOST;
            case "RAISER" -> TrimPattern.RAISER;
            case "RIB" -> TrimPattern.RIB;
            case "SILENCE" -> TrimPattern.SILENCE;
            case "SENTRY" -> TrimPattern.SENTRY;
            case "SHAPER" -> TrimPattern.SHAPER;
            case "SNOUT" -> TrimPattern.SNOUT;
            case "TIDE" -> TrimPattern.TIDE;
            case "VEX" -> TrimPattern.VEX;
            case "WARD" -> TrimPattern.WARD;
            case "WILD" -> TrimPattern.WILD;
            case "WAYFINDER" -> TrimPattern.WAYFINDER;
            default -> null;
        };
    }
}