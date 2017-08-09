package com.github.ladicek.losiot;

public enum DeploymentType {
    ZIP("ZIP File"),
    OPENSHIFT(null),
    ;

    public final String text;

    DeploymentType(String text) {
        this.text = text;
    }
}
