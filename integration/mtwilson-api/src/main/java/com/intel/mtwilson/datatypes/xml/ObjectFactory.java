//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.13 at 09:38:08 AM PDT 
//


package com.intel.mtwilson.datatypes.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.intel.mtwilson.datatypes.xml package. 
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

    private final static QName _Hosts_QNAME = new QName("", "Hosts");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.intel.mtwilson.datatypes.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HostTrustXmlResponseList }
     * 
     */
    public HostTrustXmlResponseList createHostTrustXmlResponseList() {
        return new HostTrustXmlResponseList();
    }

    /**
     * Create an instance of {@link HostTrustXmlResponse }
     * 
     */
    public HostTrustXmlResponse createHostTrustXmlResponse() {
        return new HostTrustXmlResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HostTrustXmlResponseList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Hosts")
    public JAXBElement<HostTrustXmlResponseList> createHosts(HostTrustXmlResponseList value) {
        return new JAXBElement<HostTrustXmlResponseList>(_Hosts_QNAME, HostTrustXmlResponseList.class, null, value);
    }

}
