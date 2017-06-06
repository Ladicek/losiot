package com.github.ladicek.losiot;

import java.util.Objects;

public final class MavenCoordinates {
    public final String groupId;
    public final String artifactId;
    public final String version;

    public static MavenCoordinates forTest() {
        return new MavenCoordinates("io.openshift.losiot", "test-artifact", "2.3.5-SNAPSHOT");
    }

    public MavenCoordinates(String groupId, String artifactId, String version) {
        this.groupId = Objects.requireNonNull(groupId);
        this.artifactId = Objects.requireNonNull(artifactId);
        this.version = Objects.requireNonNull(version);
    }
}
