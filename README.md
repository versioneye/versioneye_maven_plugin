# VersionEye Maven Plugin

This is the maven plugin for [VersionEye](http://www.VersionEye.com). With this plugin you can create or update a project at VersionEye. 
VersionEye is a platform for Continuous Updating. It will help you to keep your project up-to-date and notify you automatically about out-daten dependencies in your project.

## Intro

You can add the plugin to your project by adding this snippet to your pom.xml file.  

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

This is how you convert your pom.xml to a maven.json 

```
mvn versioneye:json 
```
This will take all your direct dependencies and write them into "/target/pom.json". This is just for fun. You don't really need it, but we thought it's fun to write a small pom.xml to pom.json converter :-)  


