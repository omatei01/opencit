/*
 * Copyright (C) 2011-2012 Intel Corporation
 * All rights reserved.
 */
package security;

import com.intel.mtwilson.ApiClient;
import com.intel.mtwilson.api.*;
import com.intel.mtwilson.KeystoreUtil;
import com.intel.mtwilson.My;
import com.intel.mtwilson.MyConfiguration;
import com.intel.dcsg.cpg.crypto.CryptographyException;
import com.intel.dcsg.cpg.crypto.RsaUtil;
import com.intel.mtwilson.datatypes.ApiClientCreateRequest;
import com.intel.mtwilson.datatypes.ApiClientStatus;
import com.intel.mtwilson.datatypes.ApiClientUpdateRequest;
import com.intel.mtwilson.datatypes.Role;
import com.intel.dcsg.cpg.crypto.RsaCredential;
import com.intel.dcsg.cpg.crypto.RsaCredentialX509;
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
//import org.codehaus.plexus.util.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author jbuhacoff
 */
public class RegisterApiClientTest {
    private static final Logger log = LoggerFactory.getLogger(RegisterApiClientTest.class);
    private static URL baseurl;
    private static String serviceRootPassword;
    
    @BeforeClass
    public static void configure() throws IOException {
        // look for the properties file in our java classpath since this is a test class not production code
        // there should be one properties file for each environment being tested. 
        // DO NOT change the configuration of an existing properties file without coordinating with the team
        String filename = "/mtwilson.properties";
        InputStream in = RegisterApiClientTest.class.getResourceAsStream(filename);
        if( in == null ) {
            throw new FileNotFoundException("Cannot find properties: "+filename);
        }
        Properties config = new Properties();
        config.load(in);
        baseurl = new URL(config.getProperty("mtwilson.api.baseurl"));
    }
    
    @Test
    public void testRegisterNewApiClient() throws ClientException, IOException, ApiException, NoSuchAlgorithmException, GeneralSecurityException, CryptographyException {
        // create a new private key and certificate
        KeyPair keypair = RsaUtil.generateRsaKeyPair(RsaUtil.MINIMUM_RSA_KEY_SIZE);
        X509Certificate certificate = RsaUtil.generateX509Certificate("jonathan"/*CN=jonathan, OU=IASI, O=Intel, L=Folsom, ST=CA, C=US"*/, keypair, 365);
        RsaCredentialX509 credential = new RsaCredentialX509(keypair.getPrivate(), certificate);
        
        // create a new keystore and save the new key into it
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null, null);
        keystore.setKeyEntry("mykey", keypair.getPrivate(), "changeit".toCharArray(), new X509Certificate[] { certificate });
        File tmp = File.createTempFile("keystore", ".jks"); // IOException.  // creates a temporary file
        KeystoreUtil.save(keystore, "changeit", tmp);
        System.out.println("Keystore is in "+tmp.getAbsolutePath());

        // register the new key with Mt Wilson
        Properties p = new Properties();
        p.setProperty("mtwilson.api.ssl.requireTrustedCertificate", "false");
        p.setProperty("mtwilson.api.ssl.verifyHostname", "false");
        ApiClient c = new ApiClient(baseurl, credential, p);
        //ApiClient c = new ApiClient("mtwilson.properties");
        ApiClientCreateRequest me = new ApiClientCreateRequest();
        me.setCertificate(credential.identity());
        me.setRoles(new String[] { Role.Attestation.toString(), Role.Whitelist.toString() });
        c.register(me);
    }
    
    @Test
    public void testRegisterExistingApiClient() throws ClientException, NoSuchAlgorithmException, MalformedURLException, KeyManagementException, KeyStoreException, IOException, CertificateException, UnrecoverableEntryException, GeneralSecurityException, ApiException, CryptographyException {
        File keystoreFile = new File("C:\\Users\\jbuhacoff\\AppData\\Local\\Temp\\keystore5757222749157892628.jks"); // file automatically generated by testRegisterNewApiClinet
        KeyStore keystore = KeystoreUtil.open(new FileInputStream(keystoreFile), "changeit"); // KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, [FileNotFoundException]
        RsaCredentialX509 credential = KeystoreUtil.loadX509(keystore, "mykey", "changeit"); // UnrecoverableEntryException, [CertificateEncodingException]
        // api client
        Properties p = new Properties();
        p.setProperty("mtwilson.api.ssl.requireTrustedCertificate", "false");
        p.setProperty("mtwilson.api.ssl.verifyHostname", "false");
        ApiClient c = new ApiClient(baseurl, credential, p); // KeyManagementException, [MalformedURLException], [UnsupportedEncodingException]
        // duplicate registration request
        ApiClientCreateRequest me = new ApiClientCreateRequest();
        me.setCertificate(credential.getCertificate().getEncoded());
        me.setRoles(new String[] { Role.Attestation.toString(), Role.Whitelist.toString() });
        c.register(me);
    }
    
    @Test
    public void testUpdateApiClient() throws ClientException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, KeyManagementException, ApiException, SignatureException, CryptographyException  {
        // grant access to a given key by using the trusted ip feature on the server to trust all requests from this ip
        File keystoreFile = new File("C:\\Users\\jbuhacoff\\AppData\\Local\\Temp\\keystore5757222749157892628.jks"); // file automatically generated by testRegisterNewApiClinet
        KeyStore keystore = KeystoreUtil.open(new FileInputStream(keystoreFile), "changeit"); // KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, [FileNotFoundException]
        RsaCredential credential = KeystoreUtil.loadX509(keystore, "mykey", "changeit"); // UnrecoverableEntryException, [CertificateEncodingException]
        ApiClientUpdateRequest update = new ApiClientUpdateRequest();
        update.fingerprint = credential.identity();
        update.enabled = true;
        update.roles = new String[] { Role.Attestation.toString(), Role.Whitelist.toString() };
        update.status = ApiClientStatus.APPROVED.toString();
        update.comment = "Bootstrap approval sample code in JavaIntegrationTests project";
        Properties p = new Properties();
        p.setProperty("mtwilson.api.ssl.requireTrustedCertificate", "false");
        p.setProperty("mtwilson.api.ssl.verifyHostname", "false");
        ApiClient c = new ApiClient(baseurl, credential, p); // KeyManagementException, [MalformedURLException], [UnsupportedEncodingException]
        c.updateApiClient(update); // ApiException, SignatureException
    }
    
    
    
    
    
    @Test
    public void testApproveMyApiClient() throws ClientException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, KeyManagementException, ApiException, SignatureException, CryptographyException  {
        // grant access to a given key by using the trusted ip feature on the server to trust all requests from this ip
        KeyStore keystore = KeystoreUtil.open(new FileInputStream(My.configuration().getKeystoreFile()), "changeit"); // KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, [FileNotFoundException]
        RsaCredential credential = KeystoreUtil.loadX509(keystore, My.configuration().getKeystoreUsername(), My.configuration().getKeystorePassword()); // UnrecoverableEntryException, [CertificateEncodingException]
        ApiClientUpdateRequest update = new ApiClientUpdateRequest();
        update.fingerprint = credential.identity();
        update.enabled = true;
        update.roles = new String[] { Role.Attestation.toString(), Role.Whitelist.toString() };
        update.status = ApiClientStatus.APPROVED.toString();
        update.comment = "Bootstrap approval sample code in JavaIntegrationTests project";
        Properties p = new Properties();
        p.setProperty("mtwilson.api.ssl.requireTrustedCertificate", "false");
        p.setProperty("mtwilson.api.ssl.verifyHostname", "false");
        ApiClient c = new ApiClient(baseurl, credential, p); // KeyManagementException, [MalformedURLException], [UnsupportedEncodingException]
        c.updateApiClient(update); // ApiException, SignatureException
    }
    
    
    /**
     * Executes a remote command with no timeout
     * 
     * @param ssh
     * @param command
     * @return
     * @throws ConnectionException
     * @throws TransportException
     * @throws IOException 
     */
    private String remote(SSHClient ssh, String command) throws ConnectionException, TransportException, IOException {
        return remote(ssh, command, null);
    }
    
    /**
     * The sshj client is designed to permit one command per "session". But
     * you can start multiple sessions per connection so this is ok.
     * 
     * @param ssh the ssh client
     * @param command string to execute on the remote shell
     * @param timeoutSeconds or null to wait indefinitely for the command to complete
     * @return
     * @throws ConnectionException
     * @throws TransportException
     * @throws IOException 
     */
    private String remote(SSHClient ssh, String command, Integer timeoutSeconds) throws ConnectionException, TransportException, IOException {
        Session session = ssh.startSession();
        try {
            Command cmd = session.exec(command); // ConnectionException, TransportException
            if( timeoutSeconds == null ) {
                cmd.join();
            }
            else {
                cmd.join(timeoutSeconds, TimeUnit.SECONDS);  // the parameters are the timeout. if you want to wait indefinitely call join()
            }
            log.debug("Command exit status: {}", cmd.getExitStatus());
            String output = IOUtils.toString(cmd.getInputStream()); // IOException
            return output;
        }
        finally {
            session.close();
        }
    }
    
    private void bootstrapFirstApiClient(SSHClient ssh) throws ConnectionException, TransportException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, KeyManagementException, ApiException, SignatureException, ClientException, CryptographyException {
            // find out what is the previous list of trusted hosts
            String previousWhitelistString = remote(ssh, "asctl show mtwilson.api.trust");
            String[] previousWhitelist = previousWhitelistString.trim().split(","); // trim is required to remove the newline at the end of the output
            log.debug("Previous trusted clients network address list: {}", previousWhitelistString);
            // get local ip address and add it to the list
            InetAddress addr = InetAddress.getLocalHost();
            String[] updatedWhitelist = (String[]) ArrayUtils.add(previousWhitelist, addr.getHostAddress());
            // Removing the dependency on  codehaus.plexus
            // String updatedWhitelistString = StringUtils.join(updatedWhitelist, ",");
            String updatedWhitelistString = "";
            for (String temp : updatedWhitelist) {
                updatedWhitelistString = updatedWhitelistString + temp + ",";
            }
            // need to remove the last occurance of the ","
            updatedWhitelistString.substring(0, (updatedWhitelistString.lastIndexOf(",")-1));
            log.debug("Updated trusted clients network address list: {}", updatedWhitelistString);
            // set the new list on the server and restart the application
            remote(ssh, String.format("asctl edit mtwilson.api.trust \"%s\"", updatedWhitelistString));
            remote(ssh, "asctl restart");
            // grant privileges to ourselves
            testUpdateApiClient();
            log.info("Granted privileges to self");
            // now restore the original trusted hosts whitelist
            remote(ssh, String.format("asctl edit mtwilson.api.trust \"%s\"", previousWhitelistString));        
            log.info("Restored previous trusted clients network address list");
    }
    
    /**
     * This should not be a @Test method because it requires the root password
     * of the server and we should not store that. So invoke it by running this
     * class via the main() method which will prompt for the root password.
     * 
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableEntryException
     * @throws KeyManagementException
     * @throws ApiException
     * @throws SignatureException 
     */
    private void testAuthorizeApiClientFromTrustedHostViaSsh() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, KeyManagementException, ApiException, SignatureException, ConnectionException, TransportException, TransportException, ClientException, CryptographyException {
        SSHClient ssh = new SSHClient();
        //ssh.loadKnownHosts(); // this is only if we have a known_hosts file...
        //ssh.addHostKeyVerifier("..."); // this is only if we know the fingerprint of the remote host we're connecting to
        ssh.addHostKeyVerifier(new HostKeyVerifier() {@Override public boolean verify(String arg0, int arg1, PublicKey arg2) { return true; } }); // this accepts all remote public keys
        ssh.connect("10.1.71.81");
        try {
            ssh.authPassword("root", serviceRootPassword);
            bootstrapFirstApiClient(ssh);
        }
        finally {
            ssh.disconnect();
        }
    }
    
    public static void main(String[] args) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, KeyManagementException, ApiException, SignatureException, ConnectionException, TransportException, TransportException, ClientException, CryptographyException {
        configure();
        System.out.println("URL: "+baseurl.toExternalForm());
        System.out.print("Root password: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        serviceRootPassword = in.readLine().trim();
        RegisterApiClientTest test = new RegisterApiClientTest();
        test.testAuthorizeApiClientFromTrustedHostViaSsh();
    }
}
