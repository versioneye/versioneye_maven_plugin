[![Dependency Status](http://www.versioneye.com/user/projects/51e2af93cbe2eb000203df22/badge.png)](http://www.versioneye.com/user/projects/51e2af93cbe2eb000203df22)

# VersionEye Maven Plugin

This is the maven plugin for [VersionEye](http://www.VersionEye.com). With this plugin you can create or update a project at VersionEye. 
VersionEye is a platform for Continuous Updating. It will help you to keep your project up-to-date and notify you automatically about out-daten dependencies in your project. You can check it out here: [www.VersionEye.com](http://www.versioneye.com). 

## Install

Currently this plugin is not on any maven repository server. That's why you have to checkout the code: 

```
git clone https://github.com/versioneye/versioneye_maven_plugin.git
```

Switch to the root directory of the project: 

```
cd versioneye_maven_plugin
```

And install it in your local maven repository: 

```
mvn install 
```

Now the plugin is installed on your locale machine! 

Now switch to your project! The project where you want to use this plugin. You can add the plugin to your project by adding this snippet to your pom.xml file.  

```
<build>
  <plugins>
    <plugin>
      <groupId>versioneye</groupId>
      <artifactId>versioneye-maven-plugin</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </plugin>
  </plugin>
</build>
```

That's it. The plugin is now installed and added to your project

## Getting Started

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

This will list all your direct and recursive dependencies and tell you how many dependencies you have in your project all together.

This is how you convert your pom.xml to a pom.json 

```
mvn versioneye:json 
```
This will take all your direct dependencies and write them into "/target/pom.json". This is just for fun! You don't really need it, but I thought it's fun to write a small pom.xml to pom.json converter :-)  

## API Key

This plugin can push your dependencies to the VersionEye API, create a project at VersionEye and tell you which of your dependencies are outdated. VersionEye will check your project automatically and notify you about out-dated dependencies. Some of the resources at the VersionEye API you can use without an API KEY. But for the project resource you need an API KEY. If you are [signed up](https://www.versioneye.com/signup) you can find your API KEY here: [https://www.versioneye.com/settings/api](https://www.versioneye.com/settings/api). 

If you have your API KEY, create a properties file in your project root and add your KEY like this:  

```
echo "api_key=YOUR_API_KEY" > versioneye.properties
```

The versioneye-maven-plugin will read from there the API KEY for create and update operations on the project API resource. 

Now you can create a VersionEye project based on the dependencies in your pom.xml. Just execute this: 

```
mvn versioneye:create
```

If that was successfull you will see in the output the URL to your new created VersionEye project. Just copy and paste it into you browser to check it out: Here is an example how it could look like: 

![VersionEye Dependencies](src/site/images/VersionEyeDependencies.png)

Beside that, the plugin will add a project_id and project_key to the versioneye.properties file. The project_id is the connection between your pom.xml and the VersionEye project. 

With this command here you can update an existing VersionEye project. 

```
mvn versioneye:update
``` 
That will simply update the existing VersionEye project with the dependencies from your pom.xml. 

## Feedback

If you have questions, bugs or feature requests to this project, feel free to open a ticket [here](https://github.com/versioneye/versioneye_maven_plugin/issues). Pull-Requests are welcome! 

