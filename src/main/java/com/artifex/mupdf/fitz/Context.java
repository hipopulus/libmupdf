package com.artifex.mupdf.fitz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// This class handles the loading of the MuPDF shared library, together
// with the ThreadLocal magic to get the required context.
//
// The only publicly accessible method here is Context.setStoreSize, which
// sets the store size to use. This must be called before any other MuPDF
// function.
public class Context
{
	private static boolean inited = false;
	private static native int initNative();
	public static native int gprfSupportedNative();

	public synchronized static void init() {
		if (!inited) {
			String libname = libname();
			try {
				System.load(libpath());
			} catch (UnsatisfiedLinkError e) {
				System.loadLibrary(libname);
			}
			if (initNative() < 0) {
				throw new RuntimeException("cannot initialize mupdf library");
			}
			inited = true;
		}
	}
	
	public synchronized static void download(String cdn) {
		String libname = Context.libname();
		String libpath = Context.libpath();
		File libFile = new File(libpath);
		if (!libFile.exists()) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				URL url = null;
				if (cdn.endsWith("/")) {
					url = new URL(cdn + libname);
				} else {
					url = new URL(cdn + "/" + libname);
				}
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3 * 1000);
				inputStream = conn.getInputStream();
				outputStream = new FileOutputStream(libFile);
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to download mupdf library.");
			} finally {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
				try {
					outputStream.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public static String libpath() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		File libFile = new File(tmpdir, libname());
		return libFile.getAbsolutePath();
	}
	
	public static String libname() {
		String libname = "mupdf_java";
		String os_arch = System.getProperty("os.arch");
		if (os_arch.contains("64")) {
			libname = libname + "64";
		}
		return System.mapLibraryName(libname);
	}

	// FIXME: We should support the store size being changed dynamically.
	// This requires changes within the MuPDF core.
	//public native static void setStoreSize(long newSize);
}