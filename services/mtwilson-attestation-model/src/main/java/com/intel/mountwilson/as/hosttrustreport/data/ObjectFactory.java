//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.24 at 02:22:33 PM PST 
//


package com.intel.mountwilson.as.hosttrustreport.data;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.intel.mountwilson.as.hosttrustreport.data package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _HostsTrustReport_QNAME = new QName("", "hosts_trust_report");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.intel.mountwilson.as.hosttrustreport.data
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HostType }
     * 
     */
    public HostType createHostType() {
        return new HostType();
    }

    /**
     * Create an instance of {@link HostsTrustReportType }
     * 
     */
    public HostsTrustReportType createHostsTrustReportType() {
        return new HostsTrustReportType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HostsTrustReportType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "hosts_trust_report")
    public JAXBElement<HostsTrustReportType> createHostsTrustReport(HostsTrustReportType value) {
        return new JAXBElement<HostsTrustReportType>(_HostsTrustReport_QNAME, HostsTrustReportType.class, null, value);
    }

}
