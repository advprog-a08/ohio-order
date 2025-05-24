package id.ac.ui.cs.advprog.ohioorder.meja.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MejaConfig {
    private static MejaConfig instance;
    
    @Getter
    private final int maxTableCount = 100; 
    
    @Getter
    private final String tablePrefix = "A"; // Default prefix for table numbers and validation
    
    private MejaConfig() {
        // Private constructor to prevent instantiation
    }
    
    public static synchronized MejaConfig getInstance() {
        if (instance == null) {
            instance = new MejaConfig();
        }
        return instance;
    }

    // Default table number generator
    public String generateTableNumber(int number) {
        return tablePrefix + number;
    }
    
    public boolean isValidTableNumber(String nomorMeja) {
        if (nomorMeja == null || nomorMeja.trim().isEmpty()) {
            return false;
        }

        // Table number should start with the prefix and be followed by a number
        if (!nomorMeja.matches("^[A-Z]\\d+$")) {
            return false;
        }
        
        String numberPart = nomorMeja.substring(tablePrefix.length());
        try {
            int tableNumber = Integer.parseInt(numberPart);
            return tableNumber > 0 && tableNumber <= maxTableCount;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}