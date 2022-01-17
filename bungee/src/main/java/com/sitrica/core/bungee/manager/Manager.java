package com.sitrica.core.bungee.manager;

import com.sitrica.core.bungee.SourBungeePlugin;
import com.sitrica.core.common.database.Database;
import com.sitrica.core.common.database.H2Database;
import com.sitrica.core.common.database.MySQLDatabase;
import com.sitrica.core.common.database.Serializer;
import net.md_5.bungee.api.plugin.Listener;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Manager implements Listener {

	private final Map<Class<?>, Database<?>> databases = new HashMap<>();
	private final boolean listener;

	protected Manager(boolean listener) {
		this.listener = listener;
	}

	/**
	 * 	database:
		    # Types are MYSQL and H2.
		    type: "H2"
		    autosave: "5 minutes"
		    # Table name configuration for databases.
		    mines-table: "Mines"
		    mysql:
		        user: "root"
		        address: "localhost"
		        password: "password"
		        name: "mines-example"
	 *
	 * @param <T> The type of this database, used to format the database.
	 * @param tableNode The table node within the configuration section that this database should map to.
	 * @param type The type class the database will be.
	 * @return The complete database.
	 * @throws IllegalAccessException If the configuration section doesn't exist.
	 */
	public <T, S> Database<T> getNewDatabase(SourBungeePlugin instance, String tableNode, Class<T> type) throws IllegalAccessException {
		return getNewDatabase(instance, tableNode, type, new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	public <T, S> Database<T> getNewDatabase(SourBungeePlugin instance, String tableNode, Class<T> type, Map<Type, Serializer<?>> serializers) throws IllegalAccessException {
		if (databases.containsKey(type))
			return (Database<T>) databases.get(type);

		Optional<ConfigurationNode> configuration = instance.getConfiguration();
		if (!configuration.isPresent())
			throw new IllegalAccessException("There was no config.yml found for " + instance.getName());

		ConfigurationNode section = configuration.get().node("database");
		if (section == null)
			throw new IllegalAccessException("There was no database configuration section for " + instance.getName());

        String table = section.node("table").getString(tableNode);

		if (section.node("type").getString("H2").equalsIgnoreCase("H2"))
			return getFileDatabase(instance, table, type, serializers);

		String address = section.node("mysql", "address").getString("localhost");
		String password = section.node("mysql", "password").getString("1234");
		String name = section.node("mysql", "name").getString("username");
		String user = section.node("mysql", "user").getString("root");
		Database<T> database = null;

		try {
			database = new MySQLDatabase<>(address, name, table, user, password, type, serializers);
			instance.debugMessage("MySQL connection " + address + " was a success!");
			databases.put(type, database);
			return database;
		} catch (SQLException exception) {
			instance.consoleMessage("&cMySQL connection failed!");
			instance.consoleMessage("Address: " + address + " with user: " + user);
			instance.consoleMessage("Reason: " + exception.getMessage());
		} finally {
			if (database == null) {
				instance.consoleMessage("Attempting to use H2 instead...");
				database = getFileDatabase(instance, table, type, serializers);
			}
		}
		return database;
	}

	@SuppressWarnings("unchecked")
	protected <T> Database<T> getFileDatabase(SourBungeePlugin instance, String table, Class<T> type, Map<Type, Serializer<?>> serializers) {
		if (databases.containsKey(type))
			return (H2Database<T>) databases.get(type);
		Database<T> database = null;
		try {
			database = new H2Database<>(instance, table, type, serializers);
			instance.debugMessage("Using H2 database for " + type.getSimpleName() + " data");
			databases.put(type, database);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return database;
	}

	public boolean hasListener() {
		return listener;
	}

	public void afterInitialize() {}

}
