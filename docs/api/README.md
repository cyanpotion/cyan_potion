# Cyan Potion Game Engine API Documentation

## Overview

Cyan Potion is a 2D/3D hybrid game engine built on Java and LWJGL3. This documentation covers the public API for game development.

## Table of Contents

1. [Core Engine](#core-engine)
2. [Window Management](#window-management)
3. [Rendering](#rendering)
4. [Input Handling](#input-handling)
5. [Audio](#audio)
6. [Resource Management](#resource-management)
7. [Scene Management](#scene-management)
8. [3D Extension](#3d-extension)

---

## Core Engine

### GameManager

The main entry point for the game engine.

```java
// Create a game configuration
GameManagerConfig config = new GameManagerConfig();
config.setTitle("My Game");
config.setWindowWidth(1280);
config.setWindowHeight(720);

// Initialize and run
GameManager gameManager = new GameManager(config);
gameManager.start();
```

### GameWindow

Manages the game window and display settings.

```java
GameWindow window = gameManager.getGameWindow();
window.setFullScreen(true);
window.setVSyncEnabled(true);
```

---

## Window Management

### Creating a Window

```java
GameWindow window = new GameWindow("Game Title", 1280, 720);
window.create();
```

### Window Events

```java
// Resize event
window.addResizeCallback((width, height) -> {
    System.out.println("Window resized to: " + width + "x" + height);
});

// Close event
window.setCloseCallback(() -> {
    System.out.println("Window closing");
    return true; // Allow close
});
```

---

## Rendering

### 2D Rendering

```java
// Get the renderer
Drawer drawer = gameManager.getDrawer();

// Draw a texture
drawer.draw(texture, x, y, width, height);

// Draw with rotation and scaling
drawer.draw(texture, x, y, width, height, rotation, scaleX, scaleY);
```

### Camera

```java
Camera camera = new Camera(initialX, initialY);
camera.setPosX(playerX);
camera.setPosY(playerY);
```

### Texture

```java
// Load texture
Texture texture = new Texture(resourceManager, resourceInfo);
texture.bind();
```

### Shader

```java
Shader shader = new Shader(gameManager, "shaders/custom.vert", "shaders/custom.frag");
shader.bind();
```

---

## Input Handling

### Keyboard

```java
// Check key states
if (gameManager.getInputManager().isKeyDown(KeyboardKeyEnum.KEY_SPACE)) {
    // Jump
}

// Key pressed (single trigger)
if (gameManager.getInputManager().isKeyPressed(KeyboardKeyEnum.KEY_ESCAPE)) {
    // Open menu
}
```

### Mouse

```java
// Mouse position
float mouseX = gameManager.getInputManager().getMouseX();
float mouseY = gameManager.getInputManager().getMouseY();

// Mouse buttons
if (gameManager.getInputManager().isMouseButtonDown(MouseButtonKeyEnum.MOUSE_BUTTON_LEFT)) {
    // Primary action
}
```

### Gamepad

```java
GamepadInputManager gamepad = gameManager.getGamepadInputManager();
if (gamepad.isConnected(0)) {
    float xAxis = gamepad.getAxis(0, GamepadAxis.LEFT_X);
}
```

---

## Audio

### AudioManager

```java
AudioManager audio = gameManager.getAudioManager();

// Play sound
Source soundSource = new Source();
soundSource.setCurrentWaveData(waveData);
soundSource.play();

// Set volume
soundSource.setVolume(0.8f);

// Loop
soundSource.setLooping(true);
```

### WaveData

```java
// Load audio file
WaveData waveData = new WaveData("audio/sound.wav");
```

---

## Resource Management

### Loading Resources

```java
ResourceManager resourceManager = gameManager.getResourceManager();

// Load texture
Texture texture = resourceManager.loadTexture("textures/player.png");

// Load string from file
String content = ResourceManager.loadString("data/config.json");

// Resolve file path
FileObject file = ResourceManager.resolveFile("assets/level1.map");
```

### Cross-Platform Paths

```java
// Use FilePathUtil for cross-platform compatibility
String normalizedPath = FilePathUtil.normalize("path/to/file.txt");

// Get config directory
String configDir = FilePathUtil.getConfigDir();
// Windows: %APPDATA%/cyan_potion
// Linux: ~/.config/cyan_potion
// macOS: ~/Library/Application Support/cyan_potion
```

---

## Scene Management

### AbstractScene

```java
public class GameScene extends AbstractScene {
    
    @Override
    public void init() {
        // Initialize scene
    }
    
    @Override
    public void update() {
        // Update logic
    }
    
    @Override
    public void render() {
        // Render scene
    }
}
```

### GameWindowComponent

```java
// Add UI components
Button button = new Button(gameManager, "Click Me");
button.setPosition(100, 100);
button.setSize(200, 50);
button.setCallback(() -> {
    System.out.println("Button clicked!");
});
```

---

## 3D Extension

### Renderer3D

```java
// Initialize 3D renderer (requires engine-3d module)
Renderer3D renderer = new Renderer3DImpl();
renderer.init();

// Set camera
renderer.setViewMatrix(viewMatrix);
renderer.setProjectionMatrix(projectionMatrix);
```

### Model

```java
// Create a cube model
Material material = Material.createColor(1.0f, 0.0f, 0.0f); // Red
Model cube = Model.createCube(2.0f, material);

// Transform
cube.setPosition(0, 0, -5);
cube.setScale(2.0f);

// Render
renderer.render(cube, cube.getTransformMatrix());
```

### Custom Mesh

```java
float[] positions = { /* vertex positions */ };
float[] normals = { /* vertex normals */ };
float[] texCoords = { /* texture coordinates */ };
int[] indices = { /* indices */ };

Mesh mesh = new Mesh(positions, normals, texCoords, indices);
Material material = new Material();
material.setDiffuseTexturePath("texture.png");

Model model = new Model("custom");
model.addMesh(mesh, material);
```

---

## Configuration

### GameSettings

```java
GameSettings settings = new GameSettings();
settings.loadFromFile("settings.ini");

// Get values
int width = settings.getInt("window.width", 1280);
String title = settings.getString("game.title", "My Game");
```

### Runtime

```java
RuntimeManager runtime = gameManager.getRuntimeManager();

// Register save data
RuntimeVariableStruct playerData = new RuntimeVariableStruct();
runtime.registerRuntimeVariableStruct(playerData);

// Save/Load
runtime.save(0);  // Save to slot 0
runtime.load(0);  // Load from slot 0
```

---

## Performance Tips

1. **Batch Rendering**: Group draw calls by texture to minimize state changes
2. **Object Pooling**: Reuse objects to reduce GC pressure
3. **Texture Atlases**: Combine small textures into larger atlases
4. **Lazy Loading**: Load resources only when needed
5. **VSync**: Enable VSync to prevent excessive CPU/GPU usage

---

## Platform-Specific Notes

### Windows
- Config directory: `%APPDATA%\cyan_potion`
- Data directory: `%LOCALAPPDATA%\cyan_potion`

### Linux
- Config directory: `~/.config/cyan_potion`
- Data directory: `~/.local/share/cyan_potion`

### macOS
- Config/Data directory: `~/Library/Application Support/cyan_potion`

---

## License

MIT License - See LICENSE file for details

Copyright (c) 2020 XenoAmess
