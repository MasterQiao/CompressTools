package per.masterqiao.compresstools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class BZip2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		compress(new File("tartest.tar"), new File("tartest.tar.bz2"));
		uncompress(new File("tartest.tar.bz2"), new File("bz2test.tar"));
	}

	public static void uncompress(File bz2File, File dest) throws IOException {
		dest = new File(dest.getCanonicalPath());
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}

		InputStream ips = new BufferedInputStream(new FileInputStream(bz2File));
		BZip2CompressorInputStream bz2ips = new BZip2CompressorInputStream(ips);
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(dest));
		int size = -1;
		byte[] buffer = new byte[2048];
		while ((size = bz2ips.read(buffer)) != -1) {
			ops.write(buffer, 0, size);
		}
		ops.close();
		bz2ips.close();
	}

	public static void compress(File src, File bz2File) throws IOException {
		bz2File = new File(bz2File.getCanonicalPath());
		if (!bz2File.getParentFile().exists()) {
			bz2File.getParentFile().mkdirs();
		}
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(bz2File));
		BZip2CompressorOutputStream bz2ops = new BZip2CompressorOutputStream(ops);
		InputStream ips = new BufferedInputStream(new FileInputStream(src));
		int size = -1;
		byte[] buffer = new byte[2048];
		while ((size = ips.read(buffer)) != -1) {
			bz2ops.write(buffer, 0, size);
		}
		ips.close();
		bz2ops.close();
	}

}
