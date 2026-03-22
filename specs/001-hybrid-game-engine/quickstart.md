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
├── engine/              # 引擎核心代码
│   ├── src/
│   └── pom.xml
├── engine-3d/           # 3D扩展（可选）
├── engine-groovy/       # Groovy脚本支持（可选）
├── demo-game/           # 示例游戏
├── specs/               # 设计文档
│   └── 001-hybrid-game-engine/
└── pom.xml              # 根POM
```

---

## 步骤 2: 构建引擎 (10分钟)

### 构建全部模块

```bash
# 在仓库根目录执行
mvn clean install -DskipTests
```

### 预期输出

```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] cyan_potion ........................ SUCCESS [  1.2 s]
[INFO] engine ............................. SUCCESS [ 15.3 s]
[INFO] engine-3d .......................... SUCCESS [  5.2 s]
[INFO] engine-groovy ...................... SUCCESS [  3.1 s]
[INFO] demo-game .......................... SUCCESS [  8.7 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 常见问题

**问题**: `UnsatisfiedLinkError: Failed to locate library: lwjgl.dll`

**解决**: Maven会自动下载native库，如果失败手动清理重新下载：
```bash
rm -rf ~/.m2/repository/org/lwjgl
mvn clean install
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
cd demo-game
mvn exec:java -Dexec.mainClass="com.xenoamess.demo.DemoGame"
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
        <cyan.potion.version>0.1.0-SNAPSHOT</cyan.potion.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.xenoamess</groupId>
            <artifactId>engine</artifactId>
            <version>${cyan.potion.version}</version>
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

import com.xenoamess.cyan_potion.core.GameEngine;
import com.xenoamess.cyan_potion.core.EngineConfig;
import com.xenoamess.cyan_potion.core.scene.Scene;
import com.xenoamess.cyan_potion.core.scene.GameObject;
import com.xenoamess.cyan_potion.core.graphics.Renderer2D;
import com.xenoamess.cyan_potion.core.input.KeyboardKey;

public class MyGame {
    public static void main(String[] args) {
        // 配置引擎
        EngineConfig config = new EngineConfig.Builder()
            .setTitle("My First Game")
            .setWidth(800)
            .setHeight(600)
            .setTargetFPS(60)
            .build();

        // 创建并启动引擎
        GameEngine engine = new GameEngine(config);
        engine.setScene(new MyScene());
        engine.run();
    }
}

class MyScene extends Scene {
    
    private GameObject player;
    
    @Override
    protected void create() {
        // 创建玩家对象
        player = createGameObject("Player");
        player.getTransform().setPosition(400, 300);
        
        // 这里可以添加更多组件
    }
    
    @Override
    protected void update(float deltaTime) {
        // 玩家移动
        float speed = 200 * deltaTime; // 200像素/秒
        
        if (getInputManager().isKeyDown(KeyboardKey.LEFT)) {
            player.getTransform().translate(-speed, 0);
        }
        if (getInputManager().isKeyDown(KeyboardKey.RIGHT)) {
            player.getTransform().translate(speed, 0);
        }
        if (getInputManager().isKeyDown(KeyboardKey.UP)) {
            player.getTransform().translate(0, -speed);
        }
        if (getInputManager().isKeyDown(KeyboardKey.DOWN)) {
            player.getTransform().translate(0, speed);
        }
        
        // 按ESC退出
        if (getInputManager().isKeyPressed(KeyboardKey.ESCAPE)) {
            getEngine().stop();
        }
    }
    
    @Override
    protected void render(Renderer2D renderer, float alpha) {
        // 清屏为黑色
        renderer.clear(new Color(0.1f, 0.1f, 0.1f, 1.0f));
        
        // 绘制一个简单的矩形代表玩家
        // 注意：真实项目中应使用Texture
        Vector2f pos = player.getTransform().getPosition();
        renderer.drawRect(pos.x - 25, pos.y - 25, 50, 50, Color.GREEN);
    }
}
```

### 4.4 运行游戏

```bash
mvn compile exec:java -Dexec.mainClass="com.example.MyGame"
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
import com.xenoamess.cyan_potion.core.resource.Texture;

class MyScene extends Scene {
    
    private Texture playerTexture;
    
    @Override
    protected void create() {
        // 加载纹理
        playerTexture = getResourceManager().loadTexture("textures/player.png");
        
        player = createGameObject("Player");
        player.getTransform().setPosition(400, 300);
    }
    
    @Override
    protected void render(Renderer2D renderer, float alpha) {
        renderer.clear(Color.BLACK);
        
        // 使用纹理绘制
        Vector2f pos = player.getTransform().getPosition();
        renderer.draw(
            playerTexture,
            pos.x - 32, pos.y - 32,  // 位置
            64, 64                    // 大小
        );
    }
    
    @Override
    protected void dispose() {
        // 资源由ResourceManager管理，通常无需手动释放
        // 但可以请求卸载
        getResourceManager().unload("textures/player.png");
    }
}
```

---

## 下一步

### 学习路径

1. **深入了解ECS架构**
   - 阅读 `data-model.md` 了解实体-组件-系统
   - 创建自定义组件

2. **添加音频**
   - 使用 `AudioManager` 播放音效和音乐

3. **输入映射**
   - 创建 `InputAction` 替代直接按键检查
   - 支持手柄输入

4. **场景管理**
   - 创建多个场景（菜单、游戏、暂停）
   - 实现场景切换

5. **UI系统**
   - 使用引擎UI组件创建按钮、文本

### 参考文档

| 文档 | 路径 |
|------|------|
| API参考 | `specs/001-hybrid-game-engine/contracts/public-api.md` |
| 事件系统 | `specs/001-hybrid-game-engine/contracts/event-protocol.md` |
| 数据模型 | `specs/001-hybrid-game-engine/data-model.md` |
| 技术调研 | `specs/001-hybrid-game-engine/research.md` |

### 示例代码

参考 `demo-game/src/main/java/` 中的完整示例：
- `DemoGame.java` - 主入口
- `scenes/MainScene.java` - 场景实现
- `entities/Player.java` - 玩家实体
- `systems/PlayerController.java` - 控制系统

---

## 故障排除

### 游戏无法启动

**检查清单**:
- [ ] Java版本正确 (17或21)
- [ ] Maven依赖下载完整
- [ ] 显卡驱动最新
- [ ] 其他程序未占用所需端口（如果有网络功能）

### 性能问题

**诊断**:
```java
// 在update中打印FPS
@Override
protected void update(float deltaTime) {
    System.out.println("FPS: " + (1.0f / deltaTime));
}
```

**优化建议**:
- 减少每帧绘制的精灵数量
- 使用纹理图集减少Draw Call
- 启用VSync防止过度渲染

### 调试模式

```java
// 启用调试输出
EngineConfig config = new EngineConfig.Builder()
    .setDebugMode(true)  // 启用OpenGL调试输出
    .build();
```

---

## 恭喜！

你已经完成了第一个Cyan Potion引擎游戏的创建！🎉

现在你可以：
- 探索更多引擎功能
- 阅读完整API文档
- 开始开发你的游戏

遇到问题？查看项目文档或提交Issue获取帮助。
