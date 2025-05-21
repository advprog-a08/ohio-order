package id.ac.ui.cs.advprog.ohioorder.pesanan.client;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.util.NoSuchElementException;

@Component
public class MenuServiceClient {
    private final RestTemplate restTemplate;
    private final String menuServiceBaseUrl;

    public MenuServiceClient(RestTemplate restTemplate) {
        String menuServiceBaseUrl1;
        this.restTemplate = restTemplate;
        menuServiceBaseUrl1 = System.getProperty("MENU_SERVICE_URL");

        if (menuServiceBaseUrl1 == null || menuServiceBaseUrl1.isEmpty()) {
            menuServiceBaseUrl1 = "";
        }
        this.menuServiceBaseUrl = menuServiceBaseUrl1;
    }

    public MenuServiceResponse getMenuItem(String menuItemId) {
        String url = menuServiceBaseUrl + "/api/menus/" + menuItemId;
        ResponseEntity<MenuServiceResponse> response = restTemplate.getForEntity(url, MenuServiceResponse.class);
        
        if (response.getBody() == null) {
            throw new NoSuchElementException("Menu item not found with ID: " + menuItemId);
        }
        
        return response.getBody();
    }

    public boolean verifyMenuItemExists(String menuItemId) {
        try {
            getMenuItem(menuItemId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}