package com.github.ladicek.losiot;

public enum Mission {
    M100_HTTP_API("REST API Level 0"),
    M101_CRUD(null),
    M102_CONFIGMAP(null),
    M103_SSO(null),
    M104_HEALTH_CHECK("Health Check"),
    ;

    public final String text;

    Mission(String text) {
        this.text = text;
    }
}
