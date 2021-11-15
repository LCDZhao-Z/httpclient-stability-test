package esastack;

import esa.restlight.core.util.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RestlightDemoApplication
 *
 * @author duanwei
 * @date 2020/5/13 17:21
 */
@SpringBootApplication
public class RestLightApplication {

    public static void main(String[] args) {
        if (args.length > 0) {
            String port = args[0];
            System.setProperty(Constants.SERVER_PORT, port);
        }
        SpringApplication.run(RestLightApplication.class, args);
    }

}
