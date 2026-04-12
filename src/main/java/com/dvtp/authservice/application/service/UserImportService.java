package com.dvtp.authservice.application.service;

import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.repository.SpringDataAppClientRepository;
import com.dvtp.authservice.infrastructure.persistence.repository.SpringDataJpaRoleRepository;
import com.dvtp.authservice.infrastructure.persistence.repository.UserBulkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImportService {

    private final UserBulkRepository userBulkRepository;
    // Inject thêm 2 cái này để lấy ID mặc định
    private final SpringDataAppClientRepository clientRepository;
    private final SpringDataJpaRoleRepository roleRepository;

    private static final int BATCH_CHUNK_SIZE = 2000;

    @Transactional
    public void importUsersInBulk(List<User> hugeUserList, String defaultClientId, String defaultRoleName) {
        log.info("Bắt đầu xử lý import tổng cộng {} users", hugeUserList.size());

        // Lấy UUID thực tế từ DB
        AppClientJpaEntity client = clientRepository.findByClientId(defaultClientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Client: " + defaultClientId));
        RoleJpaEntity role = roleRepository.findByName(defaultRoleName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role: " + defaultRoleName));

        UUID clientUuid = client.getId();
        UUID roleUuid = role.getId();

        List<User> chunk = new ArrayList<>();

        for (int i = 0; i < hugeUserList.size(); i++) {
            chunk.add(hugeUserList.get(i));

            if (chunk.size() == BATCH_CHUNK_SIZE || i == hugeUserList.size() - 1) {
                // Truyền thêm 2 cái UUID vào đây
                userBulkRepository.bulkInsertUsersAndRoles(chunk, clientUuid, roleUuid);
                chunk.clear();
            }
        }

        log.info("Hoàn tất tiến trình Import & Phân quyền!");
    }
}