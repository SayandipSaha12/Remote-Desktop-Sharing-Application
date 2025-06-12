# 📡 Remote Desktop Sharing Application 
A complete **Remote Desktop Sharing** application built in **Java** using **VS Code**. This project enables users to **share their desktop screen in real-time over localhost**, along with **remote mouse and keyboard control**.

## 🚀 Features

* 📺 **Live Screen Streaming at 10 FPS**
* 🖱️ **Full Remote Mouse & Keyboard Control**
* 🌐 **Multi-Client Server Architecture**
* 📡 **TCP Socket Networking**
* 🎛️ **Swing-based GUI Interface**
* ⚙️ **Concurrent Connection Handling**
* 💾 Demonstrates **Image Compression, Event Handling, and Multi-Threading**

## 📁 Project Structure

```
remote-desktop-app/
├── src/
│   ├── RemoteDesktopLauncher.java
│   ├── RemoteDesktopServer.java
│   ├── RemoteDesktopClient.java
│   ├── ScreenData.java
│   └── RemoteCommand.java
├── .vscode/
│   └── launch.json
└── README.md
```

## 🛠️ Setup & Installation

### 1️⃣ Install JDK
* Install **JDK 8** or higher from Oracle or OpenJDK.

### 2️⃣ Install VS Code & Java Extensions
* Install **Visual Studio Code**
* Install **"Extension Pack for Java"** (includes Java Language Support, Debugger, Maven, etc.)

## 📦 How To Run

### 📌 Method 1: Using VS Code Run/Debug
* Open the project folder in VS Code
* Press **F5** or click **Run**
* Select:
  * **"Launch Launcher"** — to open the launcher UI
  * **"Launch Server"** — to start the server
  * **"Launch Client"** — to start a client

### 📌 Method 2: Using VS Code Terminal

```bash
cd src
javac *.java
java RemoteDesktopLauncher
```

### 📌 Method 3: Using Code Lens
* Open any Java file
* Click **"Run"** above the `main` method

## 🖥️ How To Test

1. **Start Server**
   * Run **RemoteDesktopLauncher**
   * Click **Start as Server**

2. **Start Client**
   * Run **another instance** of **RemoteDesktopLauncher**
   * Click **Start as Client**
   * Enter `localhost` as the server address
   * Click **Connect**

## 🔧 Troubleshooting

### Common Issues:
* **"Java not found"** — Ensure JDK is installed and added to PATH
* **Robot Class Issues (Screen/Microphone Permissions)**
  * On macOS: Grant screen recording permissions to Terminal or VS Code
* **Firewall** — Allow Java through Windows Firewall
* **VS Code Java Extension Issues**
  * Press `Ctrl+Shift+P` → *"Java: Reload Projects"*

## ⚠️ Common Question: Why So Many `.class` Files After Compilation?

**Problem:** After compiling your project, you might notice **many more** `.class` files in the `src/` folder than the original 5 `.java` files you created.

**Why It Happens:** When Java compiles your code:
* It generates one `.class` file for **each class**.
* It also creates **separate** `.class` files for every inner class, enum, or anonymous class.

**For Example:**
* `RemoteDesktopServer.class` — main server class
* `RemoteDesktopServer$ClientHandler.class` — inner class handling each client
* `RemoteDesktopClient$1.class`, `RemoteDesktopClient$2.class`, etc. — anonymous classes for event listeners
* `RemoteCommand$CommandType.class` — for the enum inside `RemoteCommand`

This is **normal behavior** in Java and indicates that your code compiled successfully.

**Solution:**
* ✅ To remove these `.class` files:

```bash
cd src
del *.class     # On Windows
# or
rm *.class      # On Mac/Linux
```

* ✅ To prevent them from cluttering your repository:
  * Create a `.gitignore` file in your project root:

```
*.class
.vscode/
```

* ✅ Or in VS Code:
  * Click the **filter icon** in the Explorer pane
  * Add filter pattern: `!*.class` to temporarily hide compiled files from the view

**Summary:**
* ✅ **5** `.java` files = your editable source code
* ✅ **Multiple** `.class` files = compiled bytecode (safe, expected)
* ✅ Application working = everything is correct!

The extra files are a good sign — your compilation was successful, and the application is ready to run.

## 📖 Concepts Demonstrated

✅ **Socket Programming**  
✅ **Image Serialization & Compression**  
✅ **Event Handling**  
✅ **Multi-threading**  
✅ **Swing GUI Development**

