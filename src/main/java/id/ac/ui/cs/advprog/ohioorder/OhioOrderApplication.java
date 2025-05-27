package id.ac.ui.cs.advprog.ohioorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class OhioOrderApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("spring.datasource.url", dotenv.get("DATABASE_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("DATABASE_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("DATABASE_PASSWORD"));
        System.setProperty("MENU_SERVICE_URL", dotenv.get("MENU_SERVICE_URL"));

        System.setProperty("grpc.host", dotenv.get("SIGMA_AUTHENTICATION_GRPC_HOST"));
        System.setProperty("grpc.port", dotenv.get("SIGMA_AUTHENTICATION_GRPC_PORT"));

        SpringApplication.run(OhioOrderApplication.class, args);
    }

}
