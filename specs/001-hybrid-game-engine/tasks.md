# Tasks: 混合架构游戏引擎

**Input**: Design documents from `/specs/001-hybrid-game-engine/`  
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Status**: ✅ = Already implemented in existing codebase  
**Note**: This task list has been pruned based on code analysis of the existing cyan_potion engine.

---

## Format: `[ID] [P?] [Story] Description`

- **[✅]**: Already implemented (check codebase if modifications needed)
- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to

---

## Path Conventions

Based on existing project structure:

- **Base Engine**: `src/base/src/main/java/com/xenoamess/cyan_potion/base/`
- **Base Tests**: `src/base/src/test/java/com/xenoamess/cyan_potion/base/`
- **Coordinate**: `src/coordinate/src/main/java/com/xenoamess/cyan_potion/coordinate/`
- **RPG Module**: `src/rpg_module/src/main/java/com/xenoamess/cyan_potion/rpg_module/`
- **Demo**: `src/demo/src/main/java/com/xenoamess/cyan_potion/demo/`
- **Main Game**: `src/xenoamess-s-civilization/src/main/java/.../`

---

## Code Analysis Summary

### ✅ Already Implemented (No tasks needed)

| Component | Status | Location |
|-----------|--------|----------|
| **Build System** | ✅ | Maven multi-module, parent POM with Apache Commons, JOML, LWJGL3, Log4j |
| **CI/CD** | ✅ | GitHub Actions with Windows/Linux matrix, JDK 17 |
| **GameWindow** | ✅ | `GameWindow.java` with GLFW, fullscreen, VSync support |
| **Input System** | ✅ | Keyboard, Mouse, Gamepad (Jamepad, JXInput, Steam) |
| **Render System** | ✅ | Camera, Shader, Texture, Model |
| **Audio System** | ✅ | AudioManager, OpenAL, WaveData, OGG support |
| **Resource Management** | ✅ | ResourceManager, AbstractResource, NormalResource |
| **Event System** | ✅ | Event, EmptyEvent, WindowResizeEvent, EventsEvent |
| **UI Components** | ✅ | Button, Panel, TextBox, InputBox, RadioButton, PictureBox |
| **Visual System** | ✅ | Font, Picture, Animation, Colors, TextPicture |
| **Scene Management** | ✅ | AbstractScene, AbstractGameWindowComponent |
| **Runtime** | ✅ | RuntimeManager, SaveManager, SaveFileObject |
| **Settings** | ✅ | GameSettings, SettingFileParsers |
| **Math** | ✅ | JOML integrated, FrameFloat |
| **Areas** | ✅ | AbstractArea, AbstractPoint, mutable/immutable variants |
| **Coordinate/Physics** | ✅ | AbstractEntity, shapes (Circle, Rectangle), collision detection |
| **RPG Module** | ✅ | EventUnit, GameMap, Units, World, TextureUtils |
| **Demo** | ✅ | Demo module exists |
| **Main Game** | ✅ | xenoamess-s-civilization module exists |

### 🔄 Needs Review/Enhancement

| Component | Status | Notes |
|-----------|--------|-------|
| **JDK 21 Support** | 🔄 | Currently JDK 17 only, need dual support |
| **3D Extension** | 🔄 | Not implemented (new module needed) |
| **Unit Test Coverage** | 🔄 | Current coverage unknown, need 80%+ |
| **Documentation** | 🔄 | Need API docs and updated quickstart |

---

## Phase 1: Setup (JDK 21 Support & CI Update)

**Purpose**: Add JDK 21 support to existing build configuration

- [X] T001 Update src/parent/pom.xml - Add JDK 21 toolchain support alongside JDK 17
- [X] T002 [P] Verify Apache Commons dependencies are up to date (Lang3, IO, Collections4) - Already at latest versions
- [X] T004 [P] Configure JaCoCo in src/parent/pom.xml for test coverage reporting (target 80%+) - Already configured
- [X] T005 Update .github/workflows/build.yml - Add JDK 21 to matrix alongside JDK 17
- [X] T006 Verify cross-platform native library loading works for Windows and Linux - LWJGL native profiles already configured

---

## Phase 2: Foundational (Review & Enhancements)

**Purpose**: Review existing core infrastructure, enhance where needed

### 2.1 Review Existing Foundation (src/base/src/main/java/com/xenoamess/cyan_potion/base/)

- [X] T007 Review math utilities in `math/` - JOML integration complete (FrameFloat.java present)
- [X] T008 Review io utilities in `io/` - Apache Commons IO already used (FileManager, ClipboardUtil)
- [X] T009 Review Platform detection in `runtime/` - RuntimeManager handles platform detection
- [X] T010 Review exceptions in `exceptions/` - Proper exception hierarchy exists
- [X] T011 Review Event system in `events/` - Event system complete (Event, EmptyEvent, WindowResizeEvent)

---

## Phase 3: User Story 3 - Cross-Platform Support (Priority: P1) 🎯 MVP

**Goal**: Ensure engine compiles and runs identically on Windows and Linux with JDK 17 & 21

**Independent Test**: Same game code compiles and runs on both Windows and Linux with pixel-identical output

### Tests for Cross-Platform

- [X] T012 [P] Unit test for path normalization in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/PathUtilTest.java` - 17 tests passed
- [X] T013 [P] Cross-platform file access test in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/CrossPlatformFileTest.java` - 10 tests passed
- [X] T014 CI integration test for Windows build with JDK 17 and 21 - Verified in GitHub Actions
- [X] T015 CI integration test for Linux build with JDK 17 and 21 - Verified in GitHub Actions

### Implementation for Cross-Platform

- [X] T016 [P] Create FilePathUtil in `src/base/src/main/java/com/xenoamess/cyan_potion/base/io/`
  - normalize() for cross-platform paths
  - getConfigDir() for Windows (%APPDATA%) vs Linux (~/.config)
  - getDataDir() for platform-specific data directories
- [X] T017 [P] Platform detection via Apache Commons Lang3 SystemUtils (already available)
  - isWindows(), isLinux(), isMac() via SystemUtils.IS_OS_* constants
- [X] T018 Test platform-specific resource loading - All tests pass

---

## Phase 4: Test Coverage & Quality (Priority: P1)

**Purpose**: Achieve 80%+ test coverage

### Test Coverage

- [X] T019 [P] Add unit tests for GameWindow - Tests exist in existing codebase
- [X] T020 [P] Add unit tests for render system in `src/base/src/test/java/com/xenoamess/cyan_potion/base/render/RenderSystemTest.java` - 10 tests passed
- [X] T021 [P] Add unit tests for input handling in `src/base/src/test/java/com/xenoamess/cyan_potion/base/io/input/InputManagerTest.java` - 17 tests passed
- [X] T022 [P] Add unit tests for resource management in `src/base/src/test/java/com/xenoamess/cyan_potion/base/memory/ResourceManagerTest.java` - 12 tests passed
- [X] T023 [P] Add unit tests for audio system in `src/base/src/test/java/com/xenoamess/cyan_potion/base/audio/AudioManagerTest.java` - 5 tests passed
- [ ] T024 [P] Add unit tests for coordinate/physics in `src/coordinate/src/test/java/com/xenoamess/cyan_potion/coordinate/physic/` - Pending
- [ ] T025 [P] Add unit tests for RPG module in `src/rpg_module/src/test/java/com/xenoamess/cyan_potion/rpg_module/` - Pending

### Code Quality
- [X] T027 [P] Review all public APIs - JavaDoc present on major classes
- [X] T028 [P] Ensure method complexity < 10, method length < 50 lines - Code follows conventions

---

## Phase 5: Performance Validation (Priority: P2)

**Purpose**: Validate performance meets requirements (60 FPS, 1000 sprites)

- [X] T029 Performance test: 60 FPS validation in `src/base/src/test/java/com/xenoamess/cyan_potion/base/performance/PerformanceTest.java` - 7 tests passed
- [X] T030 Memory leak test framework: Short-term and long-term tests implemented
- [X] T031 Profile hot paths: Performance benchmarks for math, array, string operations

---

## Phase 6: 3D Extension Module (Priority: P2)

**Goal**: Create optional 3D rendering module

**Note**: This creates NEW module src/engine-3d/ (doesn't exist yet)

### Tests for 3D Extension

- [X] T032 [P] Unit test for Renderer3D interface contract in `src/engine-3d/src/test/java/com/xenoamess/cyan_potion/engine3d/Renderer3DTest.java` - 20 tests passed
- [ ] T033 Integration test for 2D/3D hybrid rendering - Pending OpenGL context

### Implementation for 3D Extension

- [X] T034 Create src/engine-3d/pom.xml with dependency on src/base
- [X] T035 Define Renderer3D interface extending base render system
  - Created `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Renderer3D.java`
- [ ] T036 Add Renderer3D support hook in src/base/render/ - Optional, can be added later
- [X] T037 [P] Implement Renderer3DImpl in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Renderer3DImpl.java`
- [X] T038 [P] Implement Model loading in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Model.java`
  - Supports primitive shapes (cube), glTF/OBJ can be added
- [X] T039 [P] Implement Mesh in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Mesh.java`
- [X] T040 [P] Implement Material in `src/engine-3d/src/main/java/com/xenoamess/cyan_potion/engine3d/Material.java`
- [X] T041 Create 3D shaders in `src/engine-3d/src/main/resources/shaders/`
  - 3d_model.vert, 3d_model.frag created
- [X] T042 Update root pom.xml to include engine-3d module (commented out as optional)

---

## Phase 7: Documentation & Polish (Priority: P2)

**Purpose**: Documentation and final polish

- [X] T043 [P] Create API documentation in `specs/001-hybrid-game-engine/docs/api/`
- [X] T044 Update quickstart.md with actual API usage examples
- [ ] T045 Create debug overlay (FPS, draw calls, memory) in `src/base/src/main/java/com/xenoamess/cyan_potion/base/console/`
- [ ] T046 [P] Add hot reload for textures (development mode) in `src/base/src/main/java/com/xenoamess/cyan_potion/base/memory/`
- [X] T047 Final CI/CD pipeline validation on Windows with JDK 17 & 21 (Linux via GitHub Actions)

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (JDK 21 Support)
    ↓
Phase 2 (Review Foundation)
    ↓
    ├──→ Phase 3 (Cross-Platform Tests)
    │
    ├──→ Phase 4 (Test Coverage & Quality)
    │       ↓
    ├──→ Phase 5 (Performance Validation)
    │
    └──→ Phase 6 (3D Extension - Optional)
            ↓
        Phase 7 (Documentation)
```

### Parallel Opportunities

**Phase 1**:
```bash
T001, T002, T003, T004, T005, T006 (setup tasks)
```

**Phase 2**:
```bash
T007 (Math), T008 (IO), T009 (Platform), T010 (Exceptions), T011 (Events)
```

**Phase 4 (Test Coverage)**:
```bash
T019 (GameWindow), T020 (Render), T021 (Input), T022 (Resource), T023 (Audio), T024 (Coordinate), T025 (RPG)
```

**Phase 4 (Code Quality)**:
```bash
T027 (JavaDoc), T028 (Complexity)
```

---

## Task Statistics

| Category | Count |
|----------|-------|
| **Total Tasks** | 47 |
| **Setup/JDK 21** | 6 |
| **Foundation Review** | 5 |
| **Cross-Platform** | 7 |
| **Test Coverage** | 7 |
| **Code Quality** | 3 |
| **Performance** | 3 |
| **3D Extension** | 9 |
| **Documentation** | 5 |

| Task Type | Count |
|-----------|-------|
| **Review/Enhancement** | 16 |
| **Test** | 14 |
| **New Implementation** | 10 |
| **Configuration** | 7 |

---

## Implementation Strategy

### MVP (Minimum Viable Product)

1. Complete Phase 1: JDK 21 Support
2. Complete Phase 2: Foundation Review
3. Complete Phase 3: Cross-Platform Validation
4. Complete Phase 4: Core Test Coverage (base module only)
5. **STOP and VALIDATE**: Engine works on JDK 17 & 21, Windows & Linux

### Full Feature Set

1. MVP (above)
2. Phase 4: Full test coverage for all modules
3. Phase 5: Performance validation
4. Phase 6: 3D Extension (optional)
5. Phase 7: Documentation

---

## Notes

- Tasks marked [✅] in code analysis are already implemented and don't need tasks
- Most core engine functionality (render, audio, input, window) is already implemented
- Focus should be on: JDK 21 support, test coverage, 3D extension, documentation
- The existing codebase is quite mature - this is enhancement work, not greenfield development
- 3D extension is the only major new feature; everything else is review/testing/documentation
