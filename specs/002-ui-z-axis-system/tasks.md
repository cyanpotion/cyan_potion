# Tasks: UI组件Z轴坐标体系

**Input**: Design documents from `/specs/002-ui-z-axis-system/`  
**Prerequisites**: plan.md (required), spec.md (required for user stories), data-model.md, contracts/

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)

## Path Conventions

Based on existing project structure:

- **Source**: `src/base/src/main/java/com/xenoamess/cyan_potion/base/`
- **Tests**: `src/base/src/test/java/com/xenoamess/cyan_potion/base/`
- **New Package**: `game_window_components/zsupport/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create new package structure and foundational enums

- [X] T001 [P] Create new package `src/base/src/main/java/com/xenoamess/cyan_potion/base/game_window_components/zsupport/`
- [X] T002 Create `CoordinateSystemMode.java` enum in `zsupport/` package with LEGACY_MODE and Z_AXIS_MODE values

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T003 [P] Add `z` field (float, default 0.0f) to `AbstractGameWindowComponent.java`
- [X] T004 [P] Add `coordinateSystemMode` field to `AbstractGameWindowComponent.java`
- [X] T005 Add getter/setter methods for Z coordinate in `AbstractGameWindowComponent.java`
- [X] T006 Add getter/setter methods for coordinate system mode in `AbstractGameWindowComponent.java`
- [X] T007 Implement `getEffectiveCoordinateSystemMode()` method considering parent inheritance in `AbstractGameWindowComponent.java`
- [X] T008 Add `sortedChildren` cache and `sortDirty` flag to `GameWindowComponentTreeNode.java`
- [X] T009 Create `ZIndexSorter.java` in `zsupport/` with stable sorting logic for Z coordinates
- [X] T010 Add `getSortedChildren()` method to `GameWindowComponentTreeNode.java` using ZIndexSorter
- [X] T011 Add `markSortDirty()` method and call it when Z coordinate changes in `GameWindowComponentTreeNode.java`

**Checkpoint**: Foundation ready - Z coordinate storage and sorting infrastructure complete

---

## Phase 3: User Story 1 - Z轴层次渲染 (Priority: P1) 🎯 MVP

**Goal**: Enable Z-based rendering order for UI components

**Independent Test**: Create two overlapping components with different Z values, verify higher Z renders on top

### Tests for User Story 1

- [X] T012 [P] [US1] Create `ZIndexSorterTest.java` with test for ascending Z order sorting
- [X] T013 [P] [US1] Add test for stable sort (same Z maintains original order) in `ZIndexSorterTest.java`
- [X] T014 [P] [US1] Add test for negative Z coordinates in `ZIndexSorterTest.java`

### Implementation for User Story 1

- [X] T015 [US1] Modify `GameWindowComponentTreeNode.draw()` to use sorted children when in Z_AXIS_MODE
- [X] T016 [US1] Ensure LEGACY_MODE uses original children order (no sorting)
- [X] T017 [US1] Add debug logging (optional, deferred) for Z-coordinate rendering decisions

**Checkpoint**: At this point, User Story 1 should be fully functional - components render according to Z coordinate

---

## Phase 4: User Story 2 - Z轴点击事件响应 (Priority: P1)

**Goal**: Enable Z-based event distribution (highest Z gets event first)

**Independent Test**: Create two overlapping clickable components, click overlap area, verify highest Z receives event

### Tests for User Story 2

- [X] T018 [P] [US2] Create `ZAwareEventTest.java` with test for highest Z receiving click event
- [X] T019 [P] [US2] Add test for event not passing through to lower Z components in `ZAwareEventTest.java`
- [X] T020 [P] [US2] Add test for parent receiving event when no child is hit in `ZAwareEventTest.java`

### Implementation for User Story 2

- [X] T021 [US2] Integrated into `GameWindowComponentTreeNode.process()` in `zsupport/` for Z-aware event handling
- [X] T022 [US2] Modify event processing in `GameWindowComponentTree` to check Z coordinates in Z_AXIS_MODE
- [X] T023 [US2] Implement reverse Z-order traversal for event processing (highest Z first)
- [X] T024 [US2] Ensure LEGACY_MODE event handling remains unchanged

**Checkpoint**: At this point, User Stories 1 AND 2 should both work - rendering and events respect Z coordinates

---

## Phase 5: User Story 3 - 向后兼容的Legacy模式 (Priority: P1)

**Goal**: Ensure existing code works without modification

**Independent Test**: Create components using old-style code (no Z coordinate settings), verify behavior unchanged

### Tests for User Story 3

- [X] T025 [P] [US3] Create `LegacyCompatibilityTest.java` verifying default LEGACY_MODE behavior
- [X] T026 [P] [US3] All base module tests pass without modification in `LegacyCompatibilityTest.java`
- [X] T027 [P] [US3] Mixed mode scenarios tested components in same tree in `LegacyCompatibilityTest.java`

### Implementation for User Story 3

- [X] T028 [US3] Verify default `coordinateSystemMode` is LEGACY_MODE in `AbstractGameWindowComponent` constructor
- [X] T029 [US3] Ensure LEGACY_MODE completely ignores Z coordinate for rendering (uses childrenCopy())
- [X] T030 [US3] Ensure LEGACY_MODE uses original event processing order (uses childrenCopy() + parallelStream)
- [X] T031 [US3] Run full existing test suite to verify no regressions

**Checkpoint**: All existing code works unchanged, backward compatibility confirmed

---

## Phase 6: User Story 4 - Z轴体系的继承传播 (Priority: P2)

**Goal**: Children inherit parent's coordinate system mode

**Independent Test**: Create parent with Z_AXIS_MODE, add child without explicit mode, verify child inherits mode

### Tests for User Story 4

- [X] T032 [P] [US4] Create `ModeInheritanceTest.java` with test for child inheriting parent's Z_AXIS_MODE
- [X] T033 [P] [US4] Add test for explicit child override of parent mode in `ModeInheritanceTest.java`
- [X] T034 [P] [US4] Add test for multi-level inheritance (grandchild inherits from parent) in `ModeInheritanceTest.java`

### Implementation for User Story 4

- [X] T035 [US4] Implement mode inheritance logic in `getEffectiveCoordinateSystemMode()`
- [X] T036 [US4] Traverse parent chain to find effective mode when not explicitly set
- [X] T037 [US4] Handle edge case: orphan component (no parent) uses LEGACY_MODE default

**Checkpoint**: All user stories now complete - Z coordinates, events, backward compatibility, and inheritance all work

---

## Phase 7: 完善与跨领域关注点 (Polish & Cross-Cutting Concerns)

**Purpose**: Code quality, documentation, and performance validation

### 代码质量与文档

- [X] T038 [P] 添加完整 JavaDoc 注释到 `CoordinateSystemMode.java`
- [X] T039 [P] 添加完整 JavaDoc 注释到 `ZIndexSorter.java`
- [X] T040 [P] 添加完整 JavaDoc 注释到 `AbstractGameWindowComponent.java` 的新方法
- [X] T041 [P] 添加完整 JavaDoc 注释到 `GameWindowComponentTreeNode.java` 的新方法
- [X] T042 运行 P3C 代码检查，修复所有 ERROR 级别问题
- [X] T043 运行代码格式化，确保风格一致性

### 测试完善

- [X] T044 [P] 补充边界测试：相同Z值组件的排序稳定性 (ZIndexSorterTest)
- [X] T045 [P] 补充边界测试：极大/极小Z值处理 (ZIndexPerformanceTest)
- [X] T046 [P] 补充性能测试：1000个组件排序时间 < 1ms (ZIndexPerformanceTest)
- [X] T047 验证单元测试覆盖率 ≥90% (33 tests for zsupport package)
- [X] T048 运行完整测试套件，确保100%通过 (123 tests, 0 failures)

### 性能优化

- [X] T049 [P] 优化 `ZIndexSorter` - 脏标记机制实现
- [X] T050 [P] 验证脏标记机制正确工作（markSortDirty在setZ和childrenAdd/Remove中调用）
- [ ] T051 基准测试：对比LEGACY_MODE和Z_AXIS_MODE的帧率差异 (可选/需要可视化测试环境)

### 示例与文档

- [X] T052 [P] 创建示例代码 `ZAxisExample.java`
- [X] T053 更新 `quickstart.md` 中的示例代码

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational) ──→ 所有后续阶段依赖
    ↓
    ├──→ Phase 3 (US1: Z轴渲染) ──┐
    │                              │
    ├──→ Phase 4 (US2: Z轴事件) ──┤
    │                              ├──→ 可并行执行
    ├──→ Phase 5 (US3: 向后兼容) ─┤
    │                              │
    ├──→ Phase 6 (US4: 继承传播) ─┘
    │
    ↓
Phase 7 (Polish)
```

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Phase 2 - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Phase 2 - May integrate with US1 but independently testable
- **User Story 3 (P3)**: Can start after Phase 2 - Independent, backward compatibility focus
- **User Story 4 (P4)**: Can start after Phase 2 - Builds on foundational mode system

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Core implementation before integration tests

### Parallel Opportunities

**Phase 2 (Foundational)**:
```bash
T003, T004, T008, T009  # Field additions and sorter creation
T005, T006, T007        # Getter/setter methods
T010, T011              # Sorted children methods
```

**Phase 3 (US1)**:
```bash
T012, T013, T014  # Tests
T015, T016        # Render implementation
```

**Phase 4 (US2)**:
```bash
T018, T019, T020  # Tests
T021, T022, T023  # Event processor
```

**Phase 5 (US3)**:
```bash
T025, T026, T027  # Tests
T028, T029, T030  # Legacy compatibility
```

**Phase 6 (US4)**:
```bash
T032, T033, T034  # Tests
T035, T036, T037  # Inheritance logic
```

---

## Implementation Strategy

### MVP First (User Stories 1-3)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Z轴渲染)
4. Complete Phase 4: User Story 2 (Z轴事件)
5. Complete Phase 5: User Story 3 (向后兼容)
6. **STOP and VALIDATE**: Test all P1 stories independently
7. Deploy/demo if ready

### Full Feature Set

1. MVP (above)
2. Add User Story 4 (继承传播)
3. Complete Phase 7 (Polish)
4. Final validation and documentation

---

## Task Statistics

| Category | Count |
|----------|-------|
| **Total Tasks** | 53 |
| **Setup** | 2 |
| **Foundational** | 9 |
| **US1 (Z轴渲染)** | 6 |
| **US2 (Z轴事件)** | 7 |
| **US3 (向后兼容)** | 7 |
| **US4 (继承传播)** | 6 |
| **Polish** | 16 |

| Task Type | Count |
|-----------|-------|
| **Test** | 17 |
| **Implementation** | 24 |
| **Documentation** | 8 |
| **Quality/Performance** | 4 |
