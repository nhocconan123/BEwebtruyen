package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.admin.AdminUpdateUserRequest;
import com.truyen.webtruyen.dto.admin.UpdateUserRoleRequest;
import com.truyen.webtruyen.dto.admin.UpdateUserStatusRequest;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.UserRole;
import com.truyen.webtruyen.entity.enums.UserStatus;
import com.truyen.webtruyen.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<User> getAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "createdAt") String sortBy,
                             @RequestParam(defaultValue = "desc") String sortDir,
                             @RequestParam(required = false) UserRole role,
                             @RequestParam(required = false) UserStatus status,
                             @RequestParam(required = false) String keyword) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), like),
                        cb.like(cb.lower(root.get("email")), like)
                ));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    @PatchMapping("/{id}/role")
    public User updateRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        user.setRole(payload.getRole());
        return userRepository.save(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody AdminUpdateUserRequest payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        boolean changed = false;

        if (payload.getUsername() != null) {
            String username = payload.getUsername().trim();
            if (username.isEmpty()) {
                throw new ResponseStatusException(BAD_REQUEST, "username must not be blank");
            }

            boolean same = user.getUsername() != null && user.getUsername().equalsIgnoreCase(username);
            if (!same && userRepository.existsByUsername(username)) {
                throw new ResponseStatusException(BAD_REQUEST, "Username already exists");
            }

            if (!same) {
                user.setUsername(username);
                changed = true;
            }
        }

        if (payload.getRole() != null && payload.getRole() != user.getRole()) {
            user.setRole(payload.getRole());
            changed = true;
        }

        if (!changed) {
            throw new ResponseStatusException(BAD_REQUEST, "No fields to update");
        }

        return userRepository.save(user);
    }

    @PatchMapping("/{id}/status")
    public User updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        user.setStatus(payload.getStatus());
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }
}
