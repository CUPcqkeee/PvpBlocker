package cupcqkeee.plugin.pvpblocker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PvpBlockerCommand extends JavaPlugin implements Listener {

    private Set<String> protectedPlayers = new HashSet<>();
    private File usersFile;
    private FileConfiguration usersConfig;

    @Override
    public void onEnable() {
        // Загрузка файла users.yml, если он существует
        usersFile = new File(getDataFolder(), "users.yml");
        if (usersFile.exists()) {
            usersConfig = YamlConfiguration.loadConfiguration(usersFile);
            loadProtectedPlayers();
        }

        // Регистрация команды и событий
        Objects.requireNonNull(this.getCommand("pb")).setExecutor(new PvpCommand(this));
        getServer().getPluginManager().registerEvents(this, this);

        // Вывод сообщения при запуске плагина
        String message = ChatColor.RED + "**********\n"
                       + ChatColor.RED + "*         *\n"
                       + ChatColor.RED + "* PvpBlocker *\n"
                       + ChatColor.RED + "*         *\n"
                       + ChatColor.RED + "**********";
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    @Override
    public void onDisable() {
        saveProtectedPlayers();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();

            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if (protectedPlayers.contains(damagedPlayer.getName()) || protectedPlayers.contains(damager.getName())) {
                    event.setCancelled(true);
                }
            } else if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();
                    if (protectedPlayers.contains(damagedPlayer.getName()) || protectedPlayers.contains(shooter.getName())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() instanceof Player) {
            Player caughtPlayer = (Player) event.getCaught();
            if (protectedPlayers.contains(caughtPlayer.getName())) {
                event.setCancelled(true);
            }
        }
    }

    public void addProtectedPlayer(String playerName) {
        protectedPlayers.add(playerName);
        if (usersConfig != null) {
            usersConfig.set(playerName, "enable");
            saveUsersFile();
        }
    }

    public void removeProtectedPlayer(String playerName) {
        protectedPlayers.remove(playerName);
        if (usersConfig != null) {
            usersConfig.set(playerName, "disable");
            saveUsersFile();
        }
    }

    public boolean isProtectedPlayer(String playerName) {
        return protectedPlayers.contains(playerName);
    }

    private void loadProtectedPlayers() {
        for (String key : usersConfig.getKeys(false)) {
            if ("enable".equalsIgnoreCase(usersConfig.getString(key))) {
                protectedPlayers.add(key);
            }
        }
    }

    private void saveProtectedPlayers() {
        if (usersConfig != null) {
            for (String playerName : protectedPlayers) {
                usersConfig.set(playerName, "enable");
            }
            for (String key : usersConfig.getKeys(false)) {
                if (!protectedPlayers.contains(key)) {
                    usersConfig.set(key, "disable");
                }
            }
            saveUsersFile();
        }
    }

    private void saveUsersFile() {
        try {
            usersConfig.save(usersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
