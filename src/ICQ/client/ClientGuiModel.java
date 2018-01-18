package ICQ.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel {
    private final Set<String> allUserNames = new HashSet();
    private String newMessage;

    public ClientGuiModel() {
    }

    public Set<String> getAllUserNames() {
        return Collections.unmodifiableSet(this.allUserNames);
    }

    public String getNewMessage() {
        return this.newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public void addUser(String newUserName) {
        this.allUserNames.add(newUserName);
    }

    public void deleteUser(String userName) {
        this.allUserNames.remove(userName);
    }
}
