package com.rikka.raymispring.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikka.raymispring.model.entity.ExceptionLogEntity;
import com.rikka.raymispring.repository.ExceptionLogRepository;
import com.rikka.raymispring.service.ExceptionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExceptionLogServiceImpl implements ExceptionLogService {

    private static final String ASYNC_EXCEPTION_LOGGER = "asyncExceptionLogger";

    private final ExceptionLogRepository exceptionLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Async(ASYNC_EXCEPTION_LOGGER)
    public void asyncLog(String errorCode, String errorCodeDesc, String errorMessage,
                         String remark, String dataSource, Object sourceData) {
        ExceptionLogEntity entity = ExceptionLogEntity.builder()
                .errorCode(errorCode)
                .errorCodeDesc(errorCodeDesc)
                .errorMessage(errorMessage)
                .remark(remark)
                .dataSource(dataSource)
                .sourceDataJson(toJson(sourceData))
                .build();
        exceptionLogRepository.save(entity);
    }

    private String toJson(Object sourceData) {
        if (sourceData == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(sourceData);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize source data for exception log", e);
            return "{\"serialization\":\"failed\"}";
        }
    }
}
