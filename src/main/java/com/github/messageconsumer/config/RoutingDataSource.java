package com.github.messageconsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
    private static final String MASTER = "master";
    private static final String SLAVE = "slave";
    private static final String SLAVE2 = "slave2";

    //Java에서 멀티스레드 환경에서 각 스레드별로 독립적인 값을 유지하기 위한 클래스
    //withInitial 메서드는 초기값을 제공하는 람다식 또는 Supplier를 인자로 받아 스레드마다 독립적인 값의 복사본을 생성합니다.
    //즉, 각 스레드는 자신만의 dataSourceKey 값을 가지며 이 값은 스레드 간에 독립적으로 유지됩니다.
    // 따라서 getNextIndex 메서드에서 dataSourceKey 값을 수정하더라도 이 값은 해당 스레드 내에서만 변경되며 다른 스레드에 영향을 주지 않습니다.
    private static final ThreadLocal<String> dataSourceKey = ThreadLocal.withInitial(() -> MASTER);
    private static final String[] readOnlyDataSourceKeys = {SLAVE, SLAVE2};

    @Override
    protected Object determineCurrentLookupKey() {

        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (isReadOnly) {
            int index = getNextIndex();
            log.info(readOnlyDataSourceKeys[index]);
            return readOnlyDataSourceKeys[index];
        } else {
            return MASTER;
        }
    }

    private int getNextIndex() {
        String currentKey = dataSourceKey.get();
        if (currentKey.equals(SLAVE)) {
            dataSourceKey.set(SLAVE2);
            return 1;
        } else {
            dataSourceKey.set(SLAVE);
            return 0;
        }
    }
}
