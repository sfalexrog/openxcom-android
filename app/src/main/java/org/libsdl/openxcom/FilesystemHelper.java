package org.libsdl.openxcom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;


/*
 * A collection of methods to be used by other classes.
 */

public final class FilesystemHelper {
	
	public static final int BUFFER_SIZE = 8192;
	
	/**
	 * Creates a copy of the file
	 * @param in Source file
	 * @param out Destination file
	 * @throws IOException if the operation fails.
	 * 
	 */
	public static void copyFile(File in, File out) throws IOException {
		InputStream in_stream = new FileInputStream(in);
		OutputStream out_stream = new FileOutputStream(out);
		BufferedInputStream bis = new BufferedInputStream(in_stream, BUFFER_SIZE);
		BufferedOutputStream bos = new BufferedOutputStream(out_stream, BUFFER_SIZE);
		copyStream(in_stream, out_stream);
	}
	
	/**
	 * Copies inputStream to outputStream in a somewhat buffered way
	 * @param in Input stream
	 * @param out Output stream
	 * @throws IOException if the operation fails
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	/**
	 * Copies contents of in_folder to out_folder.
	 * @param in_folder Source folder.
	 * @param out_folder Destination folder.
	 * @param recursive Copy subdirectories as well.
	 * @throws IOException
	 */
	public static void copyFolder(File in_folder, File out_folder, boolean recursive) throws IOException {
		if (!out_folder.exists()) {
			if (!out_folder.mkdirs()) {
				throw new IOException("Could not create target directory: " + out_folder.getAbsolutePath());
			}
		}
		if (!in_folder.isDirectory()) {
			throw new IOException("Source is not a directory: " + in_folder.getAbsolutePath());
		}
		if (!out_folder.isDirectory()) {
			throw new IOException("Target is not a directory: " + out_folder.getAbsolutePath());
		}
		Log.i("copyFolder", "Source folder: " + in_folder.getPath() + "; Target folder: " + out_folder.getPath());
		File[] in_list = in_folder.listFiles();
		for (File in_file : in_list) {
			if (in_file.isDirectory())
			{
				if (recursive) {
					File out_subfolder = new File(out_folder.getAbsolutePath() + "/" + in_file.getName());
					copyFolder(in_file, out_subfolder, recursive);
				}
			} else {
				File out_file = new File(out_folder.getAbsolutePath() + "/" + in_file.getName());
				Log.i("FileHelper", "Source: " + in_file.getPath() + "; Destination: " + out_file.getPath());
				copyFile(in_file, out_file);
			}
			
			
		}
	}
	
	/**
	 * Extracts the whole contents of in_file to out_dir.
	 * @param in_file A file object pointing to a zip file.
	 * @param out_dir A directory where the contents of the file should be placed.
	 * @throws IOException if there's a problem with one of the files
	 */
	public static void zipExtract(File in_file, File out_dir) throws IOException {
		InputStream is = new FileInputStream(in_file);
		BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
		zipExtract(bis, out_dir);
	}
	/**
	 * Reads the in_stream and extracts them to out_dir
	 * @param in_stream
	 * @param out_dir
	 * @throws IOException
	 */
	public static void zipExtract(InputStream in_stream, File out_dir) throws IOException {
		if (!out_dir.exists()) {
			if(!out_dir.mkdirs()) {
				throw new IOException("Could not create output directory: " + out_dir.getAbsolutePath());
			}
		}
		ZipInputStream zis = new ZipInputStream(in_stream);
		ZipEntry ze;
		byte[] buffer = new byte[BUFFER_SIZE];
		int count;
		while((ze = zis.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				File fmd = new File(out_dir.getAbsolutePath() + "/" + ze.getName());
				fmd.mkdirs();
				continue;
			}
			FileOutputStream fout = new FileOutputStream(out_dir.getAbsolutePath() + "/" + ze.getName());
			while ((count = zis.read(buffer)) != -1) {
				fout.write(buffer, 0, count);
			}
			
			fout.close();
			zis.closeEntry();
		}
		zis.close();
	}
	
	/**
	 * Returns true if files are same and false value if the files are different.
	 * This function is very slow, so it will probably need a rewrite.
	 * @param fileIS1 Input stream associated with the first file
	 * @param fileIS2 Input stream associated with the second file
	 * @return True if the files are the same, false otherwise.
	 * @throws IOException if an error is encountered.
	 */
	public static boolean sameContents(InputStream fileIS1, InputStream fileIS2) throws IOException {
		int b1 = fileIS1.read();
		int b2 = fileIS2.read();
		while((b1 != -1) && (b2 != -1))
		{
			if (b1 != b2) {
				return false;
			}
			b1 = fileIS1.read();
			b2 = fileIS2.read();
		}
		return b1 == b2;
	}
	
	
}
