package org.atmgigi.hyobankingbe.txn.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnDetailDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;
import org.atmgigi.hyobankingbe.txn.repository.TxnEntryRepository;
import org.atmgigi.hyobankingbe.txn.repository.TxnRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TxnServiceImpl implements TxnService {

    private final TxnRepository txnRepository;
    private final TxnEntryRepository txnEntryRepository;

    @Override
    public TxnCreatedResponseDTO createTxn(final TxnRequestDTO txnRequestDTO) {
        //TODO: 유저 주입 및 입출금 판별 후 데이터로 전표 작성
        // 1. 계좌번호로 계좌 엔티티 조회
        // 2. 계좌에서 사용자 연관관계 통해 가져오기
        // 3. 거래 생성 시 user 연결
        // 4. 분개 엔트리 생성
        return null;
    }

    @Override
    public TxnDetailDTO getTxnDetail(final Long txnId) {
        return null;
    }
}
