/*
 * Copyright (C) 2009 Wayne Meissner
 *
 * This file is part of java native runtime.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kenai.jnr.unixsocket;

import com.kenai.constantine.platform.ProtocolFamily;
import com.kenai.jaffl.Platform;
import com.kenai.jaffl.struct.Struct;
import com.kenai.jaffl.struct.Struct.UTF8String;
import com.kenai.jaffl.struct.Struct.Unsigned16;
import com.kenai.jaffl.struct.Struct.Unsigned8;

/**
 * Native unix domain socket address structure.
 */
abstract class SockAddrUnix extends Struct {
    public final static int ADDR_LENGTH = 108;
    
    protected abstract UTF8String getPathField();
    protected abstract NumberField getFamilyField();

    /**
     * Sets the protocol family of this unix socket address.
     *
     * @param family The protocol family, usually {@link com.kenai.constantine.platform.ProtocolFamily.PF_UNIX}
     */
    public final void setFamily(ProtocolFamily family) {
        getFamilyField().set(family.value());
    }


    /**
     * Gets the protocol family of this unix socket address.
     *
     * @return The protocol family
     */
    public final ProtocolFamily getFamily() {
        return ProtocolFamily.valueOf(getFamilyField().intValue());
    }

    /**
     * Sets the file system path of this socket address
     *
     * @param path The unix socket address
     */
    public final void setPath(java.lang.String path) {
        getPathField().set(path);
    }

    /**
     * Gets the file system path of this socket address
     *
     * @return A String
     */
    public final java.lang.String getPath() {
        return getPathField().get();
    }

    /**
     * Gets the maximum length of this address (including len/family header)
     *
     * @return The maximum size of the address in bytes
     */
    public int getMaximumLength() {
        return 2 + getPathField().length();
    }

    /**
     * Gets the actual length of this address (including len/family header)
     *
     * @return The actual size of this address, in bytes
     */
    public int length() {
        return 2 + strlen(getPathField());
    }

    
    /**
     * Creates a new instance of <tt>SockAddrUnix</tt>
     *
     * @return An instance of <tt>SockAddrUnix</tt>
     */
    static SockAddrUnix create() {
        return Platform.getPlatform().isBSD() ? new BSDSockAddrUnix() : new DefaultSockAddrUnix();
    }

    private static final int strlen(UTF8String str) {
        int end = str.getMemoryIO().indexOf(str.offset(), (byte) 0);
        return end >= 0 ? end : str.length();
    }
    
    /**
     * An implementation of {@link SockAddrUnix} for BSD systems 
     */
    static final class BSDSockAddrUnix extends SockAddrUnix {

        public final Unsigned8 sun_len = new Unsigned8();
        public final Unsigned8 sun_family = new Unsigned8();
        public final UTF8String sun_addr = new UTF8String(ADDR_LENGTH);

        protected final UTF8String getPathField() {
            return sun_addr;
        }
        protected final NumberField getFamilyField() {
            return sun_family;
        }
    }


    /**
     * An implementation of {@link SockAddrUnix} for Linux, Solaris, et, al
     */
    static final class DefaultSockAddrUnix extends SockAddrUnix {
        public final Unsigned16 sun_family = new Unsigned16();
        public final UTF8String sun_addr = new UTF8String(ADDR_LENGTH);

        protected final UTF8String getPathField() {
            return sun_addr;
        }

        protected final NumberField getFamilyField() {
            return sun_family;
        }
    }
}