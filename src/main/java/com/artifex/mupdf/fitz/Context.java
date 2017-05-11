package com.artifex.mupdf.fitz;

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
			inited = true;
			try {
				String libpath = "META-INF/lib";
				URL url = Context.class.getClassLoader().getResource(libpath);
				String libname = "mupdf_java";
				String os_arch = System.getProperty("os.arch");
				if (os_arch.contains("64")) {
					libname = libname + "64";
				}
				System.load(url.getPath() + System.mapLibraryName(libname));
			} catch (UnsatisfiedLinkError e) {
				throw new RuntimeException("cannot initialize mupdf library");
			}
			if (initNative() < 0)
				throw new RuntimeException("cannot initialize mupdf library");
		}
	}

	static { init(); }

	// FIXME: We should support the store size being changed dynamically.
	// This requires changes within the MuPDF core.
	//public native static void setStoreSize(long newSize);
}