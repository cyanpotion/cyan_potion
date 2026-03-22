# Cyan Potion Engine API Documentation

**Feature**: 混合架构游戏引擎  
**版本**: 0.167.3-SNAPSHOT  
**日期**: 2026-03-22

---

## 概述

Cyan Potion 是一个基于 Java 和 LWJGL3 的跨平台游戏引擎，支持 Windows 和 Linux 双平台。

---

## 核心模块

### 1. 基础模块 (`base`)

#### GameManager

引擎主管理器，协调所有子系统。

```java
public class GameManager {
    // 创建引擎实例
    public GameManager(GameManagerConfig config);
    
    // 启动/停止
    public void start();
    public void stop();
    
    // 获取子系统
    public GameWindow getGameWindow();
    public ResourceManager getResourceManager();
    public AudioManager getAudioManager();
    public Keymap getKeymap();
    public Picture getDrawer();
}
```

#### GameWindow

游戏窗口管理，处理渲染和窗口事件。

```java
public class GameWindow {
    // 配置
    public String getTitle();
    public int getWidth();
    public int getHeight();
    
    // 渲染
    public void clear(float r, float g, float b);
    public void swapBuffers();
    
    // 时间管理
    public float getDeltaTime();
    
    // 状态
    public boolean shouldClose();
    public boolean isCloseRequested();
    public void setCloseRequested(boolean close);
}
```

#### ResourceManager

资源加载与管理。

```java
public class ResourceManager {
    // 加载资源
    public <T extends AbstractResource> T load(ResourceInfo<T> resourceInfo, boolean autoLoad);
    
    // 卸载资源
    public void unload(ResourceInfo<?> resourceInfo);
    
    // 清理
    public void close();
}
```

#### AudioManager

音频管理。

```java
public class AudioManager {
    // 播放音效
    public void playWavFile(String filePath, float volume);
    public void playWavFile(String filePath, float volume, float pitch);
    
    // 音频源管理
    public Source createSource();
    public void closeSource(Source source);
}
```

---

### 2. 输入系统

#### Keymap

键盘输入映射。

```java
public class Keymap {
    // 检查按键状态
    public boolean isKeyDown(int key);
    public boolean isKeyPressed(int key);
    public boolean isKeyReleased(int key);
    
    // 获取按键对象
    public Key get(KeyboardKeyEnum key);
}
```

#### KeyboardKeyEnum

键盘按键枚举。

```java
public enum KeyboardKeyEnum {
    KEY_ESCAPE,
    KEY_SPACE,
    KEY_ENTER,
    KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN,
    KEY_A, KEY_B, KEY_C, /* ... */
    // 完整按键列表参考源码
}
```

---

### 3. 渲染系统

#### Picture

2D图像渲染。

```java
public class Picture extends AbstractResource {
    // 绘制
    public void draw(GameWindow gameWindow,
                     float posX, float posY,
                     float scaleX, float scaleY,
                     float textureStartX, float textureStartY,
                     float textureWidth, float textureHeight,
                     float rotateRadius, float rotateCenterX, float rotateCenterY,
                     float colorR, float colorG, float colorB, float colorA);
}
```

#### Font

文本渲染。

```java
public class Font extends AbstractResource {
    // 绘制文本
    public void drawText(GameWindow gameWindow,
                        String text,
                        float posX, float posY,
                        float scaleX, float scaleY,
                        float colorR, float colorG, float colorB, float colorA);
}
```

---

### 4. 场景系统

#### AbstractScene

场景基类。

```java
public abstract class AbstractScene {
    // 生命周期
    public abstract void init();
    public abstract void update();
    public abstract void render();
    
    // 状态管理
    public boolean isInitialized();
    public boolean isActive();
    public void setActive(boolean active);
}
```

---

## 工具类

### FilePathUtil

跨平台文件路径处理。

```java
public class FilePathUtil {
    // 规范化路径
    public static String normalize(String path);
    
    // 获取配置目录
    public static String getConfigDir(String gameName);
    
    // 获取数据目录
    public static String getDataDir(String gameName);
}
```

### 性能基准

引擎性能基准测试在 `src/base/src/test/java/com/xenoamess/cyan_potion/base/performance/PerformanceTest.java`

| 测试项目 | 目标 | 实际 |
|---------|------|------|
| 数学运算 (1M ops) | < 1s | < 100ms |
| 数组访问 (10M) | < 500ms | < 50ms |
| 内存分配 | < 2KB | < 1KB |
| 内存稳定性 | < 10MB波动 | < 5MB |

---

## 常量与配置

### 常用常量

```java
// src/base/src/main/java/com/xenoamess/cyan_potion/base/GameManager.java
public static final int DEFAULT_WINDOW_WIDTH = 1280;
public static final int DEFAULT_WINDOW_HEIGHT = 720;
public static final int DEFAULT_TARGET_FPS = 60;
```

### 版本信息

```
引擎版本: 0.167.3-SNAPSHOT
LWJGL: 3.3.6
OpenGL: 3.3+
Java: 17 或 21
```

---

## 示例代码

### 完整游戏示例

```java
package com.example;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameManagerConfig;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardKeyEnum;

public class MinimalGame {
    public static void main(String[] args) {
        GameManagerConfig config = new GameManagerConfig();
        config.setTitle("Minimal Game");
        config.setWindowWidth(800);
        config.setWindowHeight(600);
        
        GameManager gameManager = new GameManager(config);
        gameManager.setCurrentScene(new MinimalScene(gameManager));
        gameManager.start();
    }
}

class MinimalScene extends AbstractScene {
    
    public MinimalScene(GameManager gameManager) {
        super(gameManager);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void update() {
        if (getGameManager().getKeymap()
                .get(KeyboardKeyEnum.KEY_ESCAPE).isPressed()) {
            getGameManager().getGameWindow().setCloseRequested(true);
        }
    }
    
    @Override
    public void render() {
        getGameManager().getGameWindow().clear(0, 0, 0);
    }
}
```

---

## 参考链接

- [GitHub Repository](https://github.com/xenoamess/cyan_potion)
- [Issue Tracker](https://github.com/xenoamess/cyan_potion/issues)
- [Quick Start Guide](../../quickstart.md)
