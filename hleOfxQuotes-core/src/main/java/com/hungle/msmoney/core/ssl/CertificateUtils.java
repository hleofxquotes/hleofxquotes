package com.hungle.msmoney.core.ssl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import javax.security.cert.CertificateEncodingException;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class CertificateUtils.
 */
public class CertificateUtils {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(CertificateUtils.class);

    /**
     * Convert certificate.
     *
     * @param cert the cert
     * @return the java.security.cert. X 509 certificate
     * @throws CertificateEncodingException the certificate encoding exception
     * @throws CertificateException the certificate exception
     */
    /*
     * Convert javax.security.cert.X509Certificate to java.security.cert.X509Certificate
     */
    public static java.security.cert.X509Certificate convertCertificate(javax.security.cert.X509Certificate cert) throws CertificateEncodingException,
            CertificateException {
        byte[] encoded = cert.getEncoded();
        ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
        java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
        return (java.security.cert.X509Certificate) cf.generateCertificate(bis);
    }

    /**
     * Gets the cn.
     *
     * @param name the name
     * @return the cn
     */
    static Object getCN(String name) {
        Object cn = null;
        try {
            LdapName ldapDN = new LdapName(name);
            for (Rdn rdn : ldapDN.getRdns()) {
                String type = rdn.getType();
                Object value = rdn.getValue();
                if (log.isDebugEnabled()) {
                    log.debug("    " + type + "=" + value);
                }
                if (type.compareTo("CN") == 0) {
                    cn = value;
                }
            }
        } catch (InvalidNameException e) {
            log.warn(e);
        }
        return cn;
    }

    /**
     * Write certs.
     *
     * @param certs the certs
     * @param outFile the out file
     * @throws CertificateEncodingException the certificate encoding exception
     */
    static void writeCerts(X509Certificate[] certs, File outFile) throws java.security.cert.CertificateEncodingException {
        log.info("writeCurrentCertificates=" + outFile.getAbsolutePath());
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
    
            int counter = 0;
            for (X509Certificate cert : certs) {
                X500Principal x500Principal = cert.getSubjectX500Principal();
                String name = x500Principal.getName();
                if (log.isDebugEnabled()) {
                    log.debug(name);
                }
                counter++;
                
                writer.println("# Certificate: " + counter);
                writer.println("Issued To");
                Object cn = getCN(name);
                writer.println("    Common Name (CN): " + cn);
                writer.println("    Serial Number: " + Utils.encodeHexString(cert.getSerialNumber().toByteArray()));
    
                writer.println("Issued By");
                X500Principal issuerX500Principal = cert.getIssuerX500Principal();
                writer.println("    Common Name (CN): " + getCN(issuerX500Principal.getName()));
    
                writer.println("Validity");
                writer.println("    Issued On: " + cert.getNotBefore());
                writer.println("    Expires On: " + cert.getNotAfter());
    
                writer.println("Fingerprints");
                try {
                    MessageDigest md = null;
    
                    md = MessageDigest.getInstance("SHA1");
                    md.update(cert.getEncoded());
                    writer.println("    SHA1 Fingerprint: " + Utils.encodeHexString(md));
    
                    md = MessageDigest.getInstance("MD5");
                    md.update(cert.getEncoded());
                    writer.println("    MD5 Fingerprint: " + Utils.encodeHexString(md));
                } catch (NoSuchAlgorithmException e) {
                    log.warn(e);
                }
    
                byte[] signature = cert.getSignature();
                if (log.isDebugEnabled()) {
                    log.debug("    signatureAlgorithm=" + cert.getSigAlgName());
                    log.debug("    signature=" + Utils.encodeHexString(signature));
                }
                writer.println("Certificate Signature");
                try {
                    writer.println("    signatureAlgorithm: " + cert.getSigAlgName());
                    
                    MessageDigest md = null;
    
                    md = MessageDigest.getInstance("SHA1");
                    md.update(signature);
                    writer.println("    SHA1: " + Utils.encodeHexString(md));
    
                    md = MessageDigest.getInstance("MD5");
                    md.update(signature);
                    writer.println("    MD5: " + Utils.encodeHexString(md));
                } catch (NoSuchAlgorithmException e) {
                    log.warn(e);
                }
    
                writer.println("");
            }
    
        } catch (IOException e) {
            log.warn(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
