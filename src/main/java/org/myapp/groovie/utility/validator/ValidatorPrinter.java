package org.myapp.groovie.utility.validator;

import org.springframework.validation.BindingResult;

public class ValidatorPrinter {
    public String printErrors(BindingResult bindingResult){
        StringBuilder errors = new StringBuilder();
        for(var error : bindingResult.getFieldErrors()){
            String errorString = error.getField()+ ": " + error.getDefaultMessage() + "\n";
            errors.append(errorString);
        }
        return errors.toString();
    }
}
