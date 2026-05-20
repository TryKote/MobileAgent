import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.Base64;

public class JadSign {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Usage: JadSign <keystore.jks> <password> <alias> <jarfile> <jadfile>");
            System.exit(1);
        }
        String ksFile = args[0], ksPass = args[1], alias = args[2], jarFile = args[3], jadFile = args[4];

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(ksFile), ksPass.toCharArray());

        PrivateKey key = (PrivateKey) ks.getKey(alias, ksPass.toCharArray());
        java.security.cert.Certificate cert = ks.getCertificate(alias);

        // Sign JAR with SHA1withRSA
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        FileInputStream jarIn = new FileInputStream(jarFile);
        byte[] buf = new byte[8192];
        int n;
        while ((n = jarIn.read(buf)) > 0) sig.update(buf, 0, n);
        jarIn.close();
        String sigB64 = Base64.getEncoder().encodeToString(sig.sign());

        // Cert to base64
        String certB64 = Base64.getEncoder().encodeToString(cert.getEncoded());

        // Read existing JAD, add/replace cert and sig lines
        StringBuilder jad = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(jadFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("MIDlet-Certificate-") && !line.startsWith("MIDlet-Jar-RSA-SHA1"))
                jad.append(line).append("\n");
        }
        reader.close();

        jad.append("MIDlet-Certificate-1-1: ").append(certB64).append("\n");
        jad.append("MIDlet-Jar-RSA-SHA1: ").append(sigB64).append("\n");

        FileWriter writer = new FileWriter(jadFile);
        writer.write(jad.toString());
        writer.close();

        System.out.println("Signed. Sig=" + sigB64.length() + " chars, Cert=" + certB64.length() + " chars");
    }
}
