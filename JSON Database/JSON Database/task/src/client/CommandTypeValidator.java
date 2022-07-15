package client;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class CommandTypeValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        switch (value) {
            case "get":
            case "set":
            case "delete":
            case "exit":
                break;
            default:
                throw new ParameterException("Parameter " + name + " should be get, set, delete, or exit command");
        }

    }
}