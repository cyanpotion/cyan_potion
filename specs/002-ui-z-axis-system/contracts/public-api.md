# Public API Contract: UI组件Z轴坐标体系

**Feature**: UI组件Z轴坐标体系  
**Date**: 2026-03-22

---

## API Overview

本API扩展了`AbstractGameWindowComponent`类，添加Z坐标支持和坐标体系模式切换功能。

---

## CoordinateSystemMode Enum

```java
package com.xenoamess.cyan_potion.base.game_window_components.zsupport;

/**
 * 坐标体系模式枚举
 */
public enum CoordinateSystemMode {
    /**
     * 传统模式：忽略Z坐标，按组件树遍历顺序渲染和处理事件
     */
    LEGACY_MODE,
    
    /**
     * Z轴模式：按Z坐标排序渲染，高Z优先响应事件
     */
    Z_AXIS_MODE
}
```

---

## AbstractGameWindowComponent Extensions

### Z坐标管理

```java
/**
 * 获取组件的Z坐标
 * 
 * @return Z坐标值，值越大表示越靠前（覆盖其他组件）
 */
public float getZ();

/**
 * 设置组件的Z坐标
 * 
 * @param z Z坐标值，可以是任意浮点数（正、负、零）
 * 
 * @apiNote 设置Z坐标会自动标记父节点的排序缓存为脏，
 *          下次渲染时会重新排序。不会立即触发排序。
 */
public void setZ(float z);
```

### 坐标体系模式管理

```java
/**
 * 获取组件的坐标体系模式
 * 
 * @return 当前设置的坐标体系模式，默认为LEGACY_MODE
 */
public CoordinateSystemMode getCoordinateSystemMode();

/**
 * 设置组件的坐标体系模式
 * 
 * @param mode 坐标体系模式，不能为null
 * @throws IllegalArgumentException 如果mode为null
 * 
 * @apiNote 设置为Z_AXIS_MODE后，该组件及其子组件（除非显式覆盖）
 *          将使用Z坐标进行渲染排序和事件分发
 */
public void setCoordinateSystemMode(CoordinateSystemMode mode);

/**
 * 获取实际生效的坐标体系模式
 * 
 * 如果当前组件未显式设置模式，则继承父组件的模式。
 * 如果父组件也未设置，则使用默认值LEGACY_MODE。
 * 
 * @return 实际生效的坐标体系模式
 */
public CoordinateSystemMode getEffectiveCoordinateSystemMode();
```

---

## Usage Examples

### Example 1: Basic Z-Index Usage

```java
// 创建两个重叠的按钮
Button button1 = new Button(gameWindow, "Button 1");
button1.setLeftTopPosX(100);
button1.setLeftTopPosY(100);
button1.setZ(0);
button1.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);

Button button2 = new Button(gameWindow, "Button 2");
button2.setLeftTopPosX(100);
button2.setLeftTopPosY(100);
button2.setZ(10);  // Higher Z, will be rendered on top
button2.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);

// button2 will render above button1 and receive click events first
```

### Example 2: Modal Dialog (High Z-Index)

```java
// 创建模态对话框，确保覆盖所有其他UI元素
Panel modalDialog = new Panel(gameWindow);
modalDialog.setLeftTopPosX(200);
modalDialog.setLeftTopPosY(150);
modalDialog.setWidth(400);
modalDialog.setHeight(300);
modalDialog.setZ(1000);  // Very high Z to cover everything
modalDialog.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
```

### Example 3: Legacy Mode (Backward Compatibility)

```java
// 不修改任何代码，保持原有行为
Button legacyButton = new Button(gameWindow, "Legacy Button");
legacyButton.setLeftTopPosX(100);
legacyButton.setLeftTopPosY(100);
// Z coordinate defaults to 0
// Mode defaults to LEGACY_MODE - existing behavior preserved
```

### Example 4: Parent-Child Inheritance

```java
// 父组件启用Z轴模式
Panel parent = new Panel(gameWindow);
parent.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);

// 子组件自动继承Z轴模式（除非显式覆盖）
Button child = new Button(gameWindow, "Child Button");
parent.add(child);  // child.getEffectiveCoordinateSystemMode() == Z_AXIS_MODE

// 子组件可以显式覆盖
child.setCoordinateSystemMode(CoordinateSystemMode.LEGACY_MODE);
```

---

## Behavior Contracts

### Rendering Behavior

| Mode | Rendering Order | Z Coordinate Effect |
|------|-----------------|---------------------|
| LEGACY_MODE | 按添加顺序（children列表顺序） | 忽略，不影响渲染 |
| Z_AXIS_MODE | 按Z坐标从低到高排序 | 决定渲染顺序，高Z后渲染（覆盖） |

### Event Handling Behavior

| Mode | Event Processing Order | Z Coordinate Effect |
|------|------------------------|---------------------|
| LEGACY_MODE | 按添加顺序遍历 | 忽略，第一个命中的组件接收事件 |
| Z_AXIS_MODE | 按Z坐标从高到低遍历 | 高Z组件优先接收事件 |

### Mode Inheritance

```
Parent (Z_AXIS_MODE)
├── Child1 (no explicit mode) → inherits Z_AXIS_MODE
├── Child2 (LEGACY_MODE) → uses LEGACY_MODE (override)
└── Child3 (Z_AXIS_MODE) → uses Z_AXIS_MODE (explicit)

Parent (LEGACY_MODE)
├── Child1 (no explicit mode) → inherits LEGACY_MODE
├── Child2 (Z_AXIS_MODE) → uses Z_AXIS_MODE (override)
└── Child3 (no explicit mode) → inherits LEGACY_MODE
```

---

## Constraints & Limitations

1. **Z Coordinate Range**: 无限制，但极大/极小值可能导致浮点精度问题
2. **Same Z Value**: 相同Z值的组件保持添加顺序（稳定排序）
3. **Mode Switching**: 可以在运行时切换模式，但建议在初始化时设置
4. **Memory Overhead**: 每个组件增加约12字节（float z + enum引用）
5. **Performance**: Z坐标排序使用脏标记策略，仅在必要时排序

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2026-03-22 | Initial API |
