package me.hipoplar.libmupdf;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.artifex.mupdf.fitz.ColorSpace;
import com.artifex.mupdf.fitz.Cookie;
import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.DrawDevice;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Pixmap;

public class Convertor {
	
	public static List<BufferedImage> toImage(String filename, float scale) throws ConvertorException { 
		Document document = null;
		List<BufferedImage> images = new ArrayList<>();
		try {
			document = new Document(filename);
			for (int i = 0; i < document.countPages(); i++) {
				Page p = null;
				try {
					p = document.loadPage(i);
					images.add(Convertor.toImage(p, 2f));
				} catch (Exception e) {
					e.printStackTrace();
					throw new ConvertorException(e);
				} finally {
					if (p != null) {
						p.destroy();
					}
				}
			}
			return images;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConvertorException(e);
		} finally {
			if (document != null) {
				document.destroy();
			}
		}
	}
	
	public static List<BufferedImage> toImage(byte[] pdf, String magic, float scale) throws ConvertorException { 
		Document document = null;
		List<BufferedImage> images = new ArrayList<>();
		try {
			document = new Document(pdf, magic);
			for (int i = 0; i < document.countPages(); i++) {
				Page p = null;
				try {
					p = document.loadPage(i);
					images.add(Convertor.toImage(p, 2f));
				} catch (Exception e) {
					e.printStackTrace();
					throw new ConvertorException(e);
				} finally {
					if (p != null) {
						p.destroy();
					}
				}
			}
			return images;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConvertorException(e);
		} finally {
			if (document != null) {
				document.destroy();
			}
		}
	}
	
	public static BufferedImage toImage(byte[] pdf, String magic, int page, float scale) throws ConvertorException { 
		Document document = null;
		Page p = null;
		try {
			document = new Document(pdf, magic);
			p = document.loadPage(page - 1);
			return Convertor.toImage(p, 2f);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConvertorException(e);
		} finally {
			if (document != null) {
				document.destroy();
			}
			if (p != null) {
				p.destroy();
			}
		}
	}

	public static BufferedImage toImage(Page page, float scale) throws ConvertorException {
		Pixmap pixmap = null;
		DrawDevice dev = null;
		Cookie cookie = null;
		try {
			Matrix ctm = new Matrix();
			ctm.scale(scale);
			pixmap = page.toPixmap(ctm, ColorSpace.DeviceBGR, true);
			pixmap.clear(255);
			dev = new DrawDevice(pixmap);
			cookie = new Cookie();
			page.run(dev, ctm, cookie);
			BufferedImage image = imageFromPixmap(pixmap);
			return image;
		} catch (Exception e) {
			throw new ConvertorException(e);
		} finally {
			if (pixmap != null) {
				pixmap.destroy();
			}
			if (cookie != null) {
				cookie.destroy();
			}
			if (dev != null) {
				dev.destroy();
			}
		}
	}

	private static BufferedImage imageFromPixmap(Pixmap pixmap) {
		int w = pixmap.getWidth();
		int h = pixmap.getHeight();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, w, h, pixmap.getPixels(), 0, w);
		return image;
	}
}
