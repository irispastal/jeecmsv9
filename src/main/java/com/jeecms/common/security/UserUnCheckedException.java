package com.jeecms.common.security;

/**
 * 用户未审核异常
 * @Description:TODO
 * @author: ztx
 * @date:   2018年5月12日 下午2:51:29     
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
 * Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@SuppressWarnings("serial")
public class UserUnCheckedException extends AccountStatusException{
	
	public UserUnCheckedException() {
	}

	public UserUnCheckedException(String msg) {
		super(msg);
	}

	public UserUnCheckedException(String msg, Object extraInformation) {
		super(msg, extraInformation);
	}
	
}
