package com.samajackun.comcat.parser.coa.cmd;

import java.io.File;
import java.net.URL;

import com.samajackun.comcat.parser.coa.CoaAgent;

public class CoaAgentMain
{
	public static void main(String[] args)
		throws Throwable
	{
		if (args.length < 2)
		{
			System.err.println("Usage: <index URL> <output file>");
		}
		else
		{
			int n=0;
			URL indexUrl=new URL(args[n++]);
			File outputFile=new File(args[n++]);
			CoaAgent.importIndexAndExportToExcel(indexUrl, outputFile);
		}
	}

}
