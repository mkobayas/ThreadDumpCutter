package org.mk300.tdcutter.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mk300.tdcutter.CutterDef;

public class RegexCutterDef implements CutterDef {

    private List<List<Pattern>> cutters = new ArrayList<>();

    @Override
    public void init(File cutterDefDir) throws Exception {
        Map<String, Pattern> cache = new HashMap<>();
        for (File f : cutterDefDir.listFiles()) {

            List<Pattern> patternList = new ArrayList<>();

            for (String line : Files.readAllLines(f.toPath(), Charset.forName("UTF-8"))) {
                String tmp = line.trim();
                if (tmp.startsWith("#") || tmp.length() == 0) {
                    if (patternList.size() > 0) {
                        cutters.add(patternList);
                        patternList = new ArrayList<>();
                    }
                    continue;
                }

                Pattern p = cache.get(line);
                if(p == null) {
                    String orginal = line;
                    line = line.replace("(", "\\(");
                    line = line.replace(")", "\\)");
                    line = line.replace("$", "\\$");
                    line = line.replace("[", "\\[");
                    line = line.replace("]", "\\]");
                    p = Pattern.compile(line);
                    cache.put(orginal, p);
                }
                patternList.add(p);
            }
            if (patternList.size() > 0) {
                cutters.add(patternList);
            }
        }
    }

    @Override
    public boolean isMatch(List<String> stack) {
        C: for (List<Pattern> patternList : cutters) {
            if (stack.size() != patternList.size()) {
                continue C;
            }

            for (int i = 0; i < stack.size(); i++) {
                Matcher m = patternList.get(i).matcher(stack.get(i));
                if (!m.matches()) {
                    continue C;
                }
            }
            return true;
        }
        return false;
    }

}
