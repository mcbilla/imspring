package com.mcb.imspring.aop.advisor;

public class TargetSource {

	private Class<?> targetClass;

    private Class<?>[] interfaces;

	private Object target;

	private Object proxy;

	public TargetSource(Object target, Object proxy, Class<?> targetClass, Class<?>... interfaces) {
		this.target = target;
		this.proxy = proxy;
		this.targetClass = targetClass;
        this.interfaces = interfaces;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Object getTarget() {
		return target;
	}

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

	public Object getProxy() {
		return proxy;
	}
}
