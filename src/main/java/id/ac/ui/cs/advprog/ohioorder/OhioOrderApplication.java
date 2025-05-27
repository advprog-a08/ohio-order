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

        System.out.println("Connecting to database at: " + System.getProperty("spring.datasource.url"));
        System.out.println("Using menu service URL: " + System.getProperty("MENU_SERVICE_URL"));
        System.out.println("gRPC host: " + System.getProperty("grpc.host"));
        System.out.println("gRPC port: " + System.getProperty("grpc.port"));

        SpringApplication.run(OhioOrderApplication.class, args);
    }

}
