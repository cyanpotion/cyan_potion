# Quick Start Guide

**Feature**: 混合架构游戏引擎  
**预计时间**: 30分钟  
**日期**: 2026-03-22

---

## 目标

在本指南结束时，你将：
1. 完成引擎开发环境配置
2. 运行第一个示例程序
3. 理解引擎基本架构
4. 创建简单的2D游戏场景

---

## 前置要求

### 系统要求

- **操作系统**: Windows 10+ 或 Linux (Ubuntu 20.04+)
- **Java**: JDK 17 或 JDK 21 (LTS版本)
- **Maven**: 3.8+
- **GPU**: 支持OpenGL 3.3+

### 验证环境

```bash
# 验证Java版本
java -version
# 期望输出: openjdk version "17" 或 "21"

# 验证Maven
mvn -version
# 期望输出: Apache Maven 3.8.x
```

---

## 步骤 1: 获取代码 (5分钟)

### 克隆仓库

```bash
git clone <repository-url>
cd xenoamess-s-civilization
```

### 检出功能分支

```bash
git checkout 001-hybrid-game-engine
```

### 目录结构说明

```
xenoamess-s-civilization/
├── src/parent/          # 父POM，统一定义依赖版本
├── src/base/            # 引擎核心模块
├── src/coordinate/      # 坐标/物理模块
├── src/rpg_module/      # RPG游戏模块
├── src/demo/            # 示例游戏
├── src/xenoamess-s-civilization/  # 主游戏
├── specs/               # 设计文档
│   └── 001-hybrid-game-engine/
└── pom.xml              # 根POM
```

---

## 步骤 2: 构建引擎 (10分钟)

### 构建全部模块

```bash
# 在仓库根目录执行
mvnw clean install -DskipTests
```

### 预期输出

```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] project ............................ SUCCESS [  1.2 s]
[INFO] parent ............................. SUCCESS [  0.5 s]
[INFO] base ............................... SUCCESS [ 15.3 s]
[INFO] coordinate ......................... SUCCESS [  5.2 s]
[INFO] rpg_module ......................... SUCCESS [  8.7 s]
[INFO] xenoamess-s-civilization ........... SUCCESS [ 10.1 s]
[INFO] demo ............................... SUCCESS [  3.2 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 常见问题

**问题**: `UnsatisfiedLinkError: Failed to locate library: lwjgl.dll`

**解决**: Maven会自动下载native库，如果失败手动清理重新下载：
```bash
rm -rf ~/.m2/repository/org/lwjgl
mvnw clean install
```

**问题**: OpenGL版本不支持

**解决**: 更新显卡驱动，或使用Mesa软件渲染（仅用于测试）：
```bash
export LIBGL_ALWAYS_SOFTWARE=1  # Linux
set LIBGL_ALWAYS_SOFTWARE=1      # Windows CMD
```

---

## 步骤 3: 运行示例游戏 (5分钟)

### 运行演示

```bash
cd src/demo
mvnw exec:java -Dexec.mainClass="com.xenoamess.cyan_potion.demo.DemoGame"
```

### 或使用IDE

1. 打开项目（支持IntelliJ IDEA或Eclipse）
2. 导入Maven项目
3. 运行 `DemoGame.main()`

### 预期结果

- 打开1280x720窗口
- 显示标题 "Cyan Potion Demo"
- 看到一个玩家角色在屏幕中央
- 可以使用方向键移动角色
- 按ESC退出

---

## 步骤 4: 创建你的第一个游戏 (10分钟)

### 4.1 创建项目结构

```bash
mkdir -p my-game/src/main/java/com/example
cd my-game
```

### 4.2 创建POM文件

`pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-game</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.xenoamess.cyan_potion</groupId>
            <artifactId>base</artifactId>
            <version>0.167.3-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4.3 创建主类

`src/main/java/com/example/MyGame.java`:
```java
package com.example;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameManagerConfig;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardKeyEnum;

public class MyGame {
    public static void main(String[] args) {
        // 配置引擎
        GameManagerConfig config = new GameManagerConfig();
        config.setTitle("My First Game");
        config.setWindowWidth(800);
        config.setWindowHeight(600);
        
        // 创建并启动引擎
        GameManager gameManager = new GameManager(config);
        gameManager.setCurrentScene(new MyScene(gameManager));
        gameManager.start();
    }
}

class MyScene extends AbstractScene {
    
    private float playerX = 400;
    private float playerY = 300;
    
    public MyScene(GameManager gameManager) {
        super(gameManager);
    }
    
    @Override
    public void init() {
        // 初始化场景
    }
    
    @Override
    public void update() {
        // 玩家移动 (200像素/秒)
        float speed = 200 * getGameManager().getGameWindow().getDeltaTime();
        
        if (getGameManager().getKeymap().get(KeyboardKeyEnum.KEY_LEFT).isPressed()) {
            playerX -= speed;
        }
        if (getGameManager().getKeymap().get(KeyboardKeyEnum.KEY_RIGHT).isPressed()) {
            playerX += speed;
        }
        if (getGameManager().getKeymap().get(KeyboardKeyEnum.KEY_UP).isPressed()) {
            playerY -= speed;
        }
        if (getGameManager().getKeymap().get(KeyboardKeyEnum.KEY_DOWN).isPressed()) {
            playerY += speed;
        }
        
        // 按ESC退出
        if (getGameManager().getKeymap().get(KeyboardKeyEnum.KEY_ESCAPE).isPressed()) {
            getGameManager().getGameWindow().setCloseRequested(true);
        }
    }
    
    @Override
    public void render() {
        // 清屏为黑色
        getGameManager().getGameWindow().clear((float)0.1, (float)0.1, (float)0.1);
        
        // 绘制玩家矩形
        getGameManager().getDrawer().drawRect(
            playerX - 25, playerY - 25, 50, 50,
            0.0f, 1.0f, 0.0f, 1.0f  // 绿色
        );
    }
}
```

### 4.4 运行游戏

```bash
mvnw compile exec:java -Dexec.mainClass="com.example.MyGame"
```

### 预期结果

- 800x600窗口
- 黑色背景
- 绿色矩形玩家角色
- 方向键控制移动
- ESC退出

---

## 步骤 5: 添加资源 (可选，5分钟)

### 5.1 准备资源

创建目录:
```bash
mkdir -p src/main/resources/textures
```

放置一张PNG图片到 `src/main/resources/textures/player.png`

### 5.2 加载并显示纹理

修改 `MyScene.java`:

```java
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;

class MyScene extends AbstractScene {
    
    private Picture playerPicture;
    
    @Override
    public void init() {
        // 加载纹理
        ResourceInfo<Picture> resourceInfo = new ResourceInfo<>("textures/player.png");
        playerPicture = new Picture(getGameManager().getResourceManager(), resourceInfo);
        
        playerX = 400;
        playerY = 300;
    }
    
    @Override
    public void render() {
        getGameManager().getGameWindow().clear((float)0.1, (float)0.1, (float)0.1);
        
        // 使用纹理绘制
        if (playerPicture != null) {
            playerPicture.draw(getGameManager().getGameWindow(),
                playerX - 32, playerY - 32,  // 位置
                64, 64,                       // 大小
                0, 0,                         // 纹理坐标
                1, 1,                         // 纹理大小
                0, 0, 0, 0,                   // 旋转和缩放
                1, 1, 1, 1);                  // 颜色
        }
    }
}
```

---

## 下一步

### 学习路径

1. **深入了解引擎架构**
   - 阅读 `data-model.md` 了解引擎设计
   - 探索 `src/base` 模块的源代码

2. **添加音频**
   - 使用 `AudioManager` 播放音效和音乐

3. **输入映射**
   - 使用 `Keymap` 处理键盘输入
   - 支持手柄输入 (`GamepadInputManager`)

4. **场景管理**
   - 创建多个场景（菜单、游戏、暂停）
   - 实现场景切换

5. **UI系统**
   - 使用引擎UI组件创建按钮、文本
   - 探索 `game_window_components` 包

### 参考文档

| 文档 | 路径 |
|------|------|
| API参考 | `docs/api/README.md` |
| 设计规范 | `specs/001-hybrid-game-engine/spec.md` |
| 数据模型 | `specs/001-hybrid-game-engine/data-model.md` |
| 技术调研 | `specs/001-hybrid-game-engine/research.md` |

### 示例代码

参考 `src/demo/src/main/java/` 中的完整示例

---

## 故障排除

### 游戏无法启动

**检查清单**:
- [ ] Java版本正确 (17或21)
- [ ] Maven依赖下载完整
- [ ] 显卡驱动最新

### 性能问题

**诊断**:
```java
// 在update中打印FPS
@Override
public void update() {
    System.out.println("FPS: " + (1.0f / getGameManager().getGameWindow().getDeltaTime()));
}
```

**优化建议**:
- 减少每帧绘制的精灵数量
- 使用纹理图集减少Draw Call
- 启用VSync防止过度渲染

---

## 恭喜！

你已经完成了第一个Cyan Potion引擎游戏的创建！🎉

现在你可以：
- 探索更多引擎功能
- 阅读完整API文档
- 开始开发你的游戏

遇到问题？查看项目文档或提交Issue获取帮助。
