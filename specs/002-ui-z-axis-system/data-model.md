# Data Model: UI组件Z轴坐标体系

**Feature**: UI组件Z轴坐标体系  
**Date**: 2026-03-22

---

## Entity Overview

```
┌─────────────────────────────┐
│ AbstractGameWindowComponent │
├─────────────────────────────┤
│ + leftTopPosX: float        │
│ + leftTopPosY: float        │
│ + z: float [NEW]            │
│ + width: float              │
│ + height: float             │
│ + coordinateSystemMode:     │
│   CoordinateSystemMode      │
│   [NEW]                     │
└─────────────┬───────────────┘
              │
              │ has-a
              ▼
┌─────────────────────────────┐
│ GameWindowComponentTreeNode │
├─────────────────────────────┤
│ + parent: TreeNode          │
│ + children: List<TreeNode>  │
│ + depth: int                │
│ - sortedChildren:           │
│   List<TreeNode> [NEW]      │
│ - sortDirty: boolean [NEW]  │
└─────────────────────────────┘

┌─────────────────────────────┐
│ CoordinateSystemMode        │
│ (Enum) [NEW]                │
├─────────────────────────────┤
│ LEGACY_MODE                 │
│ Z_AXIS_MODE                 │
└─────────────────────────────┘
```

---

## Entity Details

### AbstractGameWindowComponent

**Description**: UI组件基类，新增Z坐标和坐标体系模式支持

**Attributes**:

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| leftTopPosX | float | NaN | 组件左上角X坐标 |
| leftTopPosY | float | NaN | 组件左上角Y坐标 |
| **z** | **float** | **0.0f** | **组件Z坐标，值越大越靠前** |
| width | float | NaN | 组件宽度 |
| height | float | NaN | 组件高度 |
| **coordinateSystemMode** | **CoordinateSystemMode** | **LEGACY_MODE** | **坐标体系模式** |

**Methods (New)**:

```java
// Z坐标管理
public float getZ();
public void setZ(float z);

// 坐标体系模式管理
public CoordinateSystemMode getCoordinateSystemMode();
public void setCoordinateSystemMode(CoordinateSystemMode mode);

// 获取实际使用的坐标体系模式（考虑继承）
public CoordinateSystemMode getEffectiveCoordinateSystemMode();
```

**Invariants**:
- Z坐标可以是任意浮点值（正、负、零）
- 设置Z坐标后，父节点的排序缓存标记为脏
- 坐标体系模式可以运行时动态切换

---

### GameWindowComponentTreeNode

**Description**: 组件树节点，负责管理子组件和排序

**Attributes**:

| Field | Type | Description |
|-------|------|-------------|
| parent | GameWindowComponentTreeNode | 父节点（根节点为null） |
| children | List<GameWindowComponentTreeNode> | 子节点列表（原始顺序） |
| depth | int | 节点深度（根节点为0） |
| **sortedChildren** | **List<GameWindowComponentTreeNode>** | **按Z坐标排序后的子节点列表** |
| **sortDirty** | **boolean** | **排序缓存是否过期** |

**Methods (New)**:

```java
// 获取按Z坐标排序后的子节点
public List<GameWindowComponentTreeNode> getSortedChildren();

// 标记排序缓存为脏
public void markSortDirty();

// 获取Z感知的事件处理器
public EventProcessor getZAwareEventProcessor();
```

**Invariants**:
- `sortedChildren`仅在`sortedDirty=true`时重新计算
- Z_AXIS_MODE模式下使用`sortedChildren`进行渲染和事件处理
- LEGACY_MODE模式下忽略Z坐标，使用原始`children`列表

---

### CoordinateSystemMode (Enum)

**Description**: 坐标体系模式枚举

**Values**:

| Value | Description |
|-------|-------------|
| LEGACY_MODE | 传统模式：忽略Z坐标，按组件树遍历顺序渲染和处理事件 |
| Z_AXIS_MODE | Z轴模式：按Z坐标排序渲染，高Z优先响应事件 |

**Behavior**:

- **LEGACY_MODE**:
  - 渲染：按`children`列表顺序（添加顺序）
  - 事件：按`children`列表顺序遍历
  - 忽略`z`属性值

- **Z_AXIS_MODE**:
  - 渲染：按`sortedChildren`顺序（Z从低到高）
  - 事件：按`sortedChildren`反向遍历（Z从高到低）
  - `z`值决定层次关系

---

## Relationships

### Inheritance

```
AbstractGameWindowComponent
    △
    │
    ├─ AbstractControllableGameWindowComponent
    │       △
    │       │
    │       ├─ Button
    │       ├─ Panel
    │       ├─ Label
    │       └─ ... (所有现有UI组件)
    │
    └─ AbstractScene
            △
            │
            └─ ... (场景类)
```

所有继承`AbstractGameWindowComponent`的类自动获得Z坐标支持。

### Composition

```
GameWindowComponentTree
    │
    └── root: GameWindowComponentTreeNode
            │
            ├── children: List<GameWindowComponentTreeNode>
            │       │
            │       ├─ node1: GameWindowComponentTreeNode
            │       │       └── gameWindowComponent: Button(z=0)
            │       │
            │       ├─ node2: GameWindowComponentTreeNode
            │       │       └── gameWindowComponent: Panel(z=10)
            │       │
            │       └─ node3: GameWindowComponentTreeNode
            │               └── gameWindowComponent: Dialog(z=100)
            │
            └── sortedChildren: [node1, node2, node3] (by z ascending)
```

---

## State Transitions

### CoordinateSystemMode Transition

```
                    setMode(LEGACY_MODE)
    ┌─────────────────────────────────────────┐
    │                                         │
    ▼                                         │
┌──────────┐      setMode(Z_AXIS_MODE)    ┌──────────┐
│ LEGACY   │ ─────────────────────────────►│ Z_AXIS   │
│ _MODE    │                               │ _MODE    │
└──────────┘                               └──────────┘
```

**Transition Rules**:
- 切换模式可运行时进行
- 切换后立即生效（下一帧）
- 子组件继承父组件模式（除非显式覆盖）

### Z Coordinate Change Flow

```
setZ(newZ)
    │
    ▼
┌──────────────────┐
│ Update z field   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Mark parent's    │
│ sortDirty = true │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ Next render:     │
│ Re-sort children │
└──────────────────┘
```

---

## Validation Rules

1. **Z Coordinate**:
   - 类型：float
   - 范围：无限制（允许负值）
   - 默认值：0.0f

2. **CoordinateSystemMode**:
   - 不可为null
   - 默认：LEGACY_MODE
   - 运行时允许修改

3. **Sorted Children Cache**:
   - 任何子节点的Z坐标变化 → `sortDirty = true`
   - 子节点添加/移除 → `sortDirty = true`
   - 获取排序列表时检查`sortDirty`，必要时重新排序

---

## Performance Considerations

| Operation | Complexity | Notes |
|-----------|------------|-------|
| getZ() | O(1) | 直接字段访问 |
| setZ() | O(1) | 标记脏，不立即排序 |
| getSortedChildren() | O(1) best, O(n log n) worst | 仅在脏时排序 |
| 事件分发 | O(k) | k为命中测试通过的组件数 |

**Optimization**: 脏标记策略确保排序只在必要时执行。
