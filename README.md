# Jingle Example Plugin

An example project to act as a template in making Jingle Plugins.

Things you should probably change:
- Java package name
- Jingle dependency in build.gradle (could be a newer version available)
- gradle.properties
  - archives_base_name and maven_group
- src/resources/jingle.plugin.json
  - name, id, initializer
- All the default initialized stuff and the GUI

## Developing

Jingle GUIs are made with the IntelliJ IDEA form designer, if you intend on changing GUI portions of the code, IntelliJ IDEA must be configured in a certain way to ensure the GUI form works properly:
- `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` -> `Build and run using: IntelliJ Idea`
- `Settings` -> `Editor` -> `GUI Designer` -> `Generate GUI into: Java source code`