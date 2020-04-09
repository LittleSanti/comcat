package com.samajackun.comcat.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;

public final class ImageFactory
{
	private static final Log LOG=LogFactory.getLog(ImageFactory.class);

	private static final ImageFactory INSTANCE=new ImageFactory();

	private final File root=createRootDir();

	public static ImageFactory getInstance()
	{
		return INSTANCE;
	}

	private static File createRootDir()
	{
		String rootDir=System.getProperty("com.samajackun.comcat.cache.dir");
		File root=new File(rootDir == null
			? System.getProperty("user.dir")
			: rootDir);
		root.mkdirs();
		return root;
	}

	private ImageFactory()
	{
	}

	public Image getImage(File src)
	{
		Image image;
		String name=src.getName();
		File cachedFile=new File(this.root, name);
		if (!cachedFile.exists())
		{
			synchronized (this)
			{
				if (!cachedFile.exists())
				{
					try
					{
						Files.copy(src.toPath(), cachedFile.toPath());
						image=new FileImage(cachedFile);
					}
					catch (IOException e)
					{
						image=null;
						LOG.error("Error copying image " + src.getAbsolutePath() + " to " + this.root.getAbsolutePath());
					}
				}
				else
				{
					image=new FileImage(cachedFile);
				}
			}
		}
		else
		{
			image=new FileImage(cachedFile);
		}
		return image;
	}

	public Image getImage(URL src)
	{
		Image image;
		String name=getName(src);
		File cachedFile=new File(this.root, name);
		if (!cachedFile.exists())
		{
			synchronized (this)
			{
				if (!cachedFile.exists())
				{
					try (InputStream input=src.openStream(); OutputStream output=new FileOutputStream(cachedFile))
					{
						IOUtils.copy(input, output);
						image=new FileImage(cachedFile);
					}
					catch (IOException e)
					{
						image=null;
						LOG.error("Error downloading image " + src + " to " + this.root.getAbsolutePath());
					}
				}
				else
				{
					image=new FileImage(cachedFile);
				}
			}
		}
		else
		{
			image=new FileImage(cachedFile);
		}
		return image;
	}

	private static String getName(URL src)
	{
		int p=src.getPath().lastIndexOf('/');
		return p < 0
			? src.getPath()
			: src.getPath().substring(1 + p);
	}
}
