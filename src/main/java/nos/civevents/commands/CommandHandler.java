package nos.civevents.commands;

import nos.civevents.commands.admin.*;
import nos.civevents.luckperms.CivilizationCommands;
import nos.civevents.luckperms.TeamCommands;
import nos.civevents.commands.utility.ScytherCommands;
import nos.civevents.commands.utility.DeathCommands;
import nos.civevents.commands.utility.EntityCommands;
import nos.civevents.CivEvents;
import nos.civevents.commands.utility.FlagCommands;
import nos.civevents.commands.help.HelpCommands;
import nos.civevents.commands.help.HelpOptions;
import nos.civevents.items.ItemCommands;
import nos.civevents.commands.utility.LocationCommands;
import nos.civevents.commands.utility.PortalCommands;
import nos.civevents.commands.utility.CivPackages.*;
import nos.civevents.commands.utility.OperatorCommands;
import nos.civevents.commands.utility.RecipeCommands;
import nos.civevents.commands.utility.WorldCommands;
import nos.civevents.commands.utility.BanCommands;

import java.util.Objects;

public class CommandHandler {
    public CommandHandler(CivEvents plugin) {
        // CivAdmins
        Objects.requireNonNull(plugin.getCommand("civadmins")).setExecutor(new AdminCommands(new AdminBomb()));
        Objects.requireNonNull(plugin.getCommand("civadmins")).setTabCompleter(new AdminCommands(new AdminBomb()));
        Objects.requireNonNull(plugin.getCommand("gms")).setExecutor(new AdminSurvival());
        Objects.requireNonNull(plugin.getCommand("gms")).setTabCompleter(new AdminSurvival());
        Objects.requireNonNull(plugin.getCommand("gmc")).setExecutor(new AdminCreative());
        Objects.requireNonNull(plugin.getCommand("gmc")).setTabCompleter(new AdminCreative());
        Objects.requireNonNull(plugin.getCommand("gma")).setExecutor(new AdminAdventure());
        Objects.requireNonNull(plugin.getCommand("gma")).setTabCompleter(new AdminAdventure());
        Objects.requireNonNull(plugin.getCommand("gmsp")).setExecutor(new AdminSpectator());
        Objects.requireNonNull(plugin.getCommand("gmsp")).setTabCompleter(new AdminSpectator());


        // CivBans
        Objects.requireNonNull(plugin.getCommand("civban")).setExecutor(new BanCommands());
        Objects.requireNonNull(plugin.getCommand("civban")).setTabCompleter(new BanCommands());

        Objects.requireNonNull(plugin.getCommand("antiscythers")).setExecutor(new ScytherCommands());
        Objects.requireNonNull(plugin.getCommand("antiscythers")).setTabCompleter(new ScytherCommands());


        // CivDeaths
        Objects.requireNonNull(plugin.getCommand("civdeaths")).setExecutor(new DeathCommands());
        Objects.requireNonNull(plugin.getCommand("civdeaths")).setTabCompleter(new DeathCommands());


        // CivEntities
        Objects.requireNonNull(plugin.getCommand("civentities")).setExecutor(new EntityCommands());
        Objects.requireNonNull(plugin.getCommand("civentities")).setTabCompleter(new EntityCommands());


        // CivFlags
        Objects.requireNonNull(plugin.getCommand("civflags")).setExecutor(new FlagCommands());
        Objects.requireNonNull(plugin.getCommand("civflags")).setTabCompleter(new FlagCommands());


        // CivHelp
        Objects.requireNonNull(plugin.getCommand("civhelp")).setExecutor(new HelpCommands(new HelpOptions()));
        Objects.requireNonNull(plugin.getCommand("civhelp")).setTabCompleter(new HelpCommands(new HelpOptions()));

        // CivItems
        Objects.requireNonNull(plugin.getCommand("civitems")).setExecutor(new ItemCommands());
        Objects.requireNonNull(plugin.getCommand("civitems")).setTabCompleter(new ItemCommands());


        // CivLocations
        Objects.requireNonNull(plugin.getCommand("civlocations")).setExecutor(new LocationCommands());
        Objects.requireNonNull(plugin.getCommand("civlocations")).setTabCompleter(new LocationCommands());
        Objects.requireNonNull(plugin.getCommand("civportals")).setExecutor(new PortalCommands());
        Objects.requireNonNull(plugin.getCommand("civportals")).setTabCompleter(new PortalCommands());


        // CivPackages
        Objects.requireNonNull(plugin.getCommand("civpackages")).setExecutor(new PackageCommands());
        Objects.requireNonNull(plugin.getCommand("civpackages")).setTabCompleter(new PackageCommands());

        // CivRecipes
        Objects.requireNonNull(plugin.getCommand("civrecipes")).setExecutor(new OperatorCommands());
        Objects.requireNonNull(plugin.getCommand("civrecipes")).setTabCompleter(new OperatorCommands());
        Objects.requireNonNull(plugin.getCommand("recipes")).setExecutor(new RecipeCommands());


        // CivWorlds
        Objects.requireNonNull(plugin.getCommand("civworlds")).setExecutor(new WorldCommands());
        Objects.requireNonNull(plugin.getCommand("civworlds")).setTabCompleter(new WorldCommands());

        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            Objects.requireNonNull(plugin.getCommand("civteams")).setExecutor(new CivilizationCommands());
            Objects.requireNonNull(plugin.getCommand("civteams")).setTabCompleter(new CivilizationCommands());
            Objects.requireNonNull(plugin.getCommand("team")).setExecutor(new TeamCommands());
            Objects.requireNonNull(plugin.getCommand("team")).setTabCompleter(new TeamCommands());
            plugin.getServer().getPluginManager().registerEvents(new TeamCommands(), plugin);
        }



    }
}
