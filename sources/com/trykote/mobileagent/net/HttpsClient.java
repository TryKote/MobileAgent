package com.trykote.mobileagent.net;


import com.trykote.mobileagent.util.RemoteLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class HttpsClient {

    public interface ProgressListener {
        void onUploadProgress(int sent, int total);
    }

    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_HEADER_SIZE = 4096;
    private static final int PUT_CHUNK_SIZE = 2048;

    private TlsConnection tls;
    private InputStream in;
    private int responseCode;
    private long contentLength = -1;

    public HttpsClient(String url) throws IOException {
        this(url, "GET", null, null, null, null);
    }

    public HttpsClient(String url, String method, byte[] body, String contentType, String[] extraHeaders) throws IOException {
        this(url, method, body, contentType, extraHeaders, null);
    }

    public HttpsClient(String url, String method, byte[] body, String contentType, String[] extraHeaders, ProgressListener listener) throws IOException {
        String remaining = url.substring(8);
        int slashIdx = remaining.indexOf('/');
        String hostPort = slashIdx >= 0 ? remaining.substring(0, slashIdx) : remaining;
        String path = slashIdx >= 0 ? remaining.substring(slashIdx) : "/";

        String host;
        int port = 443;
        int colonIdx = hostPort.indexOf(':');
        if (colonIdx >= 0) {
            host = hostPort.substring(0, colonIdx);
            port = Integer.parseInt(hostPort.substring(colonIdx + 1));
        } else {
            host = hostPort;
        }

        tls = new TlsConnection(host, port);
        OutputStream out = tls.getOutputStream();
        in = tls.getInputStream();

        StringBuffer req = new StringBuffer();
        req.append(method).append(' ').append(path).append(" HTTP/1.1\r\n");
        req.append("Host: ").append(host).append("\r\n");
        req.append("Connection: close\r\n");
        if (body != null) {
            req.append("Content-Length: ").append(body.length).append("\r\n");
            if (contentType != null) {
                req.append("Content-Type: ").append(contentType).append("\r\n");
            }
        }
        if (extraHeaders != null) {
            for (int i = 0; i + 1 < extraHeaders.length; i += 2) {
                req.append(extraHeaders[i]).append(": ").append(extraHeaders[i + 1]).append("\r\n");
            }
        }
        req.append("\r\n");

        RemoteLogger.debug("TLS", "sending " + method + " " + path.substring(0, Math.min(path.length(), 40)) + " body=" + (body != null ? body.length : 0));
        out.write(req.toString().getBytes("UTF-8"));
        if (body != null) {
            int offset = 0;
            while (offset < body.length) {
                int chunk = Math.min(PUT_CHUNK_SIZE, body.length - offset);
                out.write(body, offset, chunk);
                offset += chunk;
                if (listener != null) {
                    listener.onUploadProgress(offset, body.length);
                }
            }
        }
        out.flush();
        RemoteLogger.debug("TLS", "request sent, reading headers");

        parseResponseHeaders();
        RemoteLogger.info("TLS", "response " + responseCode + " len=" + contentLength);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public long getContentLength() {
        return contentLength;
    }

    public InputStream getInputStream() {
        return in;
    }

    public void close() {
        if (tls != null) {
            tls.close();
            tls = null;
        }
    }

    private void parseResponseHeaders() throws IOException {
        byte[] buf = new byte[MAX_HEADER_SIZE];
        int pos = 0;
        boolean headersDone = false;

        RemoteLogger.debug("TLS", "parseHeaders: reading first byte");
        while (!headersDone && pos < MAX_HEADER_SIZE) {
            int b = in.read();
            if (pos == 0) {
                RemoteLogger.debug("TLS", "parseHeaders: first byte=" + b);
            }
            if (b < 0) {
                RemoteLogger.debug("TLS", "parseHeaders: EOF at pos=" + pos);
                break;
            }
            buf[pos++] = (byte) b;
            if (pos >= 4
                    && buf[pos - 4] == '\r' && buf[pos - 3] == '\n'
                    && buf[pos - 2] == '\r' && buf[pos - 1] == '\n') {
                headersDone = true;
            }
        }
        RemoteLogger.debug("TLS", "parseHeaders: done=" + headersDone + " pos=" + pos);

        String headers = new String(buf, 0, pos, "UTF-8");
        RemoteLogger.debug("TLS", "headers: " + headers.substring(0, Math.min(headers.length(), 120)));
        int statusStart = headers.indexOf(' ');
        if (statusStart > 0) {
            responseCode = Integer.parseInt(
                    headers.substring(statusStart + 1, statusStart + 4));
        }

        String lower = headers.toLowerCase();
        int clIdx = lower.indexOf("content-length:");
        if (clIdx >= 0) {
            int valStart = clIdx + 15;
            while (valStart < lower.length() && lower.charAt(valStart) == ' ') {
                valStart++;
            }
            int valEnd = lower.indexOf('\r', valStart);
            if (valEnd < 0) {
                valEnd = lower.indexOf('\n', valStart);
            }
            if (valEnd > valStart) {
                contentLength = Long.parseLong(lower.substring(valStart, valEnd).trim());
            }
        }
    }
}
