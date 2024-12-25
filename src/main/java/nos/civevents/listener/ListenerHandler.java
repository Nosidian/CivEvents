package nos.civevents.listener;

import nos.civevents.commands.admin.AdminBomb;
import nos.civevents.commands.admin.AdminCommands;
import nos.civevents.listener.player.PlayerDeathListener;
import nos.civevents.commands.utility.*;
import nos.civevents.CivEvents;
import nos.civevents.items.Events.ObsidianMace;
import nos.civevents.items.Events.ObsidianScythe;
import nos.civevents.items.Events.ObsidianSpear;
import nos.civevents.items.Items.*;
import nos.civevents.Recipes.RecipesCreate;
import nos.civevents.Recipes.RecipesGui;
import nos.civevents.generation.WorldBackrooms;
import nos.civevents.generation.WorldGenerator;
import nos.civevents.configuration.BanConfig;
import nos.civevents.configuration.PlayerConfig;
import nos.civevents.configuration.ScytherConfig;
import org.bukkit.plugin.PluginManager;

public class ListenerHandler {
    public ListenerHandler(CivEvents plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        
        pluginManager.registerEvents(new BanCommands(), plugin);
        pluginManager.registerEvents(new BanConfig(), plugin);
        pluginManager.registerEvents(new AdminCommands(new AdminBomb()), plugin);
        pluginManager.registerEvents(new ScytherCommands(), plugin);
        pluginManager.registerEvents(new ScytherConfig(), plugin);
        pluginManager.registerEvents(new PlayerConfig(), plugin);
        pluginManager.registerEvents(new PlayerDeathListener(), plugin);
        pluginManager.registerEvents(new EntityCommands(), plugin);
        pluginManager.registerEvents(new FlagCommands(), plugin);
        pluginManager.registerEvents(new LocationCommands(), plugin);
        pluginManager.registerEvents(new PortalCommands(), plugin);
        pluginManager.registerEvents(new RecipesCreate(), plugin);
        pluginManager.registerEvents(new RecipesGui(), plugin);
        pluginManager.registerEvents(new WorldGenerator(), plugin);
        pluginManager.registerEvents(new WorldBackrooms(), plugin);

        // Items
        pluginManager.registerEvents(new GhostStaff(), plugin);
        //pluginManager.registerEvents(new Hammer(plugin), plugin);
        pluginManager.registerEvents(new Spear(), plugin);
        pluginManager.registerEvents(new Dagger(), plugin);
        pluginManager.registerEvents(new Scythe(), plugin);
        pluginManager.registerEvents(new Mace(), plugin);
        pluginManager.registerEvents(new ObsidianSpear(), plugin);
        pluginManager.registerEvents(new ObsidianMace(), plugin);
        pluginManager.registerEvents(new ObsidianScythe(), plugin);
    }
}
