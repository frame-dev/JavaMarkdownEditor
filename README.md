# Markdown Editor Swing

A simple, lightweight Markdown editor built with **Java Swing** and **flexmark-java**. It provides live preview, file operations, HTML export, and dark mode — all in a pure Swing environment (no JavaFX required).

---

## 🚀 Features

- ✍️ Live Markdown editing with HTML preview
- 💾 Open and save `.md` files
- 🌐 Export styled HTML files
- 🌗 Toggleable dark mode
- 🧾 Table and code block support
- ✅ No JavaFX or browser dependency
- ⌨️ Keyboard shortcuts for common operations
- 📱 Responsive HTML export with proper styling
- 🎨 Customizable font sizes
- 📂 Recent files management
- 🔗 Clickable links in preview
- 📋 Drag and drop file support

---

## ⌨️ Keyboard Shortcuts

- `Ctrl + S` - Save file
- `Ctrl + O` - Open file
- `Ctrl + E` - Export as HTML
- `Ctrl + D` - Toggle dark mode

---

## 📷 Screenshot

![screenshot](screenshot.png)

---

## 🔧 How to Build & Run

### 📦 Prerequisites

- Java 17 or later
- Maven

### ⚙️ Build

```bash
mvn clean package
```

### 🚀 Run

```bash
java -jar target/markdown-editor-swing-1.0-SNAPSHOT.jar
```

---

## 📝 Usage Guide

### Basic Editing
1. Start typing in the left panel - your Markdown will be rendered in real-time on the right
2. Use standard Markdown syntax for formatting:
   - `#` for headings
   - `*` or `_` for emphasis
   - `**` or `__` for bold
   - `-` or `*` for lists
   - ``` for code blocks
   - `|` for tables

### File Operations
- Use File menu or keyboard shortcuts to:
  - Open existing `.md` files
  - Save your work
  - Export to HTML with proper styling
- Drag and drop `.md` files directly into the editor
- Access recently opened files from the Recent Files menu

### View Options
- Toggle dark mode for comfortable viewing
- Adjust font size from the View menu
- Links in the preview are clickable and will open in your default browser

### HTML Export
- Exports include:
  - Proper HTML5 structure
  - Responsive design
  - Dark/light theme support
  - Syntax highlighting for code blocks
  - Styled tables
  - Custom fonts and spacing

---

## 🛠️ Technical Details

### Dependencies
- flexmark-java for Markdown parsing
- Java Swing for the GUI
- No external browser or JavaFX required

### Supported Markdown Features
- Headers (H1-H6)
- Emphasis and bold
- Lists (ordered and unordered)
- Code blocks with syntax highlighting
- Tables
- Links and images
- Blockquotes
- Horizontal rules
- Strikethrough text
- Auto-linking of URLs

---

## 🤝 Contributing

Feel free to submit issues and enhancement requests!