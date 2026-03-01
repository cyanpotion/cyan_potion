# GUI Components for xenoamess-s-civilization

This package contains GUI components for displaying and managing persons in the civilization game.

## Components

### PersonListComponent
A scrollable list component for displaying and filtering persons.

**Features:**
- Scrollable list of persons
- Real-time search/filter by name, ID, gender, or clan
- Gender filtering (male/female/all)
- Scrollbar navigation
- Keyboard navigation (Up/Down/Home/End)
- Selection callback

**Usage:**
```java
PersonListComponent list = new PersonListComponent(gameWindow);
list.setLeftTopPos(50, 80);
list.setSize(500, 600);
list.setOnPersonSelected(person -> {
    // Handle person selection
});
list.setPersons(personList); // Set the list of persons to display
```

### PersonDetailComponent
A detailed view component showing all person attributes.

**Features:**
- Displays all basic attributes (health, constitution, intelligence, etc.)
- Shows derived attributes (strength, charm, management)
- Clan information with primary/secondary distinction
- Parent information
- Gender visualization
- Close button and ESC key support

**Usage:**
```java
PersonDetailComponent detail = new PersonDetailComponent(gameWindow);
detail.setLeftTopPos(580, 80);
detail.setSize(550, 600);
detail.show(person); // Show details for a person
detail.hide(); // Hide the detail view
```

### PersonBrowserDemo
A demo component combining the list and detail views with control buttons.

**Features:**
- Generate random persons
- Filter by gender
- Clear filters
- Select person to view details
- Full keyboard navigation

**Usage:**
```java
PersonBrowserDemo browser = new PersonBrowserDemo(gameWindow);
browser.addToGameWindowComponentTree(gameWindow.getGameWindowComponentTree());
```

## Building

These GUI components depend on the cyan_potion engine's base module. To compile:

1. Build and install the parent pom:
```bash
cd src/parent
mvn install -DskipTests
```

2. Build and install the base module:
```bash
cd src/base
mvn install -DskipTests
```

3. Update the civilization module's pom.xml to include the base dependency:
```xml
<dependency>
    <groupId>com.xenoamess.cyan_potion</groupId>
    <artifactId>base</artifactId>
    <version>0.167.3-SNAPSHOT</version>
</dependency>
```

4. Build the civilization module:
```bash
cd src/xenoamess-s-civilization
mvn compile
```

## Architecture

The GUI components extend `AbstractControllableGameWindowComponent` from the cyan_potion engine,
which provides:
- Event processing (mouse, keyboard)
- Position and size management
- Drawing and updating lifecycle
- Component tree integration

### Component Hierarchy
```
PersonBrowserDemo (demo/world component)
├── PersonListComponent (scrollable list)
│   ├── PersonListItem (individual person row)
│   ├── InputBox (search field)
│   └── Panel (list container)
├── PersonDetailComponent (detail view)
│   └── Button (close button)
└── Buttons (control buttons)
```

## Integration Example

```java
public class MyGameWorld extends World {
    private PersonBrowserDemo personBrowser;
    
    public MyGameWorld(GameWindow gameWindow) {
        super(gameWindow);
        
        // Create person browser
        personBrowser = new PersonBrowserDemo(gameWindow);
        
        // Generate some test data
        RandomPersonGenerator generator = new RandomPersonGenerator();
        List<Person> persons = generator.generateMultiple(50);
        personBrowser.getListComponent().setPersons(persons);
    }
    
    @Override
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        personBrowser.addToGameWindowComponentTree(node);
    }
    
    @Override
    public boolean draw() {
        super.draw();
        personBrowser.draw();
        return true;
    }
    
    @Override
    public void update() {
        super.update();
        personBrowser.update();
    }
}
```
