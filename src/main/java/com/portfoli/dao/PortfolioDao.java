package com.portfoli.dao;

import java.util.List;
import java.util.Map;
import com.portfoli.domain.Portfolio;

public interface PortfolioDao {

  List<Portfolio> findAll(Portfolio portfolio) throws Exception;

  Portfolio findByNo(int boardNumber) throws Exception;

  List<Portfolio> findByMember(Portfolio portfolio) throws Exception;

  List<Portfolio> findAllRecommendedlist(Portfolio portfolio) throws Exception;//
  
  boolean insert(Portfolio portfolio) throws Exception;

  boolean delete(int boardNumber) throws Exception;

  boolean update(Portfolio portfolio) throws Exception;

  int selectListCnt() throws Exception;

  int selectMyListCnt(int generalMemberNumber) throws Exception;

  int selectMyRecommendedListCnt(int generalMemberNumber) throws Exception;
  
  void readableon(Portfolio portfolio) throws Exception;

  void readableoff(Portfolio portfolio) throws Exception;

  List<Portfolio> search(Map<String, String> map) throws Exception;









}
