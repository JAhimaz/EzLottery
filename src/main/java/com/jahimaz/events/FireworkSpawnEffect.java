package com.jahimaz.events;

import com.jahimaz.Misc;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkSpawnEffect {
    static public void createFirework(Player player, Location local, boolean flicker, boolean trail, String rawColour, String rawFadeColor, int power){

        Color baseColour = Misc.getColor(rawColour);
        Color fadeColour = Misc.getColor(rawFadeColor);

        org.bukkit.entity.Firework fw = player.getWorld().spawn(local, org.bukkit.entity.Firework.class);
        FireworkMeta fmeta = fw.getFireworkMeta();

        FireworkEffect.Builder fwbuilder = FireworkEffect.builder();
        fmeta.addEffect(fwbuilder.flicker(flicker).withColor(baseColour).trail(trail).withFade(fadeColour).with(FireworkEffect.Type.BALL_LARGE).build());
        fmeta.setPower(power);
        fw.setFireworkMeta(fmeta);
    }
}
