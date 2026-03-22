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
# Maven多模块结构 (现有项目结构)
pom.xml                              # 根POM，聚合所有模块
├── src/parent/                      # 父POM，统一定义依赖版本
│   └── pom.xml
│
├── src/base/                        # 引擎基础模块 (cyan_potion base)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/xenoamess/cyan_potion/base/
│       │   ├── areas/               # 区域/场景管理
│       │   ├── audio/               # 音频系统 (OpenAL)
│       │   ├── console/             # 控制台/调试
│       │   ├── events/              # 事件系统
│       │   ├── exceptions/          # 异常定义
│       │   ├── game_window_components/  # 窗口组件
│       │   ├── io/                  # 输入/输出、文件操作
│       │   │   ├── input/           # 键盘、鼠标、手柄输入
│       │   │   └── resource/        # 资源加载管理
│       │   ├── math/                # 数学工具 (JOML包装)
│       │   ├── memory/              # 内存管理
│       │   ├── modified_sources/    # 第三方修改代码
│       │   ├── plugins/             # 插件系统
│       │   ├── render/              # 2D渲染系统
│       │   ├── runtime/             # 运行时管理
│       │   ├── setting_file/        # 配置文件
│       │   ├── steam/               # Steam集成
│       │   └── visual/              # 视觉效果
│       ├── main/resources/          # 资源文件
│       └── test/java/               # 单元测试
│
├── src/coordinate/                  # 坐标/物理模块
│   ├── pom.xml
│   └── src/main/java/com/xenoamess/cyan_potion/coordinate/
│       ├── entity/                  # 实体定义
│       └── physic/                  # 物理系统
│           ├── shapes/              # 碰撞形状
│           └── shape_relation_judges/   # 碰撞检测
│
├── src/rpg_module/                  # RPG游戏模块
│   ├── pom.xml
│   └── src/main/java/com/xenoamess/cyan_potion/rpg_module/
│       ├── event_unit/              # 事件单元
│       ├── event_unit_program_language_grammar/  # 脚本语法
│       ├── game_map/                # 游戏地图
│       ├── jsons/                   # JSON数据定义
│       ├── plugins/                 # RPG插件
│       ├── render/                  # RPG渲染
│       ├── units/                   # 游戏单位
│       └── world/                   # 世界管理
│
├── src/xenoamess-s-civilization/    # 主游戏模块 (xenoamess's-civilization)
│   ├── pom.xml
│   └── src/
│       ├── main/java/               # 游戏逻辑
│       ├── main/resources/          # 游戏资源
│       └── test/java/               # 游戏测试
│
└── src/demo/                        # 演示/示例模块
    ├── pom.xml
    └── src/
        ├── main/java/               # 演示代码
        └── test/java/               # 演示测试
├── src/extension_groovy/              # Groovy脚本支持（非核心）
│   ├── pom.xml
│   └── src/
│       └── main/groovy/
│           └── com/xenoamess/cyan_potion/extension_groovy
│               └── script/
│                   └── GroovyScriptEngine.java
```

**Structure Decision**: 项目采用Maven多模块结构，基于已存在的cyan_potion引擎架构：
- **src/parent**: 父POM，统一管理依赖版本（LWJGL3、Apache Commons等）
- **src/base**: 引擎核心，包含渲染、音频、输入、事件等基础系统
- **src/coordinate**: 坐标和物理系统，处理2D空间计算和碰撞检测
- **src/rpg_module**: RPG游戏通用模块，提供RPG游戏常用功能
- **src/xenoamess-s-civilization**: 具体游戏实现，依赖上述模块
- **src/demo**: 示例和演示代码

**3D扩展预留**: 如需添加3D支持，可新建 `src/engine-3d/` 模块，在 `src/base/render/` 基础上扩展3D渲染能力，保持2D模块不受影响。

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
