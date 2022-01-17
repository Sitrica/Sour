package com.sitrica.core.bukkit.sounds;

import com.sitrica.core.bukkit.SourBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.stream.Collectors;

public class SoundPlayer {

	private final Set<SourSound> sounds = new HashSet<>();
	private final SourBukkitPlugin instance;

	public SoundPlayer(SourBukkitPlugin instance, String node) {
		this.instance = instance;
		Optional<ConfigurationNode> configuration = instance.getConfiguration("sounds");
		if (!configuration.isPresent())
			return;
		ConfigurationNode section = configuration.get().node(node);
		if (!section.node("enabled").getBoolean(true))
			return;
		section = section.node("sounds");

		try {
			for (String key : section.getList(String.class)) {
				this.sounds.add(new SourSound(section.node(key), "CLICK"));
			}
		} catch (SerializationException e) {
			e.printStackTrace();
		}
	}

	public SoundPlayer(SourBukkitPlugin instance, ConfigurationNode section) {
		this.instance = instance;
		if (!section.node("enabled").getBoolean(true))
			return;
		section = section.node("sounds");
		try {
			for (String key : section.getList(String.class)) {
				this.sounds.add(new SourSound(section.node(key), "CLICK"));
			}
		} catch (SerializationException e) {
			e.printStackTrace();
		}
	}

	public SoundPlayer(SourBukkitPlugin instance, Collection<SourSound> sounds) {
		this.sounds.addAll(sounds);
		this.instance = instance;
	}

	private List<SourSound> getSorted() {
		return sounds.parallelStream()
				.sorted(Comparator.comparing(SourSound::getDelay))
				.collect(Collectors.toList());
	}

	public void playAt(Collection<Location> locations) {
		playAt(locations.toArray(new Location[0]));
	}

	public void playAt(Location... locations) {
		if (sounds.isEmpty())
			return;
		for (SourSound sound : getSorted()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> sound.playAt(locations), sound.getDelay());
		}
	}

	public void playTo(Collection<Player> players) {
		playTo(players.toArray(new Player[0]));
	}

	public void playTo(Player... player) {
		if (sounds.isEmpty())
			return;
		for (SourSound sound : getSorted()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> sound.playTo(player), sound.getDelay());
		}
	}

}
