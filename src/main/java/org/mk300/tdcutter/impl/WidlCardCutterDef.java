package org.mk300.tdcutter.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.mk300.tdcutter.CutterDef;

public class WidlCardCutterDef implements CutterDef {

    private List<List<WildPattern>> cutters = new ArrayList<>();

    @Override
    public void init(File cutterDefDir) throws Exception {
        Map<String, WildPattern> cache = new HashMap<>();
        for (File f : cutterDefDir.listFiles()) {

            List<WildPattern> patternList = new ArrayList<>();

            for (String line : Files.readAllLines(f.toPath(), Charset.forName("UTF-8"))) {
                String tmp = line.trim();
                if (tmp.startsWith("#") || tmp.length() == 0) {
                    if (patternList.size() > 0) {
                        cutters.add(patternList);
                        patternList = new ArrayList<>();
                    }
                    continue;
                }

                WildPattern p = cache.get(line);
                if(p == null) {
                    p = new WildPattern(line);
                    cache.put(line, p);
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
        C: for (List<WildPattern> patternList : cutters) {
            if (stack.size() != patternList.size()) {
                continue C;
            }

            for (int i = 0; i < stack.size(); i++) {
                String line = stack.get(i);
                WildPattern pattern = patternList.get(i);
                if(! pattern.match(line)) {
                    continue C;
                }
            }
            return true;
        }
        return false;
    }
    
    private static class WildPattern {
        String pattern;
        List<String> elements;
        boolean matchAll = false;
        boolean startWithCheck = false;
        boolean endWithCheck = false;
        boolean sameCheck = false;
        
        WildPattern(String pattern) {
            this.pattern = pattern;
            if(Objects.equals("*", pattern)) {
                matchAll = true;
                return;
            }
            if(!pattern.contains("*")) {
                sameCheck = true;
                return;
            }
            
            if(!pattern.startsWith("*")) {
                startWithCheck = true;
            }
            if(!pattern.endsWith("*")) {
                endWithCheck = true;
            }

            elements = Arrays.asList(pattern.split("\\*"));
            if(elements.get(0).length() == 0) {
                elements = elements.subList(1, elements.size());
            }
        }
        
        boolean match(String line) {
            if(matchAll) {
                return true;
            }
            
            if(sameCheck) {
                if(pattern.equals(line)) {
                    return true;
                } else {
                    return false;
                }
            }
            
            if(startWithCheck) {
                if(!line.startsWith(elements.get(0))) {
                    return false;
                }
            }
            if(endWithCheck) {
                if(!line.endsWith(elements.get(elements.size()-1))) {
                    return false;
                }
            }

            int idx = 0;
            for (String element : elements) {
                if (line.indexOf(element, idx) > -1) {
                    idx += element.length();
                } else {
                    return false;
                }
            }

            return true;
        }
    }
}
