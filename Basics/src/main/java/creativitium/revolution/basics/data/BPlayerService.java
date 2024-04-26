package creativitium.revolution.basics.data;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.templates.RPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class BPlayerService extends RPlayerService<BPlayer>
{
    public BPlayerService()
    {
        super(Basics.getInstance());
    }

    @Override
    public void loadDataFromConfiguration()
    {
        getConfiguration().getKeys(false).forEach(key -> getData().put(UUID.fromString(key), getConfiguration().getSerializable(key, BPlayer.class)));
    }

    @Override
    public BPlayer createPlayerData(UUID uuid)
    {
        return new BPlayer(Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getName).orElseGet(uuid::toString));
    }

    @Override
    public Optional<BPlayer> getPlayerData(String name)
    {
        final OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(name);
        return Optional.ofNullable(cached != null ? getPlayerData(cached.getUniqueId()) : null);
    }

    public Collection<BPlayer> getAllPlayerData()
    {
        return getData().values();
    }
}
