package com.sitrica.core.bukkit.utils;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

public class Fallback {

	public static EntityType entityAttempt(String attempt, String fallback) {
		EntityType entity = null;
		try {
			entity = EntityType.valueOf(attempt.toUpperCase());
		} catch (Exception e) {
			try {
				entity = EntityType.valueOf(fallback);
			} catch (Exception ignored) {}
		}
		if (entity == null)
			entity = EntityType.ARROW;
		return entity;
	}

	public static Material materialAttempt(String attempt, String fallback) {
		Material material = null;
		try {
			material = Material.valueOf(attempt.toUpperCase());
		} catch (Exception e) {
			try {
				material = Material.valueOf(fallback);
			} catch (Exception ignored) {}
		}
		if (material == null)
			material = Material.CHEST;
		return material;
	}

	public static Sound soundAttempt(String attempt, String fallback) {
		Sound sound = null;
		try {
			sound = Sound.valueOf(attempt.toUpperCase());
		} catch (Exception e) {
			try {
				sound = Sound.valueOf(fallback);
			} catch (Exception ignored) {}
		}
		if (sound == null)
			sound = Sound.ENTITY_PLAYER_LEVELUP;
		return sound;
	}

}
