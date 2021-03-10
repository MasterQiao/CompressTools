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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class Tar {

	public static void main(String[] args) throws IOException {
//		compress(new File("src"), new File("tartest.tar"));
//		uncompress(new File("tartest.tar"), new File("tartest"));
		System.out.println(listFiles(new File("tartest.tar")));
	}

	public static List<String> listFiles(File tarFile) throws IOException {
		List<String> ret = new ArrayList<>();
		TarArchiveInputStream tips = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
		TarArchiveEntry entry = null;
		while ((entry = tips.getNextTarEntry()) != null) {
			ret.add(entry.getName());
		}
		tips.close();
		return ret;
	}

	public static void uncompress(File tarFile, File dest) throws IOException {
		if (!dest.exists()) {
			dest.mkdirs();
		}
		if (!dest.isDirectory()) {
			throw new IllegalArgumentException("Argument dest must be a directory");
		}
		TarArchiveInputStream tips = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
		byte[] data = new byte[2048];
		TarArchiveEntry entry = null;
		while ((entry = tips.getNextTarEntry()) != null) {
			if (entry.isDirectory()) {
				new File(dest, entry.getName()).mkdirs();
			} else {
				File f = new File(dest, entry.getName());
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				OutputStream ops = new BufferedOutputStream(new FileOutputStream(f));
				int size = -1;
				while ((size = tips.read(data)) != -1) {
					ops.write(data, 0, size);
				}
				ops.close();
			}
		}
		tips.close();
	}

	public static void compress(File src, File tarFile) throws IOException {
		tarFile = new File(tarFile.getCanonicalPath());
		if (!tarFile.getParentFile().exists()) {
			tarFile.getParentFile().mkdirs();
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

		OutputStream ops = new BufferedOutputStream(new FileOutputStream(tarFile));
		TarArchiveOutputStream tops = new TarArchiveOutputStream(ops);
		byte[] buffer = new byte[2048];
		for (File file : files) {
			String path = file.getCanonicalPath();
			path = path.substring(beginIndex, path.length());
			if (file.isDirectory()) {
				path = path + "/";
			}
			TarArchiveEntry entry = (TarArchiveEntry) tops.createArchiveEntry(file, path);
			tops.putArchiveEntry(entry);
			if (file.isFile()) {
				InputStream ips = new BufferedInputStream(new FileInputStream(file));
				int size = -1;
				while ((size = ips.read(buffer)) != -1) {
					tops.write(buffer, 0, size);
				}
				ips.close();
			}
			tops.closeArchiveEntry();
		}
		tops.close();
	}

}
