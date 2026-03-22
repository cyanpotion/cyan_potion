# Data Model Design

**Feature**: 混合架构游戏引擎  
**Date**: 2026-03-22

---

## 1. Entity Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           GameEngine (引擎核心)                              │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  GameLoop    │  │ GameWindow   │  │ RenderSystem │  │ResourceManager│  │
│  │  (游戏循环)   │  │   (窗口)      │  │  (渲染系统)   │  │  (资源管理)   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
└─────────┼─────────────────┼─────────────────┼─────────────────┼───────────┘
          │                 │                 │                 │
          ▼                 ▼                 ▼                 ▼
   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
   │ InputManager │  │   Scene      │  │  Renderer2D  │  │   Resource   │
   │   (输入)      │  │   (场景)      │  │   (2D渲染)    │  │   (资源)      │
   └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
          │                 │                 │                 │
          ▼                 ▼                 ▼                 ▼
   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
   │  Keyboard    │  │ GameObject   │  │ SpriteBatch  │  │   Texture    │
   │  Mouse       │  │   (游戏对象)  │  │  (精灵批处理) │  │    Audio     │
   │  Gamepad     │  │              │  │              │  │    Font      │
   └──────────────┘  └──────┬───────┘  └──────────────┘  └──────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │  Component   │
                     │   (组件)      │
                     └──────┬───────┘
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │Transform │ │ Sprite   │ │  Script  │
        │Collider  │ │ Animator │ │   ...    │
        └──────────┘ └──────────┘ └──────────┘
```

---

## 2. Core Entities

### 2.1 GameEngine

**Purpose**: 引擎入口和生命周期管理

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| config | EngineConfig | 引擎配置（窗口大小、FPS限制等） |
| gameLoop | GameLoop | 游戏循环实例 |
| window | GameWindow | 窗口实例 |
| renderSystem | RenderSystem | 渲染系统 |
| resourceManager | ResourceManager | 资源管理器 |
| inputManager | InputManager | 输入管理器 |
| audioManager | AudioManager | 音频管理器 |
| currentScene | Scene | 当前场景 |
| isRunning | boolean | 引擎运行状态 |

**State Machine**:
```
CREATED → INITIALIZING → RUNNING → PAUSED → STOPPING → STOPPED
            ↓                ↓         ↓
          Error           Error     Resume
            ↓                ↓         ↓
         STOPPED          STOPPED   RUNNING
```

**Validation Rules**:
- `config` must not be null on init
- `window` must be created before `renderSystem` init
- Only one `GameEngine` instance allowed (单例)

---

### 2.2 GameLoop

**Purpose**: 控制游戏更新和渲染时序

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| targetFPS | int | 目标帧率 (默认60) |
| targetDelta | long | 目标帧时间 (nanoseconds) |
| lastFrameTime | long | 上一帧时间戳 |
| deltaTime | float | 距上一帧经过时间 (seconds) |
| accumulator | double | 固定时间步长累加器 |
| fixedTimeStep | float | 固定更新间隔 (1/60秒) |
| maxDeltaTime | float | 最大帧时间限制 (防止卡顿) |
| isVSyncEnabled | boolean | 垂直同步开关 |
| stats | FrameStats | 帧统计信息 |

**Timing Strategy**:
- **Variable Timestep**: 渲染使用可变时间步长（流畅动画）
- **Fixed Timestep**: 更新使用固定时间步长（确定性物理/逻辑）

**Algorithm**:
```java
while (running) {
    double currentTime = getTime();
    deltaTime = currentTime - lastFrameTime;
    lastFrameTime = currentTime;
    
    // 固定时间步长更新
    accumulator += deltaTime;
    while (accumulator >= fixedTimeStep) {
        scene.update(fixedTimeStep);
        accumulator -= fixedTimeStep;
    }
    
    // 插值因子用于渲染平滑
    float alpha = accumulator / fixedTimeStep;
    
    // 渲染
    renderSystem.render(scene, alpha);
}
```

---

### 2.3 GameWindow

**Purpose**: 平台抽象窗口管理

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| windowHandle | long | GLFW窗口句柄 |
| width | int | 窗口宽度 |
| height | int | 窗口高度 |
| title | String | 窗口标题 |
| isFullscreen | boolean | 全屏状态 |
| isVSync | boolean | 垂直同步 |
| cursorMode | CursorMode | 光标模式(NORMAL/HIDDEN/DISABLED) |
| resizeCallbacks | List<Consumer<WindowSize>> | 大小变化回调 |
| closeRequested | boolean | 关闭请求标志 |

**Events**:
- `onResize(width, height)`: 窗口大小改变
- `onFocusChange(focused)`: 焦点变化
- `onCloseRequested()`: 用户请求关闭

---

## 3. Render System

### 3.1 RenderSystem

**Purpose**: 渲染系统抽象和协调

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| renderer2D | Renderer2D | 2D渲染器 |
| renderer3D | Renderer3D | 3D渲染器（可选） |
| defaultShader | Shader | 默认着色器 |
| currentCamera | Camera | 当前相机 |
| clearColor | Color | 清屏颜色 |
| isInitialized | boolean | 初始化状态 |

**Methods**:
- `init()`: 初始化OpenGL上下文
- `render(Scene, float alpha)`: 渲染场景
- `setViewport(int x, int y, int w, int h)`: 设置视口
- `clear()`: 清屏

---

### 3.2 Renderer2D

**Purpose**: 2D渲染实现

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| spriteBatch | SpriteBatch | 精灵批处理器 |
| defaultTexture | Texture | 默认白色纹理（用于纯色矩形） |
| maxSpritesPerBatch | int | 每批最大精灵数 (默认10000) |
| currentTexture | Texture | 当前绑定的纹理 |

**Batching Strategy**:
- 相同纹理的精灵自动合并
- 纹理切换时提交当前批次
- 支持透明排序（Back-to-Front）

---

### 3.3 SpriteBatch

**Purpose**: 高效批处理精灵渲染

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| vertices | float[] | 顶点数据数组 (位置、UV、颜色) |
| indices | int[] | 索引数组 |
| maxSprites | int | 最大批处理精灵数 |
| spriteCount | int | 当前批次精灵数 |
| vbo | int | 顶点缓冲对象ID |
| vao | int | 顶点数组对象ID |
| ebo | int | 元素缓冲对象ID |
| shader | Shader | 批处理着色器 |
| isDrawing | boolean | 是否正在绘制 |

**Vertex Layout** (每个顶点):
```
Position (3 floats) + UV (2 floats) + Color (4 floats) = 9 floats/vertex
每个精灵4个顶点，总: 36 floats/sprite
```

---

### 3.4 Camera

**Purpose**: 2D/3D相机抽象

**Fields** (OrthographicCamera for 2D):
| Field | Type | Description |
|-------|------|-------------|
| position | Vector3f | 相机位置 |
| zoom | float | 缩放级别 |
| viewportWidth | float | 视口宽度 |
| viewportHeight | float | 视口高度 |
| projectionMatrix | Matrix4f | 投影矩阵 |
| viewMatrix | Matrix4f | 视图矩阵 |
| combinedMatrix | Matrix4f | 投影*视图矩阵 |

**Methods**:
- `update()`: 重新计算矩阵
- `unproject(screenX, screenY)`: 屏幕坐标转世界坐标
- `project(worldX, worldY)`: 世界坐标转屏幕坐标

---

## 4. Resource Management

### 4.1 ResourceManager

**Purpose**: 资源加载、缓存和生命周期管理

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| resources | Map<String, Resource> | 资源缓存表 |
| loaders | Map<Class<?>, ResourceLoader<?>> | 资源加载器注册表 |
| maxMemory | long | 最大缓存内存 (bytes) |
| currentMemory | long | 当前使用内存 |
| asyncExecutor | ExecutorService | 异步加载线程池 |
| hotReloadEnabled | boolean | 热重载开关 |

**LRU Eviction**:
- 使用`LinkedHashMap`实现LRU
- 当`currentMemory > maxMemory`时移除最久未使用
- 引用计数>0的资源不被移除

---

### 4.2 Resource (Abstract)

**Purpose**: 资源基类

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| id | String | 唯一标识符 |
| path | String | 资源路径 |
| refCount | AtomicInteger | 引用计数 |
| memorySize | long | 内存占用 (bytes) |
| lastUsed | long | 最后使用时间戳 |
| isLoaded | boolean | 加载状态 |
| dependencies | List<Resource> | 依赖资源列表 |

**Lifecycle**:
```
CREATED → LOADING → LOADED → UNLOADING → UNLOADED
            ↓          ↓
          Error     Unload
            ↓          ↓
         FAILED    UNLOADED
```

**Reference Counting**:
- `acquire()`: 引用计数+1
- `release()`: 引用计数-1，为0时标记可回收

---

### 4.3 Texture extends Resource

**Purpose**: 2D纹理资源

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| textureId | int | OpenGL纹理ID |
| width | int | 纹理宽度 |
| height | int | 纹理高度 |
| channels | int | 通道数 (3=RGB, 4=RGBA) |
| format | Format | 格式 (RGBA8, RGB8, etc.) |
| wrapS | WrapMode | 水平环绕模式 |
| wrapT | WrapMode | 垂直环绕模式 |
| minFilter | FilterMode | 缩小过滤模式 |
| magFilter | FilterMode | 放大过滤模式 |
| hasMipmaps | boolean | 是否生成Mipmaps |

---

### 4.4 Audio Resources

**Sound**: 短音效（射击、跳跃），完全加载到内存
**Music**: 长音乐流，流式播放

**Fields (Sound)**:
| Field | Type | Description |
|-------|------|-------------|
| bufferId | int | OpenAL缓冲ID |
| duration | float | 持续时间(秒) |
| sampleRate | int | 采样率 |
| channels | int | 通道数 |

**Fields (Music)**:
| Field | Type | Description |
|-------|------|-------------|
| sourceId | int | OpenAL源ID |
| isPlaying | boolean | 播放状态 |
| isLooping | boolean | 循环标志 |
| volume | float | 音量 (0-1) |

---

## 5. Scene Management

### 5.1 Scene

**Purpose**: 游戏场景容器

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| name | String | 场景名称 |
| gameObjects | List<GameObject> | 游戏对象列表 |
| renderLayers | Map<Integer, List<GameObject>> | 渲染层分组 |
| camera | Camera | 场景相机 |
| ambientColor | Color | 环境光颜色 |
| isActive | boolean | 是否激活 |
| systems | List<EntitySystem> | ECS系统列表 |

**Layer Management**:
- 层号越小越先渲染（背景层0，UI层100）
- 同层内按Y坐标或手动指定顺序排序

---

### 5.2 GameObject

**Purpose**: 游戏实体

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | 唯一标识 |
| name | String | 对象名称 |
| tag | String | 标签（用于查询） |
| components | Map<Class<?>, Component> | 组件映射 |
| transform | Transform | 变换组件（必须） |
| isActive | boolean | 激活状态 |
| isDestroyed | boolean | 销毁标记 |
| parent | GameObject | 父对象 |
| children | List<GameObject> | 子对象列表 |

**Component Pattern**:
```java
GameObject player = new GameObject("Player");
player.addComponent(new SpriteComponent(texture));
player.addComponent(new PlayerController());
player.addComponent(new BoxCollider(width, height));
```

---

### 5.3 Component (Abstract)

**Purpose**: 游戏对象行为和数据单元

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| gameObject | GameObject | 所属游戏对象 |
| isEnabled | boolean | 是否启用 |

**Lifecycle Methods**:
- `onAttach()`: 添加到对象时调用
- `onDetach()`: 移除时调用
- `update(float deltaTime)`: 每帧更新
- `render(Renderer2D renderer)`: 渲染（如果有可视组件）

---

### 5.4 Transform extends Component

**Purpose**: 位置、旋转、缩放变换

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| position | Vector2f | 位置 |
| rotation | float | 旋转角度（弧度） |
| scale | Vector2f | 缩放 |
| localMatrix | Matrix3x2f | 局部变换矩阵 |
| worldMatrix | Matrix3x2f | 世界变换矩阵 |
| isDirty | boolean | 是否需要重新计算矩阵 |

**Hierarchy**:
- 子对象继承父对象的变换
- 修改父对象会自动标记子对象dirty

---

## 6. Input System

### 6.1 InputManager

**Purpose**: 统一输入管理

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| keyboard | Keyboard | 键盘状态 |
| mouse | Mouse | 鼠标状态 |
| gamepads | Gamepad[] | 游戏手柄数组（最多4个） |
| inputActions | Map<String, InputAction> | 输入动作映射 |
| eventQueue | Queue<InputEvent> | 输入事件队列 |

**Input Action**: 抽象输入绑定
```java
InputAction jump = new InputAction("Jump");
jump.addBinding(KeyboardKey.SPACE);
jump.addBinding(GamepadButton.A);
```

---

### 6.2 Keyboard

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| keyStates | boolean[512] | 当前帧按键状态 |
| prevKeyStates | boolean[512] | 上一帧按键状态 |
| typedChars | StringBuilder | 文本输入缓冲区 |

**Query Methods**:
- `isDown(key)`: 当前按下
- `isPressed(key)`: 刚按下（边沿检测）
- `isReleased(key)`: 刚释放

---

## 7. Validation Rules

### 7.1 Entity Validation

**GameEngine**:
- config不能为空且必须验证通过
- 初始化顺序: Window → RenderSystem → ResourceManager → InputManager → AudioManager

**GameWindow**:
- width/height必须>0
- 标题不能为空

**Texture**:
- width/height必须>0且<=GL_MAX_TEXTURE_SIZE
- 数据大小必须匹配width*height*channels

**GameObject**:
- 必须包含Transform组件
- 名称不能为空
- 不能添加重复类型的Component（除非标记为@MultiInstance）

### 7.2 State Transition Rules

**Resource**:
- LOADING状态只能由CREATED进入
- 只有LOADED状态可以进入UNLOADING
- FAILED状态不能转为LOADED（必须重新创建）

**GameLoop**:
- 未初始化不能调用start()
- 停止后不能重新启动（必须新建实例）

---

## 8. Relationships Summary

```
GameEngine 1→1 GameLoop
GameEngine 1→1 GameWindow
GameEngine 1→1 RenderSystem
GameEngine 1→1 ResourceManager
GameEngine 1→1 InputManager
GameEngine 1→1 AudioManager
GameEngine 1→1 Scene

RenderSystem 1→1 Renderer2D
RenderSystem 0..1 Renderer3D
Renderer2D 1→1 SpriteBatch
SpriteBatch 1→* Shader
SpriteBatch 1→* Texture

ResourceManager *→* Resource
Texture --|> Resource
Sound --|> Resource
Music --|> Resource

Scene 1→* GameObject
Scene 1→1 Camera
GameObject 1→* Component
GameObject *→1 GameObject (parent)
Transform --|> Component
SpriteComponent --|> Component
ScriptComponent --|> Component

InputManager 1→1 Keyboard
InputManager 1→1 Mouse
InputManager 1→* Gamepad
```

---

## 9. Thread Safety

**线程安全组件**:
- ResourceManager (资源缓存操作需同步)
- InputManager (事件队列需同步)

**非线程安全（仅主线程访问）**:
- RenderSystem (OpenGL上下文绑定)
- Scene/GameObject (游戏逻辑)
- GameWindow (GLFW要求主线程)

**线程安全策略**:
- 资源异步加载：后台线程加载数据 → 主线程上传GPU
- 输入事件：GLFW回调收集 → 主线程开始时处理队列
