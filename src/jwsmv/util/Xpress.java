// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.util;

import java.io.IOException;

/**
 * A Java implementation of MS-XCA (LZ77) "Xpress" encoding.
 *
 * @see http://msdn.microsoft.com/en-us/library/ee896247%28v=prot.20%29.aspx
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class Xpress {
    public Xpress() {
	throw new RuntimeException("Xpress encoding is not implemented");
    }

    /**
     * Compress and encode.
     */
    public byte[] encode(byte[] buff) throws IOException {
	return buff;
    }

    /**
     * Decode and uncompress.
     */
    public byte[] decode(byte[] buff) throws IOException {
	return buff;
    }
}
