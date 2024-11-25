package com.cams.stock.service;

import com.cams.stock.model.*;
//import com.cams.stock.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
import java.util.Queue;

public interface TransactionService {
	void addToWaitList(WaitListEntry entry, Queue<WaitListEntry> waitList, Long stockId);
}

