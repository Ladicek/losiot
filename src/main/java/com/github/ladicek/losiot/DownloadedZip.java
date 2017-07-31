package com.github.ladicek.losiot;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class DownloadedZip implements AutoCloseable {
    public static final Path DOWNLOAD_DIRECTORY;

    private final Path file;

    static {
        try {
            // "target" is Maven's target directory
            DOWNLOAD_DIRECTORY = Paths.get("target", "download").toAbsolutePath();
            Files.createDirectories(DOWNLOAD_DIRECTORY);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static DownloadedZip find() throws IOException {
        try (Stream<Path> files = Files.walk(DOWNLOAD_DIRECTORY)) {
            List<Path> foundFiles = files.filter(Files::isRegularFile).collect(Collectors.toList());
            if (foundFiles.size() != 1) {
                throw new AssertionError("There must only be one file in the download directory");
            }
            Path onlyFile = foundFiles.get(0);
            return new DownloadedZip(onlyFile);
        }
    }

    private DownloadedZip(Path file) {
        this.file = file;
    }

    public boolean exists(String path) throws IOException {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            return zip.getEntry(path) != null;
        }
    }

    public Optional<String> readFileAsString(String fileName) throws IOException {
        try (ZipFile zip = new ZipFile(file.toFile())) {
            ZipEntry entry = zip.getEntry(fileName);

            if (entry == null) {
                return Optional.empty();
            }

            String value = CharStreams.toString(new InputStreamReader(zip.getInputStream(entry)));
            return Optional.of(value);
        }
    }

    @Override
    public void close() throws IOException {
        Files.delete(file);
    }
}
