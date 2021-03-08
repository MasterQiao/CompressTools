package per.masterqiao.compresstools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {

	public static void main(String[] args) throws IOException {
//		compress(new File("src"), new File("ziptest.zip"));
//		uncompress(new File("ziptest.zip"), new File("ziptest"));
		System.out.println(listFiles(new File("ziptest.zip")));
	}

	public static List<String> listFiles(File zipFile) throws IOException {
		List<String> ret = new ArrayList<>();
		ZipInputStream zips = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
		ZipEntry entry = null;
		while ((entry = zips.getNextEntry()) != null) {
			ret.add(entry.getName());
		}
		zips.close();
		return ret;
	}

	public static void uncompress(File zipFile, File dest) throws IOException {
		if (!dest.exists()) {
			dest.mkdirs();
		}
		if (!dest.isDirectory()) {
			throw new IllegalArgumentException("Argument dest must be a directory");
		}
		ZipInputStream zips = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
		byte[] data = new byte[2048];
		ZipEntry entry = null;
		while ((entry = zips.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				new File(dest, entry.getName()).mkdirs();
			} else {
				File f = new File(dest, entry.getName());
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				OutputStream ops = new BufferedOutputStream(new FileOutputStream(f));
				int size = -1;
				while ((size = zips.read(data)) != -1) {
					ops.write(data, 0, size);
				}
				ops.close();
			}
		}
		zips.close();
	}

	public static void compress(File src, File zipFile) throws IOException {
		zipFile = new File(zipFile.getCanonicalPath());
		if (!zipFile.getParentFile().exists()) {
			zipFile.getParentFile().mkdirs();
		}

		final List<File> files = new ArrayList<>();
		SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				files.add(file.toFile());
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				files.add(dir.toFile());
				return super.preVisitDirectory(dir, attrs);
			}
		};
		Files.walkFileTree(src.toPath(), visitor);
		int beginIndex = src.getCanonicalPath().lastIndexOf(src.getName());

		OutputStream ops = new BufferedOutputStream(new FileOutputStream(zipFile));
		ZipOutputStream zops = new ZipOutputStream(ops);
		zops.setLevel(9);
		byte[] buffer = new byte[2048];
		for (File file : files) {
			String path = file.getCanonicalPath();
			path = path.substring(beginIndex, path.length());
			if (file.isDirectory()) {
				path = path + "/";
			}
			ZipEntry entry = new ZipEntry(path);
			zops.putNextEntry(entry);
			if (file.isFile()) {
				InputStream ips = new BufferedInputStream(new FileInputStream(file));
				int size = -1;
				while ((size = ips.read(buffer)) != -1) {
					zops.write(buffer, 0, size);
				}
				ips.close();
			}
		}
		zops.close();
	}
}
