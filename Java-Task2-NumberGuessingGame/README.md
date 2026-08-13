# Number Guessing Game

A modern, high-contrast, fully functional Java Swing desktop game where players attempt to guess a randomly generated hidden number within customizable difficulty levels and limited attempt bounds.

---

## 🌟 Key Features

- **Modern Swing Desktop Interface**: Built with FlatLaf dark theme, responsive layout managers (`BorderLayout`, `GridBagLayout`, `GridLayout`), high-contrast typography, and adaptive window scaling (`JFrame.MAXIMIZED_BOTH`).
- **Dynamic Random Target Generation**: Automatically generates a non-repeating random target number (`java.util.Random`) in the active range at the start of every new round.
- **Difficulty Configurations**:
  - **Easy**: Range `1 – 50`, `10` Attempts
  - **Medium**: Range `1 – 100`, `7` Attempts
  - **Hard**: Range `1 – 200`, `5` Attempts
- **Interactive Feedback Engine**: Displays real-time visual banners for `"Too High!"`, `"Too Low!"`, `"Correct!"`, or `"You Lost!"` along with dynamically narrowing hint boundaries (`minHint – maxHint`).
- **Attempts Progress Tracking**: Visual progress bar updating after every valid guess (Green → Yellow → Red).
- **Strict Input Validation**: Rejects empty inputs, non-numeric strings, decimals, and out-of-bounds guesses without consuming attempts.
- **Score & Round History Dashboard**:
  - Tracks total rounds played, rounds won, rounds lost, and win percentage.
  - Interactive `JTable` rendering round-by-round logs.
  - Dedicated `"Reset Score"` feature with safety confirmation modal.

---

## 🚀 Architecture & Class Overview

| Package | Class | Purpose |
| :--- | :--- | :--- |
| `com.oasis.guessinggame.model` | `Difficulty` | Enum defining difficulty parameters (range bounds, max attempts). |
| `com.oasis.guessinggame.model` | `GuessResult` | Enum representing guess evaluation outcomes. |
| `com.oasis.guessinggame.model` | `GuessFeedback` | Data transfer model encapsulating attempt state and hints. |
| `com.oasis.guessinggame.model` | `GameRound` | Model tracking individual active or completed round data. |
| `com.oasis.guessinggame.model` | `GameStatistics` | Aggregate score metrics, win rates, and history log store. |
| `com.oasis.guessinggame.service` | `NumberGuessingGame` | Core game engine decoupled from UI layer. |
| `com.oasis.guessinggame.util` | `GameConstants` | High-contrast design tokens, color palette, and typography fonts. |
| `com.oasis.guessinggame.ui` | `StartPanel` | Welcome screen panel with difficulty selection cards. |
| `com.oasis.guessinggame.ui` | `GamePanel` | Main active gameplay UI with guess input field and result banner. |
| `com.oasis.guessinggame.ui` | `StatisticsPanel` | Dashboard displaying performance cards and round history table. |
| `com.oasis.guessinggame.ui` | `MainFrame` | Top-level maximized window container. |
| `com.oasis.guessinggame` | `Main` | Entry point setting look-and-feel and launching GUI on EDT. |

---

## ⚙️ Requirements & Prerequisites

- **Java Development Kit (JDK)**: Java 21 or higher
- **Build Tool**: Apache Maven 3.8+

---

## 🔨 Building and Running

### 1. Run Automated Unit Tests
```powershell
mvn clean test
```

### 2. Build Executable Package
```powershell
mvn clean package
```

### 3. Run Application
```powershell
mvn exec:java
```
Or run the compiled standalone JAR:
```powershell
java -jar target/number-guessing-game-1.0.0-jar-with-dependencies.jar
```
