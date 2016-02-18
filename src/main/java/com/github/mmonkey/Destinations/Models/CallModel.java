package com.github.mmonkey.Destinations.Models;


import org.spongepowered.api.entity.living.player.Player;

public class CallModel {

    private Player caller;
    private Player target;

    public Player getCaller() {
        return this.caller;
    }

    public Player getTarget() {
        return this.target;
    }

    public CallModel(Player caller, Player target) {
        this.caller = caller;
        this.target = target;
    }

}
