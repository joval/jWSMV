// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

/**
 * A Java implementation of MS-XCA (LZ77) "Xpress" encoding.
 *
 * @see http://msdn.microsoft.com/en-us/library/ee896247%28v=prot.20%29.aspx
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class Xpress {
    public static final int MAX_LEN = 65535;

    private Deflater deflater;
    private Inflater inflater;

    public Xpress() {
	deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
	inflater = new Inflater(true);
    }

    /**
     * Compress and encode.
     */
    public byte[] encode(byte[] buff) throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream zOut = new DeflaterOutputStream(out, deflater);
        zOut.write(buff);
	zOut.close();
	return out.toByteArray();
    }

    /**
     * Decode and uncompress.
     */
    public byte[] decode(byte[] buff) throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ByteArrayInputStream in = new ByteArrayInputStream(buff);
        InflaterInputStream zIn = new InflaterInputStream(in);
	byte[] ba = new byte[256];
	int len = 0;
	while ((len = zIn.read(ba)) > 0) {
	    out.write(ba, 0, len);
	}
	return out.toByteArray();
    }
}
