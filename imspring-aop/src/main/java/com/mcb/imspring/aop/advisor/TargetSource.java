package com.mcb.imspring.aop.advisor;

/**
 * TargetSource 是 AOP 直接代理的对象，被 MethodInvocation 直接调用。
 * TargetSource 内部持有原始对象 target，MethodInvocation 在调用 TargetSource 的时候会通过 method.invode(target,args) 间接调用原始对象 target 的方法
 * 为什么 SpringAOP 代理不直接代理 target，而需要通过代理 TargetSource 间接代理 target 呢？
 * 因为一个 proxy 只能代理一个目标对象，如果 proxy 直接代理 target，当一个 target 需要多重代理，就需要多个 proxy。
 * 如果让 proxy 代理 TargetSource 来间接代理 target，只需要替换 TargetSource，一个 proxy 就可以实现多重代理的功能。
 */
public class TargetSource {

	private Class<?> targetClass;

    private Class<?>[] interfaces;

	/**
	 * 原始bean
	 */
	private Object target;

	public TargetSource(Object target, Class<?> targetClass, Class<?>... interfaces) {
		this.target = target;
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
}
