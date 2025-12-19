package org.example.cruddemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 1. 定义一个接口 (必须有接口，JDK动态代理只能代理接口)
interface IDeveloper {
    void writeCode();
}

// 2. 定义真实对象 (ACMer)
class ACMer implements IDeveloper {
    @Override
    public void writeCode() {
        System.out.println("ACMer: 正在疯狂敲代码，AC题目...");
    }
}

// 3. 测试类
public class DynamicProxyTest {
    public static void main(String[] args) {
        // 创建一个真实的 ACMer
        IDeveloper acmer = new ACMer();

        // 创建代理对象 (这个对象是 JDK 在运行时动态生成的)
        IDeveloper acmerProxy = (IDeveloper) Proxy.newProxyInstance(
                acmer.getClass().getClassLoader(), // 1. 类加载器 (固定写法)
                acmer.getClass().getInterfaces(),  // 2. 代理哪些接口 (固定写法)
                new InvocationHandler() {          // 3. 拦截处理器 (核心逻辑在这里)
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // --- 方法执行前 ---
                        System.out.println(">> 教练: 比赛开始，准备好键盘！");

                        // 执行真实对象的方法 (相当于 C++ 的函数指针调用)
                        Object result = method.invoke(acmer, args);

                        // --- 方法执行后 ---
                        System.out.println(">> 教练: 比赛结束，提交代码！");

                        return result;
                    }
                }
        );

        // 调用代理对象的方法，会自动触发上面的 invoke
        acmerProxy.writeCode();
    }
}