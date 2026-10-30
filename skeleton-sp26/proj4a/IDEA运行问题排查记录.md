# Proj4A：IDEA 运行问题排查记录

## 一、编译报错：类重复 browser.NgordnetServer

### 现象

运行 `main.Main` 时出现：

```text
java: 类重复: browser.NgordnetServer
```

### 本次排查发现的原因

下面两个项目各自包含同名类：

```text
skeleton-sp26/
├── proj4a/src/browser/NgordnetServer.java
└── proj4cd/src/browser/NgordnetServer.java
```

两个文件都声明了 `package browser` 和 `public class NgordnetServer`，所以全限定类名都是 `browser.NgordnetServer`。

它们位于独立模块中，本身不意味着一定编译失败。但当时的 IDEA 配置存在循环依赖：

```text
proj4a → proj4cd → proj4a
```

这使两个模块进入同一编译循环，重复类发生冲突。

### 处理方式

移除两个模块之间不必要的依赖，保留各自的源码和公共依赖库：

- `proj4a/proj4a.iml`：移除对 `proj4cd` 的模块依赖。
- `proj4cd/proj4cd.iml`：移除对 `proj4a` 的模块依赖。

也可以在 IDEA 的 **File → Project Structure → Modules → Dependencies** 中检查并移除上述依赖。

不需要删除或重命名任一 `NgordnetServer.java`。

### 当时的验证结果

移除循环依赖后，两个模块的生产源码均通过了独立的命令行 `javac` 编译。此结果不等于已经完成所有单元测试或 IDEA 运行验证。

---

## 二、服务启动后，访问页面返回 404

### 本次排查证据

运行中的服务表现为：

- `/ngordnet_4a.html` 返回 **404**。
- `/historytext?words=cat&startYear=2000&endYear=2001` 返回 **200** 和演示文本。
- JVM 的 `user.dir` 是 `D:\dev\code\java\cs61-sp26\skeleton-sp26`。
- 页面实际存在于 `proj4a/static/ngordnet_4a.html`。

这说明后端已启动，问题在静态页面的路径解析，而不是服务完全没有运行。

### 原因：运行工作目录不正确

`src/browser/NgordnetServer.java` 中使用了相对路径：

```java
staticFiles.externalLocation("static");
```

`static` 相对于程序的 **Working directory（工作目录）** 查找，不是相对于 Java 源文件位置查找。

| 工作目录 | 实际查找的页面路径 | 本项目情况 |
|---|---|---|
| `skeleton-sp26` | `skeleton-sp26/static/ngordnet_4a.html` | 不存在 |
| `skeleton-sp26/proj4a` | `skeleton-sp26/proj4a/static/ngordnet_4a.html` | 存在 |

`Main.java` 中的 `./data/` 也依赖相同的工作目录设置。

### 推荐解决步骤

1. 打开 **Run → Edit Configurations…**。
2. 选择运行 `main.Main` 的 **Main** 配置。
3. 确认使用的模块是 **proj4a**。
4. 将 **Working directory** 设置为：

   ```text
   $PROJECT_DIR$/proj4a
   ```

   这里假定 IDEA 当前打开的项目根目录是 `skeleton-sp26`。也可以填写绝对路径：

   ```text
   D:\dev\code\java\cs61-sp26\skeleton-sp26\proj4a
   ```

5. 如果看不到 Working directory，在 **Modify options** 中启用该项。
6. 点击 **Apply / OK**，停止旧进程，再重新运行 Main。
7. 打开完整页面地址：

   ```text
   http://localhost:4567/ngordnet_4a.html
   ```

> 只访问 `http://localhost:4567/` 仍可能返回 404，因为当前没有配置根路径首页。

### 验证标准

重启后确认 `/ngordnet_4a.html` 返回 200，并能显示页面。上述工作目录修正是本次排查给出的解决步骤；本记录不声称已经替用户完成 IDEA 配置修改或重启后验证。

---

## 三、为什么不能把整个 proj4a 标成 Sources Root 来解决？

**Sources Root 和 Working directory 是不同的概念。**

| 概念 | 作用 | 当前推荐位置 |
|---|---|---|
| Project 根目录 | IDEA 打开的整体项目目录 | `skeleton-sp26` |
| Module 内容根目录 | 一个模块的内容范围 | `proj4a` |
| Sources Root | Java 源码包路径的起点 | `proj4a/src` |
| Test Sources Root | 测试源码包路径的起点 | `proj4a/tests` |
| Working directory | 程序运行时相对文件路径的起点 | `proj4a` |

例如：

```text
proj4a/src/browser/NgordnetServer.java
```

因为源码根目录是 `proj4a/src`，它对应：

```java
package browser;
```

如果误把整个 `proj4a` 标为 Sources Root，IDEA 可能会期望包路径包含 `src`，造成包路径识别问题；而且这项设置不会把运行工作目录自动改成 `proj4a`。

**结论：保留 `proj4a/src` 为 Sources Root，只调整 Main 运行配置的 Working directory。**

### 能否单独打开 proj4a？

可以通过 IDEA 的 **File → Open** 单独打开 `proj4a`。此时默认运行工作目录通常会是它，但仍应检查运行配置，并可能需要重新配置 JDK 和依赖库。

如果继续使用现有多模块项目，直接调整 Main 的 Working directory 最简单；保存后，该运行配置会持续使用此设置。

---

## 四、以后遇到同类问题的检查顺序

1. **编译报类重复**：检查同名类、源码根目录和模块循环依赖，不要直接删除课程自带源码。
2. **连接被拒绝**：先检查服务是否启动、端口是否正确以及启动日志。
3. **接口正常，页面 404**：检查页面 URL、静态文件位置和 Working directory。
4. **读取数据文件失败**：检查 `./data/` 等相对路径是否基于正确的工作目录。
5. **修改运行配置后**：停止旧进程再启动，不是只刷新浏览器。

一句话记忆：**Sources Root 管源码和包路径，Working directory 管运行时的相对文件路径。**
