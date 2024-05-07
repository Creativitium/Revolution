package creativitium.revolution.administration.data;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.templates.RPlayerService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;

public class APlayerService extends RPlayerService<APlayer>
{
    public APlayerService()
    {
        super(Administration.getInstance(), "preferences.yml");
    }

    @Override
    public void loadDataFromConfiguration()
    {
        getConfiguration().getKeys(false).forEach(key -> getData().put(UUID.fromString(key), getConfiguration().getSerializable(key, APlayer.class)));
    }

    @Override
    public APlayer createPlayerData(UUID uuid)
    {
        return new APlayer();
    }

    @Override
    public Optional<APlayer> getPlayerData(String name)
    {
        // Intentionally avoid getting an admin entry if the player has never joined the server before
        final OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(name);
        return Optional.ofNullable(cached != null ? getPlayerData(cached.getUniqueId()) : null);
    }
}
