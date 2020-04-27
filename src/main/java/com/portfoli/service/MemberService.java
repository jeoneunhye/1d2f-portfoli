package com.portfoli.service;

import java.util.Map;
import com.portfoli.domain.CompanyMember;
import com.portfoli.domain.GeneralMember;
import com.portfoli.domain.Member;

public interface MemberService {

  int add(Member member, GeneralMember generalMember) throws Exception;

  int add(Member member, CompanyMember companyMember, int companyNumber) throws Exception;
  
  int delete(int memberType, int memberNumber) throws Exception;

  Member get(String email, String password) throws Exception;

  Member get(int number) throws Exception;
  
  int updateDefaultInfo(GeneralMember member) throws Exception;

  int updateProfilePic(Map<String, Object> generalMember) throws Exception;
  
  int updatePassword(int memberNumber, String newPassword) throws Exception;
  
  int updateAddress(Member member) throws Exception;

  GeneralMember getGeneralMember(int number) throws Exception;
  
  GeneralMember getSessionInfo(int memberNumber) throws Exception;

}