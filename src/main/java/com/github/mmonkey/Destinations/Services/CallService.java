package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CallService implements RemovalListener<Pair<User, User>, ObjectUtils.Null> {

	private Destinations plugin;
	private SchedulerService schedulerService;
	private Cache<Pair<User, User>, ObjectUtils.Null> calls;

    private void startCleanupTask(final Cache<Pair<User, User>, ObjectUtils.Null> calls) {

        this.schedulerService.getTaskBuilder().interval(1, TimeUnit.SECONDS).name("CallCache cleanup").execute(new Runnable() {
            public void run() {
                calls.cleanUp();
            }
        }).submit(plugin);

    }

	public void onRemoval(final RemovalNotification<Pair<User, User>, ObjectUtils.Null> removal) {
		
		schedulerService.getTaskBuilder()
			.delay(0)
			.name("Notify Caller")
			.execute(new Runnable() {
				public void run() {
                    expiredNotification(removal.getKey());
				}
			}).submit(plugin);

	}

	public void expiredNotification(Pair<User, User> pair) {
		
		Optional<Player> caller = plugin.getGame().getServer().getPlayer(pair.getRight().getUniqueId());

		if (caller.isPresent()) {
            TextBuilder message = Texts.builder();
            message.append(Texts.of(FormatUtil.WARN, "Your call to "));
            message.append(Texts.of(FormatUtil.OBJECT, pair.getLeft().getName()));
            message.append(Texts.of(FormatUtil.WARN, " has expired."));
            caller.get().sendMessage(message.build());
		}
		
	}

	public void call(User caller, User callee) {
		Pair<User, User> pair = Pair.of(caller, callee);
		this.calls.put(pair, ObjectUtils.NULL);
	}

	public List<String> getCalling(Player callee) {
		
		List<String> list = new ArrayList<String>();
		
		for (Map.Entry<Pair<User, User>, ObjectUtils.Null> entry : calls.asMap().entrySet()) {
			UUID uuid = entry.getKey().getRight().getUniqueId();	
			if (uuid.equals(callee.getUniqueId())) {
				list.add(entry.getKey().getLeft().getName());
			}
		}
		
		return list;
	}

    public boolean isCalling(Player caller, Player callee) {

        for (Map.Entry<Pair<User, User>, ObjectUtils.Null> entry : calls.asMap().entrySet()) {
            if (entry.getKey().getRight().getUniqueId().equals(caller.getUniqueId())
                    && entry.getKey().getLeft().getUniqueId().equals(callee.getUniqueId())) {
                return true;
            }
        }

        return false;
    }
	
	public CallService(Destinations plugin, SchedulerService schedulerService) {
		this.plugin = plugin;
		this.schedulerService = schedulerService;

        int timeout = plugin.getDefaultConfig().get().getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).getInt(1);
		this.calls = CacheBuilder.newBuilder().expireAfterWrite(timeout, TimeUnit.MINUTES).removalListener(this).build();
        this.startCleanupTask(calls);
	}
}
