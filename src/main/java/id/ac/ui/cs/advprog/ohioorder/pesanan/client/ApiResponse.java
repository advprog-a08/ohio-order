package id.ac.ui.cs.advprog.ohioorder.pesanan.client;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
