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

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

public class SevenZ {

	public static void main(String[] args) throws IOException {
//		compress(new File("src"), new File("7ztest.7z"));
//		uncompress(new File("7ztest.7z"), new File("7ztest"));
		System.out.println(listFiles(new File("7ztest.7z")));
	}

	public static List<String> listFiles(File sevenZFile) throws IOException {
		List<String> ret = new ArrayList<>();
		SevenZFile svnZFile = new SevenZFile(sevenZFile);
		SevenZArchiveEntry entry = null;
		while ((entry = svnZFile.getNextEntry()) != null) {
			ret.add(entry.getName());
		}
		svnZFile.close();
		return ret;
	}

	public static void uncompress(File sevenZFile, File dest) throws IOException {
		if (!dest.exists()) {
			dest.mkdirs();
		}
		if (!dest.isDirectory()) {
			throw new IllegalArgumentException("Argument dest must be a directory");
		}
		SevenZFile svnZFile = new SevenZFile(sevenZFile);
		byte[] data = new byte[2048];
		SevenZArchiveEntry entry = null;
		while ((entry = svnZFile.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				new File(dest, entry.getName()).mkdirs();
			} else {
				File f = new File(dest, entry.getName());
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				OutputStream ops = new BufferedOutputStream(new FileOutputStream(f));
				int size = -1;
				while ((size = svnZFile.read(data)) != -1) {
					ops.write(data, 0, size);
				}
				ops.close();
			}
		}
		svnZFile.close();
	}

	public static void compress(File src, File sevenZFile) throws IOException {
		sevenZFile = new File(sevenZFile.getCanonicalPath());
		if (!sevenZFile.getParentFile().exists()) {
			sevenZFile.getParentFile().mkdirs();
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

		SevenZOutputFile sevenZOutputFile = new SevenZOutputFile(sevenZFile);

		byte[] buffer = new byte[2048];
		for (File file : files) {
			String path = file.getCanonicalPath();
			path = path.substring(beginIndex, path.length());
			if (file.isDirectory()) {
				path = path + "/";
			}
			SevenZArchiveEntry entry = sevenZOutputFile.createArchiveEntry(file, path);
			sevenZOutputFile.putArchiveEntry(entry);
			if (file.isFile()) {
				InputStream ips = new BufferedInputStream(new FileInputStream(file));
				int size = -1;
				while ((size = ips.read(buffer)) != -1) {
					sevenZOutputFile.write(buffer, 0, size);
				}
				ips.close();
			}
			sevenZOutputFile.closeArchiveEntry();
		}
		sevenZOutputFile.close();
	}

}
