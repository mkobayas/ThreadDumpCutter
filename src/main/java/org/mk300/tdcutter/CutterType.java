package org.mk300.tdcutter;

import java.io.File;

import org.mk300.tdcutter.impl.RegexCutterDef;
import org.mk300.tdcutter.impl.WidlCardCutterDef;

public enum CutterType {

    REGREX(RegexCutterDef.class),
    WILDCARD(WidlCardCutterDef.class);

    private CutterType(Class<? extends CutterDef> impl) {
        this.impl = impl;
    }

    public CutterDef getInstance(File cutterDefDir) throws Exception {
        CutterDef cutter = impl.newInstance();
        cutter.init(cutterDefDir);
        return cutter;
    }

    private Class<? extends CutterDef> impl;
}
