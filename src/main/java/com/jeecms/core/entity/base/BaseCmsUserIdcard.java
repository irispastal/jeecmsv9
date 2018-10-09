package com.jeecms.core.entity.base;

import com.jeecms.core.entity.CmsUser;

import java.io.Serializable;
import java.util.Objects;

/**
 *  table="jc_user_idcard"
 */
public abstract class BaseCmsUserIdcard implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String REF = "CmsUserIdcard";
    public static String PROP_MOBILE = "mobile";
    public static String PROP_ADDRESS = "address";
    public static String PROP_USER = "user";
    public static String PROP_REALNAME = "realname";
    public static String PROP_IDCARD = "idcard";
    public static String PROP_ID = "id";

    Integer id;

    String idcard;

    String realname;

    String mobile;

    String address;

    // many to one
    CmsUser user;

    public BaseCmsUserIdcard() {
        initialize();
    }

    public BaseCmsUserIdcard(Integer id) {
        this.id = id;
        initialize();
    }

    protected void initialize () {}

    public Integer getId() {
        return id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CmsUser getUser() {
        return user;
    }

    public void setUser(CmsUser user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseCmsUserIdcard)) return false;
        BaseCmsUserIdcard that = (BaseCmsUserIdcard) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getIdcard(), that.getIdcard()) &&
                Objects.equals(getRealname(), that.getRealname()) &&
                Objects.equals(getMobile(), that.getMobile()) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getIdcard(), getRealname(), getMobile(), address);
    }

    @Override
    public String toString() {
        return "BaseCmsUserIdcard{" +
                "id=" + id +
                ", idcard='" + idcard + '\'' +
                ", realname='" + realname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
