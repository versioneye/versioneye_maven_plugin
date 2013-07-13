# VersionEye Maven Plugin

This is the maven plugin for http://www.VersionEye.com. With this plugin you can create or update a project at VersionEye. 

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
Now you can run it lie this: 

```
mvn versioneye:list
```

This will list all your direct and recursive dependencies and tell you how many dependencies you have in your project all together. 
