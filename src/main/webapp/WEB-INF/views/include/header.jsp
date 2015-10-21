<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<head>
	<title>ITA Dražba</title>

	<!-- CSS -->
	<link href="${appRoot}/resources/css/style.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="${appRoot}/resources/css/jquery-ui.css" rel="stylesheet" type="text/css" media="screen" />
	<link href="${appRoot}/resources/css/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" type="text/css" media="screen" />
	
	<!-- JavaScripts-->
	<script type="text/javascript" src="${appRoot}/resources/javascript/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="${appRoot}/resources/javascript/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="${appRoot}/resources/javascript/jquery-ui-sl.js"></script>
	<script type="text/javascript" src="${appRoot}/resources/javascript/jquery-ui-timepicker-addon.js"></script>
	<script type="text/javascript" src="${appRoot}/resources/javascript/moment.min.js"></script>
	<script type="text/javascript" src="${appRoot}/resources/javascript/moment.sl.js"></script>
</head>
<body>
	<div class="wrapper">
		<div class="main">
			<a href="${appRoot}">
				<div class="header">
					<span class="header-title">ITA Dražba</span>
				</div>
			</a>
			
			<div class="mid">
				<div class="mid-left">
					<c:choose>
						<c:when test="${loggedUser ne null}">
							<h2 class="gap-2">Pozdravljen ${loggedUser.firstName}!</h2>
							<ul class="left-nav">
								<li><a href="${appRoot}/auction/new">Dodaj izdelek na dražbo</a></li>
								<li><a href="${appRoot}/logout">Odjava</a></li>
							</ul>
						</c:when>
						<c:otherwise>
							<h2 class="gap-2">Prijava</h2>
							<form action="${appRoot}/login" method="POST">
								<input type="text" name="email" placeholder="E-pošta" /><br/>
								<input type="password" name="password" placeholder="Geslo" /><br/>
								<input type="submit" value="Prijava" />
							</form>
						</c:otherwise>
					</c:choose>
					<h2 class="gap-2">Menu</h2>
					<ul class="left-nav">
						<li><a href="${appRoot}">Domov</a></li>
					</ul>
				</div>