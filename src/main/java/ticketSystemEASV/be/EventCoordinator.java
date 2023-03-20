package ticketSystemEASV.be;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EventCoordinator {
    private UUID id;
    private String name, username, password, profilePicture;
    private List<Event> assignedEvents;

    public EventCoordinator(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
        assignedEvents = new ArrayList<>();
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

    public Collection<Event> getAssignedEvents() {
        return assignedEvents;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
