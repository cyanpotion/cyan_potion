# Event System Protocol

**Feature**: 混合架构游戏引擎  
**Version**: 1.0.0  
**Date**: 2026-03-22

---

## 1. Overview

本文档定义引擎内部和引擎与游戏之间的事件系统协议。事件系统用于：

1. **解耦模块**: 模块间通过事件通信，降低耦合
2. **生命周期管理**: 游戏对象、场景的生命周期事件
3. **输入分发**: 输入事件分发到监听者
4. **扩展点**: 游戏逻辑可以监听和响应引擎事件

---

## 2. Event Architecture

### 2.1 事件流

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Source     │────▶│ Event Bus    │────▶│  Listeners   │
│  (事件源)     │     │  (事件总线)   │     │   (监听者)    │
└──────────────┘     └──────────────┘     └──────────────┘
       │                                           ▲
       │                                           │
       └───────────────────────────────────────────┘
                    (Direct Callback)
```

### 2.2 事件类型层次

```
Event (基类)
├── EngineEvent
│   ├── WindowEvent
│   │   ├── WindowResizeEvent
│   │   ├── WindowFocusEvent
│   │   └── WindowCloseEvent
│   ├── ResourceEvent
│   │   ├── ResourceLoadEvent
│   │   ├── ResourceUnloadEvent
│   │   └── ResourceHotReloadEvent
│   └── SceneEvent
│       ├── SceneLoadEvent
│       ├── SceneUnloadEvent
│       └── SceneSwitchEvent
├── InputEvent
│   ├── KeyEvent
│   │   ├── KeyPressEvent
│   │   ├── KeyReleaseEvent
│   │   └── KeyRepeatEvent
│   ├── MouseEvent
│   │   ├── MouseMoveEvent
│   │   ├── MouseButtonEvent
│   │   └── MouseScrollEvent
│   └── GamepadEvent
│       ├── GamepadConnectEvent
│       └── GamepadButtonEvent
└── GameplayEvent (游戏扩展)
    ├── CollisionEvent
    └── CustomGameEvent
```

---

## 3. Event Base Class

```java
/**
 * 所有事件的基类。
 */
public abstract class Event {
    
    /**
     * 事件发生时间戳（纳秒）。
     */
    private final long timestamp;
    
    /**
     * 事件是否被取消。
     * 被取消的事件不会继续传播。
     */
    private boolean cancelled = false;
    
    public Event() {
        this.timestamp = System.nanoTime();
    }
    
    /**
     * 取消事件。
     */
    public void cancel() {
        this.cancelled = true;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * 获取事件类型标识。
     */
    public abstract EventType getType();
}
```

---

## 4. Engine Events

### 4.1 Window Events

#### WindowResizeEvent

窗口大小改变事件。

```java
public class WindowResizeEvent extends EngineEvent {
    
    private final int oldWidth;
    private final int oldHeight;
    private final int newWidth;
    private final int newHeight;
    
    public WindowResizeEvent(int oldWidth, int oldHeight, int newWidth, int newHeight);
    
    public int getOldWidth();
    public int getOldHeight();
    public int getNewWidth();
    public int getNewHeight();
}
```

**触发时机**: 用户调整窗口大小或调用`window.setSize()`

**处理建议**:
- 更新相机视口
- 重新计算UI布局
- 调整渲染目标尺寸

---

#### WindowFocusEvent

窗口焦点变化事件。

```java
public class WindowFocusEvent extends EngineEvent {
    
    private final boolean focused;
    
    public WindowFocusEvent(boolean focused);
    
    /**
     * 窗口是否获得焦点。
     */
    public boolean isFocused();
}
```

**处理建议**:
- 失去焦点时暂停游戏（可选）
- 失去焦点时静音或降低音量
- 获得焦点时恢复输入处理

---

### 4.2 Resource Events

#### ResourceLoadEvent

资源加载完成事件。

```java
public class ResourceLoadEvent extends EngineEvent {
    
    private final String resourcePath;
    private final ResourceType type;
    private final long loadTimeMs;
    private final long memorySize;
    
    public ResourceLoadEvent(String path, ResourceType type, long loadTimeMs, long memorySize);
    
    public String getResourcePath();
    public ResourceType getType();
    public long getLoadTimeMs();
    public long getMemorySize();
}
```

**触发时机**: 资源异步加载完成后

---

#### ResourceHotReloadEvent

资源热重载事件。

```java
public class ResourceHotReloadEvent extends EngineEvent {
    
    private final String resourcePath;
    private final Resource oldResource;
    private final Resource newResource;
    
    public ResourceHotReloadEvent(String path, Resource old, Resource new);
}
```

**触发时机**: 开发模式下检测到资源文件变更

---

### 4.3 Scene Events

#### SceneSwitchEvent

场景切换事件。

```java
public class SceneSwitchEvent extends EngineEvent {
    
    private final Scene oldScene;
    private final Scene newScene;
    private final SwitchType type;
    
    public enum SwitchType {
        NORMAL,     // 正常切换
        RESTART,    // 重新开始当前场景
        POP,        // 返回上一场景（场景栈）
        PUSH        // 压入新场景（场景栈）
    }
    
    public SceneSwitchEvent(Scene old, Scene new, SwitchType type);
}
```

---

## 5. Input Events

### 5.1 Key Events

#### KeyPressEvent

按键按下事件。

```java
public class KeyPressEvent extends InputEvent {
    
    private final KeyboardKey key;
    private final int scancode;
    private final KeyModifiers modifiers;
    
    public KeyPressEvent(KeyboardKey key, int scancode, KeyModifiers mods);
    
    public KeyboardKey getKey();
    public boolean isShiftDown();
    public boolean isCtrlDown();
    public boolean isAltDown();
}
```

**Key Modifiers**:
```java
public class KeyModifiers {
    private final boolean shift;
    private final boolean ctrl;
    private final boolean alt;
    private final boolean super;  // Windows键/Command键
}
```

---

### 5.2 Mouse Events

#### MouseMoveEvent

鼠标移动事件。

```java
public class MouseMoveEvent extends InputEvent {
    
    private final float x;
    private final float y;
    private final float deltaX;
    private final float deltaY;
    
    public MouseMoveEvent(float x, float y, float dx, float dy);
}
```

**注意**: 鼠标坐标以窗口左上角为原点(0,0)，Y轴向下。

---

#### MouseButtonEvent

鼠标按钮事件。

```java
public class MouseButtonEvent extends InputEvent {
    
    private final MouseButton button;
    private final ButtonAction action;
    private final float x;
    private final float y;
    
    public enum ButtonAction {
        PRESS, RELEASE
    }
    
    public MouseButtonEvent(MouseButton btn, ButtonAction act, float x, float y);
}
```

---

#### MouseScrollEvent

鼠标滚轮事件。

```java
public class MouseScrollEvent extends InputEvent {
    
    private final float offsetX;
    private final float offsetY;
    
    public MouseScrollEvent(float x, float y);
}
```

---

## 6. Event Bus

### 6.1 EventBus API

```java
public class EventBus {
    
    /**
     * 注册事件监听者。
     * 
     * @param type 事件类型
     * @param listener 监听者
     */
    public <T extends Event> void on(EventType<T> type, EventListener<T> listener);
    
    /**
     * 注册一次性监听者（触发后自动注销）。
     */
    public <T extends Event> void once(EventType<T> type, EventListener<T> listener);
    
    /**
     * 注销事件监听者。
     */
    public <T extends Event> void off(EventType<T> type, EventListener<T> listener);
    
    /**
     * 发布事件。
     */
    public void emit(Event event);
    
    /**
     * 发布事件（延迟到下一帧处理）。
     */
    public void emitLater(Event event);
}
```

### 6.2 EventListener Interface

```java
@FunctionalInterface
public interface EventListener<T extends Event> {
    
    /**
     * 处理事件。
     * 
     * @param event 事件对象
     */
    void onEvent(T event);
    
    /**
     * 监听优先级（数值越小优先级越高）。
     * 默认: 0
     */
    default int getPriority() {
        return 0;
    }
}
```

---

## 7. Event Propagation

### 7.1 传播阶段

```
Capture Phase ──▶ Target Phase ──▶ Bubble Phase
(从上到下)        (目标对象)        (从下到上)
```

### 7.2 游戏对象事件传播

对于游戏对象层级（Scene → Parent → Child）：

1. **Capture Phase**: 从Scene向下传播到目标对象
2. **Target Phase**: 目标对象处理
3. **Bubble Phase**: 从目标对象向上传播到Scene

```java
// 在GameObject中处理事件
public class GameObject {
    
    /**
     * 处理事件。
     * 
     * @param event 事件
     * @param phase 传播阶段
     * @return 是否消耗事件（阻止继续传播）
     */
    public boolean handleEvent(Event event, EventPhase phase);
}
```

### 7.3 事件消耗

```java
// 消耗事件，阻止进一步传播
event.cancel();

// 检查是否已消耗
if (!event.isCancelled()) {
    // 处理事件
}
```

---

## 8. Usage Examples

### 8.1 监听窗口大小变化

```java
public class MainScene extends Scene {
    
    @Override
    protected void create() {
        // 获取引擎事件总线
        EventBus bus = getEngine().getEventBus();
        
        // 监听窗口大小变化
        bus.on(EventType.WINDOW_RESIZE, this::onWindowResize);
    }
    
    private void onWindowResize(WindowResizeEvent event) {
        System.out.println("Window resized: " + event.getNewWidth() + "x" + event.getNewHeight());
        
        // 调整相机
        getCamera().setViewport(event.getNewWidth(), event.getNewHeight());
    }
}
```

### 8.2 监听输入事件

```java
public class PlayerController extends Component {
    
    @Override
    public void onAttach() {
        EventBus bus = getGameObject().getScene().getEventBus();
        
        bus.on(EventType.KEY_PRESS, this::onKeyPress);
        bus.on(EventType.MOUSE_BUTTON, this::onMouseButton);
    }
    
    private void onKeyPress(KeyPressEvent event) {
        if (event.getKey() == KeyboardKey.SPACE) {
            jump();
            event.cancel(); // 消耗事件，防止其他对象响应
        }
    }
    
    private void onMouseButton(MouseButtonEvent event) {
        if (event.getButton() == MouseButton.LEFT && event.getAction() == ButtonAction.PRESS) {
            Vector2f worldPos = getScene().getCamera().unproject(event.getX(), event.getY());
            attack(worldPos);
        }
    }
}
```

### 8.3 自定义游戏事件

```java
// 定义自定义事件
public class PlayerDeathEvent extends GameplayEvent {
    
    private final GameObject player;
    private final String cause;
    
    public PlayerDeathEvent(GameObject player, String cause) {
        this.player = player;
        this.cause = cause;
    }
    
    public GameObject getPlayer() { return player; }
    public String getCause() { return cause; }
    
    @Override
    public EventType getType() {
        return EventType.PLAYER_DEATH; // 自定义类型
    }
}

// 发布事件
public class HealthComponent extends Component {
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            EventBus bus = getGameObject().getScene().getEventBus();
            bus.emit(new PlayerDeathEvent(getGameObject(), "damage"));
        }
    }
}

// 监听事件
public class GameManager extends Component {
    
    @Override
    public void onAttach() {
        getScene().getEventBus().on(EventType.PLAYER_DEATH, this::onPlayerDeath);
    }
    
    private void onPlayerDeath(PlayerDeathEvent event) {
        System.out.println("Player died: " + event.getCause());
        // 游戏结束逻辑
    }
}
```

---

## 9. Performance Considerations

### 9.1 事件池

高频事件（如MouseMoveEvent）使用对象池避免GC压力。

```java
public class EventPool<T extends Event> {
    
    private final Queue<T> pool;
    private final Supplier<T> factory;
    private final int maxSize;
    
    public T acquire();
    public void release(T event);
}
```

### 9.2 延迟事件

非紧急事件（如ResourceLoadEvent）可以延迟到帧末处理，避免阻塞渲染。

```java
// 延迟发布
eventBus.emitLater(event);

// 在帧末处理延迟事件
eventBus.processDelayedEvents();
```

### 9.3 监听者数量限制

- 避免过多监听者监听同一事件类型
- 考虑使用事件过滤器减少不必要调用

---

## 10. Thread Safety

**线程安全规则**:

1. **主线程事件**: 所有渲染相关事件在主线程触发和处理
2. **后台线程事件**: 资源加载事件在后台线程触发，通过队列同步到主线程
3. **线程安全方法**: `emitLater()` 是线程安全的，可从任意线程调用

```java
// 后台线程安全发布事件
resourceLoader.onComplete(resource -> {
    // 在后台线程
    Event event = new ResourceLoadEvent(resource);
    
    // 延迟到主线程处理
    eventBus.emitLater(event);
});
```
