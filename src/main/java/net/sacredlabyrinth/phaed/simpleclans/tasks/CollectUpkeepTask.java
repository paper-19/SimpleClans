package net.sacredlabyrinth.phaed.simpleclans.tasks;

import java.text.MessageFormat;
import net.md_5.bungee.api.ChatColor;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author roinujnosde
 */
public class CollectUpkeepTask extends BukkitRunnable {
	private final SimpleClans plugin;
	private final SettingsManager settingsManager;
	
	public CollectUpkeepTask() {
		plugin = SimpleClans.getInstance();
		settingsManager = plugin.getSettingsManager();
	}

    /**
     * Starts the repetitive task
     */
    public void start() {
    	int hour = settingsManager.getTasksCollectUpkeepHour();
    	int minute = settingsManager.getTasksCollectUpkeepMinute();
        long delay = Helper.getDelayTo(hour, minute);

        this.runTaskTimerAsynchronously(SimpleClans.getInstance(), delay * 20, 86400 * 20);
    }

    /**
     * (used internally)
     */
    @Override
    public void run() {
    	if (plugin == null) {
    		throw new IllegalStateException("Use the start() method!");
    	}        
    	plugin.getClanManager().getClans().forEach((clan) -> {
        	if (settingsManager.isChargeUpkeepOnlyIfMemberFeeEnabled() && !clan.isMemberFeeEnabled()) {
        		return;
        	}
            double upkeep = settingsManager.getClanUpkeep();
            if (settingsManager.isMultiplyUpkeepBySize()) {
                upkeep = upkeep * clan.getSize();
            }
            final double balance = clan.getBalance();
            if (balance >= upkeep) {
                clan.setBalance(balance - upkeep);
                clan.addBb(ChatColor.AQUA + MessageFormat.format(plugin.getLang("upkeep.collected"), upkeep), false);
            } else {
                clan.disband();
            }
        });
    }

}
