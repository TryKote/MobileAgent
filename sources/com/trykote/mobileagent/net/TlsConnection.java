package com.trykote.mobileagent.net;


import com.trykote.mobileagent.util.RemoteLogger;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.NameType;
import org.bouncycastle.crypto.tls.ServerNameList;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Vector;

public final class TlsConnection {

    private SocketConnection socket;
    private TlsClientProtocol tlsProtocol;

    public TlsConnection(String host, int port) throws IOException {
        RemoteLogger.log("TLS", "connecting to " + host + ":" + port);
        socket = (SocketConnection) Connector.open("socket://" + host + ":" + port);
        InputStream plainIn = socket.openInputStream();
        OutputStream plainOut = socket.openOutputStream();

        tlsProtocol = new TlsClientProtocol(plainIn, plainOut, new SecureRandom());
        tlsProtocol.connect(new SniTlsClient(host));
        RemoteLogger.log("TLS", "handshake complete");
    }

    public InputStream getInputStream() {
        return tlsProtocol.getInputStream();
    }

    public OutputStream getOutputStream() {
        return tlsProtocol.getOutputStream();
    }

    public void close() {
        try {
            tlsProtocol.close();
        } catch (Throwable ignored) {
        }
        try {
            socket.close();
        } catch (Throwable ignored) {
        }
    }

    private static class SniTlsClient extends DefaultTlsClient {

        private final String host;

        SniTlsClient(String host) {
            this.host = host;
        }

        public Hashtable getClientExtensions() throws IOException {
            Hashtable ext = TlsExtensionsUtils.ensureExtensionsInitialised(
                    super.getClientExtensions());
            Vector names = new Vector();
            names.addElement(new LegacyServerName(NameType.host_name, host));
            TlsExtensionsUtils.addServerNameExtension(ext, new ServerNameList(names));
            return ext;
        }

        public TlsAuthentication getAuthentication() throws IOException {
            return new TlsAuthentication() {
                public void notifyServerCertificate(
                        org.bouncycastle.crypto.tls.Certificate cert) throws IOException {
                }

                public TlsCredentials getClientCredentials(
                        CertificateRequest req) throws IOException {
                    return null;
                }
            };
        }
    }
}
