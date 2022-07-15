package client;

import com.beust.jcommander.Parameter;

public class Args {
//    @Parameter
//    private List<String> parameters = new ArrayList<>();

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getFilename() {
        return filename;
    }

    @Parameter(names = {"-t"}, validateWith = {CommandTypeValidator.class}, description = "type of request")
    private String type;

    @Parameter(names = {"-k"}, description = "key of cell")
    private String key;

    @Parameter(names = {"-v"}, description = "value to save")
    private String value;

    @Parameter(names = {"-in"}, description = "file to read command")
    private String filename;


    @Override
    public String toString() {
        return "Args{" +
                "type='" + type + '\'' +
                ", key=" + key +
                ", message='" + value + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}