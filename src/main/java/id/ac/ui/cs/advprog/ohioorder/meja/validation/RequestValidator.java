package id.ac.ui.cs.advprog.ohioorder.meja.validation;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.InvalidRequestException;

public abstract class RequestValidator<T> {

    public final void validate(T request) {
        preValidate(request);
        doValidate(request);
        postValidate(request);
    }

    protected void preValidate(T request) {

    }
    
    protected abstract void doValidate(T request);
   
    protected void postValidate(T request) {

    }
}