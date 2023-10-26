package net.runelite.pluginhub.packager;

import java.io.FileWriter;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
//import org.bouncycastle.x509.X509V3CertificateGenerator;
//import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
public class KeyGen {

    private static final String COMMON_NAME = "Your Name";
    private static final String ORGANIZATION = "Your Organization";
    private static final String ORGANIZATIONAL_UNIT = "Your Organizational Unit";
    private static final String LOCALITY = "Your City";
    private static final String STATE_OR_PROVINCE_NAME = "Your State";
    private static final String COUNTRY_NAME = "Your Country";
    public static void main(String[] args) throws Exception
    {
//        Security.addProvider(new BouncyCastleProvider());
//
//        // Generate RSA key pair
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
//        keyPairGenerator.initialize(2048);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//
//        // Generate self-signed X.509 certificate
//        String caInfo = "CN="+COMMON_NAME+", O="+ORGANIZATION+", OU="+ORGANIZATIONAL_UNIT+", L="+LOCALITY+", ST="+STATE_OR_PROVINCE_NAME+", C="+COUNTRY_NAME;
//
//        X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();
//        certGenerator.setSerialNumber(java.math.BigInteger.valueOf(System.currentTimeMillis()));
//        certGenerator.setIssuerDN(new javax.security.auth.x500.X500Principal(caInfo));
//        certGenerator.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24)); // 1 day ago
//        certGenerator.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365))); // 1 year from now
//        certGenerator.setSubjectDN(new javax.security.auth.x500.X500Principal(caInfo));
//        certGenerator.setPublicKey(publicKey);
//        certGenerator.setSignatureAlgorithm("SHA256WithRSAEncryption");
//
//        X509Certificate certificate = certGenerator.generate(privateKey);
//        // Save keys and certificate to files
//        FileWriter publicKeyFile = new FileWriter("publicKey.asc");
//        JcaPEMWriter publicKeyWriter = new JcaPEMWriter(publicKeyFile);
//        publicKeyWriter.writeObject(publicKey);
//        publicKeyWriter.close();
//
//        FileWriter privateKeyFile = new FileWriter("privateKey.asc");
//        JcaPEMWriter privateKeyWriter = new JcaPEMWriter(privateKeyFile);
//        privateKeyWriter.writeObject(new JcaPKCS8Generator(privateKey, null));
//        privateKeyWriter.close();
//
//        FileWriter certificateFile = new FileWriter("externalplugins.crt");
//        JcaPEMWriter certificateWriter = new JcaPEMWriter(certificateFile);
//        certificateWriter.writeObject(certificate);
//        certificateWriter.close();


        System.out.println("RSA key pair and self-signed certificate generated successfully.");
    }
}