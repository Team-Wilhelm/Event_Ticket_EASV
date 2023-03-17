package be;

import java.util.UUID;

public class EventCoordinator {
    private UUID id;
    private String name, username, password;

    public EventCoordinator(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public EventCoordinator(UUID id, String name, String username, String password) {
        this(name, username, password);
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
}