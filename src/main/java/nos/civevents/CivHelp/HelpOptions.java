package nos.civevents.CivHelp;

import java.util.HashMap;
import java.util.Map;

public class HelpOptions {
    private final Map<String, String[]> helpMessages = new HashMap<>();
    public HelpOptions() {
        helpMessages.put("admins", new String[]{
                "§f§lCivEvents §f| §eAdmins Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civadmin <args> <args>:",
                "§f- Airstrike <player> <size>",
                "§7- airstrike at player location",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("bans", new String[]{
                "§f§lCivEvents §f| §eBans Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civban <args> <player>",
                "§f- Add:",
                "§7- bans the player's username",
                "§f- Remove:",
                "§7- unbans the player's username",
                "§f- Ipadd:",
                "§7- bans the player's ip",
                "§f- Ipremove:",
                "§7- unbans the player's ip",
                "§f- Alts:",
                "§7- checks the player's alts",
                "§f- All:",
                "§7- ban username and ip",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("antiscythers", new String[]{
                "§f§lCivEvents §f| §eAntiScythers Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/antiscythers <args> <player>",
                "§f- Add:",
                "§7- bans the scyther's username",
                "§f- Remove:",
                "§7- unbans the scyther's username",
                "§f- Ipadd:",
                "§7- bans the scyther's ip",
                "§f- Ipremove:",
                "§7- unbans the scyther's ip",
                "§f- Alts:",
                "§7- checks the scyther's alts",
                "§f- All:",
                "§7- ban username and ip",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("deaths", new String[]{
                "§f§lCivEvents §f| §eDeaths Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civdeath <args> <true/false>",
                "§f- Lightning:",
                "§7- strikes lightning on death",
                "§f- Explosion:",
                "§7- creates explostion on death",
                "§f- Fireworks:",
                "§7- spawns a firework on death",
                "§f- Grave:",
                "§7- spawns grave with loot on death",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("entities", new String[]{
                "§f§lCivEvents §f| §eEntities Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civentity <args> <args> <args>",
                "§f- Create <name> <entitytype>:",
                "§7- creates a custom entity",
                "§f- Delete <name>:",
                "§7- deletes the custom entity",
                "§f- Edit <name> <body> <item> etc:",
                "§7- used to add armor or tools",
                "§f- Give <name> <player>:",
                "§7- gives wand to summon entity",
                "§f- Spawner <name> <player>:",
                "§7- gives spawner to summon entity",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("flags", new String[]{
                "§f§lCivEvents §f| §eFlags Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civflags <args> <true/false>",
                "§f- Pvp:",
                "§f- Freeze:",
                "§f- Deathban:",
                "§f- Unfreeze:",
                "§f- No-build:",
                "§f- No-break:",
                "§f- Allow-fly:",
                "§f- Unfreeze-all:",
                "§f- Invulnerable:",
                "§f- Disable-mobs:",
                "§f- Disable-drops:",
                "§7- used to toggle event flags",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("items", new String[]{
                "§f§lCivEvents §f| §eItems Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civitems <args> <player>",
                "§f- Ghost-staff:",
                "§7- combo's the player on hit",
                "§f- Hammer:",
                "§7- breaks the ground on impact",
                "§f- Mace:",
                "§7- launches players and nausea",
                "§f- Scythe:",
                "§7- used to dash forward",
                "§f- Spear:",
                "§7- gives target bleeding",
                "§f- Icicle:",
                "§7- freezes target players",
                "§f- Katana:",
                "§7- used to dash forward",
                "§f- BattleAxe:",
                "§7- breaks the ground on impact",
                "§f- VikingSpear:",
                "§7- gives target bleeding",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("locations", new String[]{
                "§f§lCivEvents §f| §eLocations Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§a- < Used For HungerGames Mainly >",
                "§f/civlocations <args> <args> <args>",
                "§f- Set <number>:",
                "§7- sets a location in config",
                "§f- Remove <number>:",
                "§7- removes a location in config",
                "§f- Start:",
                "§7- teleports players to locations",
                "§f- Release:",
                "§7- unfreezes players at locations",
                "§f- Platform <block> <slab>:",
                "§7- to set platform type (optional)",
                "§f- Automatic <distance>",
                "§7- to set locations around player",
                "§a- < Used For Civilizations >",
                "§f- /portal <args>",
                "§f- Create:",
                "§7- to create new portal",
                "§f- Delete:",
                "§7- to delete a portal",
                "§f- Set:",
                "§7- to set blocks for portal",
                "§f- Event:",
                "§7- to set location of event",
                "§a- < Used For Civilizations >",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("packages", new String[]{
                "§f§lCivEvents §f| §ePackages Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civpackages <args> <args> <args>",
                "§f- Tier Wool-Color Coords:",
                "§7- used to summon care packages",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("recipes", new String[]{
                "§f§lCivEvents §f| §eRecipes Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civrecipes <args> <args> <args>",
                "§f- Create <name> <slot>:",
                "§7- used to create a new recipe",
                "§7- best to start at slot 9",
                "§f- Remove <name>:",
                "§7- removes the recipe",
                "§7- best to remove the example",
                "§f- View <name>:",
                "§7- used to view the recipe",
                "§f/recipes = to see recipes gui",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("teams", new String[]{
                "§f§lCivEvents §f| §eRecipes Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/teams <args>",
                "§7- Create",
                "§7- Invite <player>",
                "§7- Join <team-number>",
                "§7- Disband",
                "§7- Kick <player>",
                "§7- Leave",
                "§7- Info",
                "§c< In Beta >",
                "§f/civteams <args>",
                "§c< In Beta >",
                "§e§l=---=---=---=---=---=---=---="
        });
        helpMessages.put("worlds", new String[]{
                "§f§lCivEvents §f| §eWorlds Help",
                "§e§l=---=---=---=---=---=---=---=",
                "§f/civworlds <args> <args> <args>",
                "§f- Create <new-world-name> <type>",
                "§7- used to create a new world",
                "§f- Delete <world-name>",
                "§7- used to delete a world",
                "§f- Edit <world-name> <chunks>",
                "§7- to clear world chunks",
                "§f- Tp <world-name>",
                "§7- used to tp to a world",
                "§e§l=---=---=---=---=---=---=---="
        });
    }
    public String[] getHelpMessages(String option) {
        return helpMessages.getOrDefault(option, new String[]{
                "§f§lCivEvents §f| §cInvalid option"
        });
    }
}