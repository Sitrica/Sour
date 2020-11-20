package com.sitrica.core.common.database;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sitrica.core.common.database.serializers.ItemStackSerializer;
import com.sitrica.core.common.database.serializers.LocationSerializer;
import com.sitrica.core.common.utils.Utils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract class Database<T> {

	protected final Gson gson;

	public Database(Map<Type, Serializer<?>> serializers) {
		GsonBuilder builder = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
				.enableComplexMapKeySerialization()
				.serializeNulls();

		if (Utils.isBukkit()) {
			builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
					.registerTypeAdapter(Location.class, new LocationSerializer());
		}

		serializers.forEach(builder::registerTypeAdapter);
		gson = builder.create();
	}

	public Database() {
		GsonBuilder builder = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
				.enableComplexMapKeySerialization()
				.serializeNulls();

		if (Utils.isBukkit()) {
			builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
					.registerTypeAdapter(Location.class, new LocationSerializer());
		}

		gson = builder.create();
	}

	public abstract void close();

	public abstract void put(String key, T value);

	public abstract T get(String key, T def);

	public abstract boolean has(String key);

	public abstract Set<String> getKeys();

	public T get(String key) {
		return get(key, null);
	}

	public void delete(String key) {
		put(key, null);
	}

	public abstract void clear();

	public String serialize(Object object, Type type) {
		return gson.toJson(object, type);
	}

	public Object deserialize(String json, Type type) {
		return gson.fromJson(json, type);
	}

}
