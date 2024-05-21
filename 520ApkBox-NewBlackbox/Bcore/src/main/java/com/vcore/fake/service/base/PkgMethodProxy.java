package com.vcore.fake.service.base;

import java.lang.reflect.Method;

import com.vcore.fake.hook.MethodHook;
import com.vcore.utils.MethodParameterUtils;

public class PkgMethodProxy extends MethodHook {
	final String mName;

	public PkgMethodProxy(String name) {
		this.mName = name;
	}

	@Override
	protected String getMethodName() {
		return mName;
	}

	@Override
	protected Object hook(Object who, Method method, Object[] args) throws Throwable {
		MethodParameterUtils.replaceFirstAppPkg(args);
		return method.invoke(who, args);
	}
}
