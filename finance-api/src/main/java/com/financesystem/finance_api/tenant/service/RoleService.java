package com.financesystem.finance_api.tenant.service;

import com.financesystem.finance_api.tenant.dto.RoleRequestDTO;
import com.financesystem.finance_api.tenant.dto.RoleResponseDTO;
import com.financesystem.finance_api.tenant.model.Role;
import com.financesystem.finance_api.tenant.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponseDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }

    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return new RoleResponseDTO(role.getId(), role.getName());
    }

    public RoleResponseDTO createRole(RoleRequestDTO request) {
        Role role = new Role();
        role.setName(request.getName());
        role = roleRepository.save(role);
        return new RoleResponseDTO(role.getId(), role.getName());
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
