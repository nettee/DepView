# DepView

Analyze calling dependencies in Java projects, and visualize them in a dependency graph.

[English](./README.md) | [简体中文](./README_zh.md)

If you want to know more about this tool, you can read [my blog post](https://nettee.io/blog/depview-java-code-dependencies-view/).

![Dependency graph](img/depgraph.png)

Try the interactive demo: [nettee.io/depview](https://nettee.io/depview/)

## ✨ Features

- Visualize calling dependencies in Java projects
- Help understand the structure of new projects, especially those with little documentation
- Help adjust package structure according to connections between classes

## 🚀 Quick Start

This project targets **Java 8**.

Write a configuration file for the project that you want to analyze. Follow the examples in the `conf-example/` directory.

Build and run DepView:

```bash
export JAVA_HOME=/path/to/jdk8
export PATH="$JAVA_HOME/bin:$PATH"

mvn clean package
java -jar target/depview-0.2-SNAPSHOT.jar <CONF_FILE>
```

The generated dependency output is in `output/<PROJECT_NAME>/`. To view the dependency graph:

```bash
python3 -m http.server 8000 --bind 127.0.0.1 -d output/<PROJECT_NAME>
```

Open [http://localhost:8000](http://localhost:8000) in your browser.

### Example on macOS

If Java 8 is installed but not the default Java on macOS:

```bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

mvn clean package
java -jar target/depview-0.2-SNAPSHOT.jar conf-example/depview-mac.conf
python3 -m http.server 8000 --bind 127.0.0.1 -d output/DepView
```
