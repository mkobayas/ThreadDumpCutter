package org.mk300.tdcutter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Option;

public class ThreadDumpCutter {

    @Option(name = "-h", aliases = { "--Help" }, help = true)
    public boolean help;

    @Option(name = "-t", aliases = { "--CutterType" }, usage = "Type of CutterDef", hidden = true)
    private CutterType cutterType = CutterType.REGREX;

    @Option(name = "-c", aliases = { "--CutterDef" }, usage = "Directory of CutterDef", required = true)
    private String cutterDefDirString;

    @Option(name = "-i", aliases = { "--InDir" }, usage = "Directory of input threaddumps", required = true)
    private File inDir;

    @Option(name = "-p", aliases = { "--FileNamePattern" }, usage = "Pattern(wildcard) of input fileName")
    private String fileNamePattern = "*";

    @Option(name = "-o", aliases = { "--OutDir" }, usage = "Directory of output threaddumps", required = true)
    private File outDir;

    public void doMain() throws Exception {

        CutterDef cutterDef = cutterType.getInstance();

        File cutterDefDir = new File("cutterDef", cutterDefDirString);
        cutterDef.init(cutterDefDir);

        List<Path> inFilePathList = findFiles(inDir, fileNamePattern);

        int fileNum = inFilePathList.size();
        int current = 0;
        for (Path inFilePath : inFilePathList) {

            List<List<String>> thDump = new ArrayList<>();
            List<String> th = new ArrayList<>();
            for (String line : Files.readAllLines(inFilePath, Charset.forName("UTF-8"))) {
                if (line.equals("")) {
                    thDump.add(th);
                    th = new ArrayList<>();
                } else {
                    th.add(line);
                }
            }
            if (th.size() > 0) {
                thDump.add(th);
            }

            List<List<String>> filtered = new ArrayList<>();

            for (List<String> stack : thDump) {
                if (!cutterDef.isMatch(stack)) {
                    filtered.add(stack);
                }
            }

            List<String> outLines = new ArrayList<>();
            for (List<String> thFilterd : filtered) {
                for (String line : thFilterd) {
                    outLines.add(line);
                }
                outLines.add("");
            }

            Path outPath = toOutPath(outDir, inDir, inFilePath);

            Files.createDirectories(outPath.getParent());
            Files.write(outPath, outLines, Charset.forName("UTF-8"));

            current++;

            System.out.println(String.format("File: %,d/%,d %s (%,d->%,d)", current, fileNum, outPath.toString(),
                    thDump.size(), filtered.size()));
        }
    }

    private List<Path> findFiles(File inDir, String pattern) throws Exception {
        final String regPattern = pattern.replace(".", "\\.").replace("*", ".*?");

        final List<Path> pathList = new ArrayList<>();
        Files.walkFileTree(inDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().matches(regPattern)) {
                    pathList.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return pathList;
    }

    private Path toOutPath(File outDir, File inDir, Path inFilePath) {
        Path outDirPath = outDir.toPath();
        Path inAbsPath = inDir.toPath().toAbsolutePath();
        Path inFileAbsPath = inFilePath.toAbsolutePath();

        String outFileName = inFileAbsPath.subpath(inAbsPath.getNameCount(), inFileAbsPath.getNameCount()).toString();
        Path outPath = Paths.get(outDirPath.toString(), outFileName);

        return outPath;
    }
}
