package com.financesystem.finance_api.tenant.dto;

public class RoleRequestDTO {
    private Long id;
    private String name;

    public RoleRequestDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
