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

- **Engine Core**: `engine/src/main/java/com/xenoamess/cyan_potion/`
- **Engine Tests**: `engine/src/test/java/com/xenoamess/cyan_potion/`
- **Engine Resources**: `engine/src/main/resources/`
- **Demo Game**: `demo-game/src/main/java/com/xenoamess/demo/`
- **Demo Tests**: `demo-game/src/test/java/com/xenoamess/demo/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and Maven multi-module structure

- [ ] T001 Create root pom.xml with dependency management (LWJGL3, Apache Commons, JOML, JUnit 5, Log4j2)
- [ ] T002 [P] Create engine module structure: engine/pom.xml and directory layout
- [ ] T003 [P] Create engine-3d module structure: engine-3d/pom.xml and directory layout
- [ ] T004 [P] Create engine-groovy module structure: engine-groovy/pom.xml and directory layout
- [ ] T005 [P] Create demo-game module structure: demo-game/pom.xml and directory layout
- [ ] T006 Configure P3C Maven plugin for code quality checks
- [ ] T007 Configure JaCoCo for test coverage reporting (target 80%+)
- [ ] T008 Create CI workflow for Windows and Linux builds

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### 2.1 Math & Utility Foundation

- [ ] T009 [P] Create math utility classes in `engine/src/main/java/com/xenoamess/cyan_potion/math/`
  - Vector2f, Vector3f wrappers around JOML
  - Matrix4f utility functions
  - Color class with RGBA support
- [ ] T010 Create utility classes in `engine/src/main/java/com/xenoamess/cyan_potion/util/`
  - FilePathUtil for cross-platform path handling
  - Time utilities (nanoTime to seconds conversion)

### 2.2 Configuration & Logging

- [ ] T011 Implement EngineConfig in `engine/src/main/java/com/xenoamess/cyan_potion/core/EngineConfig.java`
- [ ] T012 Set up Log4j2 configuration in `engine/src/main/resources/log4j2.xml`

### 2.3 Exception Hierarchy

- [ ] T013 [P] Create exception classes in `engine/src/main/java/com/xenoamess/cyan_potion/exception/`
  - GameEngineException (checked)
  - ResourceNotFoundException
  - ResourceLoadException
  - InitializationException

### 2.4 Event System Foundation

- [ ] T014 Implement Event base class in `engine/src/main/java/com/xenoamess/cyan_potion/event/Event.java`
- [ ] T015 Implement EventBus in `engine/src/main/java/com/xenoamess/cyan_potion/event/EventBus.java`
- [ ] T016 [P] Create basic event types in `engine/src/main/java/com/xenoamess/cyan_potion/event/`
  - EngineEvent
  - WindowResizeEvent, WindowFocusEvent
  - ResourceLoadEvent

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 2D Game Creation (Priority: P1) 🎯 MVP

**Goal**: Enable developers to create 2D games with window, rendering, input, and basic scene management

**Independent Test**: Developer can create a window, render a sprite, and handle keyboard input to move the sprite

### Tests for User Story 1

- [ ] T017 [P] [US1] Unit test for EngineConfig validation in `engine/src/test/java/com/xenoamess/cyan_potion/core/EngineConfigTest.java`
- [ ] T018 [P] [US1] Unit test for GameWindow mock (headless) in `engine/src/test/java/com/xenoamess/cyan_potion/core/GameWindowTest.java`
- [ ] T019 [P] [US1] Unit test for SpriteBatch vertex calculation in `engine/src/test/java/com/xenoamess/cyan_potion/graphics/SpriteBatchTest.java`
- [ ] T020 [P] [US1] Unit test for Resource reference counting in `engine/src/test/java/com/xenoamess/cyan_potion/resource/ResourceManagerTest.java`
- [ ] T021 [US1] Integration test for render-resource pipeline in `engine/src/test/java/com/xenoamess/cyan_potion/integration/RenderResourceIntegrationTest.java`

### Implementation for User Story 1

#### Core Engine

- [ ] T022 [US1] Implement GameWindow in `engine/src/main/java/com/xenoamess/cyan_potion/core/GameWindow.java`
  - GLFW window creation, windowed/fullscreen toggle
  - Event callbacks (resize, focus, close)
  - VSync support
- [ ] T023 [US1] Implement GameLoop in `engine/src/main/java/com/xenoamess/cyan_potion/core/GameLoop.java`
  - Fixed timestep update algorithm
  - Frame statistics (FPS, delta time)
- [ ] T024 [US1] Implement GameEngine in `engine/src/main/java/com/xenoamess/cyan_potion/core/GameEngine.java`
  - Lifecycle management (init, run, stop)
  - Subsystem coordination
  - Scene switching

#### Render System

- [ ] T025 [P] [US1] Implement Camera (Orthographic) in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/Camera.java`
- [ ] T026 [P] [US1] Implement Shader in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/Shader.java`
  - Load/compile GLSL shaders
  - Uniform variable management
- [ ] T027 [P] [US1] Create default shaders in `engine/src/main/resources/shaders/`
  - sprite.vert (vertex shader)
  - sprite.frag (fragment shader)
- [ ] T028 [US1] Implement Texture in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/Texture.java`
  - OpenGL texture creation
  - PNG/JPG loading via stb_image
- [ ] T029 [US1] Implement SpriteBatch in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/SpriteBatch.java`
  - VBO/VAO/EBO management
  - Batching algorithm (texture-based)
  - Max 10000 sprites per batch
- [ ] T030 [US1] Implement Renderer2D in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/Renderer2D.java`
  - draw(Texture, x, y, width, height)
  - draw(Texture, Transform)
  - drawRect() for solid colors
- [ ] T031 [US1] Implement RenderSystem in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/RenderSystem.java`
  - OpenGL context initialization
  - Render target management
  - Clear and viewport setup

#### Resource Management

- [ ] T032 [P] [US1] Implement Resource base class in `engine/src/main/java/com/xenoamess/cyan_potion/resource/Resource.java`
- [ ] T033 [P] [US1] Implement TextureLoader in `engine/src/main/java/com/xenoamess/cyan_potion/resource/TextureLoader.java`
- [ ] T034 [US1] Implement ResourceManager in `engine/src/main/java/com/xenoamess/cyan_potion/resource/ResourceManager.java`
  - LRU cache implementation
  - Reference counting
  - Async loading support

#### Input System

- [ ] T035 [P] [US1] Implement Keyboard key definitions in `engine/src/main/java/com/xenoamess/cyan_potion/input/KeyboardKey.java`
- [ ] T036 [P] [US1] Implement Keyboard state in `engine/src/main/java/com/xenoamess/cyan_potion/input/Keyboard.java`
  - isDown(), isPressed(), isReleased()
- [ ] T037 [P] [US1] Implement Mouse in `engine/src/main/java/com/xenoamess/cyan_potion/input/Mouse.java`
  - Position tracking
  - Button states
  - Scroll wheel
- [ ] T038 [US1] Implement InputManager in `engine/src/main/java/com/xenoamess/cyan_potion/input/InputManager.java`
  - GLFW input callbacks
  - Event dispatch to EventBus

#### Scene Management

- [ ] T039 [P] [US1] Implement Component base class in `engine/src/main/java/com/xenoamess/cyan_potion/scene/Component.java`
- [ ] T040 [P] [US1] Implement Transform in `engine/src/main/java/com/xenoamess/cyan_potion/scene/Transform.java`
  - Position, rotation, scale
  - Parent-child hierarchy
  - Local/world matrix conversion
- [ ] T041 [US1] Implement GameObject in `engine/src/main/java/com/xenoamess/cyan_potion/scene/GameObject.java`
  - Component management
  - Lifecycle (create, update, render)
  - Parent-child relationships
- [ ] T042 [US1] Implement Scene in `engine/src/main/java/com/xenoamess/cyan_potion/scene/Scene.java`
  - GameObject management
  - Layer-based rendering
  - Camera management

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 3 - Cross-Platform Support (Priority: P1) 🎯 MVP

**Goal**: Ensure engine compiles and runs identically on Windows and Linux

**Independent Test**: Same game code compiles and runs on both Windows and Linux with pixel-identical output

**Note**: This phase can proceed in parallel with US1 once foundational phase is complete

### Tests for User Story 3

- [ ] T043 [P] [US3] Unit test for FilePathUtil path normalization in `engine/src/test/java/com/xenoamess/cyan_potion/util/FilePathUtilTest.java`
- [ ] T044 [P] [US3] Cross-platform path test (Windows-style vs Unix-style) in `engine/src/test/java/com/xenoamess/cyan_potion/util/PathCompatibilityTest.java`
- [ ] T045 [US3] CI integration test for Windows build
- [ ] T046 [US3] CI integration test for Linux build

### Implementation for User Story 3

- [ ] T047 [P] [US3] Enhance FilePathUtil in `engine/src/main/java/com/xenoamess/cyan_potion/util/FilePathUtil.java`
  - normalize() for cross-platform paths
  - getConfigDir() for Windows (%APPDATA%) vs Linux (~/.config)
- [ ] T048 [P] [US3] Add LWJGL native library configuration in engine/pom.xml for Windows and Linux
- [ ] T049 [P] [US3] Create platform detection utility in `engine/src/main/java/com/xenoamess/cyan_potion/util/Platform.java`
  - isWindows(), isLinux(), isMac()
- [ ] T050 [US3] Ensure GameWindow handles DPI scaling on both platforms
- [ ] T051 [US3] Create cross-platform resource loading test in `engine/src/test/java/com/xenoamess/cyan_potion/integration/ResourceLoadingCrossPlatformTest.java`
- [ ] T052 [US3] Update CI workflow (.github/workflows/ci.yml) with Windows and Linux matrix builds

**Checkpoint**: Cross-platform compatibility verified on both Windows and Linux

---

## Phase 5: User Story 4 - Engine & Game Co-management (Priority: P2)

**Goal**: Enable engine and demo game to coexist in same repository with independent builds

**Independent Test**: Developer can build only engine, or build both engine and demo game; CI passes for both configurations

**Note**: This phase can proceed in parallel with US1/US3 once foundational phase is complete

### Tests for User Story 4

- [ ] T053 [P] [US4] Test engine module can be built independently (mvn -pl engine install)
- [ ] T054 [P] [US4] Test demo-game depends on engine module correctly

### Implementation for User Story 4

- [ ] T055 [US4] Configure demo-game/pom.xml with engine dependency
- [ ] T056 [P] [US4] Create DemoGame main class in `demo-game/src/main/java/com/xenoamess/demo/DemoGame.java`
- [ ] T057 [P] [US4] Create MainScene in `demo-game/src/main/java/com/xenoamess/demo/scenes/MainScene.java`
- [ ] T058 [P] [US4] Create Player entity in `demo-game/src/main/java/com/xenoamess/demo/entities/Player.java`
- [ ] T059 [US4] Create PlayerController component in `demo-game/src/main/java/com/xenoamess/demo/components/PlayerController.java`
- [ ] T060 [P] [US4] Add demo resources (textures, audio) in `demo-game/src/main/resources/`
- [ ] T061 [US4] Configure Maven exec plugin for easy demo launch

**Checkpoint**: Demo game can be built and run independently, showcasing engine capabilities

---

## Phase 6: User Story 2 - 3D Extension Capability (Priority: P2)

**Goal**: Engine architecture supports optional 3D rendering without affecting 2D functionality

**Independent Test**: 3D module can be added as dependency; 3D models render correctly alongside 2D sprites; pure 2D games exclude 3D module

**Note**: This phase should wait until US1 is complete to build upon stable 2D rendering

### Tests for User Story 2

- [ ] T062 [P] [US2] Unit test for Renderer3D interface contract in `engine/src/test/java/com/xenoamess/cyan_potion/graphics/Renderer3DContractTest.java`
- [ ] T063 [US2] Integration test for 2D/3D hybrid rendering in `engine-3d/src/test/java/com/xenoamess/cyan_potion/graphics3d/HybridRenderIntegrationTest.java`

### Implementation for User Story 2

- [ ] T064 [US2] Define Renderer3D interface in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/Renderer3D.java`
- [ ] T065 [US2] Add Renderer3D support to RenderSystem in `engine/src/main/java/com/xenoamess/cyan_potion/graphics/RenderSystem.java`
  - Optional 3D renderer field
  - Hybrid render pipeline
- [ ] T066 [P] [US2] Implement Renderer3DImpl in `engine-3d/src/main/java/com/xenoamess/cyan_potion/graphics3d/Renderer3DImpl.java`
- [ ] T067 [P] [US2] Implement Model loading in `engine-3d/src/main/java/com/xenoamess/cyan_potion/graphics3d/Model.java`
  - glTF/OBJ support
- [ ] T068 [P] [US2] Implement Mesh in `engine-3d/src/main/java/com/xenoamess/cyan_potion/graphics3d/Mesh.java`
- [ ] T069 [P] [US2] Implement Material in `engine-3d/src/main/java/com/xenoamess/cyan_potion/graphics3d/Material.java`
- [ ] T070 [US2] Create 3D shaders in `engine-3d/src/main/resources/shaders/`
  - 3d_model.vert
  - 3d_model.frag
- [ ] T071 [US2] Update demo-game to optionally use 3D (configurable via system property)

**Checkpoint**: 3D module builds independently, 2D-only games can exclude it

---

## Phase 7: Additional Systems (Cross-Cutting)

**Purpose**: Audio, UI, and polish that enhance all user stories

### Audio System

- [ ] T072 [P] Implement Sound (short audio) in `engine/src/main/java/com/xenoamess/cyan_potion/audio/Sound.java`
- [ ] T073 [P] Implement Music (streaming audio) in `engine/src/main/java/com/xenoamess/cyan_potion/audio/Music.java`
- [ ] T074 [P] Implement AudioManager in `engine/src/main/java/com/xenoamess/cyan_potion/audio/AudioManager.java`
  - OpenAL integration
  - Volume control
  - OGG loading
- [ ] T075 [P] Create AudioLoader in `engine/src/main/java/com/xenoamess/cyan_potion/resource/AudioLoader.java`
- [ ] T076 Unit test for AudioManager in `engine/src/test/java/com/xenoamess/cyan_potion/audio/AudioManagerTest.java`

### UI System

- [ ] T077 [P] Implement Widget base class in `engine/src/main/java/com/xenoamess/cyan_potion/ui/Widget.java`
- [ ] T078 [P] Implement Label in `engine/src/main/java/com/xenoamess/cyan_potion/ui/Label.java`
- [ ] T079 [P] Implement Button in `engine/src/main/java/com/xenoamess/cyan_potion/ui/Button.java`
- [ ] T080 [P] Implement NinePatch (scalable UI frames) in `engine/src/main/java/com/xenoamess/cyan_potion/ui/NinePatch.java`
- [ ] T081 [P] Implement UIManager in `engine/src/main/java/com/xenoamess/cyan_potion/ui/UIManager.java`
- [ ] T082 [P] Add Font support and FontLoader in `engine/src/main/java/com/xenoamess/cyan_potion/resource/FontLoader.java`

### Gamepad Support

- [ ] T083 [P] Implement Gamepad in `engine/src/main/java/com/xenoamess/cyan_potion/input/Gamepad.java`
  - GLFW gamepad integration
  - XInput mapping for Windows

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect all user stories

### Code Quality & Documentation

- [ ] T084 [P] Review all public APIs against P3C规范 (zero ERROR tolerance)
- [ ] T085 [P] Complete JavaDoc for all public classes and methods
- [ ] T086 [P] Create API documentation in `docs/api/`
- [ ] T087 Update quickstart.md with final API changes

### Testing

- [ ] T088 [P] Add missing unit tests to reach 80%+ coverage
- [ ] T089 [P] Create visual regression test tool for rendering
- [ ] T090 Performance test: 60 FPS with 1000 sprites
- [ ] T091 Memory leak test: 4-hour continuous run

### Performance Optimization

- [ ] T092 [P] Optimize SpriteBatch buffer management
- [ ] T093 [P] Implement texture atlasing support
- [ ] T094 [P] Add object pooling for frequently created/destroyed objects
- [ ] T095 Profile and optimize hot paths with JProfiler

### Cross-Cutting Features

- [ ] T096 Implement save/load system for game state serialization
- [ ] T097 Implement hot reload for textures (development mode)
- [ ] T098 Create debug overlay (FPS, draw calls, memory)
- [ ] T099 Add input action mapping system
- [ ] T100 Final CI/CD pipeline validation on Windows and Linux

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational) - BLOCKS all user stories
    ↓
    ├──→ Phase 3 (US1: 2D Game) ───────┐
    │                                      │
    ├──→ Phase 4 (US3: Cross-Platform) ──┼──→ Phase 7 (Audio/UI)
    │                                      │       ↓
    ├──→ Phase 5 (US4: Demo Game) ───────┤   Phase 8 (Polish)
    │                                      │
    └──→ Phase 6 (US2: 3D Extension) ────┘
                (depends on US1 completion)
```

### User Story Dependencies

- **US1 (P1)**: Can start after Foundational phase - No dependencies
- **US3 (P1)**: Can start after Foundational phase - Independent
- **US4 (P2)**: Can start after Foundational phase - May integrate with US1/US3 but independently testable
- **US2 (P2)**: Should wait for US1 core rendering stable - Uses RenderSystem extension points

### Within Each User Story

- **Models before Services**: Texture before TextureLoader, Resource before ResourceManager
- **Services before Integration**: ResourceManager before Scene resource loading
- **Core before Demo**: Engine modules before demo-game

### Parallel Opportunities

**Phase 1**:
```bash
# All setup tasks can run in parallel
T002, T003, T004, T005 (module creation)
```

**Phase 2**:
```bash
# Foundation tasks marked [P]
T009 (Math), T010 (Util), T013 (Exceptions), T014-T016 (Events)
```

**Phase 3 (US1) - After Foundation**:
```bash
# Parallel within US1
T025, T026 (Camera, Shader) → T029 (SpriteBatch) → T030 (Renderer2D)
T032, T033 (Resource base) → T034 (ResourceManager)
T035, T036, T037 (Input components) → T038 (InputManager)
T039, T040 (Components) → T041 (GameObject) → T042 (Scene)
```

**Phase 4 (US3) - After Foundation**:
```bash
# Cross-platform tasks
T047, T048, T049, T050 (platform utilities)
```

**Phase 5 (US4) - After Foundation**:
```bash
# Demo game (depends on engine module but mockable)
T056, T057, T058, T059 (demo classes)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (2D Game Core)
4. **STOP and VALIDATE**: Test User Story 1 independently
   - Create minimal test game
   - Verify window creation, sprite rendering, input handling
   - Run performance test (60 FPS target)
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add US1 (2D Core) → Test independently → Deploy/Demo (MVP!)
3. Add US3 (Cross-Platform) → Test on both platforms → Deploy/Demo
4. Add US4 (Demo Game) → Showcase engine capabilities → Deploy/Demo
5. Add US2 (3D Extension) → Advanced feature → Deploy/Demo
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: US1 Core (GameEngine, RenderSystem, Scene)
   - Developer B: US1 Input & Resources (InputManager, ResourceManager)
   - Developer C: US3 Cross-Platform + CI/CD
3. After US1 core stable:
   - Developer A: US4 Demo Game
   - Developer B: Phase 7 Audio System
   - Developer C: Phase 7 UI System
   - Developer D: US2 3D Extension

---

## Task Statistics

| Category | Count |
|----------|-------|
| **Total Tasks** | 100 |
| **Setup Phase** | 8 |
| **Foundational Phase** | 9 |
| **US1 (2D Game)** | 27 |
| **US3 (Cross-Platform)** | 10 |
| **US4 (Demo Game)** | 9 |
| **US2 (3D Extension)** | 10 |
| **Phase 7 (Audio/UI)** | 12 |
| **Phase 8 (Polish)** | 17 |

| Task Type | Count |
|-----------|-------|
| **Implementation** | 73 |
| **Test** | 19 |
| **Configuration/CI** | 8 |

---

## Notes

- Tasks marked [P] can run in parallel (different files, no dependencies)
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
