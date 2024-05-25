# Forras admin project

This is a health care administration software. You can manage client informations, events, and make statistics about events.

## Requirements

- Application server : Wildfly 9.0.2

- Database server: PostgreSQL 9

## Technologies

- JavaEE 7

- JSF: Primefaces 7.0

## Youtube video for usage

Administration basics: [Youtube video](https://youtu.be/iz7qNyf2sac)

Statistics: [Youtube video](https://youtu.be/dz2aLtvqGYg)

(In the video all data are fictive, generated randomly.)

## Installation

Download Wildfly 9.0.2.Final from [download.jboss.org](https://download.jboss.org/wildfly/9.0.2.Final/wildfly-9.0.2.Final.tar.gz)

Unzip tar.gz

tar -xvzf wildfly-9.0.2.Final.tar.gz

Download PostgreSQL JDBC driver from [jdbc.postgresql.org](https://jdbc.postgresql.org/download/postgresql-42.5.0.jar)

Make subfolder wildfly-9.0.2.Final/modules/system/layers/base/org/postgresql/main

Copy JDBC driver .jar file to this folder.
Create module.xml to this folder with following content:

```
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.3" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.5.0.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
        <module name="javax.servlet.api" optional="true"/>
    </dependencies>
</module>

```
Take care about urn:jboss:module:1.3 version number!

Add JDBC driver and datasource pool to standalone.xml

```
        <subsystem xmlns="urn:jboss:domain:datasources:3.0">
            <datasources>
            ...
                <datasource jndi-name="java:jboss/datasources/forrasDS" pool-name="forras-admin" enabled="true" use-java-context="true">
                    <connection-url>jdbc:postgresql://localhost:5432/forrashaz?charSet=UTF8</connection-url>
                    <driver>org.postgresql</driver>
                    <security>
                        <user-name>USERNAME</user-name>
                        <password>PASSWORD</password>
                    </security>
                </datasource>
                <datasource jta="false" jndi-name="java:jboss/datasources/forrasDSNoTX" pool-name="forras-admin-notx" enabled="true" use-java-context="true">
                    <connection-url>jdbc:postgresql://localhost:5432/forrashaz?charSet=UTF8</connection-url>
                    <driver>org.postgresql</driver>
                    <security>
                        <user-name>USERNAME</user-name>
                        <password>PASSWORD</password>
                    </security>
                </datasource>
                ...
                <drivers>
                ...
                    <driver name="org.postgresql" module="org.postgresql">
                        <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
                    </driver>
                ...
                </drivers>
            ...
            </datasources>
```

Set service variables with adding domain:naming bindings:

```
        <subsystem xmlns="urn:jboss:domain:naming:2.0">
        ...
            <bindings>
                <simple name="java:jboss/forrashaz/default-page" value="/user/lessons.xhtml" type="java.lang.String"/>
                <simple name="java:jboss/forrashaz/templates" value="/home/pzoli/Dokumentumok/forras/templates" type="java.lang.String"/>
                <simple name="java:jboss/forrashaz/destination" value="/home/pzoli/Dokumentumok/forras" type="java.lang.String"/>
                <simple name="java:jboss/forrashaz/systemtimerstarthour" value="8" type="java.lang.Integer"/>
                <simple name="java:jboss/forrashaz/default-language" value="hu" type="java.lang.String"/>
                <simple name="java:jboss/forrashaz/default-alertmanager-startdate" value="2015.01.01" type="java.lang.String"/>
            </bindings>
            ...
            <remote-naming/>
        ...
        </subsystem>

```

Where values are the following:
* default-page: open page after login
* templates: formatted .xlsx files for statistics
* destination: directory for outputs (statistics, QR codes)
* systemtimerstarthour: start hour to generate alerts (new message generation)
* default-language: available default language (**hu** and **en** are valid)
* default-alertmanager-startdate: alert calculation start date (YYYY.MM.DD format)

Add security for authentication realm:

```
        <subsystem xmlns="urn:jboss:domain:security:1.2">
        ...
            <security-domains>
            ...
                <security-domain name="forrashazRealm">
                    <authentication>
                        <login-module code="Database" flag="required">
                            <module-option name="dsJndiName" value="java:jboss/datasources/forrasDS"/>
                            <module-option name="principalsQuery" value="select password from v_active_user where username=?"/>
                            <module-option name="rolesQuery" value="select group_name as userRoles,'Roles' from user_join_group where user_name=?"/>
                            <module-option name="unauthenticatedIdentity" value="anonymousUser"/>
                            <module-option name="hashAlgorithm" value="SHA-256"/>
                            <module-option name="hashEncoding" value="base64"/>
                        </login-module>
                    </authentication>
                </security-domain>
            ...
            <security-domains>
            ...
        </subsystem>
```

After Wildfly setup start wildfly service.

Clone git repository:
```
git clone https://github.com/pzoli/forras-admin.git
```
Make release with maven.
```
cd forras-admin
mvn package
```
copy ./target/forras-admin.war to wildfly deployment folder (wildfly-9.0.2.Final/standalone/deployments)

After deploy all database tables created. Create active users view and import defaults into the database with doc/viewndefaults.sql file. Then run doc/quartz-tables_postgres.sql for regenerate quartz tables.
Restore AccessibleType table by importing doc/accessibletype.tar to PostgreSQL.

## Connected projects
[forras-datagenerator](https://github.com/pzoli/forras-datagenerator)
[ForrasAdminRFIDReaderConfigurator](https://github.com/pzoli/ForrasAdminRFIDReaderConfigurator)
[ForrasAdminRFIDReader](https://github.com/pzoli/ForrasAdminRFIDReader)
[NFCReaderSetupSwing](https://github.com/pzoli/NFCReaderSetupSwing)
