<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<jsp:include page="include/header.jsp" />
	<script type="text/javascript">
	$(document).ready(function() {
	});
	</script>
	<div class="mid-right">
		<h2>${auction.title}</h2>
		<hr />
		<img class="auction-image" src="${auction.pictureUrl}" />
		<table cellpadding="0" cellspacing="0" border="0" align="left" class="auction-info">
			<tbody>
				<tr>
					<td>Prodajalec:</td>
					<td>${auction.seller.firstName} ${auction.seller.lastName}</td>
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
				<tr>
					<td>Minimalna cena:</td>
					<c:choose>
						<c:when test="${auction.minPriceReached}">
							<td style="color: green;">dosežena</td>
						</c:when>
						<c:otherwise>
							<td style="color: red;">ni dosežena</td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<td>Opis:</td>
					<td>${auction.description}</td>
				</tr>
				<c:if test="${auction.active and auction.seller.id ne loggedUser.id}">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<c:choose>
							<c:when test="${loggedUser ne null}">
								<form method="post">
									<td><input type="text" name="amount" value="${bidAmount}" size="5" /> €</td>
									<td><input type="submit" value="Oddaj pondubo" /></td>
								</form>
							</c:when>
							<c:otherwise>
								<td colspan="2"><em>Za oddajo ponudbe se morate prijaviti.</em></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<c:if test="${loggedUser ne null and auction.lastBid ne null and loggedUser.isSameAs(auction.lastBid.bidder) and auction.hiddenBid ne null}">
						<tr>
							<td>Skrita ponudba:</td>
							<td>${auction.hiddenBid.amountFormatted} €</td>
						</tr>
					</c:if>
				</c:if>
				<c:if test="${error ne null}">
					<tr>
						<td style="color: red;" colspan="2">${error}</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		<br class="clear" />
		<table id="rounded-corner" summary="Zgodovina ponudb">
			<thead>
				<tr>
					<th scope="col">Ponudnik</th>
					<th scope="col">Čas</th>
					<th scope="col">Znesek</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td><em>Začetek dražbe</em></td>
					<td>${auction.startTimeFormatted}</td>
					<td>${auction.startingPriceFormatted} €</td>
				</tr>
			</tfoot>
			<tbody>
				<c:forEach items="${auction.bidHistory}" var="bid">
					<tr>
						<td>${bid.bidder.firstName} ${bid.bidder.lastName}</td>
						<td>${bid.timeFormatted}</td>
						<td>${bid.amountFormatted} €</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<jsp:include page="include/footer.jsp" />
</html>