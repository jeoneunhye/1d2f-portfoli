<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  <jsp:include page="../header.jsp"/>

			<!-- PAGE TITLE -->
			<section class="bg-white">
				<div class="container py-1">

					<h1 class="h2">
						고객센터
					</h1>

					<nav aria-label="breadcrumb">
						<ol class="breadcrumb fs--14">
							<li class="breadcrumb-item"><a href="#!">portfoli</a></li>
							<li class="breadcrumb-item active" aria-current="page">고객센터</li>
						</ol>
					</nav>

				</div>
			</section>
			<!-- /PAGE TITLE -->


			<!-- FAQ -->
      <section>
        <div class="container">

          <div class="row">

          <%--왼쪽 noticebar부분 --%>
          <jsp:include page="sidebar.jsp" />
						
						<div class="col-12 col-lg-8">

							 <form action="add" method="post" enctype="multipart/form-data">
                <table border='1' style='width:100%'>

                <tr>
                 <td align="center">유형(no)</td>
                 <td style="height:50px">
                 
                 
              <select name="noticeNumber" style="height: 30px; width: 100%;">
              <c:forEach items="${list}" var="item">
                  <option value="${item.getNoticeNumber()}">[${item.getNoticeNumber()}]${item.getName()}</option>
              </c:forEach>
              </select>                
                 
                 </td>
                </tr>
                
                <tr>
                 <td align="center">제목</td>
                 <td style="height:50px"><textarea rows="1" name='title' style='resize: none; width:100%;'></textarea></td>
                </tr>
                
                <tr height="500">
                <td align="center">내용</td>
                <td style="height:300px"><textarea name='content' style="resize: none; width:100%; height:100%;"></textarea></td>
                </tr>
                
                <tr>
                  <td colspan="2" align="center">
                  <input type="file" name="attachment" />
                  </td>
                </tr>
                </table>
                <button>글쓰기</button>
                </form>
                

							</div>

						</div>
				  </div>


			</section>
			<!-- /FAQ -->



		  <jsp:include page="../footer.jsp"/>