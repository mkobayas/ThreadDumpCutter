package org.mk300.tdcutter;

import org.mk300.tdcutter.impl.RegexCutterDef;
import org.mk300.tdcutter.impl.WidlCardCutterDef;

public enum CutterType {
	
	REGREX(RegexCutterDef.class),
	WILDCARD(WidlCardCutterDef.class);
	
	private CutterType(Class<? extends CutterDef> impl) {
		this.impl = impl;
	}
	
	public CutterDef getInstance() throws Exception {
		return impl.newInstance();
	}
	
	private Class<? extends CutterDef> impl;
}
