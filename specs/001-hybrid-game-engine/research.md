# Research & Technology Decisions

**Feature**: 混合架构游戏引擎  
**Date**: 2026-03-22

---

## 1. 渲染API选择

### 问题
2D渲染使用什么API？3D扩展预留什么API支持？

### 决策
- **2D渲染**: OpenGL 3.3 Core Profile
- **3D扩展**: OpenGL 4.5 或 Vulkan（预留接口）

### 理由
- OpenGL 3.3 兼容性最好，Intel HD 4000+ 都支持
- LWJGL3对OpenGL支持最成熟
- 2D游戏不需要OpenGL 4+特性，3.3足够
- 3D模块可后期选择OpenGL 4.5（计算着色器）或Vulkan（性能优先）

### 替代方案
- **Vulkan**: 性能更好但学习曲线陡峭，2D游戏不需要
- **DirectX**: 不支持Linux，排除
- **WebGPU**: 标准尚未稳定

---

## 2. 窗口和输入管理

### 问题
使用什么库管理窗口和输入？

### 决策
- **GLFW** (通过LWJGL3)

### 理由
- LWJGL3原生绑定，无需额外依赖
- 跨平台（Windows/Linux/macOS）
- 支持多窗口、多显示器
- 输入事件处理完善（键盘、鼠标、手柄）

### 替代方案
- **SDL2**: 功能更多但体积更大，需要额外JNI
- **AWT/Swing**: 性能差，不适合游戏

---

## 3. 数学库选择

### 问题
向量/矩阵运算使用什么库？

### 决策
- **JOML** (Java OpenGL Math Library)

### 理由
- 专为OpenGL设计，API与GLSL对应
- 无依赖，轻量级
- 支持JDK 8+，包含module-info
- 活跃的维护

### 替代方案
- **自研**: 维护成本高，容易出错
- **Apache Commons Math**: 太重，不适合实时图形

---

## 4. 工具类库选择

### 问题
字符串处理、集合操作、IO等工具类使用什么？

### 决策
- **Apache Commons Lang3**
- **Apache Commons IO**
- **Apache Commons Collections4**

### 理由
- 用户明确要求使用Apache Commons类库群
- 替代Guava，避免引入Guava依赖
- Commons库JDK兼容性好，支持JDK 17/21

### Guava替换映射
| Guava | Apache Commons |
|-------|----------------|
| `Strings.nullToEmpty` | `StringUtils.defaultString` |
| `Charsets.UTF_8` | `StandardCharsets.UTF_8` |
| `Lists.newArrayList` | `new ArrayList<>()` |
| `Maps.newHashMap` | `new HashMap<>()` |
| `Files.readLines` | `FileUtils.readLines` |
| `ByteStreams.copy` | `IOUtils.copy` |
| `Preconditions.checkNotNull` | `Validate.notNull` |
| `Optional` | `java.util.Optional` |

---

## 5. 音频方案

### 问题
使用什么音频库？

### 决策
- **OpenAL** (通过LWJGL3)
- **stb_vorbis** (通过LWJGL3) - OGG解码

### 理由
- LWJGL3原生支持
- OpenAL是游戏行业标准
- OGG无专利问题，压缩率好

### 替代方案
- **JavaSound**: 延迟高，不适合游戏
- **OpenSL ES**: Android专用

---

## 6. 日志框架

### 问题
使用什么日志框架？

### 决策
- **Log4j2**

### 理由
- 性能优秀，异步日志
- 支持SLF4J接口
- 配置灵活
- 用户未禁止，且是标准选择

### 替代方案
- **SLF4J + Logback**: 也可以，但Log4j2性能更好
- **java.util.logging**: 功能有限

---

## 7. 构建工具

### 问题
使用什么构建工具？

### 决策
- **Maven 3.8+**

### 理由
- 项目已有pom.xml
- 多模块支持好
- LWJGL3提供官方Maven依赖
- 与CI/CD集成成熟

---

## 8. 测试框架

### 问题
使用什么测试框架？

### 决策
- **JUnit 5** (Jupiter)
- **Mockito** (模拟对象)
- **AssertJ** (流式断言)

### 理由
- JUnit 5是标准，支持JDK 17/21
- Mockito支持final类模拟（inline mock maker）
- AssertJ提高测试可读性

### 无头测试策略
- 使用`GLFW_VISIBLE=false`创建不可见窗口
- Mock OpenGL上下文用于单元测试
- 集成测试使用真实窗口（CI中配置Xvfb）

---

## 9. 脚本支持（非核心模块）

### 问题
如何支持Groovy脚本？

### 决策
- **Groovy 4.0+**
- 限制使用类Java语法
- 仅用于非核心模块（模组、配置、工具）

### 理由
- 用户允许非核心模块使用Groovy
- 脚本化支持模组开发
- Groovy与Java互操作好

### 类Java语法约束
```groovy
// ✅ 推荐 - 类Java语法
def calculateDamage(int base, float multiplier) {
    return (int)(base * multiplier);
}

// ❌ 避免 - Groovy特有语法
def calculateDamage(base, multiplier) {
    base * multiplier  // 隐式返回，省略类型，省略分号
}
```

---

## 10. 资源加载策略

### 问题
资源如何加载？同步还是异步？

### 决策
- **异步加载为主**
- **引用计数 + LRU缓存**
- **热重载支持**（开发模式）

### 理由
- 避免IO阻塞渲染线程
- 引用计数防止资源泄漏
- LRU控制内存使用
- 热重载提高开发效率

### 资源包结构
```
assets/
├── textures/       # PNG, JPG
├── audio/
│   ├── sfx/        # 音效 (OGG, WAV)
│   └── music/      # 音乐 (OGG)
├── fonts/          # TTF, OTF
├── shaders/        # GLSL
└── config/         # JSON, XML
```

---

## 11. 渲染架构设计

### 问题
如何设计渲染系统以支持2D为主、3D可扩展？

### 决策
**分层架构**:
```
RenderSystem (抽象)
├── Renderer2D (实现)
│   └── SpriteBatch (批处理)
└── Renderer3D (接口)
    └── Renderer3DImpl (可选实现)
```

### 2D渲染管线
1. **SpriteBatch**: 自动批处理，减少Draw Call
2. **纹理图集**: 减少纹理切换
3. **顶点缓冲**: VBO/VAO管理
4. **着色器**: 顶点变换 + 片段纹理采样

### 3D扩展点
- `Renderer3D`接口定义3D渲染契约
- 实现类在engine-3d模块
- 渲染器切换通过配置或运行时检测

---

## 12. JDK版本兼容性

### 问题
如何支持JDK 17和JDK 21？

### 决策
- **源代码级别**: JDK 17
- **运行时兼容**: JDK 17 和 JDK 21
- **使用工具**: Maven Toolchains

### 理由
- JDK 17是LTS，稳定
- JDK 21是新LTS，包含虚拟线程等特性
- 保持源代码JDK 17可兼容两个运行时
- CI同时测试两个版本

### 避免使用的特性
- 避免使用JDK 21+特有API（如未标准化的特性）
- 可以使用JDK 17+的sealed classes, pattern matching for switch

---

## 13. 跨平台兼容性

### 问题
如何处理Windows和Linux差异？

### 决策
- **路径处理**: 使用`java.nio.file.Path`，统一正斜杠
- **文件系统**: Apache Commons IO处理
- **动态库**: LWJGL3自动加载平台对应库
- **行尾符**: Git配置`.gitattributes`自动处理

### 平台差异点
| 方面 | Windows | Linux |
|------|---------|-------|
| 路径分隔符 | `\` | `/` |
| 大小写敏感 | 否 | 是 |
| 动态库扩展 | `.dll` | `.so` |
| 配置文件位置 | `%APPDATA%` | `~/.config` |

---

## 14. 依赖排除清单

根据约束，以下库**禁止**使用：

- ❌ **Spring Framework** - 用户明确禁止
- ❌ **Guava** - 用户要求使用Apache Commons替代
- ❌ **Kotlin Stdlib** - 用户禁止Kotlin代码

如果必须使用Kotlin编写的库：
- 检查是否依赖`kotlin-stdlib`
- 如果依赖，评估是否可以替换
- 如果无法替换，记录并最小化使用

---

## 技术栈总结

| 类别 | 选择 | 版本 |
|------|------|------|
| 语言 | Java | 17+ (兼容21) |
| 构建 | Maven | 3.8+ |
| 图形 | LWJGL3 + OpenGL | 3.3.4+ |
| 窗口 | GLFW (via LWJGL3) | 3.3.4+ |
| 音频 | OpenAL (via LWJGL3) | 3.3.4+ |
| 数学 | JOML | 1.10.5+ |
| 工具 | Apache Commons | 最新稳定版 |
| 日志 | Log4j2 | 2.x |
| 测试 | JUnit 5 + Mockito | 5.x |
| 脚本 | Groovy | 4.0+ (非核心) |
