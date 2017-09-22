package org.mk300.tdcutter.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mk300.tdcutter.CutterDef;

// TODO 
public class WidlCardCutterDef implements CutterDef {

    private List<List<List<String>>> cutters = new ArrayList<>();

    @Override
    public void init(File cutterDefDir) throws Exception {
        for (File f : cutterDefDir.listFiles()) {

            List<List<String>> patternList = new ArrayList<>();
            cutters.add(patternList);

            for (String line : Files.readAllLines(f.toPath(), Charset.forName("UTF-8"))) {
                List<String> lineElemnts = Arrays.asList(line.split("\\*", -1));
                patternList.add(lineElemnts);
            }
        }
    }

    @Override
    public boolean isMatch(List<String> stack) {
        C: for (List<List<String>> patternList : cutters) {
            if (stack.size() != patternList.size()) {
                continue C;
            }

            for (int i = 0; i < stack.size(); i++) {
                String line = stack.get(i);
                List<String> elements = patternList.get(i);

                int idx = 0;
                for (String element : elements) {
                    if (line.indexOf(element, idx) > -1) {
                        idx += element.length();
                    } else {
                        continue C;
                    }
                }
                if (idx != line.length()) {
                    continue C;
                }
            }
            return true;
        }
        return false;
    }
}
