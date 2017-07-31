package com.github.ladicek.losiot;

public enum Runtime {
    WILDFLY_SWARM("WildFly Swarm"),
    VERTX("Vert.x"),
    SPRING_BOOT("Spring Boot"),
    ;

    public final String text;

    Runtime(String text) {
        this.text = text;
    }

    public boolean isJava() {
        return this == WILDFLY_SWARM || this == VERTX || this == SPRING_BOOT;
    }
}
