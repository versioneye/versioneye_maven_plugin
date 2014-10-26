- Version 2.0.1 with Maven 3.0.5 [![Dependency Status](http://www.versioneye.com/user/projects/5379ad3f14c158ccc700002d/badge.svg?style=flat)](http://www.versioneye.com/user/projects/5379ad3f14c158ccc700002d)
- Version 3.1.0 with Maven 3.2.1 [![Dependency Status](https://www.versioneye.com/user/projects/544d0ff9512592562c000003/badge.svg?style=flat)](https://www.versioneye.com/user/projects/544d0ff9512592562c000003)

[![VersionEye Dependencies](src/site/images/VersionEyeLogo.png)](https://www.versioneye.com)

# VersionEye Maven Plugin

The [maven](http://maven.apache.org/) plugin for [VersionEye](http://www.versioneye.com) helps you to create/update a project at VersionEye.
VersionEye is a Notification System for Software Libraries. It will help you to keep your projects up-to-date and automatically notify you about outdated dependencies. You can check it out here: [www.versioneye.com](http://www.versioneye.com).

Summary

 - [Install binary](#install-binary)
 - [Install from source](#install-from-source)
 - [Getting started](#getting-started)
 - [API Key](#api-key)
 - [mvn versioneye:create](#mvn-versioneyecreate)
 - [mvn versioneye:update](#mvn-versioneyeupdate)
 - [Proxy](#proxy)
 - [VersionEye Enterprise](#versioneye-enterprise)
 - [Multi-Module Projects](#multi-module-projects)
 - [Feedback](#feedback)
 - [License](#license)

## Install binary

The VersionEye Maven plugin is available on the [Maven Central Repository](http://search.maven.org).
That means Maven will find it automatically, without adding any other
repositories! And you can find the project on [bintray](https://bintray.com/versioneye/versioneye/versioneye-maven-plugin/)
as well.

Switch to the project where you want to use this plugin.
You can add the plugin to your project by adding this snippet to your
`pom.xml` file.

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
    </plugin>
  </plugins>
</build>
```

The `versioneye-maven-plugin` is tested against Maven 3.2.1.
If you are using Maven 3.0.5 or older you should use the
`versioneye-maven-plugin` version 2.0.1.

Alternatively you can add `versioneye` to the plugin group search path. You do this by adding
```
<pluginGroups>
  <pluginGroup>com.versioneye</pluginGroup>
</pluginGroups>
```

to the user's Maven settings file (`~/.m2/settings.xml`). This will allow to use the `versioneye:*`
command line goals interactively in all projects.

## Install from source

If you wanna install the plugin from source, you have to follow this steps.

```
git clone https://github.com/versioneye/versioneye_maven_plugin.git
```

Switch to the root directory of the project:

```
cd versioneye_maven_plugin
```

And install it in your local maven repository:

```
mvn clean install
```

Now the plugin is installed on your local machine!


## Getting Started

You can check out all goals like this

```
mvn versioneye:help
```

That will output all possible goals on the versioneye plugin.

Now you can check if the [VersionEye API](https://www.versioneye.com/api?version=v2) is available:

```
mvn versioneye:ping
```

That should return an output like this:

```
{"success":true,"message":"pong"}
```

Now try this:

```
mvn versioneye:list
```

That will get you a list with all your direct and recursive dependencies and it will tell you how many dependencies you have in your project altogether.

Here you can convert your `pom.xml` to a `pom.json`

```
mvn versioneye:json
```

It will take all your direct dependencies and convert them into `/target/pom.json`. This is just for fun! You don't really need it, but I thought it's fun to write a small `pom.xml` to `pom.json` converter :-)

## API Key

This plugin can push your dependencies to the VersionEye API, create a project at VersionEye and tell you which of your dependencies are outdated. VersionEye will automatically check your project and notify you about outdated dependencies. You can use some of the resources at the VersionEye API without an *API KEY*, but for the project resource you need one. If you are [signed up](https://www.versioneye.com/signup) you can find your *API KEY* here: [https://www.versioneye.com/settings/api](https://www.versioneye.com/settings/api).

![VersionEye Dependencies](src/site/images/VersionEyeApiKey.png)

Now let the versioneye-maven-plugin know what your *API KEY* is.

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
	    <apiKey>MY_SECRET_API_KEY</apiKey>
	  </configuration>
    </plugin>
  </plugins>
</build>
```

If you don't want to store the api key in the pom.xml, alternatively you can store it in a `versioneye.properties` file.

```
echo "api_key=YOUR_API_KEY" > versioneye.properties
```

If the API Key is not set directly in the pom.xml file, the versioneye-maven-plugin will try to fetch the API Key from the `versioneye.properties` file and it will look up the file in this places:

```
/src/qa/resources/versioneye.properties
```

If it can't find the file there it will look it up at this place:

```
src/main/resources/versioneye.properties
```

If you want so you can configure another place for the versioneye.properties file. Just set the path explicitly in the pom.xml on the versioneye plugin configuration:

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
	    <propertiesPath>${basedir}/versioneye.properties</propertiesPath>
	  </configuration>
    </plugin>
  </plugins>
</build>
```

If the plugin can't find the API KEY in any of this locations it will look it up at this place:

```
~/.m2/versioneye.properties
```

That means if you don't want to commit your *API KEY* to the server and share it with your team you can place the file in your *home* directory and keep it for you.

## mvn versioneye:create

If your *API KEY* is in place you can create a new project at VersionEye based on the dependencies in your `pom.xml` file with this goal:

```
mvn versioneye:create
```

This command will **not** change your local project. It just sends your dependencies to the VersionEye server and creates, based on that, a new project at [www.versioneye.com](http://www.versioneye.com). If everything went right you will see in the output the URL to your new created VersionEye project. Just copy and paste it into you browser to check it out. Here is an example how it could look like:

![VersionEye Dependencies](src/site/images/VersionEyeDependencies.png)

Besides that, the plugin will add a `project_id` to the `versioneye.properties` file. The `project_id` is the connection between your `pom.xml` and the VersionEye project. If the `versioneye.properties` file doesn't exist yet, it will be created now.

If you don't want that the versioneye maven plugin creates/updates the `versioneye.properties` file you can skip that step with this line in the plugin configuration:

```
<updatePropertiesAfterCreate>false</updatePropertiesAfterCreate>
```

If you do so, you have to add the `project_id` by hand to the plugin configuration for the next step, the `versioneye:update` goal.

## mvn versioneye:update

After you created a new project on VersionEye you can update it with the dependencies from the `pom.xml` file with this goal:

```
mvn versioneye:update
```

That will simply update the existing VersionEye project with the dependencies from your `pom.xml` file. It will **not** change your `pom.xml`. This goal usually gets executed on a Continuous Integration server after each build.

By the way. If you don't like to have a `versioneye.properties` file you can set the project_id explicitly in the pom.xml. Just like this:

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
	    <projectId>_YOUR_VERSONEYE_PROJECT_ID_</projectId>
	  </configuration>
    </plugin>
  </plugins>
</build>
```

## Proxy

If you are behind a proxy server you can configure the plugin for that like this.

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
        <proxyHost>127.0.0.1</proxyHost>
        <proxyPort>8888</proxyPort>
        <proxyUser>proxy_hopsi</proxyUser>
        <proxyPassword>dont_tell_anybody</proxyPassword>
	  </configuration>
    </plugin>
  </plugins>
</build>
```

## VersionEye Enterprise

If you are using the VersionEye Enterprise VM in your own private network you probably want to use this plugin against the VersionEye Enterprise API. In that case you can change the baseUrl with this line:


```
<baseUrl>http://versioneye.my-company.com</baseUrl>
```
The whole plugin snippet would look similar to this one.

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
        <baseUrl>http://versioneye.my-company.com</baseUrl>
	    <projectId>_YOUR_VERSONEYE_PROJECT_ID_</projectId>
	  </configuration>
    </plugin>
  </plugins>
</build>
```

## Multi-Module Projects

Assume you have a big Java Enterprise multi-module project with Maven and you want to have all modules monitored by VersionEye. I furhter assume that all modules have the same parent pom and the modules are listed in the parent pom.xml file. In that case all you have to do is configuring the VersionEye Maven Plugin once in the parent pom. 

```
<build>
  <plugins>
    <plugin>
      <groupId>com.versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>3.1.0</version>
      <configuration>
	    <apiKey>MY_SECRET_API_KEY</apiKey>
	  </configuration>
    </plugin>
  </plugins>
</build>
```

Now run this command in the parent directory:

```
mvn versioneye:create
```

This command will be executed on each module. The plugin will create for each module a new project on VersionEye. Beside that the plugin will create for each module a `versioneye.properties` file with the corresponding project_id. The file will be created/updated in the `src/main/resources` directory of each module. 

After the projects are created on VersionEye we don't need the `create` goal anymore. Now we can perform the `update` goal on each build. 

```
mvn versioneye:update
```

This will update the project on VersionEye with the current dependencies in the modules pom.xml file. Executing this command in the parent pom directory will update all modules. Ideally this goal is executed on the Continuous Integration System after each build. 

Here is a YouTube video which demonstrates how to setup a multi-module project with the VersionEye Maven Plugin. 

[![VersionEye Maven Plugin Video](src/site/images/VersionEyeMavenPlugin.png)](http://www.youtube.com/watch?v=JPVEuqGHbeU)

## Feedback

For bugs and feature requests please use the [ticket system](https://github.com/versioneye/versioneye_maven_plugin/issues). Pull Requests are welcome ;-)

For short questions contact me on [Twitter](https://twitter.com/RobertReiz).

## License

[MIT](http://choosealicense.com/licenses/mit/)
