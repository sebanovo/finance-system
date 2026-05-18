package com.financesystem.finance_api.modules.governance.notifications.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationDeliveryResponse;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationTemplateResponse;
import com.financesystem.finance_api.modules.governance.notifications.application.mapper.NotificationMapper;
import com.financesystem.finance_api.modules.governance.notifications.application.service.NotificationApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
// @PreAuthorize("hasAnyRole('OWNER_ADMIN', 'ADMIN')")
public class NotificationAdminController {

    private final NotificationApplicationService notificationApplicationService;
    private final NotificationMapper notificationMapper;

    public NotificationAdminController(
            NotificationApplicationService notificationApplicationService,
            NotificationMapper notificationMapper
    ) {
        this.notificationApplicationService = notificationApplicationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAuthority('notifications.templates.read')")
    public ApiResponse<List<NotificationTemplateResponse>> listTemplates() {
        return ApiResponse.success(
                "Notification templates retrieved successfully",
                notificationApplicationService.listTemplates().stream()
                        .map(notificationMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('notifications.templates.detail')")
    public ApiResponse<NotificationTemplateResponse> getTemplateById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Notification template retrieved successfully",
                notificationMapper.toResponse(notificationApplicationService.getTemplateById(id))
        );
    }

    @GetMapping("/{notificationId}/deliveries")
    @PreAuthorize("hasAuthority('notifications.deliveries.read')")
    public ApiResponse<List<NotificationDeliveryResponse>> listDeliveriesByNotification(
            @PathVariable UUID notificationId
    ) {
        return ApiResponse.success(
                "Notification deliveries retrieved successfully",
                notificationApplicationService.listDeliveriesByNotification(notificationId).stream()
                        .map(notificationMapper::toResponse)
                        .toList()
        );
    }
}
