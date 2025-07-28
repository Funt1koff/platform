package com.bonfire;

import io.quarkus.runtime.Quarkus;

public class Main {
    public static void main(String[] args) {
        System.out.println("Running main method");
        Quarkus.run(args);
    }
}
