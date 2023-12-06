## 概况

```
_________ _______  _______  _______  _______ _________ _        _______ 
\__   __/(       )(  ____ \(  ____ )(  ____ )\__   __/( (    /|(  ____ \
   ) (   | () () || (    \/| (    )|| (    )|   ) (   |  \  ( || (    \/
   | |   | || || || (_____ | (____)|| (____)|   | |   |   \ | || |      
   | |   | |(_)| |(_____  )|  _____)|     __)   | |   | (\ \) || | ____ 
   | |   | |   | |      ) || (      | (\ (      | |   | | \   || | \_  )
___) (___| )   ( |/\____) || )      | ) \ \_____) (___| )  \  || (___) |
\_______/|/     \|\_______)|/       |/   \__/\_______/|/    )_)(_______)
:: Imspring (v1.0.0 Release)                                                          
```

熟悉一项技能最好的方式就是把他实现出来。本项目  `Imspring`  模仿 Spring 项目，实现了 Spring 的几大核心功能。

* IOC
* AOP
* WebMVC
* Transaction
* Spring Boot（开发中）

所有的代码开发，包括类/方法/字段命名、设计模式、主流程逻辑，尽量和 Spring 规范保持一致，相当于去除了一些旁枝末叶，把 Spring 最核心的功能提炼出来，做了一个 mini 版的 Spring，有助于快速了解 Spring 的核心思想。

> 主流程尽量还原，但不能做到百分百还原。比如 Spring 很经典的一个写法，要向类 A 里面的集合 registry 放入一个对象
>
> 1. 首先 registry 不是直接放在类  A 里面的，而是放在一个 Registry 接口的实现类里面，类 A 通过实现了 Registry 接口，同时持有 Registry 接口的实现类，间接持有 registry。（装饰器模式）
> 2.  Registry 接口的实现类对要放入 registry 的对象进行解析，调用另外一个类 Parser 来完成解析。（委派模式）
> 3. Parser 解析完之后，再调用一个 Utils 类，借助工具类 Utils 最终把对象放入 registry。（委派模式）
>
> 向 registry 添加对象这样一个动作，可能会跨及四到五个类，把整个过程拉长，显得非常复杂和冗余。对于这种情况，本项目就会做一些简化处理，比如把委托模式改成本类里面的一个方法，在尽量保证原流程逻辑的同时降低代码的复杂度。

## 项目结构

```
imspring
├─imspring-aop
├─imspring-boot
├─imspring-core
├─imspring-parent
├─imspring-tx
├─imspring-web
```

* imspring-aop：AOP 相关功能，依赖 core 包。
* imspring-boot：boot 自动配置和快速启动。（未完成）
* imspring-core：IOC 核心包，是所有 module （除了parent）的基础。
* imspring-parent：依赖包管理，是所有 module 的父 pom。
* imspring-tx：事务相关功能，包含 jdbc + tx 的功能，依赖 aop 包。
* imspring-web：WebMVC 相关功能，内嵌 tomcat，依赖 core 包。

相关文章链接

[Imspring 源码介绍（一）：基本概念](https://mcbilla.com/2023/09/18/Imspring-%E6%BA%90%E7%A0%81%E4%BB%8B%E7%BB%8D%EF%BC%88%E4%B8%80%EF%BC%89%EF%BC%9A%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5/)

[Imspring 源码介绍（二）：IOC 实现](https://mcbilla.com/2023/09/20/Imspring-%E6%BA%90%E7%A0%81%E4%BB%8B%E7%BB%8D%EF%BC%88%E4%BA%8C%EF%BC%89%EF%BC%9AIOC-%E5%AE%9E%E7%8E%B0/)

[Imspring 源码介绍（三）：AOP 实现](https://mcbilla.com/2023/10/01/Imspring-%E6%BA%90%E7%A0%81%E4%BB%8B%E7%BB%8D%EF%BC%88%E4%B8%89%EF%BC%89%EF%BC%9AAOP-%E5%AE%9E%E7%8E%B0/)

[Imspring 源码介绍（四）：WebMVC 实现](https://mcbilla.com/2023/10/10/Imspring-%E6%BA%90%E7%A0%81%E4%BB%8B%E7%BB%8D%EF%BC%88%E5%9B%9B%EF%BC%89%EF%BC%9AWebMVC-%E5%AE%9E%E7%8E%B0/)

[Imspring 源码介绍（五）：事务实现](https://mcbilla.com/2023/10/18/Imspring-%E6%BA%90%E7%A0%81%E4%BB%8B%E7%BB%8D%EF%BC%88%E4%BA%94%EF%BC%89%EF%BC%9A%E4%BA%8B%E5%8A%A1%E5%AE%9E%E7%8E%B0/)

## 功能简介

Imspring 基本实现了 Spring 核心功能，支持的功能如下：

| 功能            | 简介                                                         |
| --------------- | ------------------------------------------------------------ |
| IOC 容器功能    | 支持 ApplicationContext 和 BeanFactory                       |
| 配置方式        | 支持 Annotation 注解配置                                     |
| 注入 Bean 管理  | 支持 @ComponentScan 按包名扫描注入，或者 @Configuration + @Bean/@Import 配置类注入 |
| Bean 类型       | 只支持 Singleton                                             |
| Bean 前后置处理 | 支持 BeanPostProcessor 和 BeanFactoryPostProcessor           |
| 依赖注入        | 支持 Filed 注入，支持三级缓存，已解决 Filed 注入的循环依赖问题 |
| 环境配置        | 支持 @Value 自动解析配置文件                                 |
| Web容器         | 内嵌 Tomcat，支持 Web 容器启动，支持标准 MVC 语法注解        |
| 切面编程        | 支持切面功能，支持标准 AspectJ 切面语法                      |
| 代理对象        | 支持 JDK 代理和 CGLIB 代理两种方式                           |
| 事务管理        | 支持声明式事务和编程式事务，支持四种事务传播机制，支持 InnoDB 隔离级别。 |
| JDBC            | 默认支持 Hikari 连接池，支持 JdbcTemplate 静态 SQL 语法      |

Imspring 支持的注解如下。所有注解的使用方式和 Spring 完全一致。

IOC 相关：

* @Component，包括三个同类型的注解 @Controller、@Service、@Repository。
* @ComponentScan
* @Configuration、@Bean、@Import
* @Autowire
* @Value

AOP 相关：跟 Spring 一样，直接引入 aspectj 和 aopalliance 的包注解。

* @Aspect
* @Pointcut
* @Before、@After、@AfterReturning、@AfterThrowing、@Around

WebMVC相关：

* @RestController
* @RequestBody、@ResponseBody
* @RequestMapping、GetMapping、@PostMapping
* @RequestParam、@PathVariable

事务相关：

* @Transaction

## 使用示例

### IOC 容器功能

假如有个类需要放入 IOC 容器进行管理，需要使用 @Component 注解修饰

```
@Component
public class Hello {
}
```

这时候要添加这个类到 IOC 容器，有两种方式。第一种是包路径扫描，自动扫描该路径下所有类，把带有 @Component 注解的类放入容器管理

```
// 假如 Hello 的类路径是 com.mcb.imspring.core
try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.mcb.imspring.core")) {
    ComponentBean bean = context.getBean("componentBean", ComponentBean.class);
    bean.test();
} catch (Exception e) {
    throw e;
}
```

第二种是通过配置文件的方式，配置文件必须使用 @Configuration 修饰。配置文件有三种使用方式，第一种是使用 @ComponentScan 注解

```
@ComponentScan("com.mcb.imspring.core")
@Configuration
public class AppConfig {
}
```

第二种是使用 @Bean 注解

```
@Configuration
public class AppConfig {
	@Bean
	public Hello hello() {
		return new Hello();
	}
}
```

第三种是使用 @Import 注解

```
@Configuration
@Import(Hello.class)
public class AppConfig {
}
```

这三种方式都可以把 Hello 类注入到 IOC 容器进行管理，这时候在启动的时候只需要扫描配置类 AppConfig

```
try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
    ComponentBean bean = context.getBean("componentBean", ComponentBean.class);
    bean.test();
} catch (Exception e) {
    throw e;
}
```

### 依赖注入和循环依赖

通过 @Autowired 注解，可以实现 field 自动依赖注入

```
// 把 Test2 类型的 bean 自动注入到 Test1 的属性中
@Component
public class Test1 {
	@Autowired
	private Test2 test2;
}

@Component
public class Test2 {
}
```

如果两个类都有一个对方类型的属性，在容器初始化的时候，会出现两个类互相查找对方实例的情形，这时候会出现**循环依赖**的问题。**项目已经实现了三级缓存，可解决 field 注入的循环依赖问题**。

```
// Test1 和 Test2 互相持有对方类型的属性，通过三级缓存解决这种循环依赖问题
@Component
public class Test1 {
	@Autowired
	private Test2 test2;
}

@Component
public class Test2 {
	@Autowired
	private Test1 test1;
}
```

### 环境配置读取

假如配置文件内容如下：

```
# application.yml
aaa:
  name: "mcb2"

# application.properties
bbb.name=mcb3
```

使用 @Value 注解，可以自动解析占位符，并读取 yaml 和 properties 配置文件。

```
@Component
public class PropertyBean {
    @Value("mcb1") 
    private String name1; // mcb1

    @Value("${aaa.name}")
    private String name2; // mcb2

    @Value("${bbb.name}")
    private String name3; // mcb3
}
```

### 启动Web容器

使用下面代码可以启动一个标准的 Tomcat 容器

```
public class ImspringWebTest {

    private static int port = 8080;

    private static String contextPath = "/";

    private static String baseDir = new File("imspring-web/src/main/resources").getAbsolutePath();
    
    public static void main(String[] args) throws LifecycleException {
        // 创建tomcat并绑定端口
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        // Tomcat 9.0 必须调用 Tomcat#getConnector() 方法之后才会监听端口
        tomcat.getConnector();
        // 创建webapp，contextPath为URL的基路径，baseDir为包含web.xml的路径
        tomcat.addWebapp(contextPath, baseDir);
        tomcat.start();
        // 等待，避免马上运行结束。
        tomcat.getServer().await();
    }
}
```

容器启动后，可以使用标准 MVC 语法编写 Controller 层。

```
@Controller
@RequestMapping("test")
public class TestController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }
}
```

页面访问 http://localhost:8080/test/hello，返回 hello。

### 切面使用

支持标准 AspectJ 切面语法。特别的对于 Around 通知，还支持修改 joinpoint 的传参。

```
// 自定义切面，支持标准 pointcut 语法和五种类型的通知
@Component
@Aspect
public class MyAspect {

    @Pointcut("execution(* com.mcb.imspring.aop..*.*Service*.test(..))")
    public void getPointcut() {
    }

    @Around("getPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        System.out.println("我是一个AroundAdvice前置处理");
        // 注意这一步修改了传参
        res = joinPoint.proceed(new String[]{"222"});
        System.out.println("我是一个AroundAdvice后置处理");
        return res;
    }

    @Before("getPointcut()")
    public void before() {
        System.out.println("我是一个BeforeAdvice");
    }

    @After("getPointcut()")
    public void after() {
        System.out.println("我是一个AfterAdvice");
    }

    @AfterReturning("getPointcut()")
    public void afterReturning() {
        System.out.println("我是一个AfterReturningAdvice");
    }

    @AfterThrowing("getPointcut()")
    public void afterThrowing() {
        System.out.println("我是一个AfterThrowingAdvice");
    }
}
```

 然后判断 joinpoint 所在类是否实现了接口，如果实现了接口自动使用 JDK 代理，否则使用 CGLIB 代理。

```
// MyService 没有实现接口，自动创建 CGLIB 代理对象
@Component
public class MyService {
    public String test(String str) {
        String res = "这是myService的test方法 " + str;
        System.out.println(res);
        return res;
    }
}
```

使用下面测试代码

```
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImspringAopTest.class)) {
			MyService bean = context.getBean("myService");
			bean.test("111");
        } catch (Exception e) {
            throw e;
        }
```

输出如下，注意输出的参数值从一开始的111已经被改成222。

```
我是一个AroundAdvice前置处理
我是另外一个切面的BeforeAdvice
我是一个BeforeAdvice
这是myService的test方法 222
我是一个AfterReturningAdvice
我是一个AfterAdvice
我是一个AroundAdvice后置处理
```

### 事务使用

声明式事务只需要使用一个 @Transaction 注解。注意事务同 Spring 是基于 AOP 实现的，所以在同一个类调用事务方法会导致事务失效，一定要在其他类去调用事务方法。

```
@Component
public class TxService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Transactional(rollbackFor = Exception.class)
    public Object declareTx() {
        String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
        int update = jdbcTemplate.update(sql);
        return update;
    }
}
```

编程式事务，语法使用同 Spring。

```
@Component
public class TxService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
   public Object programTx() {
   		// 开启事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = platformTransactionManager.getTransaction(def);

        try {
            String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
            int res = jdbcTemplate.update(sql);
            // 提交事务
            platformTransactionManager.commit(status);
            System.out.println("数据插入成功");
            return res;
        } catch (Exception e) {
            System.err.println("数据插入失败 " + e);
            // 回滚事务
            platformTransactionManager.rollback(status);
            return null;
        }
    }
}
```

