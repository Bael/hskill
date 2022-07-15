import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.stream.Collectors;

class UserProfile implements Serializable {

//    public static void main(String[] args) {
//        UserProfile up = new UserProfile("", "", "sdsad");
//        System.out.println(up.decrypt("bcd"));
//    }
    private static final long serialVersionUID = 26292552485L;

    private String login;
    private String email;
    private transient String password;

    public UserProfile(String login, String email, String password) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    // implement readObject and writeObject properly

    private void writeObject(ObjectOutputStream oos) throws Exception {
        oos.defaultWriteObject();
        String encryptPassword = encrypt(password);
        oos.writeObject(encryptPassword);
        // write the custom serialization code here
    }

    private String encrypt(String password) {
        StringBuilder sb = new StringBuilder();

        password.codePoints().map(operand -> operand + 1).map(operand -> (char) operand)
                .forEach(value -> sb.append((char) value));
        return sb.toString();

    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        password = decrypt((String) ois.readObject());
        // write the custom deserialization code here
    }

    private String decrypt(String readObject) {
        StringBuilder sb = new StringBuilder();
        readObject.chars().map(operand -> operand - 1).map(operand -> (char) operand)
                .forEach(value -> sb.append((char) value));
        return sb.toString();
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}