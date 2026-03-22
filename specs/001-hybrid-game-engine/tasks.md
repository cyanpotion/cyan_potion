# Tasks: 混合架构游戏引擎

**Input**: Design documents from `/specs/001-hybrid-game-engine/`  
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Tests**: Tests are included based on feature specification testing requirements (TR-001 to TR-004).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Path Conventions

Based on existing project structure:

- **Base Engine**: `src/base/src/main/java/com/xenoamess/cyan_potion/base/`
- **Base Tests**: `src/base/src/test/java/com/xenoamess/cyan_potion/base/`
- **Coordinate**: `src/coordinate/src/main/java/com/xenoamess/cyan_potion/coordinate/`
- **RPG Module**: `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/`
- **Demo**: `src/demo/src/main/java/com/xenoamess/cyan_potion/demo/`
- **Main Game**: `src/xenoamess-s-civilization/src/main/java/com/xenoamess/cyan_potion/xenoamess_s_civilization/`

---

## Phase 1: Setup (Build & CI Configuration)

**Purpose**: Update build configuration for JDK 17/21 dual support, Apache Commons libraries, and cross-platform CI

- [ ] T001 Update src/parent/pom.xml - Add dependency management for Apache Commons (Lang3, IO, Collections4), JOML, JUnit 5, Log4j2
- [ ] T002 [P] Configure src/base/pom.xml - Update dependencies to use Apache Commons instead of Guava
- [ ] T003 [P] Configure P3C Maven plugin in src/parent/pom.xml for code quality checks
- [ ] T004 [P] Configure JaCoCo in src/parent/pom.xml for test coverage reporting (target 80%+)
- [ ] T005 Create/update CI workflow .github/workflows/ci.yml for Windows and Linux matrix builds with JDK 17 and 21
- [ ] T006 Verify LWJGL3 native library configuration works for both Windows and Linux

---

## Phase 2: Foundational (Core Engine Infrastructure)

**Purpose**: Core infrastructure in src/base that MUST be complete before user stories

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### 2.1 Math & Utility Foundation (src/base/src/main/java/com/xenoamess/cyan_potion/base/)

- [ ] T007 [P] Update/enhance math utilities in `math/` - Ensure JOML integration, Vector2f/Vector3f wrappers, Color class
- [ ] T008 [P] Update io utilities in `io/` - Cross-platform path handling using Apache Commons IO
- [ ] T009 Create utility class for platform detection in `runtime/Platform.java` - isWindows(), isLinux()

### 2.2 Configuration & Logging

- [ ] T010 Update logging configuration - Ensure Log4j2 is properly configured in src/base
- [ ] T011 Review and update setting_file configuration system for JDK 17/21 compatibility

### 2.3 Exception Hierarchy

- [ ] T012 [P] Review exceptions in `exceptions/` - Ensure proper hierarchy, add GameEngineException if needed

### 2.4 Event System Foundation

- [ ] T013 Review/enhance Event system in `events/` - Ensure EventBus supports the contracts defined in event-protocol.md

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 2D Game Creation (Priority: P1) 🎯 MVP

**Goal**: Enable developers to create 2D games with window, rendering, input, and basic scene management

**Independent Test**: Developer can create a window, render a sprite, and handle keyboard input to move the sprite

### Tests for User Story 1

- [ ] T014 [P] [US1] Unit test for GameWindow in `src/base/src/test/java/com/xenoamess/cyan_potion/base/game_window_components/GameWindowTest.java`
- [ ] T015 [P] [US1] Unit test for render system in `src/base/src/test/java/com/xenoamess/cyan_potion/base/render/RenderSystemTest.java`
- [ ] T016 [P] [US1] Unit test for input handling in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/input/InputManagerTest.java`
- [ ] T017 [P] [US1] Unit test for resource management in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/resource/ResourceManagerTest.java`

### Implementation for User Story 1

#### Window & Runtime System (src/base/src/main/java/com/xenoamess/cyan_potion/base/)

- [ ] T018 [US1] Review/update GameWindow in `game_window_components/GameWindow.java`
  - Ensure GLFW window creation works on Windows and Linux
  - Verify windowed/fullscreen toggle
  - Add event callbacks for resize, focus, close
- [ ] T019 [US1] Review/update runtime management in `runtime/`
  - Ensure game loop supports fixed timestep update
  - Verify frame statistics (FPS, delta time)

#### Render System (src/base/src/main/java/com/xenoamess/cyan_potion/base/render/)

- [ ] T020 [P] [US1] Review/update Camera (Orthographic) in `render/`
- [ ] T021 [P] [US1] Review/update Shader support in `render/`
  - Verify GLSL shader loading/compiling
  - Add default sprite shaders if missing
- [ ] T022 [P] [US1] Review/update Texture in `render/` or `visual/`
  - Ensure OpenGL texture creation works
  - Verify PNG/JPG loading via stb_image
- [ ] T023 [US1] Review/update sprite batch rendering
  - Verify VBO/VAO/EBO management
  - Ensure batching algorithm supports 1000+ sprites
  - Target: Max 10000 sprites per batch
- [ ] T024 [US1] Review/update Renderer2D functionality
  - Ensure draw(Texture, x, y, width, height) works
  - Verify draw(Texture, Transform) supports rotation/scale
  - Add drawRect() for solid colors if missing

#### Input System (src/base/src/main/java/com/xenoamess/cyan_potion/base/io/input/)

- [ ] T025 [P] [US1] Review/update Keyboard input in `io/input/`
  - Ensure isDown(), isPressed(), isReleased() work correctly
- [ ] T026 [P] [US1] Review/update Mouse input in `io/input/`
  - Verify position tracking, button states, scroll wheel
- [ ] T027 [US1] Review/update InputManager in `io/input/`
  - Ensure GLFW input callbacks work on both platforms
  - Verify event dispatch to EventBus

#### Resource Management (src/base/src/main/java/com/xenoamess/cyan_potion/base/io/resource/)

- [ ] T028 [P] [US1] Review/update Resource base class in `io/resource/`
- [ ] T029 [P] [US1] Review/update TextureLoader in `io/resource/`
- [ ] T030 [US1] Review/update ResourceManager in `io/resource/`
  - Ensure LRU cache implementation
  - Verify reference counting
  - Add async loading support if missing

#### Audio System (src/base/src/main/java/com/xenoamess/cyan_potion/base/audio/)

- [ ] T031 [P] [US1] Review/update AudioManager in `audio/`
  - Verify OpenAL integration works on Windows and Linux
  - Ensure volume control works
  - Verify OGG loading support
- [ ] T032 [P] [US1] Review/update Sound (short audio) support
- [ ] T033 [P] [US1] Review/update Music (streaming) support

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 3 - Cross-Platform Support (Priority: P1) 🎯 MVP

**Goal**: Ensure engine compiles and runs identically on Windows and Linux

**Independent Test**: Same game code compiles and runs on both Windows and Linux with pixel-identical output

**Note**: This phase can proceed in parallel with US1 once foundational phase is complete

### Tests for User Story 3

- [ ] T034 [P] [US3] Unit test for path normalization in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/PathUtilTest.java`
- [ ] T035 [P] [US3] Cross-platform file access test in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/CrossPlatformFileTest.java`
- [ ] T036 [US3] CI integration test for Windows build
- [ ] T037 [US3] CI integration test for Linux build

### Implementation for User Story 3

- [ ] T038 [P] [US3] Review/update FilePathUtil in `src/base/src/main/java/com/xenoamess/cyan_potion/base/io/`
  - Ensure normalize() works for cross-platform paths
  - Add getConfigDir() for Windows (%APPDATA%) vs Linux (~/.config)
- [ ] T039 [P] [US3] Verify Platform detection in `src/base/src/main/java/com/xenoamess/cyan_potion/base/runtime/Platform.java`
  - Ensure isWindows(), isLinux(), isMac() work correctly
- [ ] T040 [US3] Ensure GameWindow handles DPI scaling on both platforms in `game_window_components/`
- [ ] T041 [US3] Test and fix any platform-specific resource loading issues in `io/resource/`
- [ ] T042 [US3] Update CI workflow to run tests on both Windows and Linux with JDK 17 and 21

**Checkpoint**: Cross-platform compatibility verified on both Windows and Linux

---

## Phase 5: User Story 4 - Engine & Game Co-management (Priority: P2)

**Goal**: Enable engine (src/base, src/coordinate, src/rpg_module) and main game (src/xenoamess-s-civilization, src/demo) to coexist

**Independent Test**: Developer can build only engine modules, or build both engine and game; CI passes for both configurations

**Note**: This phase can proceed in parallel with US1/US3 once foundational phase is complete

### Tests for User Story 4

- [ ] T043 [P] [US4] Test src/base can be built independently (mvn -pl src/base install)
- [ ] T044 [P] [US4] Test src/coordinate depends on src/base correctly
- [ ] T045 [P] [US4] Test src/rpg_module depends on base and coordinate correctly
- [ ] T046 [P] [US4] Test src/demo depends on engine modules correctly

### Implementation for User Story 4

- [ ] T047 [US4] Review/update src/demo module to showcase engine capabilities
  - Create/update DemoGame main class in `src/demo/src/main/java/com/xenoamess/cyan_potion/demo/DemoGame.java`
  - Create demo scene in `src/demo/src/main/java/com/xenoamess/cyan_potion/demo/scenes/`
  - Create demo entities in `src/demo/src/main/java/com/xenoamess/cyan_potion/demo/entities/`
- [ ] T048 [P] [US4] Add demo resources (textures, audio) in `src/demo/src/main/resources/`
- [ ] T049 [US4] Configure Maven exec plugin in src/demo/pom.xml for easy demo launch
- [ ] T050 [US4] Review module dependencies in all pom.xml files
  - Ensure no circular dependencies
  - Verify all engine modules can be built without game modules

**Checkpoint**: Demo game can be built and run independently, showcasing engine capabilities

---

## Phase 6: Coordinate & Physics System Enhancement (Priority: P2)

**Goal**: Enhance coordinate and physics systems in src/coordinate for 2D games

**Independent Test**: Game objects can move, collide, and respond to physics correctly

**Note**: This builds on existing src/coordinate module structure

### Tests for Coordinate System

- [ ] T051 [P] [COORD] Unit test for entity positioning in `src/coordinate/src/test/java/com/xenoamess/cyan_potion/coordinate/entity/EntityTest.java`
- [ ] T052 [P] [COORD] Unit test for physics shapes in `src/coordinate/src/test/java/com/xenoamess/cyan_potion/coordinate/physic/shapes/ShapeTest.java`
- [ ] T053 [P] [COORD] Unit test for collision detection in `src/coordinate/src/test/java/com/xenoamess/cyan_potion/coordinate/physic/CollisionTest.java`

### Implementation for Coordinate System

- [ ] T054 [P] [COORD] Review/update entity system in `src/coordinate/src/main/java/com/xenoamess/cyan_potion/coordinate/entity/`
  - Ensure Transform component equivalent exists
  - Verify position, rotation, scale handling
- [ ] T055 [P] [COORD] Review/update physics shapes in `src/coordinate/src/main/java/com/xenoamess/cyan_potion/coordinate/physic/shapes/`
  - Ensure Box, Circle, Polygon shapes work
- [ ] T056 [P] [COORD] Review/update collision detection in `src/coordinate/src/main/java/com/xenoamess/cyan_potion/coordinate/physic/shape_relation_judges/`
  - Verify AABB collision works
  - Add circle-circle, circle-box collision if missing
- [ ] T057 [COORD] Ensure coordinate system integrates with render system in src/base

**Checkpoint**: Physics and coordinate systems work correctly with rendering

---

## Phase 7: RPG Module Enhancement (Priority: P3)

**Goal**: Enhance RPG-specific features in src/rpg_module

**Independent Test**: RPG game elements (maps, units, events) work correctly

**Note**: This is specific to RPG games, can proceed in parallel with other phases

### Tests for RPG Module

- [ ] T058 [P] [RPG] Unit test for game map in `src/rpg_module/src/test/java/com/xenoamess/cyan_potion/rpg_module/game_map/GameMapTest.java`
- [ ] T059 [P] [RPG] Unit test for units in `src/rpg_module/src/test/java/com/xenoamess/cyan_potion/rpg_module/units/UnitTest.java`
- [ ] T060 [P] [RPG] Unit test for event system in `src/rpg_module/src/test/java/com/xenoamess/cyan_potion/rpg_module/event_unit/EventUnitTest.java`

### Implementation for RPG Module

- [ ] T061 [P] [RPG] Review/update game map system in `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/game_map/`
- [ ] T062 [P] [RPG] Review/update unit system in `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/units/`
- [ ] T063 [P] [RPG] Review/update event unit system in `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/event_unit/`
- [ ] T064 [P] [RPG] Review/update render system in `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/render/`
- [ ] T065 [P] [RPG] Review/update world management in `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/world/`
- [ ] T066 [RPG] Ensure RPG module integrates with base and coordinate modules

**Checkpoint**: RPG module provides complete RPG game functionality

---

## Phase 8: User Story 2 - 3D Extension Capability (Priority: P2)

**Goal**: Engine architecture supports optional 3D rendering without affecting 2D functionality

**Independent Test**: 3D module can be added as dependency; 3D models render correctly alongside 2D sprites; pure 2D games exclude 3D module

**Note**: This creates new module src/engine-3d/ extending src/base/render/

### Tests for User Story 2

- [ ] T067 [P] [US2] Unit test for Renderer3D interface contract
- [ ] T068 [US2] Integration test for 2D/3D hybrid rendering

### Implementation for User Story 2

- [ ] T069 [US2] Create src/engine-3d/pom.xml with dependency on src/base
- [ ] T070 [US2] Define Renderer3D interface extending base render system
  - Create `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Renderer3D.java`
- [ ] T071 [US2] Add Renderer3D support hook in src/base/render/ (optional extension point)
- [ ] T072 [P] [US2] Implement Renderer3DImpl in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Renderer3DImpl.java`
- [ ] T073 [P] [US2] Implement Model loading in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Model.java`
  - Support glTF/OBJ formats
- [ ] T074 [P] [US2] Implement Mesh in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Mesh.java`
- [ ] T075 [P] [US2] Implement Material in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Material.java`
- [ ] T076 [US2] Create 3D shaders in `src/engine-3d/src/main/resources/shaders/`
  - 3d_model.vert, 3d_model.frag
- [ ] T077 [US2] Update demo to optionally use 3D (configurable via system property)

**Checkpoint**: 3D module builds independently, 2D-only games can exclude it

---

## Phase 9: UI System Enhancement (Priority: P2)

**Purpose**: Enhance UI components in src/base/visual/

- [ ] T078 [P] [UI] Review/update UI components in `src/base/src/main/java/com/xenoamess/cyan_potion/base/visual/`
  - Ensure Widget base class exists
  - Verify Label, Button implementations
  - Add NinePatch (scalable frames) if missing
- [ ] T079 [P] [UI] Review/update font support in `src/base/src/main/java/com/xenoamess/cyan_potion/base/visual/Font.java`
- [ ] T080 [UI] Add UIManager in `src/base/src/main/java/com/xenoamess/cyan_potion/base/visual/UIManager.java` if missing

---

## Phase 10: Main Game Implementation (Priority: P1)

**Purpose**: Implement xenoamess's-civilization game using the engine

**Independent Test**: Game runs and is playable

- [ ] T081 [P] [GAME] Create game main class in `src/xenoamess-s-civilization/src/main/java/com/xenoamess/cyan_potion/xenoamess_s_civilization/Game.java`
- [ ] T082 [P] [GAME] Create game scenes in `src/xenoamess-s-civilization/src/main/java/com/xenoamess/cyan_potion/xenoamess_s_civilization/scenes/`
- [ ] T083 [P] [GAME] Create game entities in `src/xenoamess-s-civilization/src/main/java/com/xenoamess/cyan_potion/xenoamess_s_civilization/entities/`
- [ ] T084 [P] [GAME] Add game resources in `src/xenoamess-s-civilization/src/main/resources/`
- [ ] T085 [GAME] Implement save/load system for game state
- [ ] T086 [GAME] Implement core gameplay mechanics

**Checkpoint**: Game is playable and demonstrates engine capabilities

---

## Phase 11: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect all user stories

### Code Quality & Documentation

- [ ] T087 [P] Review all public APIs against P3C规范 (zero ERROR tolerance)
- [ ] T088 [P] Complete JavaDoc for all public classes and methods
- [ ] T089 [P] Create API documentation
- [ ] T090 Update quickstart.md with final API changes

### Testing

- [ ] T091 [P] Add missing unit tests to reach 80%+ coverage in src/base
- [ ] T092 [P] Add missing unit tests to reach 80%+ coverage in src/coordinate
- [ ] T093 [P] Add missing unit tests to reach 80%+ coverage in src/rpg_module
- [ ] T094 Performance test: 60 FPS with 1000 sprites
- [ ] T095 Memory leak test: 4-hour continuous run

### Performance Optimization

- [ ] T096 [P] Optimize sprite batch buffer management in src/base/render/
- [ ] T097 [P] Implement texture atlasing support
- [ ] T098 [P] Add object pooling for frequently created/destroyed objects
- [ ] T099 Profile and optimize hot paths with JProfiler

### Cross-Cutting Features

- [ ] T100 Implement hot reload for textures (development mode) in src/base/io/resource/
- [ ] T101 Create debug overlay (FPS, draw calls, memory) in src/base/console/
- [ ] T102 Add input action mapping system in src/base/io/input/
- [ ] T103 Final CI/CD pipeline validation on Windows and Linux

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational) - BLOCKS all user stories
    ↓
    ├──→ Phase 3 (US1: 2D Core) ───────────┐
    │                                         │
    ├──→ Phase 4 (US3: Cross-Platform) ──────┼──→ Phase 9 (UI)
    │                                         │       ↓
    ├──→ Phase 5 (US4: Demo Game) ──────────┤   Phase 11 (Polish)
    │                                         │
    ├──→ Phase 6 (Coordinate/Physics) ──────┤
    │                                         │
    ├──→ Phase 7 (RPG Module) ──────────────┤
    │                                         │
    ├──→ Phase 10 (Main Game) ──────────────┤
    │                                         │
    └──→ Phase 8 (US2: 3D Extension) ───────┘
```

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational phase - Core 2D rendering in src/base
- **US3 (P1)**: Can start after Foundational phase - Cross-platform support
- **US4 (P2)**: Can start after Foundational phase - Demo in src/demo
- **Coordinate (P2)**: Can start after US1 begins - Physics in src/coordinate
- **RPG (P3)**: Can start after Coordinate - RPG features in src/rpg_module
- **US2 (P2)**: Should wait for US1 stable - New src/engine-3d/ module
- **Main Game (P1)**: Can start after US1 and Coordinate - src/xenoamess-s-civilization/

### Within Each Phase

- **Models before Services**: Resource before ResourceManager
- **Services before Integration**: ResourceManager before Scene resource loading
- **Core before Demo**: src/base stable before src/demo updates

### Parallel Opportunities

**Phase 1**:
```bash
# All setup tasks can run in parallel
T001, T002, T003, T004, T005, T006
```

**Phase 2 - Foundation**:
```bash
# Foundation tasks marked [P]
T007 (Math), T008 (IO), T009 (Platform), T012 (Exceptions)
```

**Phase 3 (US1) - After Foundation**:
```bash
# Parallel within US1
T020, T021, T022 (Camera, Shader, Texture)
T025, T026 (Keyboard, Mouse)
T028, T029 (Resource system)
T031, T032, T033 (Audio)
```

**Phase 4 (US3) - After Foundation**:
```bash
# Cross-platform tasks
T038, T039, T040, T041
```

**Phase 5 (US4) - After Foundation**:
```bash
# Demo game tasks
T047, T048, T049
```

---

## Implementation Strategy

### MVP First (US1 + US3 + Main Game Core)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (2D Core in src/base)
4. Complete Phase 4: User Story 3 (Cross-Platform)
5. Complete Phase 6: Coordinate/Physics basics
6. Start Phase 10: Main Game core mechanics
7. **STOP and VALIDATE**: Test game runs on Windows and Linux
8. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add US1 (2D Core) → Test independently → Deploy/Demo (MVP!)
3. Add US3 (Cross-Platform) → Test on both platforms → Deploy/Demo
4. Add Coordinate/Physics → Enhanced gameplay → Deploy/Demo
5. Add Main Game → Full game experience → Deploy/Demo
6. Add RPG Module → RPG features → Deploy/Demo
7. Add US2 (3D Extension) → Advanced feature → Deploy/Demo

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: US1 Core (Render, Input, Resource in src/base)
   - Developer B: US3 Cross-Platform + CI/CD
   - Developer C: Coordinate/Physics enhancements
3. After US1 stable:
   - Developer A: Main Game implementation
   - Developer B: RPG Module enhancements
   - Developer C: UI System enhancements
   - Developer D: US2 3D Extension

---

## Task Statistics

| Category | Count |
|----------|-------|
| **Total Tasks** | 103 |
| **Setup Phase** | 6 |
| **Foundational Phase** | 7 |
| **US1 (2D Core)** | 21 |
| **US3 (Cross-Platform)** | 9 |
| **US4 (Demo)** | 8 |
| **Coordinate/Physics** | 10 |
| **RPG Module** | 9 |
| **US2 (3D Extension)** | 11 |
| **UI Enhancement** | 3 |
| **Main Game** | 6 |
| **Polish Phase** | 17 |

| Task Type | Count |
|-----------|-------|
| **Implementation/Review** | 74 |
| **Test** | 29 |
| **Configuration/CI** | 6 |

---

## Notes

- Tasks marked [P] can run in parallel (different files, no dependencies)
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- File paths reflect actual project structure (src/base/, src/coordinate/, src/rpg_module/, src/demo/, src/xenoamess-s-civilization/)
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
