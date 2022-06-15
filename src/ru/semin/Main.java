package ru.semin;

import jdk.swing.interop.SwingInterOpUtils;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        SlopeOne so = new SlopeOne();

        so.addRate("John", "Item A", 5);
        so.addRate("John", "Item B", 3);
        so.addRate("John", "Item C", 2);
        so.addRate("Mark", "Item A", 3);
        so.addRate("Mark", "Item B", 4);
        so.addRate("Lucy", "Item B", 2);
        so.addRate("Lucy", "Item C", 5);

        Map<String, Double> r = so.predict("Lucy");

        System.out.println("Recommendations for Lucy:");
        r.entrySet().stream()
                .map(m -> m.getKey() + ": " + m.getValue())
                .forEach(System.out::println);

    }

}
