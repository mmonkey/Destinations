package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.CallModel;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.text.Text;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CallService {

    private Destinations plugin;
    private Scheduler schedulerService;
    private HashMap<CallModel, Timestamp> calls = new HashMap<CallModel, Timestamp>();
    private int expires;

    private void startCleanupTask() {

        this.schedulerService.createTaskBuilder().interval(1, TimeUnit.SECONDS).name("Call cleanup").execute(new Runnable() {
            public void run() {
                cleanup();
            }
        }).submit(plugin);

    }

    public void cleanup() {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (Map.Entry<CallModel, Timestamp> call : this.calls.entrySet()) {
            if (now.after(call.getValue())) {
                this.expiredNotification(call.getKey().getCaller(), call.getKey().getTarget());
                this.calls.remove(call.getKey());
            }
        }

    }

    public void expiredNotification(Player caller, Player target) {

        Optional<Player> optionalCaller = plugin.getGame().getServer().getPlayer(caller.getUniqueId());
        Optional<Player> optionalTarget = plugin.getGame().getServer().getPlayer(target.getUniqueId());

        if (optionalCaller.isPresent()) {

            Text.Builder message = Text.builder();
            message.append(Text.of(FormatUtil.WARN, "Your call to "));
            message.append(Text.of(FormatUtil.OBJECT, target.getName()));
            message.append(Text.of(FormatUtil.WARN, " has expired."));
            optionalCaller.get().sendMessage(message.build());
        }

        if (optionalTarget.isPresent()) {

            Text.Builder message = Text.builder();
            message.append(Text.of(FormatUtil.WARN, " The request from "));
            message.append(Text.of(FormatUtil.OBJECT, caller.getName()));
            message.append(Text.of(FormatUtil.WARN, " has expired."));
            optionalTarget.get().sendMessage(message.build());

        }

    }

    public void call(Player caller, Player target) {
        CallModel call = new CallModel(caller, target);
        Timestamp expireTime = new Timestamp(System.currentTimeMillis() + this.expires * 1000);
        this.calls.put(call, expireTime);
    }

    public void removeCall(Player caller, Player target) {

        for (Map.Entry<CallModel, Timestamp> call : this.calls.entrySet()) {
            if (call.getKey().getCaller().getUniqueId().equals(caller.getUniqueId())
                    && call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                this.calls.remove(call.getKey());
            }
        }
    }

    public Player getFirstCaller(Player target) {

        Player caller = null;
        for (Map.Entry<CallModel, Timestamp> call : this.calls.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                caller = call.getKey().getCaller();
            }
        }

        return caller;
    }

    public int getNumCallers(Player target) {

        int callers = 0;
        for (Map.Entry<CallModel, Timestamp> call : this.calls.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                callers++;
            }
        }

        return callers;
    }

    public List<String> getCalling(Player target) {

        List<String> list = new ArrayList<String>();

        for (Map.Entry<CallModel, Timestamp> call : this.calls.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())) {
                list.add(call.getKey().getCaller().getName());
            }
        }

        return list;
    }

    public boolean isCalling(Player caller, Player target) {

        for (Map.Entry<CallModel, Timestamp> call : this.calls.entrySet()) {
            if (call.getKey().getTarget().getUniqueId().equals(target.getUniqueId())
                    && call.getKey().getCaller().getUniqueId().equals(caller.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public CallService(Destinations plugin) {
        this.plugin = plugin;
        this.schedulerService = plugin.getGame().getScheduler();
        this.expires = plugin.getDefaultConfig().get().getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).getInt(60);
        this.startCleanupTask();
    }
}
