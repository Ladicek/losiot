package com.github.ladicek.losiot;

public enum Target {
    PROD("https://launch.openshift.io/"),
    STAGE("https://launch.prod-preview.openshift.io/"),
    ;

    public final String url;

    Target(String url) {
        this.url = url;
    }
}
