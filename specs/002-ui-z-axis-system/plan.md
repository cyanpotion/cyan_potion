# Implementation Plan: UI组件Z轴坐标体系

**Branch**: `002-ui-z-axis-system` | **Date**: 2026-03-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/002-ui-z-axis-system/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

---

## Summary

为cyan_potion引擎的UI组件树添加Z轴坐标支持，实现基于Z值的层次渲染和点击事件分发。通过双模式设计（Z_AXIS_MODE/LEGACY_MODE）确保与现有代码100%向后兼容。

技术方案：
- 在`AbstractGameWindowComponent`添加`z`坐标属性和`CoordinateSystemMode`枚举
- 实现`ZIndexSorter`处理Z坐标排序逻辑
- 修改渲染管线支持按Z坐标排序绘制
- 修改事件分发器支持按Z坐标反向遍历（高Z优先）

---

## Technical Context

**Language/Version**: Java 17+ (兼容 JDK 21)  
**Primary Dependencies**: cyan_potion base模块 (现有组件树系统)  
**Storage**: N/A (运行时内存状态)  
**Testing**: JUnit 5 + Mockito  
**Target Platform**: Windows 10+, Linux (Ubuntu 20.04+)  
**Project Type**: 游戏引擎UI组件扩展  
**Performance Goals**: Z坐标排序 < 1ms/1000组件, 60 FPS维持  
**Constraints**: 
- 向后兼容：现有代码无需修改即可工作
- 内存开销：每个组件新增8字节(z float) + 4字节(enum引用)
- 无外部依赖：仅使用Java标准库和引擎现有工具  
**Scale/Scope**: 
- 影响范围：所有继承`AbstractGameWindowComponent`的组件
- 预计代码量：新增~500行，修改~200行

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

基于项目宪法（.specify/memory/constitution.md）的检查项：

### I. 代码质量检查
- [X] 新模块是否符合 P3C 代码规范？
  - 使用现有代码风格，P3C插件已配置
- [X] 公共 API 是否已设计 JavaDoc 注释？
  - 新增`setZ()`, `getZ()`, `setCoordinateSystemMode()`, `getCoordinateSystemMode()`等方法
- [X] 预估的方法/类复杂度是否在限制范围内（方法 ≤50 行，圈复杂度 ≤10）？
  - 排序逻辑封装在`ZIndexSorter`，事件处理逻辑保持简单

### II. 测试标准检查
- [X] 是否规划了单元测试覆盖（目标 ≥80%，关键路径 ≥90%）？
  - Z轴排序逻辑、事件分发逻辑需≥90%覆盖
- [X] 测试用例是否可在独立环境中运行（无外部依赖）？
  - 使用Mock组件，无需真实窗口/GL上下文
- [X] 是否识别了需要集成测试的模块交互点？
  - 渲染集成测试、事件集成测试

### III. 用户体验一致性检查
- [X] UI 组件是否复用现有组件库？
  - 扩展现有基类，不新增组件类型
- [X] 用户交互流程是否符合既有模式（左键选择、右键菜单等）？
  - Legacy模式保持原有行为
- [X] 操作反馈是否在 100ms 内？耗时操作是否有进度指示？
  - Z坐标修改即时生效，无耗时操作
- [X] 错误场景是否设计了用户友好的提示信息？
  - 提供清晰的API文档说明模式切换影响

### IV. 性能要求检查
- [X] 新功能是否明确了性能预算（内存/CPU/渲染）？
  - 内存：每个组件~12字节开销
  - CPU：排序<1ms/1000组件
  - 渲染：无额外开销
- [X] 是否识别了潜在的性能瓶颈（大循环、频繁 GC、IO 操作）？
  - 风险：每帧排序可能导致性能问题
  - 缓解：仅当Z坐标变化时重新排序，缓存排序结果
- [X] 目标帧率（60 FPS）是否可维持？
  - 是，排序优化后开销可忽略

*宪法检查通过*

---

## Project Structure

### Documentation (this feature)

```text
specs/002-ui-z-axis-system/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
src/base/src/main/java/com/xenoamess/cyan_potion/base/
├── game_window_components/
│   ├── AbstractGameWindowComponent.java    # 添加z坐标和模式枚举
│   ├── GameWindowComponentTreeNode.java    # 添加Z坐标排序支持
│   ├── GameWindowComponentTree.java        # 添加排序渲染方法
│   └── zsupport/                           # 新增包
│       ├── CoordinateSystemMode.java       # 坐标体系枚举
│       ├── ZIndexSorter.java               # Z坐标排序器
│       └── ZAwareEventProcessor.java       # Z感知事件处理器
└── events/
    └── Event.java                            # 可能需要扩展

src/base/src/test/java/com/xenoamess/cyan_potion/base/
├── game_window_components/
│   └── zsupport/                           # 测试包
│       ├── ZIndexSorterTest.java           # 排序测试
│       ├── ZCoordinateTest.java            # Z坐标功能测试
│       └── LegacyCompatibilityTest.java    # 向后兼容测试
```

**Structure Decision**: 采用扩展现有类的方案，在`game_window_components`包下新增`zsupport`子包存放Z轴相关的新类，保持代码组织清晰。

---

## Complexity Tracking

> 无宪法违规项

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |

---

## Implementation Phases

### Phase 0: Research

已完成，详见 [research.md](research.md)

### Phase 1: Design

已完成，详见：
- [data-model.md](data-model.md) - 数据模型
- [contracts/](contracts/) - API契约
- [quickstart.md](quickstart.md) - 快速入门

### Phase 2: Tasks

待生成，运行 `/speckit.tasks` 生成任务列表
