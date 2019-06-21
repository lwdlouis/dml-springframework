import com.dml.controller.DmlController;
import com.dml.service.CycleDependenceA;
import com.dml.service.CycleDependenceB;
import com.dml.spring.framework.context.ApplicationContext;
import com.dml.spring.framework.ui.ConcurrentModel;
import com.dml.spring.framework.webmvc.servlet.DispatcherServlet;

import java.lang.reflect.Method;

public class SpringTest {

    public static void main(String[] args) throws Exception {

//        testCycleDependence();

//        testMethodInvoke();

    }



    private static void testMethodInvoke() throws Exception {

        ApplicationContext applicationContext = new ApplicationContext("classpath:application.properties");

        Object object = applicationContext.getBean("dmlController");

        DmlController dmlController = (DmlController) object;

        Method method = dmlController.getClass().getMethods()[0];

        Object[] params = {"louis", new ConcurrentModel()};

        Object result = method.invoke(dmlController, params);
        System.out.println(result);

    }


    private static void testCycleDependence() {
        ApplicationContext applicationContext = new ApplicationContext("classpath:application.properties");

        Object object = applicationContext.getBean("dmlController");

        DmlController dmlController = (DmlController) object;

        System.out.println(dmlController.index("Louis", new ConcurrentModel()));

        CycleDependenceA cycleDependenceA = (CycleDependenceA) applicationContext.getBean("cycleDependenceA");
        cycleDependenceA.testCycle();

        CycleDependenceB cycleDependenceB = (CycleDependenceB) applicationContext.getBean("cycleDependenceB");
        cycleDependenceB.testCycle();
    }


}
