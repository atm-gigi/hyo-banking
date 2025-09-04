package org.atmgigi.hyobankingbe.txn.service;

import org.atmgigi.hyobankingbe.txn.dto.TxnCreatedResponseDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnDetailDTO;
import org.atmgigi.hyobankingbe.txn.dto.TxnRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface TxnService {
    TxnCreatedResponseDTO createTxn(TxnRequestDTO txnRequestDTO);
    List<TxnDetailDTO> getTxns(LocalDate from, LocalDate to, String accountNo, String bankCode);
    TxnDetailDTO getTxnDetail(Long txnId);
}
