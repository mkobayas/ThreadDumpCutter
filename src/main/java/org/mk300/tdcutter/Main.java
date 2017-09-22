package org.mk300.tdcutter;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
	public static void main(String[] args) throws Exception {
		
		ThreadDumpCutter cutter = new ThreadDumpCutter();
        CmdLineParser parser = new CmdLineParser(cutter);
        try {
            parser.parseArgument(args);    
        } catch (CmdLineException e) {
            System.err.println(e);
            parser.printUsage(System.err);
            System.exit(1);
        }
        if(cutter.help) {
            parser.printUsage(System.err);
            System.exit(1);
        }

        cutter.doMain();
	}
}
