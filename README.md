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

### Drag and Drop Support
The editor supports drag and drop operations for easy file handling:
- Drag any `.md` or `.markdown` file from your file explorer
- Drop it anywhere in the editor window
- The file will automatically load and display
- Invalid file types will show an error message
- Multiple files can be dropped, but only the first one will be loaded

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

## 📖 Markdown Syntax Guide

### Text Formatting
| Syntax | Description | Example |
|--------|-------------|---------|
| `*italic*` | Italic text | *italic* |
| `**bold**` | Bold text | **bold** |
| `~~strikethrough~~` | Strikethrough text | ~~strikethrough~~ |
| `` `code` `` | Inline code | `code` |
| `[link](url)` | Hyperlink | [link](https://example.com) |
| `![alt](url)` | Image | ![alt](image.jpg) |

### Headers
```markdown
# H1 Header
## H2 Header
### H3 Header
#### H4 Header
##### H5 Header
###### H6 Header
```

### Lists
```markdown
Unordered list:
- Item 1
- Item 2
  - Subitem 2.1
  - Subitem 2.2

Ordered list:
1. First item
2. Second item
   1. Subitem 2.1
   2. Subitem 2.2
```

### Code Blocks
````markdown
```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```
````

### Syntax Highlighting
The editor supports syntax highlighting for code blocks. Simply specify the language after the opening backticks:

```markdown
```java
// Java code with syntax highlighting
public class Example {
    private String message;
    
    public void setMessage(String msg) {
        this.message = msg;
    }
}
```

```python
# Python code with syntax highlighting
def hello_world():
    print("Hello, World!")
```

```javascript
// JavaScript code with syntax highlighting
function greet(name) {
    console.log(`Hello, ${name}!`);
}
```

```html
<!-- HTML code with syntax highlighting -->
<div class="container">
    <h1>Hello World</h1>
    <p>This is a paragraph.</p>
</div>
```

```css
/* CSS code with syntax highlighting */
.container {
    background-color: #f0f0f0;
    padding: 20px;
    border-radius: 5px;
}
```
```

Supported languages include:
- Java
- Python
- JavaScript
- HTML
- CSS
- SQL
- XML
- JSON
- YAML
- Shell/Bash
- C/C++
- Ruby
- PHP
- Go
- Rust
- TypeScript
- And many more...

The syntax highlighting adapts to both light and dark themes automatically.

### Tables
```markdown
| Header 1 | Header 2 | Header 3 |
|----------|----------|----------|
| Cell 1   | Cell 2   | Cell 3   |
| Cell 4   | Cell 5   | Cell 6   |
```

### Blockquotes
```markdown
> This is a blockquote
> 
> > This is a nested blockquote
```

### Horizontal Rules
```markdown
---
***
___
```

### Task Lists
```markdown
- [x] Completed task
- [ ] Pending task
- [ ] Another task
```

### Footnotes
```markdown
Here's a sentence with a footnote. [^1]

[^1]: This is the footnote.
```

### Escaping Characters
Use `\` to escape special characters:
```markdown
\* Not italic
\# Not a header
\[ Not a link
```

### Line Breaks
```markdown
First line  
Second line (two spaces at end of first line)

New paragraph (empty line)
```

### HTML Support
Basic HTML tags are supported:
```markdown
<kbd>Ctrl+C</kbd>
<mark>Highlighted text</mark>
<sup>Superscript</sup>
<sub>Subscript</sub>
```

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