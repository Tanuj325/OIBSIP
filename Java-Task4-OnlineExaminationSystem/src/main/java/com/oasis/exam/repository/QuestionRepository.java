package com.oasis.exam.repository;

import com.oasis.exam.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dedicated repository storing the exam question bank.
 */
public class QuestionRepository {

    private final List<Question> questionBank;

    public QuestionRepository() {
        this.questionBank = initializeQuestions();
    }

    private List<Question> initializeQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(new Question(1,
                "Which of the following feature is NOT supported by Java?",
                "Object-Oriented Programming",
                "Pointers & Direct Memory Access",
                "Automatic Garbage Collection",
                "Multi-Threading Support",
                "B"));

        list.add(new Question(2,
                "What is the default initial capacity of an ArrayList in Java?",
                "5",
                "10",
                "16",
                "20",
                "B"));

        list.add(new Question(3,
                "Which keyword is used to prevent method overriding in Java?",
                "static",
                "abstract",
                "final",
                "synchronized",
                "C"));

        list.add(new Question(4,
                "What is the superclass of all classes in Java?",
                "java.lang.System",
                "java.lang.Object",
                "java.lang.Class",
                "java.util.Component",
                "B"));

        list.add(new Question(5,
                "Which exception is thrown when dividing an integer by zero in Java?",
                "NullPointerException",
                "ArithmeticException",
                "NumberFormatException",
                "IllegalArgumentException",
                "B"));

        list.add(new Question(6,
                "Which interface must be implemented to sort objects using natural ordering in Java?",
                "java.util.Comparator",
                "java.lang.Comparable",
                "java.lang.Cloneable",
                "java.io.Serializable",
                "B"));

        list.add(new Question(7,
                "What does JVM stand for?",
                "Java Virtual Machine",
                "Java Variable Memory",
                "Java Visual Model",
                "Joint Vector Machine",
                "A"));

        list.add(new Question(8,
                "Which component in Java Swing is used to layout components in a tabbed panel view?",
                "JSplitPane",
                "JTabbedPane",
                "JScrollPane",
                "JPanel",
                "B"));

        list.add(new Question(9,
                "What is the return type of the hashCode() method in Java?",
                "long",
                "String",
                "int",
                "double",
                "C"));

        list.add(new Question(10,
                "Which keyword is used to invoke a parent class constructor in Java?",
                "this",
                "super",
                "parent",
                "extends",
                "B"));

        list.add(new Question(11,
                "Which layout manager arranges components in 5 regions: North, South, East, West, and Center?",
                "FlowLayout",
                "GridLayout",
                "BorderLayout",
                "BoxLayout",
                "C"));

        list.add(new Question(12,
                "Which Java collection guarantees element uniqueness and preserves insertion order?",
                "HashSet",
                "TreeSet",
                "LinkedHashSet",
                "ArrayList",
                "C"));

        list.add(new Question(13,
                "What is the memory size of a primitive 'double' variable in Java?",
                "4 bytes",
                "8 bytes",
                "16 bytes",
                "2 bytes",
                "B"));

        list.add(new Question(14,
                "Which method is executed first when a standard standalone Java application runs?",
                "public void run()",
                "public static void main(String[] args)",
                "public void init()",
                "public static void start()",
                "B"));

        list.add(new Question(15,
                "What is the outcome of compiling and running String s1 = \"abc\"; String s2 = \"abc\"; s1 == s2?",
                "false",
                "true",
                "Compilation Error",
                "Runtime Exception",
                "B"));

        list.add(new Question(16,
                "Which access modifier makes a field accessible ONLY within the same class?",
                "public",
                "protected",
                "private",
                "default (package-private)",
                "C"));

        list.add(new Question(17,
                "Which Swing class is recommended for background countdown timers on the Event Dispatch Thread?",
                "java.util.Timer",
                "javax.swing.Timer",
                "java.lang.Thread",
                "java.util.concurrent.ScheduledExecutorService",
                "B"));

        list.add(new Question(18,
                "Which keyword is used in Java to inherit a class?",
                "implements",
                "extends",
                "inherits",
                "super",
                "B"));

        list.add(new Question(19,
                "What is the time complexity of searching a key in a balanced HashMap (average case)?",
                "O(1)",
                "O(n)",
                "O(log n)",
                "O(n^2)",
                "A"));

        list.add(new Question(20,
                "Which Java 8 feature allows passing functionality as method arguments concisely?",
                "Generics",
                "Lambda Expressions",
                "Annotations",
                "Reflection API",
                "B"));

        return Collections.unmodifiableList(list);
    }

    public List<Question> getAllQuestions() {
        return questionBank;
    }

    public int getTotalQuestionsCount() {
        return questionBank.size();
    }

    public Question getQuestionByIndex(int index) {
        if (index < 0 || index >= questionBank.size()) {
            throw new IndexOutOfBoundsException("Question index " + index + " out of bounds (0 to " + (questionBank.size() - 1) + ")");
        }
        return questionBank.get(index);
    }
}
