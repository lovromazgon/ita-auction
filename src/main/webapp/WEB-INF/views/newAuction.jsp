<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<jsp:include page="include/header.jsp" />
	<div class="mid-right">
		<c:if test="${error ne null}">
			<div class="error-message">
				${error}
			</div>
		</c:if>
		<h2>Nova dražba</h2>
		<hr />
		<form:form commandName="auction" method="post" enctype="multipart/form-data">
			<table cellpadding="0" cellspacing="0" border="0" align="left" class="auction-new">
				<tbody>
					<tr>
						<td>Naziv:</td>
						<td><form:input path="title" type="text" /></td>
					</tr>
					<tr>
						<td>Izklicna cena:</td>
						<td><form:input path="startingPriceString" type="text" />€</td>
					</tr>
					<tr>
						<td>Minimalna cena:</td>
						<td><form:input path="minPriceString" type="text" />€</td>
					</tr>
					<tr>
						<td>Trajanje dražbe (dnevi):</td>
						<td>
						<form:select path="daysToEnd">
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
							<form:option value="5">5</form:option>
							<form:option value="6">6</form:option>
							<form:option value="7">7</form:option>
						</form:select>
						</td>
					</tr>
					<tr>
						<td>Kategorija predmeta:</td>
						<td><form:select path="categoryId" items="${auctionCategories}" /></td>
					</tr>
					<tr>
						<td>Opis:</td>
						<td><form:textarea path="description"></form:textarea></td>
					</tr>
					<tr>
						<td>Slika:</td>
						<td><form:input type="file" path="fileData" /></td>
					</tr>
				</tbody>
			</table>
			<br class="clear" />
			<input type="submit" value="Ustvari" />
		</form:form>
	</div>
	<jsp:include page="include/footer.jsp" />
</html>