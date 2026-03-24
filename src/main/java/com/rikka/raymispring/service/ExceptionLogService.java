package com.rikka.raymispring.service;

public interface ExceptionLogService {

    void asyncLog(String errorCode, String errorCodeDesc, String errorMessage,
                  String remark, String dataSource, Object sourceData);
}
