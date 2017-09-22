package org.mk300.tdcutter;

import java.io.File;
import java.util.List;

public interface CutterDef {

    void init(File cutterDefDir) throws Exception;

    boolean isMatch(List<String> stack) throws Exception;
}
