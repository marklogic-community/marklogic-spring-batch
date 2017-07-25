package com.marklogic.client.helper;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;

import java.util.ArrayList;
import java.util.List;

public class DatabaseClientConfig {

    private List<String> hosts;
    private int port;
    private String username;
    private String password;
    private String database;
    private DatabaseClientFactory.SecurityContext securityContext;
    private SSLContext sslContext;
    private SSLHostnameVerifier sslHostnameVerifier;

    public DatabaseClientConfig() {

    }

    public DatabaseClientConfig(String host, int port, String username, String password) {
        setHost(host);
        this.port = port;
        this.username = username;
        this.password = password;
        this.securityContext = new DatabaseClientFactory.DigestAuthContext(username, password);
    }

    public DatabaseClientConfig(List<String> hosts, int port, String username, String password) {
        this.hosts = hosts;
        this.port = port;
        this.username = username;
        this.password = password;
        this.securityContext = new DatabaseClientFactory.DigestAuthContext(username, password);
    }

    @Override
    public String toString() {
        return String.format("[%s@%s:%d]", username, hosts, port, username);
    }

    public String getHost() {
        return hosts.get(0);
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public DatabaseClientFactory.SecurityContext getSecurityContext() {
        return securityContext;
    }

    public void setHost(String host) {
        if (hosts == null) {
            hosts = new ArrayList<String>();
        }
        hosts.add(host);
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public SSLHostnameVerifier getSslHostnameVerifier() {
        return sslHostnameVerifier;
    }

    public void setSslHostnameVerifier(SSLHostnameVerifier sslHostnameVerifier) {
        this.sslHostnameVerifier = sslHostnameVerifier;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }


    public void setSecurityContext(DatabaseClientFactory.SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

}
