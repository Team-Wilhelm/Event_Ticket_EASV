package ticketSystemEASV.be;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String name, username, password;
    private Role role;
    private final HashMap<Integer, Event> assignedEvents;
    private byte[] profilePicture;

    public User(String name, String username, String password, Role role, byte[] profilePicture) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedEvents = new HashMap<Integer, Event>();
        this.profilePicture = profilePicture;
    }

    public User(UUID id, String name, String username, String password, Role role, byte[] profilePicture) {
        this(name, username, password, role, profilePicture);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public HashMap<Integer, Event> getAssignedEvents() {
        return assignedEvents;
    }

    public void setAssignedEvents(HashMap<Integer, Event> assignedEvents) {
        this.assignedEvents.clear();
        this.assignedEvents.putAll(assignedEvents);
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }
}
