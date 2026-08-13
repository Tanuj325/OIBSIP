# Online Examination System 📝

A modern, high-performance, desktop-based **Online Examination System** built with **Java 21**, **Java Swing**, and **FlatLaf Look & Feel**. 

This system provides a full-featured online testing experience featuring user authentication, profile management, interactive multiple-choice question (MCQ) examination panels, live progress bars, real-time countdown timers with visual alerts, submission guards, and dynamic results breakdown.

---

## 🌟 Key Features

- **🔒 Authentication & Session Management**:
  - Secure login with SHA-256 password hashing.
  - Decoupled `UserRepository`, `AuthenticationService`, and `UserSession` model.
  - Complete session and state reset upon logout.

- **👤 User Profile & Security Panel**:
  - Displays read-only username and editable display name.
  - Optional password update with match confirmation.
  - Remembers profile completion state to prevent repetitive login loops.

- **📋 Examination Instructions & Overview**:
  - Pre-exam prep panel displaying exam title, dynamic total question count (20 questions), and 30-minute duration.
  - Assessment countdown timer initializes **only** when the candidate clicks **Start Exam**.

- **⏱️ Interactive MCQ Exam Interface**:
  - Displays **1 MCQ at a time** with 4 options using `JRadioButton` and `ButtonGroup`.
  - **Live Progress Bar**: `JProgressBar` tracking completion percentage (`((currentIdx + 1) * 100) / total`).
  - **Live Answered Counter**: Real-time counter (`Answered: X / N`) updating immediately upon option selection.
  - **Real-Time Countdown Timer**: `javax.swing.Timer` (30:00 ➔ 00:00) with visual alerts:
    - 🟧 **Amber Warning** when time $\le 5$ minutes.
    - 🟥 **Red Critical Alert** when time $\le 1$ minute.
  - **Question Navigator Grid**: Quick-jump buttons (1 to N) with color-coded status (Active, Answered, Unanswered).
  - **Answer Persistence**: Preserves choices across `Next`, `Previous`, and jump navigation.

- **🛡️ Submission Safety & State Machine**:
  - Guarded exam state transitions (`NOT_STARTED` ➔ `IN_PROGRESS` ➔ `SUBMITTED` ➔ `ABANDONED`) preventing duplicate submissions.
  - **Auto-Submission**: Automatically submits without dialogs when timer reaches `00:00`.
  - **Manual Submission**: Modal dialog confirmation (`YES` / `NO`).
  - **Window Close Protection**: Intercepts `WINDOW_CLOSING` during active tests to prevent accidental window closure.

- **📊 Comprehensive Results & Breakdown**:
  - Summary metrics card: Final Score (`X out of Y`), Correct, Incorrect, Unanswered, Time Elapsed.
  - **Dynamic Performance Message**: Grade-based feedback banner (*"Excellent Performance!"*, *"Good Performance!"*, etc.).
  - **Question Breakdown Table**: Read-only `JTable` inside `JScrollPane` listing Question #, Question Text, Selected Answer, Correct Answer, and Status.
  - **Retake Exam Option**: Allows candidates to cleanly reset answers and restart the test.

---

## 🏗️ Architecture & Project Structure

The project follows a clean, decoupled MVC-Service-Repository architecture:

```
src/main/java/com/oasis/exam/
├── Main.java                      # Application entry point
├── model/                         # Data transfer objects & state representations
│   ├── User.java
│   ├── Question.java
│   ├── ExamState.java             # NOT_STARTED, IN_PROGRESS, SUBMITTED, ABANDONED
│   ├── UserSession.java           # Logged-in session & profile completion state
│   ├── QuestionResult.java
│   └── ExamResult.java
├── repository/                    # In-memory repositories & data stores
│   ├── UserRepository.java        # Pre-seeded users & password hashing
│   └── QuestionRepository.java    # 20 Java & CS multiple-choice questions
├── service/                       # Core business logic services
│   ├── AuthenticationService.java
│   ├── ExamService.java           # Timer tick, navigation, submission guard
│   └── ResultService.java         # Score evaluation & result computation
├── ui/                            # Swing views & CardLayout components
│   ├── ThemeManager.java          # FlatDarkLaf theme customization & styling tokens
│   ├── MainFrame.java             # Top-level window & CardLayout navigator
│   ├── LoginPanel.java
│   ├── ProfilePanel.java
│   ├── InstructionsPanel.java
│   ├── ExamPanel.java
│   └── ResultPanel.java
└── util/                          # Validation & time formatting helpers
    ├── ValidationUtil.java
    └── TimeUtil.java
```

---

## 🛠️ Technology Stack

- **Programming Language**: Java 21
- **GUI Framework**: Java Swing
- **Look and Feel**: [FlatLaf](https://www.formdev.com/flatlaf/) 3.4.1 (FlatDarkLaf)
- **Build System**: Apache Maven 3.9+
- **Testing Framework**: JUnit 5 (JUnit Jupiter 5.10.2)

---

## 🚀 Quick Start & How to Run

### Prerequisites

Ensure you have the following installed:
- **Java Development Kit (JDK)**: Version 21 or higher
- **Apache Maven**: Version 3.8+

### Default Accounts for Testing

| Username | Password | Display Name | Role |
|---|---|---|---|
| `student1` | `password123` | Candidate Student | Standard Student |
| `admin` | `admin123` | System Administrator | Administrator |
| `candidate` | `pass123` | Alex Johnson | Candidate |

---

### Step-by-Step Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/Java-Task4-OnlineExaminationSystem.git
   cd Java-Task4-OnlineExaminationSystem
   ```

2. **Build the Project**:
   ```bash
   mvn clean package
   ```

3. **Run via Maven Exec Plugin**:
   ```bash
   mvn exec:java
   ```

4. **Or Run Standalone Executable JAR**:
   ```bash
   java -jar target/online-examination-system-1.0.0-jar-with-dependencies.jar
   ```

5. **Run Automated Unit Tests**:
   ```bash
   mvn test
   ```

---

## 🧪 Testing Coverage

The application includes 14 automated unit tests covering:
- Credential authentication, hashing, profile updates, and session clearing (`AuthenticationServiceTest`).
- Exam initialization, navigation, answer persistence, double-submission locking, and timer cancellation (`ExamServiceTest`).
- Question bank bounds and validation (`QuestionRepositoryTest`).
- Dynamic score evaluation, unanswered count calculation, and performance messaging (`ResultServiceTest`).

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
