package org.atmgigi.hyobankingbe.txn.service;

import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnDetailDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;

public interface TxnService {
    TxnCreatedResponseDTO createTxn(TxnRequestDTO txnRequestDTO);
    TxnDetailDTO getTxnDetail(Long txnId);
}
