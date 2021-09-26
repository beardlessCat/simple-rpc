package com.bgiyj.config;

import com.bgiyj.config.handler.ClientInvocationHandler;
import com.bgiyj.config.handler.MethodHandler;
import com.bgiyj.factory.MethodHandlerFactory;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvokeClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
	private ApplicationContext applicationContext ;
	@Setter
	private Class<?> type;
	/**
	 * 生成代理对象
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object getObject() {
		MethodHandlerFactory methodHandlerFactory = applicationContext.getBean(MethodHandlerFactory.class);
		methodHandlerFactory.setType(type);
		Map<Method, MethodHandler> dispatch = new ConcurrentHashMap<>();
		for (Method method : type.getMethods()) {
			//通过method的注解信息，获取远程调用的信息
			dispatch.put(method, methodHandlerFactory.fromMethodHandler(method));
		}
		ClientInvocationHandler invocationHandler = new ClientInvocationHandler(dispatch);
		return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, invocationHandler);
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
