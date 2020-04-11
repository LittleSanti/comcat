package com.samajackun.comcat.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.Issue;

public interface Exporter
{
	public void export(Stream<Issue> issues, OutputStream output, String title)
		throws IOException;

	public void export(Collection collection, OutputStream output, String title)
		throws IOException;
}
