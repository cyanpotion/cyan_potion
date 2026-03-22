# Implementation Plan: 混合架构游戏引擎

**Branch**: `001-hybrid-game-engine` | **Date**: 2026-03-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/001-hybrid-game-engine/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

---

## Summary

构建一个以2D游戏为核心、支持3D扩展的混合架构游戏引擎。引擎基于LWJGL3提供底层图形/窗口/输入/音频支持，采用模块化设计允许开发者按需引入功能。使用Apache Commons工具库替代Guava，确保JDK 17和JDK 21双版本兼容。引擎与示例游戏共仓库管理，支持Windows和Linux双平台开发和部署。

技术方案：
- 渲染层抽象设计，2D使用Sprite Batch，3D预留可插拔Renderer
- 资源管理采用引用计数+LRU缓存策略
- 游戏循环使用固定时间步长（Fixed Timestep）
- 构建系统Maven多模块结构，核心模块纯Java，非核心允许Groovy脚本

---

## Technical Context

**Language/Version**: Java 17+ (兼容 JDK 17 LTS 和 JDK 21 LTS)  
**Primary Dependencies**: 
- LWJGL3 (3.3.4+) - 底层OpenGL/Vulkan/窗口/输入/音频绑定
- Apache Commons (Lang3, IO, Collections4) - 工具类库
- JOML (1.10.5+) - 数学库（向量/矩阵/四元数）
- JUnit 5 - 测试框架
- Log4j2 - 日志框架
  
**Storage**: 文件系统（资源加载）、内存（运行时状态）、可选SQLite（存档元数据）  
**Testing**: JUnit 5 + Mockito + AssertJ，可视化测试工具  
**Target Platform**: Windows 10+, Linux (Ubuntu 20.04+, Steam Deck)  
**Project Type**: Game Engine + Sample Games (Desktop Application)  
**Performance Goals**: 60 FPS (16.67ms/帧), 1000+ 同屏精灵, 启动<10秒  
**Constraints**: 
- 禁止使用 Spring Framework
- 禁止使用 Guava
- 禁止使用 Kotlin（但可依赖Kotlin编写的库）
- 非核心模块允许Groovy，但需使用类Java语法
- OpenGL 3.3+ 支持（2D渲染），OpenGL 4.5+ 或 Vulkan（3D扩展）
  
**Scale/Scope**: 
- 引擎核心: ~50K LOC
- 示例游戏: ~20K LOC
- 支持多人协作开发

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

基于项目宪法（.specify/memory/constitution.md）的检查项：

### I. 代码质量检查
- [x] 新模块是否符合 P3C 代码规范？
  - 使用Maven P3C插件，CI中强制执行
- [x] 公共 API 是否已设计 JavaDoc 注释？
  - 所有public类和方法必须有完整JavaDoc
- [x] 预估的方法/类复杂度是否在限制范围内（方法 ≤50 行，圈复杂度 ≤10）？
  - 使用SonarQube检查，超过阈值阻断合并

### II. 测试标准检查
- [x] 是否规划了单元测试覆盖（目标 ≥80%，关键路径 ≥90%）？
  - 核心模块(GameEngine, RenderSystem, ResourceManager)目标≥90%
- [x] 测试用例是否可在独立环境中运行（无外部依赖）？
  - 使用Headless模式测试，Mock窗口/GL上下文
- [x] 是否识别了需要集成测试的模块交互点？
  - 渲染-资源管理集成、输入-场景管理集成、音频-资源管理集成

### III. 用户体验一致性检查
- [x] UI 组件是否复用现有组件库？
  - 引擎提供统一UI组件(NinePatch, Label, Button)，示例游戏复用
- [x] 用户交互流程是否符合既有模式（左键选择、右键菜单等）？
  - 定义标准输入映射(InputMapping)，示例游戏遵循
- [x] 操作反馈是否在 100ms 内？耗时操作是否有进度指示？
  - 资源异步加载，提供进度回调接口
- [x] 错误场景是否设计了用户友好的提示信息？
  - 错误码体系，区分开发者错误和运行时错误

### IV. 性能要求检查
- [x] 新功能是否明确了性能预算（内存/CPU/渲染）？
  - 纹理内存≤512MB，Draw Call≤500/帧，GC暂停<5ms
- [x] 是否识别了潜在的性能瓶颈（大循环、频繁 GC、IO 操作）？
  - 精灵批处理、对象池、资源异步加载
- [x] 目标帧率（60 FPS）是否可维持？
  - 目标硬件Intel HD 620可稳定60 FPS渲染1000精灵

*如有违反，必须在"复杂度跟踪"部分说明理由*

**状态**: ✅ 所有检查项通过，无违反项

---

## Project Structure

### Documentation (this feature)

```text
specs/001-hybrid-game-engine/
├── plan.md              # This file - 实施计划
├── research.md          # Phase 0 - 技术调研
├── data-model.md        # Phase 1 - 数据模型设计
├── quickstart.md        # Phase 1 - 快速入门指南
├── contracts/           # Phase 1 - API契约定义
│   ├── public-api.md    # 公共API接口
│   └── event-protocol.md # 事件系统协议
└── tasks.md             # Phase 2 - 任务列表（由/speckit.tasks生成）
```

### Source Code (repository root)

```text
# Maven多模块结构
pom.xml                         # 根POM，定义依赖版本
├── engine/                     # 游戏引擎核心
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/xenoamess/cyan_potion/
│       │   │       ├── core/           # 引擎核心
│       │   │       │   ├── GameEngine.java
│       │   │       │   ├── GameLoop.java
│       │   │       │   └── GameWindow.java
│       │   │       ├── graphics/       # 渲染系统
│       │   │       │   ├── RenderSystem.java
│       │   │       │   ├── Renderer2D.java
│       │   │       │   ├── Renderer3D.java (接口)
│       │   │       │   ├── SpriteBatch.java
│       │   │       │   ├── Texture.java
│       │   │       │   ├── Shader.java
│       │   │       │   └── Camera.java
│       │   │       ├── resource/       # 资源管理
│       │   │       │   ├── ResourceManager.java
│       │   │       │   ├── Resource.java
│       │   │       │   ├── TextureLoader.java
│       │   │       │   └── AudioLoader.java
│       │   │       ├── input/          # 输入系统
│       │   │       │   ├── InputManager.java
│       │   │       │   ├── Keyboard.java
│       │   │       │   ├── Mouse.java
│       │   │       │   └── Gamepad.java
│       │   │       ├── audio/          # 音频系统
│       │   │       │   ├── AudioManager.java
│       │   │       │   ├── Sound.java
│       │   │       │   └── Music.java
│       │   │       ├── scene/          # 场景管理
│       │   │       │   ├── Scene.java
│       │   │       │   ├── GameObject.java
│       │   │       │   ├── Component.java
│       │   │       │   └── Transform.java
│       │   │       ├── ui/             # UI系统
│       │   │       │   ├── UIManager.java
│       │   │       │   ├── Widget.java
│       │   │       │   ├── Label.java
│       │   │       │   └── Button.java
│       │   │       └── math/           # 数学工具
│       │   │           └── ... (JOML包装)
│       │   └── resources/
│       │       └── shaders/            # 内置着色器
│       └── test/
│           └── java/
│               └── com/xenoamess/cyan_potion/
│                   ├── core/           # 核心测试
│                   ├── graphics/       # 渲染测试
│                   └── resource/       # 资源管理测试
│
├── engine-3d/                  # 3D扩展模块（可选）
│   ├── pom.xml
│   └── src/
│       └── main/java/
│           └── com/xenoamess/cyan_potion/
│               └── graphics3d/
│                   ├── Renderer3DImpl.java
│                   ├── Model.java
│                   ├── Mesh.java
│                   └── Material.java
│
├── engine-groovy/              # Groovy脚本支持（非核心）
│   ├── pom.xml
│   └── src/
│       └── main/groovy/
│           └── com/xenoamess/cyan_potion/
│               └── script/
│                   └── GroovyScriptEngine.java
│
└── demo-game/                  # 示例游戏
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/xenoamess/demo/
        │   │       ├── DemoGame.java
        │   │       ├── scenes/
        │   │       │   └── MainScene.java
        │   │       ├── entities/
        │   │       │   └── Player.java
        │   │       └── systems/
        │   │           └── PlayerController.java
        │   └── resources/
        │       ├── textures/
        │       ├── audio/
        │       └── config/
        └── test/
            └── java/
                └── com/xenoamess/demo/
                    └── GameTest.java
```

**Structure Decision**: 采用Maven多模块结构，engine为核心模块，engine-3d为可选3D扩展，engine-groovy为非核心脚本支持，demo-game为示例游戏。这种结构允许开发者按需依赖（纯2D游戏只需engine，需要3D时额外依赖engine-3d）。

---

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| 无 | - | - |

所有宪法检查项均已通过，无需额外说明。

---

## Phase 0: Research & Technology Decisions

参见 [research.md](research.md) - 包含以下决策：

1. **渲染API选择**: OpenGL 3.3 vs OpenGL 4.5 vs Vulkan
2. **数学库选择**: JOML vs 自研
3. **音频方案**: OpenAL vs 其他
4. **窗口管理**: GLFW vs SDL2
5. **资源加载**: 同步 vs 异步策略
6. **脚本支持**: Groovy集成方案

---

## Phase 1: Design Artifacts

### 数据模型
参见 [data-model.md](data-model.md) - 实体关系、状态转换、验证规则

### API契约
参见 [contracts/](contracts/) - 公共API定义和事件协议

### 快速入门
参见 [quickstart.md](quickstart.md) - 开发者30分钟上手指南

---

## Next Steps

1. 完成 research.md 技术调研
2. 完成 data-model.md 数据模型设计  
3. 完成 contracts/ API契约定义
4. 完成 quickstart.md 快速入门
5. 执行 update-agent-context.sh 更新Agent上下文
6. 运行 `/speckit.tasks` 生成任务列表
