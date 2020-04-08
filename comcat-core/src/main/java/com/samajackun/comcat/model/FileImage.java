package com.samajackun.comcat.model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileImage implements Image
{
	private final File src;

	private final Format format;

	public FileImage(File src, Format format)
	{
		super();
		this.src=src;
		this.format=format;
	}

	public FileImage(File src)
	{
		this(src, guessFormat(src.getName()));
	}

	private static Format guessFormat(String name)
	{
		Format format;
		int p=name.lastIndexOf('.');
		String ext=p < 0
			? ""
			: name.substring(1 + p);
		switch (ext.toLowerCase())
		{
			case "jpg":
			case "jpeg":
				format=Format.JPG;
				break;
			case "png":
				format=Format.JPG;
				break;
			default:
				throw new IllegalArgumentException("Unrecognized image format from file name " + name);
		}
		return format;
	}

	@Override
	public Format getFormat()
	{
		return this.format;
	}

	@Override
	public byte[] getData()
		throws IOException
	{
		return Files.readAllBytes(this.src.toPath());
	}

	@Override
	public void serialize(OutputStream out)
		throws IOException
	{
		Files.copy(this.src.toPath(), out);
	}

}
