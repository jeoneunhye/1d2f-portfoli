package com.portfoli.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.portfoli.domain.Board;
import com.portfoli.domain.BoardAttachment;
import com.portfoli.domain.GeneralMember;
import com.portfoli.domain.Member;
import com.portfoli.domain.Pagination;
import com.portfoli.domain.Portfolio;
import com.portfoli.domain.Recommendation;
import com.portfoli.service.BoardAttachmentService;
import com.portfoli.service.BoardService;
import com.portfoli.service.MemberService;
import com.portfoli.service.PortfolioService;
import com.portfoli.service.PortfolioSkillService;
import com.portfoli.service.RecommendationService;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Controller
@RequestMapping("/portfolio")
@MultipartConfig(maxFileSize = 100000000)
public class PortfolioController {
  final int pageSize = 10;
  static Logger logger = LogManager.getLogger(PortfolioController.class);

  public PortfolioController() {
    PortfolioController.logger.debug("PortfolioController 객체 생성!");
  }

  @Autowired
  ServletContext servletContext;

  @Autowired
  MemberService memberService;

  @Autowired
  RecommendationService recommendationService;

  @Autowired
  BoardAttachmentService boardAttachmentService;

  @Autowired
  BoardService boardService;

  @Autowired
  PortfolioService portfolioService;

  @Autowired
  PortfolioSkillService portfolioSkillService;

  @RequestMapping("pdf")
  public String showPdf(String value, Model model) throws Exception {
    model.addAttribute("addr", value);
    return "portfolio/pdf";
  }

  @RequestMapping("search")
  public String search(String keyword, HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    else {
      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      Map<String, String> map = new HashMap<>();
      map.put("keyword", keyword);
      map.put("number", String.valueOf(member.getNumber()));
      List<Portfolio> portfolios = portfolioService.search(map);

      // 포트폴리오 스킬 붙이기
      for(Portfolio p : portfolios) {
        p.setSkill(portfolioSkillService.findAllSkill(p.getBoard().getNumber()).getSkill());
      }

      model.addAttribute("list", portfolios);
      model.addAttribute("keyword", keyword);
      return "portfolio/mylist";
    }
  }

  @RequestMapping("readableon")
  public String readableon(int number, HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    else {
      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      Portfolio portfolio = portfolioService.get(number);
      if(portfolio.getMember().getNumber() == member.getNumber()) {
        portfolioService.readableon(portfolio);
      } else
        throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    }
    return "portfolio/mylist";
  }

  @RequestMapping("readableoff")
  public String readableoff(int number, HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    else {
      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      Portfolio portfolio = portfolioService.get(number);
      if(portfolio.getMember().getNumber() == member.getNumber()) {
        portfolioService.readableoff(portfolio);
      } else
        throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    }
    return "portfolio/mylist";
  }




  @RequestMapping("turnon")
  public String turnon(int number, HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      Recommendation reco = new Recommendation();
      ((Recommendation) reco.setNumber(number)).setMember(member);

      recommendationService.toggleon(reco);
    }
    return "portfolio/detail";
  }

  @RequestMapping("turnoff")
  public String turnoff(int number, HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      Recommendation reco = new Recommendation();
      ((Recommendation) reco.setNumber(number)).setMember(member);

      recommendationService.toggleoff(reco);
    }
    return "portfolio/detail";
  }


  @RequestMapping("mylist")
  public String list(@ModelAttribute("portfolio") Portfolio portfolio,
      @RequestParam(defaultValue="1") int curPage,
      HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      Member member = memberService.getGeneralMember(((Member) mem).getNumber());

      // 전체리스트 개수
      int listCnt = portfolioService.selectMyListCnt(member.getNumber());

      Pagination pagination = new Pagination(listCnt, curPage);
      pagination.setPageSize(pageSize);// 한페이지에 노출할 게시글 수

      portfolio.setStartIndex(pagination.getStartIndex());
      portfolio.setPageSize(pagination.getPageSize());

      // 전체리스트 출력
      model.addAttribute("listCnt", listCnt);
      model.addAttribute("pagination", pagination);

      // 작성자 정보
      model.addAttribute("generalMember", member);
      portfolio.setMember((GeneralMember)member);
      List<Portfolio> portfolios = portfolioService.getByMemberNumber(portfolio);

      // 포트폴리오 스킬 붙이기
      for(Portfolio p : portfolios) {
        p.setSkill(portfolioSkillService.findAllSkill(p.getBoard().getNumber()).getSkill());
      }

      model.addAttribute("list", portfolios);
      return "portfolio/mylist";
    }
  }


  @RequestMapping("list")
  public String mylist(@ModelAttribute("portfolio") Portfolio portfolio,
      @RequestParam(defaultValue="1") int curPage,
      HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      Member member = memberService.getGeneralMember(((Member) mem).getNumber());

      // 전체리스트 개수
      int listCnt = portfolioService.selectListCnt(portfolio);

      Pagination pagination = new Pagination(listCnt, curPage);
      pagination.setPageSize(pageSize);// 한페이지에 노출할 게시글 수

      portfolio.setStartIndex(pagination.getStartIndex());
      portfolio.setPageSize(pagination.getPageSize());

      // 전체리스트 출력
      model.addAttribute("listCnt", listCnt);
      model.addAttribute("pagination", pagination);

      // 작성자 정보
      model.addAttribute("generalMember", member);
      List<Portfolio> portfolios = portfolioService.list(portfolio);

      // 포트폴리오 스킬 붙이기
      for(Portfolio p : portfolios) {
        p.setSkill(portfolioSkillService.findAllSkill(p.getBoard().getNumber()).getSkill());
      }

      model.addAttribute("list", portfolios);
      return "portfolio/list";
    }
  }


  @PostMapping("updateForm")
  public void updateForm(Portfolio portfolio, Model model) throws Exception {
    Portfolio item = portfolioService.get(portfolio.getNumber());
    model.addAttribute("portfolio", item);
  }

  @PostMapping("update")
  public String updateForm(HttpServletRequest request, Portfolio portfolio,
      @RequestParam("thumb") MultipartFile thumb,
      @RequestParam("files") MultipartFile[] files) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      Member member = memberService.getGeneralMember(((Member) mem).getNumber());

      // Board 객체 수정(pf_board)
      Board board = new Board();
      board = portfolio.getBoard();
      boardService.update(board);
      portfolio.setBoard(board); // board_no 값 다시 집어넣기

      // BoardAttachment 객체 추가(pf_board_attachment)
      String dirPath = servletContext.getRealPath("/upload/portfolio");
      for(MultipartFile file : files) {
        if (file.getSize() <= 0) {
          continue;
        } else {
          String filename = UUID.randomUUID().toString() + "___" + file.getOriginalFilename();
          String filepath = dirPath + "/" + filename;
          file.transferTo(new File(filepath));

          BoardAttachment boardAttachment = new BoardAttachment();
          boardAttachment.setBoardNumber(board.getNumber());
          boardAttachment.setFileName(filename);
          boardAttachment.setFilePath(filepath);
          boardAttachmentService.add(boardAttachment);
        }
      }
      // Portfolio 입력
      // Portfolio 입력 중에서 thumbnail 정보입력
      dirPath = servletContext.getRealPath("/upload/portfolio");
      if (thumb.getSize() > 0) {
        String filename = UUID.randomUUID().toString() + "___" + thumb.getOriginalFilename();
        String filepath = dirPath + "/" + filename;
        thumb.transferTo(new File(filepath));

        Thumbnails.of(dirPath + "/" + filename)//
        .size(300, 300)//
        .outputFormat("jpg")//
        .toFiles(new Rename() {
          @Override
          public String apply(String name, ThumbnailParameter param) {
            return name + "_300x300";
          }
        });
        portfolio.setThumbnail(filename);

        // Portfolio 입력 중에서 작성자 정보입력
        portfolio.getMember().setNumber(member.getNumber());

        portfolioService.update(portfolio);
      }

      return "redirect:mylist";
    }
  }


  @RequestMapping("detail")
  public String detail(int number, HttpServletRequest request, Model model) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      Portfolio portfolio = portfolioService.get(number);
      Board board = boardService.get(number);
      Recommendation recommendation = (Recommendation) new Recommendation().setMember(member).setBoard(board);
      Recommendation reco = recommendationService.check(recommendation);

      if(reco == null) {
        model.addAttribute("myRecommendation", 0);
      } else {
        model.addAttribute("myRecommendation", 1);
      }

      // 다운로드시, 원 파일명을 받기 위해
      List<BoardAttachment> boardAttachment = boardAttachmentService.get(number);
      for(BoardAttachment attch : boardAttachment) {
        String[] split = attch.getFileName().split("___");
        attch.setFilePath(split[split.length-1]);
      }

      model.addAttribute("membership", portfolio.getMember().getMembereship());

      // 조회할때마다 입력날짜가 바뀌지 않게 하기 위해
      board.setViewCount(board.getViewCount() + 1).setContent(null);
      boardService.update(board);

      // 포트폴리오 스킬 붙이기
      portfolio.setSkill(portfolioSkillService.findAllSkill(portfolio.getBoard().getNumber()).getSkill());

      model.addAttribute("portfolio", portfolio);
      model.addAttribute("attachment", boardAttachment);

      if(portfolio.getMember().getNumber() == member.getNumber()) {
        model.addAttribute("modifiable", true);
      }
      return "portfolio/detail";
    }
  }
  @RequestMapping("form")
  public void form() throws Exception {}

  @RequestMapping("add")
  public String add(Portfolio portfolio, HttpServletRequest request, Model model,
      @RequestParam("thumb") MultipartFile thumb,
      @RequestParam("files") MultipartFile[] files) throws Exception {

    Object mem = request.getSession().getAttribute("loginUser");

    if(mem == null)
      throw new Exception("로그인을 하신 후, 포트폴리오 목록을 볼 수 있습니다.");
    //      return "redirect:/";
    else {

      GeneralMember member = memberService.getGeneralMember(((GeneralMember) mem).getNumber());
      portfolio.setMember(member);

      // Board 객체 추가(pf_board)
      Board board = new Board();
      board = portfolio.getBoard();
      boardService.add(board);
      portfolio.setBoard(board); // board_no 값 다시 집어넣기

      // BoardAttachment 객체 추가(pf_board_attachment)
      String dirPath = servletContext.getRealPath("/upload/portfolio");
      for(MultipartFile file : files) {
        if (file.getSize() <= 0) {
          continue;
        } else {
          String filename = UUID.randomUUID().toString() + "___" + file.getOriginalFilename();
          String filepath = dirPath + "/" + filename;
          file.transferTo(new File(filepath));

          BoardAttachment boardAttachment = new BoardAttachment();
          boardAttachment.setBoardNumber(board.getNumber());
          boardAttachment.setFileName(filename);
          boardAttachment.setFilePath(filepath);
          boardAttachmentService.add(boardAttachment);
        }
      }
      // Portfolio 입력
      // Portfolio 입력 중에서 thumbnail 정보입력
      dirPath = servletContext.getRealPath("/upload/portfolio");
      if (thumb.getSize() > 0) {
        String filename = UUID.randomUUID().toString() + "___" + thumb.getOriginalFilename();
        String filepath = dirPath + "/" + filename;
        thumb.transferTo(new File(filepath));

        Thumbnails.of(dirPath + "/" + filename)//
        .size(300, 300)//
        .outputFormat("jpg")//
        .toFiles(new Rename() {
          @Override
          public String apply(String name, ThumbnailParameter param) {
            return name + "_300x300";
          }
        });
        portfolio.setThumbnail(filename);

        // Portfolio 입력 중에서 작성자 정보입력
        portfolio.getMember().setNumber(member.getNumber());
      }

      portfolioService.insert(portfolio);
      return "redirect:mylist";
    }
  }

  @RequestMapping("delete")
  public String delete(int number) throws Exception {
    try {
      portfolioService.delete(number);
      boardAttachmentService.delete(number);
      boardService.delete(number);
      return "redirect:mylist";
    } catch(Exception e) {
      throw new Exception("삭제중 오류발생");
    }
  }



}