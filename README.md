# Sour Core
The main core of Sitrica resources.

## Gradle
Latest version can be found at https://github.com/Sitrica/Sour/packages

In your `build.gradle` add: 
```groovy
repositories {
	maven {
		url 'https://maven.pkg.github.com/Sitrica/Sour/'
		credentials {
			username = "<INSERT USERNAME>"
			password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_PACKAGES_KEY")
		}
	}
}

dependencies {
	compile (group: 'com.sitrica.core', name: 'bukkit OR bungee', version: 'INSERT VERSION')
}
```
Getting a Github token:

1.) Go into your account settings on Github and create a personal token with the read:packages scope checked.

2.) Generate that key, and now go add a System Environment Variable named GITHUB_PACKAGES_KEY
or set the gradle property "gpr.key" to your key.

3.) Restart system or if using Chocolatey type `refreshenv`

Note: you can just directly put your token as the password, but we highly discourage that.

## Compiling from source
```sh
git clone https://github.com/Sitrica/Sour
cd Sour
gradlew build
```

The output jars will be found in the `build/libs` directory of each respective folder

## License
SourCore is licensed under the Apache License 2.0. Please see [`LICENSE.txt`](https://github.com/Sitrica/Sour/blob/master/LICENSE) for more info.