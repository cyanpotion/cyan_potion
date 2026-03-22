# Research: UI组件Z轴坐标体系

**Feature**: UI组件Z轴坐标体系  
**Date**: 2026-03-22

---

## Research Areas

### 1. Z坐标排序算法

**问题**: 如何高效地对组件按Z坐标排序？

**调研**:
- **TimSort** (Java Collections.sort默认): O(n log n)，稳定排序
- **计数排序**: O(n+k)，仅适用于小范围整数Z值
- **缓存排序结果**: 仅当Z坐标变化时重新排序

**决策**: 使用TimSort + 缓存策略
- **理由**: 1. Java原生支持，无需额外依赖 2. 稳定性保证相同Z值保持原有顺序 3. 缓存避免每帧排序开销
- **实现**: 在`ZIndexSorter`中维护一个脏标记，Z坐标变化时标记为脏，下次渲染前重新排序

### 2. 事件分发策略

**问题**: 如何处理Z轴层次的事件分发？

**调研**:
- **选项A**: 反向遍历（从高Z到低Z），第一个命中的组件接收事件
- **选项B**: 收集所有命中组件，按Z排序后选择最高的
- **选项C**: 使用空间分割结构（如四叉树）加速命中测试

**决策**: 选项A（反向遍历）
- **理由**: 1. 实现简单，与现有代码集成容易 2. 大多数情况不需要遍历所有组件 3. 保持与现有事件处理流程一致
- **实现**: 修改`GameWindowComponentTree`的事件处理方法，添加按Z坐标排序后的反向遍历

### 3. 向后兼容方案

**问题**: 如何确保现有代码不修改即可工作？

**调研**:
- **方案A**: 新增Z轴相关的方法，默认行为保持不变
- **方案B**: 使用枚举切换模式（Z_AXIS_MODE/LEGACY_MODE）
- **方案C**: 通过继承创建新类（ZAwareComponent），旧类保持不变

**决策**: 方案B（枚举模式切换）
- **理由**: 1. 显式声明意图，避免意外行为变化 2. 允许混合使用（部分组件Z轴，部分Legacy）3. 可以在运行时动态切换
- **实现**: 添加`CoordinateSystemMode`枚举，默认值为`LEGACY_MODE`，确保现有代码行为不变

### 4. 性能优化策略

**问题**: 如何避免Z坐标排序成为性能瓶颈？

**调研**:
- **策略A**: 仅对可见组件排序
- **策略B**: 分层排序（按Z值范围分组）
- **策略C**: 脏标记+缓存排序结果

**决策**: 策略C（脏标记+缓存）
- **理由**: 1. Z坐标变化频率远低于渲染频率 2. 实现简单，维护成本低 3. 1000个组件排序<1ms，完全可接受
- **实现细节**:
  - `ZIndexSorter`维护排序后的组件列表
  - `setZ()`方法标记排序列表为脏
  - `getSortedChildren()`在需要时重新排序并缓存

---

## Technical Decisions

| Decision | Rationale | Alternatives Rejected |
|----------|-----------|----------------------|
| 使用`float`类型表示Z坐标 | 支持子像素精度，与X/Y坐标类型一致 | `int`: 精度不足；`double`: 内存开销大 |
| 默认`LEGACY_MODE` | 确保100%向后兼容 | `Z_AXIS_MODE`: 会破坏现有代码 |
| Z坐标继承传播 | 简化配置，确保子组件行为一致 | 不继承: 需要手动配置每个子组件 |
| 相同Z值保持树遍历顺序 | 符合开发者直觉，便于调试 | 按其他属性排序: 增加复杂度 |
| 脏标记缓存策略 | 优化常见情况（Z不频繁变化） | 每帧排序: 不必要的开销 |

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| 向后兼容性问题 | 低 | 高 | 全面单元测试覆盖Legacy模式 |
| 性能退化 | 低 | 中 | 脏标记缓存，基准测试验证 |
| API设计不合理 | 中 | 中 | 早期设计评审，文档先行 |
| 与现有代码冲突 | 低 | 高 | 小步提交，持续集成验证 |

---

## References

- Java Collections Framework: https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html
- cyan_potion existing component tree: `GameWindowComponentTree.java`
- Event handling in cyan_potion: `Event.java`, `AbstractGameWindowComponent.java`
