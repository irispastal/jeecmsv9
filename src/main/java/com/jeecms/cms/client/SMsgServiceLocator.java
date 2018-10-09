/**
 * SMsgServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.jeecms.cms.client;

public class SMsgServiceLocator extends org.apache.axis.client.Service implements SMsgService {

    public SMsgServiceLocator() {
    }


    public SMsgServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SMsgServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SMsg
    private String SMsg_address = "http://10.129.255.253:58080/axis/services/SMsg";

    public String getSMsgAddress() {
        return SMsg_address;
    }

    // The WSDD service name defaults to the port name.
    private String SMsgWSDDServiceName = "SMsg";

    public String getSMsgWSDDServiceName() {
        return SMsgWSDDServiceName;
    }

    public void setSMsgWSDDServiceName(String name) {
        SMsgWSDDServiceName = name;
    }

    public SMsg_PortType getSMsg() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SMsg_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSMsg(endpoint);
    }

    public SMsg_PortType getSMsg(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SMsgSoapBindingStub _stub = new SMsgSoapBindingStub(portAddress, this);
            _stub.setPortName(getSMsgWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSMsgEndpointAddress(String address) {
        SMsg_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (SMsg_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                SMsgSoapBindingStub _stub = new SMsgSoapBindingStub(new java.net.URL(SMsg_address), this);
                _stub.setPortName(getSMsgWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
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
        String inputPortName = portName.getLocalPart();
        if ("SMsg".equals(inputPortName)) {
            return getSMsg();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://10.129.255.253:58080/axis/services/SMsg", "SMsgService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://10.129.255.253:58080/axis/services/SMsg", "SMsg"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("SMsg".equals(portName)) {
            setSMsgEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
