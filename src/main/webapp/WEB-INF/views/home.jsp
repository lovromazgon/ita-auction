<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<jsp:include page="include/header.jsp" />
	<script type="text/javascript">
	$(document).ready(function() {
		$("form.filterForm select").change(function() {
		    $(this).closest('form').submit();
		});
	});
	</script>
	<div class="mid-right">
		<div class="auctions-filter">
			<form method="get" class="filterForm">
				Kategorija:
				<select name="category">
					<c:forEach items="${auctionCategories}" var="category">
						<option value="${category.id}" <c:if test="${category.id eq selectedCategory}">selected="selected"</c:if>>${category.name}</option>
					</c:forEach>
				</select>
			</form>
		</div>
		<c:forEach items="${auctions}" var="auction">
			<a href="${auction.selfUrl}">
				<div class="auction">
					<img class="auction-image" src="${auction.pictureUrl}" />
					<table cellpadding="0" cellspacing="0" border="0" align="left" class="auction-info">
						<tbody>
							<tr>
								<td colspan="2"><h2>${auction.title}</h2></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td>Izklicna cena:</td>
								<td>${auction.startingPriceFormatted} €</td>
							</tr>
							<tr>
								<td>Trenutna cena:</td>
								<td>${auction.currentPriceFormatted} €</td>
							</tr>
							<tr>
								<td>Čas do konca dražbe:</td>
								<td>${auction.timeToEndString}</td>
							</tr>
						</tbody>
					</table>
					<br class="clear" />
				</div>
			</a>
		</c:forEach>
	</div>
	<jsp:include page="include/footer.jsp" />
</html>