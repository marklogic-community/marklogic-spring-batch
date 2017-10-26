package com.marklogic.spring.batch.item.reader.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class MapFileVisitor extends SimpleFileVisitor<Path> {

    private final static Logger logger = LoggerFactory.getLogger(MapFileVisitor.class);

    private PathMatcher matcher;
    private Map<String, Path> fileMap;
    private int numberOfFiles = 0;
    private int numberOfMatches = 0;

    public MapFileVisitor() {
        this("**.*");
    }

    public MapFileVisitor(String globPattern) {
        fileMap = new HashMap<String, Path>();
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
    }

    public Map<String, Path> getFileMap() {
        return fileMap;
    }

    public int getNumberOfMatches() {
        return numberOfMatches;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        numberOfFiles++;
        if (match(file)) {
            numberOfMatches++;
            String fileName = file.getFileName().toString();
            fileMap.put(file.getParent().toString() + File.separator + fileName, file);
            logger.debug(fileName);
        }
        return FileVisitResult.CONTINUE;
    }

    // Invoke the pattern matching method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        match(dir);
        return FileVisitResult.CONTINUE;
    }

    // Compares the glob pattern against the file or directory name.
    // https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    private boolean match(Path file) {
        if (matcher.matches(file)) {
            logger.debug("MATCH: " + file.getFileName());
            return true;
        } else {
            return false;
        }

    }

}
