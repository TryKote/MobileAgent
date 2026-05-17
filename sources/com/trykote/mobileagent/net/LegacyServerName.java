package com.trykote.mobileagent.net;


import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.NameType;
import org.bouncycastle.crypto.tls.ServerName;
import org.bouncycastle.crypto.tls.TlsFatalAlert;
import org.bouncycastle.crypto.tls.TlsUtils;

import java.io.IOException;
import java.io.OutputStream;

final class LegacyServerName extends ServerName {

    LegacyServerName(short type, Object value) {
        super(type, value);
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(nameType, output);
        if (nameType != NameType.host_name) {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }
        byte[] ascii;
        try {
            ascii = ((String) name).getBytes("US_ASCII");
        } catch (Exception e) {
            ascii = ((String) name).getBytes("ASCII");
        }
        if (ascii.length < 1) {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }
        TlsUtils.writeOpaque16(ascii, output);
    }
}
