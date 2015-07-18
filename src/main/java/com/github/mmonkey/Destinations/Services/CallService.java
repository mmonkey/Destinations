package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Destinations;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.text.Texts;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CallService implements RemovalListener<Pair<User, User>, ObjectUtils.Null> {


    private final Cache<Pair<User, User>, ObjectUtils.Null> calls;
    private final SchedulerService schedulerService;
    private final Destinations plugin;

    public CallService(Destinations plugin, SchedulerService schedulerService) {
        this.plugin = plugin;
        this.schedulerService = schedulerService;
        //noinspection ThisEscapedInObjectConstruction
        calls = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .removalListener(this)
                .build();
        schedulerService.getTaskBuilder().interval(1,TimeUnit.SECONDS).name("CallCache cleanup").execute(
                new Runnable() {
                    public void run() {
                        calls.cleanUp();
                    }
                }
        ).submit(plugin);
    }

    public void onRemoval(final RemovalNotification<Pair<User, User>, ObjectUtils.Null> removalNotification) {
        schedulerService.getTaskBuilder().delay(0).name("Notify Caller").execute(
                new Runnable() {
                    public void run() {
                        cooldownNotify(removalNotification.getKey());
                    }
                }
        ).submit(plugin);
    }

    public void cooldownNotify(Pair<User,User> pair){
        final Optional<Player> onlinePlayer = plugin.getGame().getServer().getPlayer(pair.getRight().getUniqueId());
        if(onlinePlayer.isPresent()){
            onlinePlayer.get().sendMessage(Texts.of(String.format("Your call to %s expired.", pair.getLeft().getName())));
        }
    }

    public void call(User from, User to) {
        final Pair<User, User> pair = Pair.of(from, to);
        this.calls.put(pair, ObjectUtils.NULL);
    }

    public List<String> getCalling(final Player player) {
        List<String> out = new ArrayList();
        for(Map.Entry<Pair<User, User>, ObjectUtils.Null> entry : calls.asMap().entrySet()){
            final UUID uuid = entry.getKey().getRight().getUniqueId();
            if(uuid.equals(player.getUniqueId())){
                out.add(entry.getKey().getLeft().getName());
            }
        }
        return out;
    }
}
