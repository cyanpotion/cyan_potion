# Public API Contract

**Feature**: 混合架构游戏引擎  
**Version**: 1.0.0  
**Date**: 2026-03-22

---

## 1. Overview

本文档定义游戏引擎的公共API契约，供游戏开发者使用。所有公共API遵循以下原则：

1. **向后兼容**: 1.x版本保持API兼容
2. **空安全**: 参数检查，空值返回Optional
3. **异常明确**: 运行时异常(RuntimeException)用于编程错误，受检异常用于可恢复错误
4. **文档完整**: 所有公共方法都有JavaDoc

---

## 2. Core API

### 2.1 GameEngine

引擎入口类，管理整个游戏生命周期。

```java
/**
 * 游戏引擎主类。
 * 
 * 使用示例:
 * <pre>
 * EngineConfig config = new EngineConfig.Builder()
 *     .setTitle("My Game")
 *     .setWidth(1280)
 *     .setHeight(720)
 *     .setTargetFPS(60)
 *     .build();
 * 
 * GameEngine engine = new GameEngine(config);
 * engine.setScene(new MainScene());
 * engine.run();
 * </pre>
 */
public class GameEngine {
    
    /**
     * 创建引擎实例。
     * 
     * @param config 引擎配置，不能为null
     * @throws IllegalArgumentException 如果config为null或无效
     */
    public GameEngine(EngineConfig config);
    
    /**
     * 设置当前场景。
     * 切换场景会在下一帧开始时生效。
     * 
     * @param scene 新场景，不能为null
     */
    public void setScene(Scene scene);
    
    /**
     * 启动游戏循环。
     * 此方法会阻塞直到游戏退出。
     * 
     * @throws IllegalStateException 如果引擎已经运行
     */
    public void run();
    
    /**
     * 请求停止引擎。
     * 在当前帧结束后优雅退出。
     */
    public void stop();
    
    /**
     * 获取资源管理器。
     */
    public ResourceManager getResourceManager();
    
    /**
     * 获取输入管理器。
     */
    public InputManager getInputManager();
    
    /**
     * 获取音频管理器。
     */
    public AudioManager getAudioManager();
}
```

### 2.2 EngineConfig

引擎配置类。

```java
public class EngineConfig {
    
    public static class Builder {
        public Builder setTitle(String title);           // 默认: "Game"
        public Builder setWidth(int width);              // 默认: 1280
        public Builder setHeight(int height);            // 默认: 720
        public Builder setFullscreen(boolean fullscreen);// 默认: false
        public Builder setVSync(boolean vSync);          // 默认: true
        public Builder setTargetFPS(int fps);            // 默认: 60
        public Builder setResizable(boolean resizable);  // 默认: true
        public Builder setResourceRoot(String path);     // 默认: "assets"
        public EngineConfig build();
    }
    
    // Getters...
}
```

---

## 3. Scene API

### 3.1 Scene

游戏场景基类。

```java
/**
 * 游戏场景基类。
 * 子类必须实现create()方法来初始化游戏对象。
 */
public abstract class Scene {
    
    /**
     * 场景被激活时调用。
     * 在此方法中创建游戏对象和初始化状态。
     */
    protected abstract void create();
    
    /**
     * 每帧更新调用。
     * 
     * @param deltaTime 距上一帧的时间（秒）
     */
    protected void update(float deltaTime);
    
    /**
     * 每帧渲染调用。
     * 
     * @param renderer 2D渲染器
     * @param alpha 插值因子(0-1)，用于平滑渲染
     */
    protected void render(Renderer2D renderer, float alpha);
    
    /**
     * 场景被销毁时调用。
     * 清理资源和游戏对象。
     */
    protected void dispose();
    
    /**
     * 创建游戏对象。
     * 
     * @param name 对象名称
     * @return 新创建的游戏对象
     */
    protected GameObject createGameObject(String name);
    
    /**
     * 按名称查找游戏对象。
     * 
     * @param name 对象名称
     * @return 找到的对象，或Optional.empty()
     */
    public Optional<GameObject> findGameObject(String name);
    
    /**
     * 按标签查找所有游戏对象。
     * 
     * @param tag 标签
     * @return 对象列表（可能为空）
     */
    public List<GameObject> findGameObjectsByTag(String tag);
    
    /**
     * 获取场景相机。
     */
    public Camera getCamera();
    
    /**
     * 设置场景相机。
     */
    public void setCamera(Camera camera);
}
```

---

## 4. GameObject API

### 4.1 GameObject

游戏实体类。

```java
public class GameObject {
    
    /**
     * 创建游戏对象。
     * 
     * @param name 对象名称，不能为null或空
     */
    public GameObject(String name);
    
    /**
     * 添加组件。
     * 
     * @param component 组件实例
     * @throws IllegalArgumentException 如果组件类型已存在且不允许重复
     */
    public void addComponent(Component component);
    
    /**
     * 获取组件。
     * 
     * @param type 组件类型
     * @return 组件实例，或Optional.empty()
     */
    public <T extends Component> Optional<T> getComponent(Class<T> type);
    
    /**
     * 移除组件。
     */
    public void removeComponent(Component component);
    
    /**
     * 获取Transform组件。
     */
    public Transform getTransform();
    
    /**
     * 设置激活状态。
     * 非激活对象不会被更新和渲染。
     */
    public void setActive(boolean active);
    
    /**
     * 标记销毁。
     * 对象将在当前帧结束时被移除。
     */
    public void destroy();
    
    /**
     * 添加子对象。
     */
    public void addChild(GameObject child);
    
    /**
     * 移除子对象。
     */
    public void removeChild(GameObject child);
}
```

### 4.2 Transform

变换组件。

```java
public class Transform extends Component {
    
    /**
     * 获取位置。
     */
    public Vector2f getPosition();
    
    /**
     * 设置位置。
     */
    public void setPosition(float x, float y);
    public void setPosition(Vector2f position);
    
    /**
     * 获取旋转角度（弧度）。
     */
    public float getRotation();
    
    /**
     * 设置旋转角度（弧度）。
     */
    public void setRotation(float rotation);
    
    /**
     * 获取缩放。
     */
    public Vector2f getScale();
    
    /**
     * 设置缩放。
     */
    public void setScale(float x, float y);
    public void setScale(float uniform);
    
    /**
     * 平移。
     */
    public void translate(float dx, float dy);
    
    /**
     * 旋转。
     */
    public void rotate(float delta);
    
    /**
     * 局部坐标转世界坐标。
     */
    public Vector2f localToWorld(Vector2f local);
    
    /**
     * 世界坐标转局部坐标。
     */
    public Vector2f worldToLocal(Vector2f world);
}
```

---

## 5. Rendering API

### 5.1 Renderer2D

2D渲染器。

```java
public class Renderer2D {
    
    /**
     * 开始渲染帧。
     */
    public void begin();
    
    /**
     * 结束渲染帧。
     */
    public void end();
    
    /**
     * 绘制精灵。
     * 
     * @param texture 纹理
     * @param x 位置X
     * @param y 位置Y
     * @param width 宽度
     * @param height 高度
     */
    public void draw(Texture texture, float x, float y, float width, float height);
    
    /**
     * 绘制精灵（带旋转和缩放）。
     * 
     * @param texture 纹理
     * @param transform 变换
     */
    public void draw(Texture texture, Transform transform);
    
    /**
     * 绘制精灵（完整参数）。
     * 
     * @param texture 纹理
     * @param x 位置X
     * @param y 位置Y
     * @param originX 原点X（旋转中心）
     * @param originY 原点Y
     * @param width 宽度
     * @param height 高度
     * @param scaleX 缩放X
     * @param scaleY 缩放Y
     * @param rotation 旋转（弧度）
     * @param srcX 纹理裁剪X
     * @param srcY 纹理裁剪Y
     * @param srcWidth 纹理裁剪宽度
     * @param srcHeight 纹理裁剪高度
     * @param flipX 水平翻转
     * @param flipY 垂直翻转
     * @param color 颜色调制
     */
    public void draw(Texture texture, 
                     float x, float y, 
                     float originX, float originY,
                     float width, float height,
                     float scaleX, float scaleY,
                     float rotation,
                     int srcX, int srcY, int srcWidth, int srcHeight,
                     boolean flipX, boolean flipY,
                     Color color);
    
    /**
     * 绘制矩形（纯色）。
     */
    public void drawRect(float x, float y, float width, float height, Color color);
    
    /**
     * 绘制线条。
     */
    public void drawLine(float x1, float y1, float x2, float y2, Color color);
    
    /**
     * 绘制文本。
     */
    public void drawText(String text, float x, float y, Font font, Color color);
}
```

### 5.2 Camera

相机。

```java
public class Camera {
    
    /**
     * 创建正交相机。
     * 
     * @param viewportWidth 视口宽度
     * @param viewportHeight 视口高度
     */
    public Camera(float viewportWidth, float viewportHeight);
    
    /**
     * 更新相机矩阵。
     */
    public void update();
    
    /**
     * 屏幕坐标转世界坐标。
     * 
     * @param screenX 屏幕X
     * @param screenY 屏幕Y（左上角原点）
     * @return 世界坐标
     */
    public Vector2f unproject(float screenX, float screenY);
    
    /**
     * 世界坐标转屏幕坐标。
     */
    public Vector2f project(float worldX, float worldY);
}
```

---

## 6. Resource API

### 6.1 ResourceManager

资源管理。

```java
public class ResourceManager {
    
    /**
     * 加载纹理。
     * 
     * @param path 资源路径（相对于resource root）
     * @return 纹理资源
     * @throws ResourceNotFoundException 如果资源不存在
     * @throws ResourceLoadException 如果加载失败
     */
    public Texture loadTexture(String path);
    
    /**
     * 异步加载纹理。
     * 
     * @param path 资源路径
     * @param callback 加载完成回调
     */
    public void loadTextureAsync(String path, Consumer<Texture> callback);
    
    /**
     * 加载音效。
     */
    public Sound loadSound(String path);
    
    /**
     * 加载音乐。
     */
    public Music loadMusic(String path);
    
    /**
     * 加载字体。
     */
    public Font loadFont(String path, int size);
    
    /**
     * 获取已加载的资源。
     * 
     * @param path 资源路径
     * @return 资源，或Optional.empty()如果未加载
     */
    public <T extends Resource> Optional<T> get(String path, Class<T> type);
    
    /**
     * 卸载资源。
     */
    public void unload(String path);
    
    /**
     * 启用热重载（开发模式）。
     */
    public void enableHotReload();
}
```

---

## 7. Input API

### 7.1 InputManager

输入管理。

```java
public class InputManager {
    
    /**
     * 检查按键是否按下。
     */
    public boolean isKeyDown(KeyboardKey key);
    
    /**
     * 检查按键是否刚按下（边沿检测）。
     */
    public boolean isKeyPressed(KeyboardKey key);
    
    /**
     * 检查按键是否刚释放。
     */
    public boolean isKeyReleased(KeyboardKey key);
    
    /**
     * 获取鼠标位置（屏幕坐标）。
     */
    public Vector2f getMousePosition();
    
    /**
     * 检查鼠标按钮状态。
     */
    public boolean isMouseButtonDown(MouseButton button);
    
    /**
     * 获取鼠标滚轮偏移。
     */
    public float getMouseScrollY();
    
    /**
     * 注册输入动作。
     */
    public void registerAction(InputAction action);
    
    /**
     * 检查输入动作是否触发。
     */
    public boolean isActionActive(String actionName);
}
```

---

## 8. Audio API

### 8.1 AudioManager

音频管理。

```java
public class AudioManager {
    
    /**
     * 设置主音量。
     * 
     * @param volume 0.0 - 1.0
     */
    public void setMasterVolume(float volume);
    
    /**
     * 播放音效。
     * 
     * @param sound 音效资源
     * @param volume 音量(0-1)
     * @param pitch 音调(0.5-2.0)
     * @param pan 声像(-1左到1右)
     * @return 播放实例ID
     */
    public long playSound(Sound sound, float volume, float pitch, float pan);
    
    /**
     * 播放音乐。
     * 
     * @param music 音乐资源
     * @param looping 是否循环
     */
    public void playMusic(Music music, boolean looping);
    
    /**
     * 停止音乐。
     */
    public void stopMusic();
    
    /**
     * 暂停音乐。
     */
    public void pauseMusic();
    
    /**
     * 恢复音乐。
     */
    public void resumeMusic();
}
```

---

## 9. Error Handling

### 9.1 Exception Hierarchy

```
GameEngineException (checked)
├── ResourceNotFoundException
├── ResourceLoadException
└── InitializationException

RuntimeException (unchecked)
├── IllegalStateException
├── IllegalArgumentException
└── NullPointerException (参数为空时)
```

### 9.2 Error Codes

| Code | Meaning | Recovery |
|------|---------|----------|
| R001 | 资源未找到 | 检查路径，提供默认资源 |
| R002 | 资源格式不支持 | 转换资源格式 |
| R003 | 资源加载超时 | 重试或降级 |
| G001 | OpenGL上下文错误 | 检查驱动，降级OpenGL版本 |
| G002 | 内存不足 | 释放资源，降低质量 |

---

## 10. Version Compatibility

### 10.1 API版本策略

- **MAJOR**: 破坏性API变更（需迁移）
- **MINOR**: 新增功能（向后兼容）
- **PATCH**: Bug修复（行为不变）

### 10.2 弃用策略

1. API弃用后将保留至少一个MINOR版本
2. 弃用方法标记`@Deprecated`，说明替代方案
3. 升级指南随版本发布

---

## 11. Usage Example

完整示例：创建一个带玩家的简单场景

```java
public class GameMain {
    public static void main(String[] args) {
        // 配置
        EngineConfig config = new EngineConfig.Builder()
            .setTitle("Demo Game")
            .setWidth(1280)
            .setHeight(720)
            .setTargetFPS(60)
            .build();
        
        // 启动
        GameEngine engine = new GameEngine(config);
        engine.setScene(new MainScene());
        engine.run();
    }
}

public class MainScene extends Scene {
    
    private GameObject player;
    private Texture playerTexture;
    
    @Override
    protected void create() {
        // 加载资源
        playerTexture = getResourceManager().loadTexture("player.png");
        
        // 创建玩家
        player = createGameObject("Player");
        player.getTransform().setPosition(400, 300);
        player.addComponent(new SpriteComponent(playerTexture));
        player.addComponent(new PlayerController());
    }
    
    @Override
    protected void update(float deltaTime) {
        // 玩家控制逻辑在PlayerController组件中
        
        // 检查输入
        if (getInputManager().isKeyPressed(KeyboardKey.SPACE)) {
            jump();
        }
    }
    
    @Override
    protected void render(Renderer2D renderer, float alpha) {
        // 背景
        renderer.drawRect(0, 0, 1280, 720, Color.DARK_GRAY);
        
        // 游戏对象渲染自动处理
    }
    
    private void jump() {
        // 跳跃逻辑
    }
    
    @Override
    protected void dispose() {
        // 资源由ResourceManager管理，无需手动释放
    }
}
```
