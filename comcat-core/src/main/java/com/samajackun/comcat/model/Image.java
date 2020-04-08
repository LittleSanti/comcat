package com.samajackun.comcat.model;

import java.io.IOException;
import java.io.OutputStream;

public interface Image
{
	public enum Format {
		JPG, PNG
	};

	public Format getFormat();

	public byte[] getData()
		throws IOException;

	public void serialize(OutputStream out)
		throws IOException;
}
