/**
 * SMsg_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.jeecms.cms.client;

public interface SMsg_PortType extends java.rmi.Remote {
    public void main(String[] args) throws java.rmi.RemoteException;
    public Object invoke(String shell) throws java.rmi.RemoteException;
    public int init(String dbIp, String dbName, String dbPort, String user, String pwd) throws java.rmi.RemoteException;
    public int release() throws java.rmi.RemoteException;
    public boolean flushHost() throws java.rmi.RemoteException;
    public int sendSM(String apiCode, String loginName, String loginPwd, String[] mobiles, String content, long smID) throws java.rmi.RemoteException;
    public int sendSM(String apiCode, String loginName, String loginPwd, String[] mobiles, String content, long smID, long srcID) throws java.rmi.RemoteException;
    public int sendSM(String apiCode, String loginName, String loginPwd, String[] mobiles, String content, long smID, String url) throws java.rmi.RemoteException;
    public int sendSM(String apiCode, String loginName, String loginPwd, String[] mobiles, String content, long smID, long srcID, String url) throws java.rmi.RemoteException;
    public int sendSM(String apiCode, String loginName, String loginPwd, String[] mobiles, String content, String sendTime, long smID, long srcID) throws java.rmi.RemoteException;
    public int sendSM(String apiCode, String loginName, String loginPwd, String[] mobiles, String content, long smID, long srcID, String url, String sendTime) throws java.rmi.RemoteException;
    public String recvRPT(String apiCode, String loginName, String loginPwd) throws java.rmi.RemoteException;
    public int sendPDU(String apiCode, String loginName, String loginPwd, String[] mobiles, byte[] content, long smID, int msgFmt, int tpPID, int tpUdhi, String feeTerminalID, String feeType, String feeCode, int feeUserType) throws java.rmi.RemoteException;
    public int sendPDU(String apiCode, String loginName, String loginPwd, String[] mobiles, byte[] content, long smID, long srcID, int msgFmt, int tpPID, int tpUdhi, String feeTerminalID, String feeType, String feeCode, int feeUserType) throws java.rmi.RemoteException;
    public String recvMo(String apiCode, String loginName, String loginPwd) throws java.rmi.RemoteException;
    public boolean checkTime(String sendTime) throws java.rmi.RemoteException;
}
