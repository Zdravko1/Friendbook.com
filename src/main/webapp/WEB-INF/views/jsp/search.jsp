<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>Friendbook</title>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-blue-grey.css">
<link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Open+Sans'>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<!-- for search function -->
<head>
	<meta charset="ISO-8859-1">
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
	<link rel="stylesheet" 
	  href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
	<!-- ================ -->
</head>
<style>
html,body,h1,h2,h3,h4,h5 {font-family: "Open Sans", sans-serif}
</style>
<body class="w3-theme-l5">


<!-- Navbar -->
<div class="w3-top">
 <div class="w3-bar w3-theme-d2 w3-left-align w3-large">
  <a class="w3-bar-item w3-button w3-hide-medium w3-hide-large w3-right w3-padding-large w3-hover-white w3-large w3-theme-d2" href="javascript:void(0);" onclick="openNav()"><i class="fa fa-bars"></i></a>
  <a  href="." class="w3-bar-item w3-button w3-padding-large w3-theme-d4"><i class="fa fa-home w3-margin-right" title="Home"></i>Home</a>
  <a href="feed" class="w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white" title="Feed"><i class="fa fa-globe"></i></a>
  <a href="logout" class="w3-bar-item w3-button w3-padding-large w3-right w3-theme-d4">Log Out</a>
  
  <form action="search" method="post">
  	<button type="submit" class="w3-bar-item w3-button w3-padding-large w3-right w3-theme-d4">Search</button>
    <input type="text" id="search" name="user" class="w3-bar-item w3-button w3-padding-large w3-right w3-theme-d4" required>
  </form>
    
 </div>
</div>

<!-- Page Container -->
<div class="w3-container w3-content" style="max-width:1400px;margin-top:80px">    
  <!-- The Grid -->
  <div class="w3-row">
    <!-- Left Column -->
    <div class="w3-col m3">
      <!-- Profile -->
      <c:forEach var="user" items="${ users }">
      <div class="w3-card w3-round w3-white">
        <div class="w3-container">
        <h4 class="w3-center">Profile</h4>
        <h4 class="w3-center">${ user }</h4>
        <br>
         	<button id="follow${ user.getId() }" name="followedId" onclick="follow(${ user.getId() })" value="${ user.getId() }" class="w3-button w3-theme">${ user.isFollowed() ? "Followed" : "Follow" }</button>
         	<form method="GET" action="search">
         		<button type="submit" name="userId" value="${ user.getId() }" class="w3-button w3-theme">Profile</button>
         	</form>
        </div>
      </div>
      <br>
      </c:forEach>

    <!-- End Left Column -->
    </div>
    
    <!-- Middle Column -->
    <div id="middleColumnId" class="w3-col m7">
    
      <div class="w3-row-padding">
        <div class="w3-col m12">
	          <div class="w3-card w3-round w3-white" >
	          </div>
        </div>
      </div>
  <!-- End Grid -->
  </div>
  
<!-- End Page Container -->
</div>
</div>
<br>

<!-- Footer -->
<footer class="w3-container w3-theme-d3 w3-padding-16">
  <h5>Footer</h5>
</footer>
 
<script>
//follow
function follow(userId) {
	var element = document.getElementById("follow" + userId);
	var value = element.value;
	var request = new XMLHttpRequest();
	request.open("POST","follow", true);
	request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	request.send("followedId=" +value);
	request.onreadystatechange=function() {
	    if (this.readyState == 4 && this.status == 200) {
	    	var result = this.responseText;
	    	result = JSON.parse(result);
	      	element.innerHTML = result;
	    }
	  }
}
</script>
</body>
</html>