package id.ac.ui.cs.advprog.ohioorder.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MejaConfig {
    private static MejaConfig instance;
    
    @Getter
    private final int maxTableCount = 100; 
    
    @Getter
    private final String tablePrefix = "T"; // Prefix for table numbers
    
    private MejaConfig() {
        // Private constructor to prevent instantiation
    }
    
    public static synchronized MejaConfig getInstance() {
        if (instance == null) {
            instance = new MejaConfig();
        }
        return instance;
    }
    
    public String generateTableNumber(int number) {
        return tablePrefix + number;
    }
    
    public boolean isValidTableNumber(String nomorMeja) {
        // Table number should start with the prefix and be followed by a number
        if (!nomorMeja.startsWith(tablePrefix)) {
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