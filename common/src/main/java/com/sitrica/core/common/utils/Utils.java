package com.sitrica.core.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sitrica.core.common.SourPlugin;

public class Utils {

	public static boolean isUUID(String uuid) {
		try {
			UUID.fromString(uuid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static UUID getUniqueId(String uuid) {
		try {
			return UUID.fromString(uuid);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean checkForMatch(List<String> matchers, String input) {
		Pattern pattern;
		Matcher matcher;
		int matches = 0;
		for (String match : matchers) {
			pattern = Pattern.compile(match);
			matcher = pattern.matcher(input);
			for (; matcher.find(); matches++);
		}
		return matches > 0;
	}

	public static void loadClasses(SourPlugin instance, String basePackage, String... subPackages) {
		for (int i = 0; i < subPackages.length; i++) {
			subPackages[i] = subPackages[i].replace('.', '/') + "/";
		}
		JarFile jar;
		try {
			jar = new JarFile(instance.getJar());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		basePackage = basePackage.replace('.', '/') + "/";
		try {
			for (Enumeration<JarEntry> jarEntry = jar.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName();
				if (name.startsWith(basePackage) && name.endsWith(".class")) {
					for (String sub : subPackages) {
						if (name.startsWith(sub, basePackage.length())) {
							String clazz = name.replace("/", ".").substring(0, name.length() - 6);
							Class.forName(clazz, true, instance.getClass().getClassLoader());
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				jar.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static <T> List<Class<T>> getClassesOf(SourPlugin instance, Class<T> type, String... basePackages) {
		return Arrays.stream(basePackages)
				.map(basePackage -> getClassesOf(instance, basePackage, type))
				.flatMap(list -> list.stream())
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> getClassesOf(SourPlugin instance, String basePackage, Class<T> type) {
		JarFile jar;
		try {
			jar = new JarFile(instance.getJar());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		basePackage = basePackage.replace('.', '/') + "/";
		List<Class<T>> classes = new ArrayList<>();
		try {
			for (Enumeration<JarEntry> jarEntry = jar.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName();
				if (name.startsWith(basePackage) && name.endsWith(".class")) {
					String className = name.replace("/", ".").substring(0, name.length() - 6);
					Class<?> clazz = null;
					try {
						clazz = Class.forName(className, true, instance.getClass().getClassLoader());
					} catch (ExceptionInInitializerError | ClassNotFoundException e) {
						instance.consoleMessage("&cClass " + className + " was formatted incorrectly, report this to the Sitrica developers.");
						e.printStackTrace();
					}
					if (clazz == null)
						continue;
					if (type.isAssignableFrom(clazz))
						classes.add((Class<T>) clazz);
				}
			}
		} finally {
			try {
				jar.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return classes;
	}

	/**
	 * Tests whether a given class exists in the classpath.
	 * 
	 * @author Skript team.
	 * @param className The {@link Class#getCanonicalName() canonical name} of the class
	 * @return Whether the given class exists.
	 */
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Tests whether a method exists in the given class.
	 * 
	 * @author Skript team.
	 * @param c The class
	 * @param methodName The name of the method
	 * @param parameterTypes The parameter types of the method
	 * @return Whether the given method exists.
	 */
	public static boolean methodExists(Class<?> c, String methodName, Class<?>... parameterTypes) {
		try {
			c.getDeclaredMethod(methodName, parameterTypes);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

	public static boolean methodExists(Class<?> c, String methodName) {
		try {
			c.getDeclaredMethod(methodName);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

}
