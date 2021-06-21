package media.xen.tradingcards.whitelist;

import media.xen.tradingcards.api.blacklist.WhitelistMode;
import media.xen.tradingcards.api.blacklist.Blacklist;
import media.xen.tradingcards.config.SimpleConfig;
import org.bukkit.World;

import java.util.List;

/**
 * @author ketelsb
 */
public class WorldBlacklist implements Blacklist<World> {
    private final SimpleConfig config;
    private final String listedWorldsName = "Worlds";
    private String whitelistModeName = "Whitelist-Mode";
    private List<String> listedWorlds;
    private WhitelistMode whitelistMode;


    public WorldBlacklist(SimpleConfig config) {
        this.config = config;
        loadWorlds();
        setWhitelistMode();
    }

    private void loadWorlds() {
        listedWorlds = this.config.getConfig().getStringList(listedWorldsName);
    }

    private void setWhitelistMode() {
        boolean isWhitelist = this.config.getConfig().getBoolean(whitelistModeName);
        if (isWhitelist)
            this.whitelistMode = WhitelistMode.WHITELIST;
        else
            this.whitelistMode = WhitelistMode.BLACKLIST;
    }

    @Override
    public boolean isAllowed(World w) {
        boolean isOnList = listedWorlds.contains(w.getName());

        //If you're not on the blacklist, you're allowed
        if (this.whitelistMode == WhitelistMode.BLACKLIST) {
            return !isOnList;
        }

        //If you're on the whitelist, you're allowed
        if (this.whitelistMode == WhitelistMode.WHITELIST) {
            return isOnList;
        }
        return false;
    }

    @Override
    public void add(World w) {
        listedWorlds.add(w.getName());
        this.config.getConfig().set(listedWorldsName, null);
        this.config.getConfig().set(listedWorldsName, listedWorlds);
        this.config.saveConfig();
    }

    @Override
    public void remove(World w) {
        listedWorlds.remove(w.getName());
        this.config.getConfig().set(listedWorldsName, null);
        this.config.getConfig().set(listedWorldsName, listedWorlds);
        this.config.saveConfig();
    }

    @Override
    public WhitelistMode getMode() {
        return whitelistMode;
    }
}
