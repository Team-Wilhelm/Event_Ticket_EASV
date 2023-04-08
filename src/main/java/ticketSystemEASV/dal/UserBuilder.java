package ticketSystemEASV.dal;

import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;

import java.util.UUID;

public class UserBuilder {
    private UUID id;
    private String name;
    private String username;
    private String password;
    private byte[] profilePicture;
    private Role role;

    public UserBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }

    public UserBuilder setRole(Role role) {
        this.role = role;
        return this;
    }

    public User build() {
        User user = new User(id, name, username, password, role, profilePicture);
        return user;
    }
}
