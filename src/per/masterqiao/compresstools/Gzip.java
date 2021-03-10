package per.masterqiao.compresstools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class Gzip {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		compress(new File("tartest.tar"), new File("tartest.tar.gz"));
		uncompress(new File("tartest.tar.gz"), new File("gztest.tar"));
	}

	public static void uncompress(File gzipFile, File dest) throws IOException {
		dest = new File(dest.getCanonicalPath());
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		
		InputStream ips = new BufferedInputStream(new FileInputStream(gzipFile));
		GzipCompressorInputStream gzips = new GzipCompressorInputStream(ips);
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(dest));
		int size = -1;
		byte[] buffer = new byte[2048];
		while ((size = gzips.read(buffer)) != -1) {
			ops.write(buffer, 0, size);
		}
		ops.close();
		gzips.close();
	}

	public static void compress(File src, File gzipFile) throws IOException {
		gzipFile = new File(gzipFile.getCanonicalPath());
		if (!gzipFile.getParentFile().exists()) {
			gzipFile.getParentFile().mkdirs();
		}
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(gzipFile));
		GzipCompressorOutputStream gzops = new GzipCompressorOutputStream(ops);
		InputStream ips = new BufferedInputStream(new FileInputStream(src));
		int size = -1;
		byte[] buffer = new byte[2048];
		while ((size = ips.read(buffer)) != -1) {
			gzops.write(buffer, 0, size);
		}
		ips.close();
		gzops.close();
	}
	
}
