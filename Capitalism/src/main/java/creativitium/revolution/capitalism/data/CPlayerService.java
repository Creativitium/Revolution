package creativitium.revolution.capitalism.data;

import creativitium.revolution.capitalism.Capitalism;
import creativitium.revolution.foundation.templates.RPlayerService;
import lombok.Getter;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class CPlayerService extends RPlayerService<CPlayer>
{
    private final NumberFormat format = NumberFormat.getCurrencyInstance();
    private final InternalEconomy economy;

    public CPlayerService()
    {
        super(Capitalism.getInstance());
        this.economy = new InternalEconomy(this);
    }

    @Override
    public void loadDataFromConfiguration()
    {
        getConfiguration().getKeys(false).forEach(key -> {
            try
            {
                getData().put(UUID.fromString(key), getConfiguration().getSerializable(key, CPlayer.class));
            }
            catch (Throwable ex)
            {
                getPlugin().getSLF4JLogger().warn("Failed to load player data for {}", key, ex);
            }
        });
    }

    @Override
    public CPlayer createPlayerData(UUID uuid)
    {
        return new CPlayer();
    }

    @Override
    public Optional<CPlayer> getPlayerData(String name)
    {
        final OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(name);
        return Optional.ofNullable(cached != null ? getPlayerData(cached.getUniqueId()) : null);
    }

    public CPlayer createPlayerData(String name)
    {
        return getPlayerData(Bukkit.getOfflinePlayer(name).getUniqueId());
    }

    public boolean hasPlayerData(String name)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);

        return player != null && getData().containsKey(player.getUniqueId());
    }

    public Collection<CPlayer> getAllPlayerData()
    {
        return getData().values();
    }

    public static class InternalEconomy extends AbstractEconomy
    {
        private CPlayerService internal;

        public InternalEconomy(CPlayerService service)
        {
            this.internal = service;
        }

        @Override
        public boolean isEnabled()
        {
            return true;
        }

        @Override
        public String getName()
        {
            return "Capitalism";
        }

        @Override
        public boolean hasBankSupport()
        {
            return false;
        }

        @Override
        public int fractionalDigits()
        {
            return 2;
        }

        @Override
        public String format(double amount)
        {
            return internal.getFormat().format(amount);
        }

        @Override
        public String currencyNamePlural()
        {
            return "Dollars";
        }

        @Override
        public String currencyNameSingular()
        {
            return "Dollar";
        }

        @Override
        public boolean hasAccount(String playerName)
        {
            return internal.hasPlayerData(playerName);
        }

        @Override
        public boolean hasAccount(String playerName, String worldName)
        {
            return hasAccount(playerName);
        }

        @Override
        public double getBalance(String playerName)
        {
            return internal.getPlayerData(playerName).map(CPlayer::getBalance).orElse(0.0);
        }

        @Override
        public double getBalance(String playerName, String world)
        {
            return getBalance(playerName);
        }

        @Override
        public boolean has(String playerName, double amount)
        {
            return internal.getPlayerData(playerName).filter(cPlayer -> cPlayer.getBalance() >= amount).isPresent();
        }

        @Override
        public boolean has(String playerName, String worldName, double amount)
        {
            return has(playerName, amount);
        }

        @Override
        public EconomyResponse withdrawPlayer(String playerName, double amount)
        {
            final Optional<CPlayer> player = internal.getPlayerData(playerName);

            if (player.isEmpty())
            {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Data for this player does not exist");
            }

            player.get().setBalance(player.get().getBalance() - amount);

            return new EconomyResponse(amount, player.get().getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
        }

        @Override
        public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
        {
            return withdrawPlayer(playerName, amount);
        }

        @Override
        public EconomyResponse depositPlayer(String playerName, double amount)
        {
            final Optional<CPlayer> player = internal.getPlayerData(playerName);

            if (player.isEmpty())
            {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Data for this player does not exist");
            }

            player.get().setBalance(player.get().getBalance() + amount);

            return new EconomyResponse(amount, player.get().getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
        }

        @Override
        public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
        {
            return depositPlayer(playerName, amount);
        }

        @Override
        public EconomyResponse createBank(String name, String player)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse deleteBank(String name)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse bankBalance(String name)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse bankHas(String name, double amount)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse bankWithdraw(String name, double amount)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse bankDeposit(String name, double amount)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse isBankOwner(String name, String playerName)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public EconomyResponse isBankMember(String name, String playerName)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Capitalism doesn't have a banking system");
        }

        @Override
        public List<String> getBanks()
        {
            return List.of();
        }

        @Override
        public boolean createPlayerAccount(String playerName)
        {
            if (internal.hasPlayerData(playerName))
            {
                return false;
            }

            internal.createPlayerData(playerName);
            return true;
        }

        @Override
        public boolean createPlayerAccount(String playerName, String worldName)
        {
            return createPlayerAccount(playerName);
        }
    }
}
