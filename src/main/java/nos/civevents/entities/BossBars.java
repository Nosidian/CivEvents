package nos.civevents.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class BossBars {
    private static final HashMap<String, NamespacedKey> ids = new HashMap<>();
    public static void createBar(String id, String title, BarColor color, BarStyle style, double percent,
                                 BarFlag... flags) {
        NamespacedKey key = createKey(id);
        ids.put(id, key);
        Bukkit.createBossBar(key, title, color, style, flags);
        setProgress(id, percent);
    }
    public static void createBar(String title, BarColor color, BarStyle style, double percent, BarFlag... flags) {
        createBar(title, title, color, style, percent, flags);
    }
    public static void createBar(String title, BarColor color, BarStyle style, double percent) {
        createBar(title, color, style, percent, BarFlag.PLAY_BOSS_MUSIC);
    }
    public static void addPlayer(Player player, String... ids) {
        addPlayers(ids, player);
    }
    public static void addPlayers(String id, Player... players) {
        BossBar bar = getBar(id);
        if (bar == null) {
            return;
        }
        for (Player player : players) {
            bar.addPlayer(player);
        }
    }
    public static void addPlayers(String[] ids, Player... players) {
        for (String id : ids) {
            addPlayers(id, players);
        }
    }
    public static void addPlayers(List<Player> players, String... ids) {
        for (Player player : players) {
            addPlayer(player, ids);
        }
    }
    public static void addAllPlayers(String... ids) {
        addPlayers((List<Player>) Bukkit.getOnlinePlayers(), ids);
    }
    public static void removePlayer(Player player, String... ids) {
        removePlayers(ids, player);
    }
    public static void removePlayers(String id, Player... players) {
        BossBar bar = getBar(id);
        if (bar == null) {
            return;
        }
        for (Player player : players) {
            bar.removePlayer(player);
        }
    }
    public static void removePlayers(String[] ids, Player... players) {
        for (String id : ids) {
            removePlayers(id, players);
        }
    }
    public static void removePlayers(List<Player> players, String... ids) {
        for (Player player : players) {
            removePlayer(player, ids);
        }
    }
    public static void removeAllPlayers(String... ids) {
        removePlayers((List<Player>) Bukkit.getOnlinePlayers(), ids);
    }
    public static BossBar getBar(String id) {
        if (ids.containsKey(id)) {
            return Bukkit.getBossBar(ids.get(id));
        }
        Bukkit.broadcastMessage("§4No BossBar with id=§6" + id + "§4 found");
        return null;
    }
    public static void deleteBars(String... ids) {
        removeAllPlayers(ids);
        for (String id : ids) {
            if (BossBars.ids.containsKey(id)) {
                Bukkit.removeBossBar(BossBars.ids.get(id));
                BossBars.ids.remove(id);
            }
        }
    }
    public static void clearBars() {
        Set<String> ids = new HashSet<>(BossBars.ids.keySet());
        ids.forEach(BossBars::deleteBars);
    }
    public static void setTitle(String id, String title) {
        BossBar bar = getBar(id);
        if (bar != null) {
            bar.setTitle(title);
        }
    }
    public static void setColor(String id, BarColor color) {
        BossBar bar = getBar(id);
        if (bar != null) {
            bar.setColor(color);
        }
    }
    public static void setStyle(String id, BarStyle style) {
        BossBar bar = getBar(id);
        if (bar != null) {
            bar.setStyle(style);
        }
    }
    public static void setProgress(String id, double percent) {
        BossBar bar = getBar(id);
        if (bar != null) {
            bar.setProgress(createProgress(percent));
        }
    }
    public static void addFlags(String id, BarFlag... flags) {
        BossBar bar = getBar(id);
        if (bar == null) {
            return;
        }
        for (BarFlag flag : flags) {
            bar.addFlag(flag);
        }
    }
    public static void removeFlags(String id, BarFlag... flags) {
        BossBar bar = getBar(id);
        if (bar == null) {
            return;
        }
        for (BarFlag flag : flags) {
            bar.removeFlag(flag);
        }
    }
    private static double createProgress(double percent) {
        percent /= 100;
        return percent < 0 ? 0 : percent > 1 ? 1 : percent;
    }
    private static NamespacedKey createKey(String id) {
        return new NamespacedKey(CivEvents.getPlugin(), getValidId(id));
    }
    private static String getValidId(String id) {
        StringBuilder validId = new StringBuilder();
        for (char c : id.toCharArray()) {
            if ((c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123)) {
                validId.append(c);
            }
        }
        return validId.toString();
    }
}