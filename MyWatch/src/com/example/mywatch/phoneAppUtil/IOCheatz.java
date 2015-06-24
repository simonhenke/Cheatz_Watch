package com.example.mywatch.phoneAppUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.example.mywatch.phoneAppData.Chapter;
import com.example.mywatch.phoneAppData.Entry;
import com.example.mywatch.phoneAppData.ImageEntry;
import com.example.mywatch.phoneAppData.Sheet;
import com.example.mywatch.phoneAppData.TextEntry;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class IOCheatz {

	private static final String LOG_TAG = "IOCheatz";
	private static String storageDirectory = Environment
			.getExternalStorageDirectory().toString() + "/cheatz/";
	private static ZipFile zipFile;

	public static String getStorageDirectory() {
		return storageDirectory;
	}

	public static void setStorageDirectory(String storageDirectory) {
		IOCheatz.storageDirectory = storageDirectory;
	}

	public static String[] getFileList() {
		String[] fileList = new File(storageDirectory).list();
		return fileList;
	}

	public static File generateSheetStorageFile(String name) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/cheatz", name + ".zip");
		// Get the directory for the user's public pictures directory.
		if (!file.getParentFile().mkdirs()) {
			Log.e(LOG_TAG, "Directory not created");
		}
		return file;
	}

	public static void saveToArchive(Sheet sheet) {
		XmlSerializer serializer = Xml.newSerializer();
		ZipOutputStream out = null;
		FileOutputStream dest = null;
		List<Chapter> chapterList = sheet.getChapterList();

		try {
			// create zip file
			File zipFile = generateSheetStorageFile(sheet.getName());
			dest = new FileOutputStream(zipFile);
			out = new ZipOutputStream(new BufferedOutputStream(dest));

			// create entry for xml
			out.putNextEntry(new ZipEntry(sheet.getName() + ".xml"));
			// write xml into zip stream
			try {
				serializer.setOutput(out, "UTF-8");
				serializer.startDocument("UTF-8", true);
				serializer.startTag("", "sheet");
				serializer.attribute("", "name", sheet.getName());
				for (Chapter c : chapterList) {
					serializer.startTag("", "chapter");
					serializer.attribute("", "name", c.getName());
					List<Entry> entryList = c.getEntryList();
					serializer.startTag("", "entries");
					for (Entry e : entryList) {
						serializer.startTag("", "entry");
						serializer
								.attribute("", "type", e.getType().toString());
						if (e.getType().equals(Entry.Type.TEXT_ENTRY)) {
							// write text
							TextEntry tEntry = (TextEntry) e;
							serializer.text(tEntry.getText());
						} else if (e.getType().equals(Entry.Type.IMAGE_ENTRY)) {
							// set attribute for src image
							ImageEntry iEntry = (ImageEntry) e;
							serializer.attribute(
									"",
									"src",
									iEntry.getSerializationName(
											chapterList.indexOf(c),
											entryList.indexOf(iEntry)));
						}
						serializer.endTag("", "entry");
					}
					serializer.endTag("", "entries");
					serializer.endTag("", "chapter");
				}
				serializer.endTag("", "sheet");
				serializer.endDocument();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// write images into archive
			for (Chapter c : chapterList) {
				for (ImageEntry e : c.getImageEntries()) {
					out.putNextEntry(new ZipEntry(
							e.getSerializationName(chapterList.indexOf(c), c
									.getEntryList().indexOf(e))));
					// stream the image into file
					Bitmap image = e.getImage();
					Log.d(LOG_TAG, image.getWidth() + "|" + image.getHeight());
					image.compress(CompressFormat.JPEG, 100, out);
				}
			}
			Log.d(LOG_TAG, "Finished Serialization");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (dest != null)
				try {
					dest.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public static Sheet loadFromArchive(String selectedFile)
			throws IOException, XmlPullParserException {
		String filePath = storageDirectory + selectedFile;
		Sheet sheet = null;
		// get zip file
		zipFile = null;
		InputStream in = null;
		try {
			zipFile = new ZipFile(filePath);
			Log.d(LOG_TAG, "ZipFile: " + zipFile);

			// parse xml filename from filepath
			String xmlFileInZip = parseXmlFileNameFromPath(filePath);
			Log.d(LOG_TAG, "XML File in zip: " + xmlFileInZip);

			// get corresponding zipentry
			ZipEntry ze = zipFile.getEntry(xmlFileInZip);
			Log.d(LOG_TAG, "ZipEntry: " + ze);

			in = zipFile.getInputStream(ze);

			// send xml to parser
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8");
			sheet = readSheet(parser);
		} catch (IOException e) {
			throw e;
		} catch (XmlPullParserException e) {
			throw e;
		} finally {
			if (in != null)
				in.close();
			if (zipFile != null)
				zipFile.close();
		}

		return sheet;
	}

	private static String parseXmlFileNameFromPath(String filePath) {
		// just get the file name and replace .zip with .xml
		return filePath.substring(filePath.lastIndexOf("/") + 1,
				filePath.indexOf(".zip"))
				+ ".xml";
	}

	private static void skip(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	private static Entry readEntry(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		Entry e = null;
		// get type attribute
		String type = parser.getAttributeValue(null, "type");
		// determine if entry is text or image entry
		if (type.equals(Entry.Type.TEXT_ENTRY.toString())) {
			// create new text entry with text in xml tag
			e = new TextEntry(readText(parser));
		} else if (type.equals(Entry.Type.IMAGE_ENTRY.toString())) {
			// get src attribute from entry tag
			String src = parser.getAttributeValue(null, "src");
			// create new image entry by loading the src image
			e = new ImageEntry(loadSrcImage(src));
			parser.nextTag();
		}

		return e;
	}

	private static String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private static Bitmap loadSrcImage(String src) throws IOException {
		// get the corresponding zip entry
		ZipEntry ze = zipFile.getEntry(src);
		// stream into bitmap
		InputStream in = zipFile.getInputStream(ze);
		return BitmapFactory.decodeStream(in);
	}

	private static Sheet readSheet(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Log.d(LOG_TAG, "Reading sheet...");
		// read name-attribute
		String sheetName = parser.getAttributeValue(null, "name");
		Sheet s = new Sheet(sheetName);
		parser.nextTag();

		// read inner tags
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the sheet tag
			if (name.equals("chapter")) {
				s.addChapter(readChapter(parser));
			} else {
				skip(parser);
			}
		}
		return s;
	}

	private static Chapter readChapter(XmlPullParser parser) throws XmlPullParserException, IOException {
		String chapterName = parser.getAttributeValue(null, "name");
		Chapter c = new Chapter(chapterName);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the sheet tag
			if (name.equals("entries")) {
				c.setEntryList(readEntries(parser));
			} else {
				skip(parser);
			}
		}
		return c;
	}

	private static List<Entry> readEntries(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Entry> list = new LinkedList<Entry>();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			Log.d(LOG_TAG, "Name: " + name);
			// Starts by looking for the sheet tag
			if (name.equals("entry")) {
				list.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return list;
	}

}
