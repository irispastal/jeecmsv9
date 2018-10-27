/**
 * JkcxImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.jeecms.cms.client;

public class JkcxImplServiceLocator extends org.apache.axis.client.Service implements com.jeecms.cms.client.JkcxImplService {

    public JkcxImplServiceLocator() {
    }


    public JkcxImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public JkcxImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for gzfw_jkcx
    private java.lang.String gzfw_jkcx_address = "http://112.50.200.6:8080/ehr/services/gzfw_jkcx.jws";

    public java.lang.String getgzfw_jkcxAddress() {
        return gzfw_jkcx_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String gzfw_jkcxWSDDServiceName = "gzfw_jkcx";

    public java.lang.String getgzfw_jkcxWSDDServiceName() {
        return gzfw_jkcxWSDDServiceName;
    }

    public void setgzfw_jkcxWSDDServiceName(java.lang.String name) {
        gzfw_jkcxWSDDServiceName = name;
    }

    public com.jeecms.cms.client.JkcxImpl getgzfw_jkcx() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(gzfw_jkcx_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getgzfw_jkcx(endpoint);
    }

    public com.jeecms.cms.client.JkcxImpl getgzfw_jkcx(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub _stub = new com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub(portAddress, this);
            _stub.setPortName(getgzfw_jkcxWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setgzfw_jkcxEndpointAddress(java.lang.String address) {
        gzfw_jkcx_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.jeecms.cms.client.JkcxImpl.class.isAssignableFrom(serviceEndpointInterface)) {
                com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub _stub = new com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub(new java.net.URL(gzfw_jkcx_address), this);
                _stub.setPortName(getgzfw_jkcxWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("gzfw_jkcx".equals(inputPortName)) {
            return getgzfw_jkcx();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://112.50.200.6:8080/ehr/services/gzfw_jkcx.jws", "JkcxImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://112.50.200.6:8080/ehr/services/gzfw_jkcx.jws", "gzfw_jkcx"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("gzfw_jkcx".equals(portName)) {
            setgzfw_jkcxEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
