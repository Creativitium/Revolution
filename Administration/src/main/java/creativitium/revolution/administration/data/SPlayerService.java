package creativitium.revolution.administration.data;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.templates.RPlayerService;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SPlayerService extends RPlayerService<SPlayer>
{
    public SPlayerService()
    {
        super(Administration.getInstance());
    }

    @Override
    public void loadDataFromConfiguration()
    {
        getConfiguration().getKeys(false).forEach(key -> getData().put(UUID.fromString(key), getConfiguration().getSerializable(key, SPlayer.class)));
    }

    @Override
    public SPlayer createPlayerData(UUID uuid)
    {
        return new SPlayer();
    }

    @Override
    public Optional<SPlayer> getPlayerData(String name)
    {
        final UUID uuid = Bukkit.getPlayer(name) != null ? Objects.requireNonNull(Bukkit.getPlayer(name)).getUniqueId() : Bukkit.getPlayerUniqueId(name);
        return uuid != null ? Optional.of(getPlayerData(uuid)) : Optional.empty();
    }
}
