package zw.mohcc.org.prep.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.mohcc.org.prep.entities.Role;
import zw.mohcc.org.prep.entities.User;
import zw.mohcc.org.prep.enums.ERole;
import zw.mohcc.org.prep.repositories.RoleRepository;
import zw.mohcc.org.prep.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public Role getRoleByName(ERole roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    @PostConstruct
    public void initRoles(){
        if(roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()){
            roleRepository.save(getRoleByName(ERole.ROLE_ADMIN));
        }
        if(roleRepository.findByName(ERole.ROLE_USER).isEmpty()){
            roleRepository.save(getRoleByName(ERole.ROLE_USER));
        }
    }

    public Set<Role> getRolesFromStrings(List<String> roles) {
        return roles.stream()
                .map(role -> {
                    if (role.equalsIgnoreCase("admin")) {
                        return roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();
                    } else {
                        return roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
                    }
                })
                .collect(Collectors.toSet());
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void updateUserRoles(Long userId, List<String> roleNames) {
        // Find user by ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Fetch roles by their names
            Set<Role> roles = roleRepository.findByNameIn(roleNames);

            // Update user's roles
            user.setRoles(roles);

            // Save the updated user
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
