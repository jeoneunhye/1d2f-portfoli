package com.portfoli.service;

import java.util.List;
import com.portfoli.domain.Qna;

public interface QnaService {
  
  List<Qna> list() throws Exception;

}