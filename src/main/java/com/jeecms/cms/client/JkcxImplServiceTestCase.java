///**
// * JkcxImplServiceTestCase.java
// *
// * This file was auto-generated from WSDL
// * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
// */
//
//package com.jeecms.cms.client;
//
//import com.jeecms.cms.util.XMLUtil;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.DocumentHelper;
//
//import javax.xml.rpc.ServiceException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class JkcxImplServiceTestCase extends junit.framework.TestCase {
//    public JkcxImplServiceTestCase(java.lang.String name) {
//        super(name);
//    }
//
//    public void testgzfw_jkcxWSDL() throws Exception {
//        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
//        java.net.URL url = new java.net.URL(new com.jeecms.cms.client.JkcxImplServiceLocator().getgzfw_jkcxAddress() + "?WSDL");
//        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.jeecms.cms.client.JkcxImplServiceLocator().getServiceName());
//        assertTrue(service != null);
//    }
//
//    public void test1gzfw_jkcxJkcx_Jkhs() throws Exception {
//        com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub binding;
//        try {
//            binding = (com.jeecms.cms.client.Gzfw_jkcxSoapBindingStub)
//                          new com.jeecms.cms.client.JkcxImplServiceLocator().getgzfw_jkcx();
//        }
//        catch (javax.xml.rpc.ServiceException jre) {
//            if(jre.getLinkedCause()!=null)
//                jre.getLinkedCause().printStackTrace();
//            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
//        }
//        assertNotNull("binding is null", binding);
//
//        // Time out after a minute
//        binding.setTimeout(60000);
//
//        // Test operation
//        java.lang.String value = null;
//        java.lang.String ywgndm = "CXJKDAXX";
//        java.lang.String ywxml =
//                        "<YWXML>\n" +
//                        "    <DLSJ>\n" +
//                        "        <DLDM>mhwz</DLDM>\n" +
//                        "        <DLMM>mhwz</DLMM>\n" +
//                        "    </DLSJ>\n" +
//                        "    <YWSJ>\n" +
//                        "        <ZJHM>$</ZJHM>\n" +
//                        "        <ZJLX>01</ZJLX>\n" +
//                        "    </YWSJ>\n" +
//                        "</YWXML>";
//        value = binding.jkcx_Jkhs(ywgndm, ywxml);
//        System.out.println(value);
//        // TBD - validate results
//    }
//
//    public void testxml() {
//        String xml ="<YWXML><STATUS>T</STATUS><MSG>查询成功</MSG><YWSJ><DA_GR_JBXX><YLJGDM>12352231490665848X</YLJGDM><GRJBXXBSH>350926205204000050919161121972</GRJBXXBSH><KLXDM></KLXDM><KLXMC></KLXMC><KH></KH><ZJLXDM></ZJLXDM><ZJLXMC></ZJLXMC><ZJHM>500223198309280021</ZJHM><DABH></DABH><JDJGDM></JDJGDM><JDJGMC></JDJGMC><JDJGLXDH></JDJGLXDH><JDYSGH></JDYSGH><JDYSXM></JDYSXM><JDRQ></JDRQ><JKKFKJGDM></JKKFKJGDM><JKKFKJGMC></JKKFKJGMC><DAGLJGDM></DAGLJGDM><DAGLJGMC></DAGLJGMC><ZRYSGH></ZRYSGH><ZRYSXM></ZRYSXM><ZRYSLXDH></ZRYSLXDH><XM>李小霞</XM><XBMC></XBMC><CSRQ>1983-09-28</CSRQ><GZDWMC></GZDWMC><GZDWLXDH></GZDWLXDH><BRDHHM>13400663693</BRDHHM><DZYJDZ></DZYJDZ><LXRGXMC></LXRGXMC><LXRXM></LXRXM><LXRDHHM>13400663693</LXRDHHM><CZDZHJBZ></CZDZHJBZ><CZRKBZ></CZRKBZ><GJMC></GJMC><MZMC></MZMC><ABOXXMC></ABOXXMC><RHXXMC></RHXXMC><WHCDMC></WHCDMC><ZYMC></ZYMC><HYZKMC></HYZKMC><YLFYZFFSMC></YLFYZFFSMC><YLFYZFFSQT></YLFYZFFSQT><MXBHBQKDM></MXBHBQKDM><YWGMSBZ></YWGMSBZ><YWGMMC></YWGMMC><YWGMQT></YWGMQT><BLSMC></BLSMC><BLSQT></BLSQT><SSSBZ></SSSBZ><FQBSMC></FQBSMC><FQBSQT></FQBSQT><MQBSMC></MQBSMC><MQBSQT></MQBSQT><XDJMBSMC></XDJMBSMC><XDJMBSQT></XDJMBSQT><ZNBSMC></ZNBSMC><ZNBSQT></ZNBSQT><YCBSBZ></YCBSBZ><YCJBMC></YCJBMC><CJBZ></CJBZ><CJMC></CJMC><CJQT></CJQT><CFPFSSBZ></CFPFSSBZ><CFPFSSLBMC></CFPFSSLBMC><RLLXMC></RLLXMC><YSLBMC></YSLBMC><CSLBMC></CSLBMC><QCLLBMC></QCLLBMC><JZDXXDZ></JZDXXDZ><JZDXZQHDM></JZDXZQHDM><JZDDZBM></JZDDZBM><JZDSSBM></JZDSSBM><JZDSSMC></JZDSSMC><JZDDSBM></JZDDSBM><JZDDSMC></JZDDSMC><JZDQXBM></JZDQXBM><JZDQXMC></JZDQXMC><JZDJDBM></JZDJDBM><JZDJDMC></JZDJDMC><JZDCBM></JZDCBM><JZDCMC></JZDCMC><JZDMPHM></JZDMPHM><JZDYZBM></JZDYZBM><JJQKLXR></JJQKLXR><JJQKLXRDH></JJQKLXRDH><DAHGBZ></DAHGBZ><DAWSBZ></DAWSBZ><SWBZ></SWBZ><SWRQ></SWRQ><SWYY></SWYY></DA_GR_JBXX></YWSJ></YWXML>";
//
//        Document doc = null;
//        try {
//            doc = DocumentHelper.parseText(xml);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        Map<String, Object> map = XMLUtil.Dom2Map(doc);
//        System.out.println(map.get("YWSJ"));
//        HashMap<String, Object> map1 = (HashMap<String, Object>) map.get("YWSJ");
//        HashMap<String, Object> map2 = (HashMap<String, Object>) map1.get("DA_GR_JBXX");
//        System.out.println(map2.get("BRDHHM"));
//    }
//
//    public void testjkcx() {
//        JkcxImpl client = null;
//        java.lang.String ywgndm = "CXJKDAXX";
//        java.lang.String ywxml =
//                "<YWXML>\n" +
//                        "    <DLSJ>\n" +
//                        "        <DLDM>mhwz</DLDM>\n" +
//                        "        <DLMM>mhwz</DLMM>\n" +
//                        "    </DLSJ>\n" +
//                        "    <YWSJ>\n" +
//                        "        <ZJHM>$</ZJHM>\n" +
//                        "        <ZJLX>01</ZJLX>\n" +
//                        "    </YWSJ>\n" +
//                        "</YWXML>";
//        try {
//            JkcxImplServiceLocator locator = new JkcxImplServiceLocator();
//            client = locator.getgzfw_jkcx();
//            String value = client.jkcx_Jkhs(ywgndm, ywxml);
//            System.out.println(value);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
