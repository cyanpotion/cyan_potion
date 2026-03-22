# Quick Start: UI组件Z轴坐标体系

**Feature**: UI组件Z轴坐标体系  
**预计时间**: 15分钟  
**日期**: 2026-03-22

---

## 目标

在本指南结束时，你将：
1. 理解Z轴坐标体系的基本概念
2. 学会为UI组件设置Z坐标
3. 学会切换坐标体系模式（Z_AXIS_MODE/LEGACY_MODE）
4. 掌握常见使用场景（模态对话框、下拉菜单等）

---

## 前置要求

- 已完成cyan_potion引擎基础环境配置
- 熟悉`AbstractGameWindowComponent`和组件树概念
- 理解基本的UI组件使用（Button, Panel等）

---

## 步骤 1: 启用Z轴模式 (5分钟)

### 1.1 为组件启用Z轴体系

```java
import com.xenoamess.cyan_potion.base.game_window_components.zsupport.CoordinateSystemMode;

// 创建一个面板并启用Z轴模式
Panel uiPanel = new Panel(gameWindow);
uiPanel.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);

// 添加子组件，它们将自动继承Z轴模式
Button button = new Button(gameWindow, "Click Me");
button.setZ(0);
uiPanel.add(button);

// 创建覆盖在上方的提示框
Label tooltip = new Label(gameWindow, "This is a tooltip");
tooltip.setZ(10);  // Higher Z, renders on top
tooltip.setLeftTopPosX(button.getLeftTopPosX());
tooltip.setLeftTopPosY(button.getLeftTopPosY() - 30);
uiPanel.add(tooltip);
```

### 1.2 预期结果

- `tooltip`（Z=10）渲染在`button`（Z=0）上方
- 点击重叠区域，`tooltip`优先接收点击事件

---

## 步骤 2: 创建模态对话框 (5分钟)

### 2.1 实现模态对话框

```java
public class ModalDialog extends Panel {
    private static final float MODAL_Z_INDEX = 1000f;
    
    public ModalDialog(GameWindow gameWindow, String title) {
        super(gameWindow);
        
        // 启用Z轴模式，设置极高的Z值
        this.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        this.setZ(MODAL_Z_INDEX);
        
        // 设置尺寸和位置（居中显示）
        this.setWidth(400);
        this.setHeight(300);
        this.setLeftTopPosX((gameWindow.getWidth() - 400) / 2f);
        this.setLeftTopPosY((gameWindow.getHeight() - 300) / 2f);
        
        // 添加标题
        Label titleLabel = new Label(gameWindow, title);
        titleLabel.setZ(1);  // 在对话框内部，Z相对于父组件
        titleLabel.setLeftTopPosX(20);
        titleLabel.setLeftTopPosY(20);
        this.add(titleLabel);
        
        // 添加关闭按钮
        Button closeButton = new Button(gameWindow, "X");
        closeButton.setZ(1);
        closeButton.setLeftTopPosX(360);
        closeButton.setLeftTopPosY(10);
        closeButton.setOnClick(() -> this.close());
        this.add(closeButton);
    }
}

// 使用
ModalDialog dialog = new ModalDialog(gameWindow, "Settings");
dialog.setVisible(true);
// dialog会覆盖所有Z值低于1000的组件
```

### 2.2 添加背景遮罩

```java
// 创建半透明背景遮罩（阻止点击穿透）
Panel overlay = new Panel(gameWindow);
overlay.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
overlay.setZ(999);  // 低于对话框，高于其他UI
overlay.setWidth(gameWindow.getWidth());
overlay.setHeight(gameWindow.getHeight());
overlay.setColor(new Color(0, 0, 0, 0.5f));  // 半透明黑色

// 阻止事件穿透到下层组件
overlay.setOnClick(event -> {
    // 消费事件，不传递给下层
    return true;
});
```

---

## 步骤 3: 处理动态Z坐标 (5分钟)

### 3.1 运行时修改Z坐标

```java
// 实现拖拽时组件置顶的效果
DraggablePanel draggable = new DraggablePanel(gameWindow) {
    private float originalZ;
    
    @Override
    public void onDragStart() {
        originalZ = this.getZ();
        // 拖拽开始时提升到最高层
        this.setZ(100f);
    }
    
    @Override
    public void onDragEnd() {
        // 拖拽结束恢复原Z值
        this.setZ(originalZ);
    }
};
```

### 3.2 实现下拉菜单

```java
public class DropdownMenu extends Panel {
    private Button triggerButton;
    private Panel menuPanel;
    private boolean isOpen = false;
    
    public DropdownMenu(GameWindow gameWindow, String label) {
        super(gameWindow);
        this.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        triggerButton = new Button(gameWindow, label);
        triggerButton.setZ(0);
        triggerButton.setOnClick(() -> toggleMenu());
        this.add(triggerButton);
        
        menuPanel = new Panel(gameWindow);
        menuPanel.setZ(50);  // 打开时覆盖在其他组件上方
        menuPanel.setVisible(false);
        this.add(menuPanel);
    }
    
    private void toggleMenu() {
        isOpen = !isOpen;
        menuPanel.setVisible(isOpen);
        if (isOpen) {
            // 打开时进一步提升Z值确保在最上层
            this.setZ(100f);
        } else {
            this.setZ(0f);
        }
    }
}
```

---

## 向后兼容指南

### 现有代码无需修改

```java
// 以下代码保持原有行为，不受Z轴功能影响
Button oldButton = new Button(gameWindow, "Old Style");
oldButton.setLeftTopPosX(100);
oldButton.setLeftTopPosY(100);
// 默认LEGACY_MODE，行为与之前完全一致
```

### 逐步迁移策略

```java
// 1. 先在新组件上启用Z轴
Panel newPanel = new Panel(gameWindow);
newPanel.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);

// 2. 混合使用（Legacy和Z轴模式可以共存）
oldPanel.add(newPanel);  // 正常工作

// 3. 需要时再迁移旧组件
oldButton.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
oldButton.setZ(0);
```

---

## 最佳实践

### Z值范围建议

| 层级 | Z值范围 | 用途 |
|------|---------|------|
| 背景 | -100 ~ -1 | 背景图、底层面板 |
| 普通UI | 0 ~ 99 | 按钮、标签、输入框 |
| 浮动元素 | 100 ~ 499 | 下拉菜单、工具提示 |
| 模态层 | 500 ~ 999 | 侧边栏、浮动面板 |
| 对话框 | 1000+ | 模态对话框、警告框 |

### 性能建议

1. **避免频繁修改Z坐标** - 每次修改会标记排序缓存为脏
2. **合理分组** - 将需要Z排序的组件放在同一个Z_AXIS_MODE父组件下
3. **不要滥用极高Z值** - 使用合理的层级范围，便于维护

---

## 故障排除

### 组件不显示在最上层

**检查清单**:
- [ ] 父组件是否启用了Z_AXIS_MODE？
- [ ] Z值是否设置正确？
- [ ] 是否有其他组件设置了更高的Z值？

### 点击事件穿透

**解决方案**:
```java
// 在模态对话框下方添加遮罩层
overlay.setOnClick(event -> true);  // 消费事件，阻止穿透
```

### 性能问题

**诊断**:
```java
// 检查Z坐标修改频率
@Override
public void setZ(float z) {
    super.setZ(z);
    System.out.println("Z changed to: " + z);
}
```

---

## 示例代码

完整示例参见 `src/demo/src/main/java/com/xenoamess/cyan_potion/demo/ZAxisExample.java`

---

## 参考文档

| 文档 | 路径 |
|------|------|
| API参考 | `specs/002-ui-z-axis-system/contracts/public-api.md` |
| 数据模型 | `specs/002-ui-z-axis-system/data-model.md` |
| 技术调研 | `specs/002-ui-z-axis-system/research.md` |
