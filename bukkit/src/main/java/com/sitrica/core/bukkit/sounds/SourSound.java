package com.sitrica.core.bukkit.sounds;

import com.sitrica.core.bukkit.utils.Fallback;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;

public class SourSound {

	private final float pitch, volume;
	private final boolean enabled;
	private final Sound sound;
	private final int delay;

	public SourSound(ConfigurationNode section, String fallback) {
		String name = section.node("sound").getString("ENTITY_PLAYER_LEVELUP");
		this.sound = Fallback.soundAttempt(name, fallback);
		this.volume = (float) section.node("volume").getDouble(1);
		this.pitch = (float) section.node("pitch").getDouble(1);
		this.enabled = section.node("enabled").getBoolean(true);
		this.delay = section.node("delay").getInt(0);
	}

	public SourSound(Sound sound, float pitch, float volume, boolean enabled) {
		this.enabled = enabled;
		this.volume = volume;
		this.sound = sound;
		this.pitch = pitch;
		this.delay = 0;
	}

	public int getDelay() {
		return delay;
	}

	public Sound getSound() {
		return sound;
	}

	public float getPitch() {
		return pitch;
	}

	public float getVolume() {
		return volume;
	}

	public void playTo(Player... players) {
		if (enabled) {
			for (Player player : players) {
				player.playSound(player.getLocation(), sound, volume, pitch);
			}
		}
	}

	public void playAt(Location... locations) {
		if (enabled) {
			for (Location location : locations) {
				location.getWorld().playSound(location, sound, volume, pitch);
			}
		}
	}

}
