package ticketSystemEASV.be;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String name, username, password;
    private Role role;
    private final List<Event> assignedEvents;

    public User(String name, String username, String password, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedEvents = new ArrayList<>();
    }

    public User(UUID id, String name, String username, String password, Role role) {
        this(name, username, password, role);
        this.id = id;
    }

    public User(UUID id, String name, String username, String password) {
        this(id, name, username, password, null);
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

    public List<Event> getAssignedEvents() {
        return assignedEvents;
    }
}
