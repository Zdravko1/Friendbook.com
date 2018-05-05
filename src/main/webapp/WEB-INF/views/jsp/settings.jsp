<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>User Settings</title>
<meta charset="UTF-8">
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

<!-- Page Container -->
<div class="w3-container w3-content" style="max-width:1400px;margin-top:80px">    
  <!-- The Grid -->
  <div class="w3-row">
    <!-- Left Column -->
    <div class="w3-col m3">
      <!-- Profile -->
      <div class="w3-card w3-round w3-white" style= "display: none">
      </div>
      <br>
    <!-- End Left Column -->
    </div>
    <!-- Middle Column -->
    <div class="w3-col m7">
    
      <div class="w3-row-padding" style= "display: none">
      </div>
      <div class="w3-container w3-card w3-white w3-round w3-margin"><br>
        <!-- -=============POST IMAGE================- -->
          <div class="w3-row-padding" style="margin:0 -16px">
	      </div>
	      <h1 style="text-align: center">Settings</h1>
	      <c:if test="${ message != null }">
	      	<h3 style="color: red; text-align: center">${ message }</h3>
	      </c:if>
	      <form action="edit" method="post">
		      <table align="center">
		      	<tr>
	              	<td>Username:</td>
	              	<td><input type="text" class="w3-border w3-padding" name="username" value="${ sessionScope.user.getUsername() }" ></td>
	            </tr>
	            <tr>
	              	<td>Password:</td>
	              	<td><input type="password" class="w3-border w3-padding" name="password" value="" required></td>
	            </tr>
	            <tr>
	              	<td>Confirm Password:</td>
	              	<td><input type="password" class="w3-border w3-padding" name="confirm_password" value="" required></td>
	            </tr>
	            <tr>
	              	<td>Email:</td>
	              	<td><input type="email" class="w3-border w3-padding" name="email" value="${ sessionScope.user.getEmail() }" ></td>
	            </tr>
	            <tr>
	              	<td>First Name:</td>
	              	<td><input type="text" class="w3-border w3-padding" name="first_name" value="${ sessionScope.user.getFirstName() }" ></td>
	            </tr>
	            <tr>
	              	<td>Last Name:</td>
	              	<td><input type="text" class="w3-border w3-padding" name="last_name" value="${ sessionScope.user.getLastName() }" ></td>
	            </tr>
	           </table>
	           <button style="margin:auto;display:block" type="submit" class="w3-button w3-theme">Save Changes</button><br>
	      </form>
       </div> 
    <!-- End Middle Column -->
    </div>
    
  <!-- End Grid -->
  </div>
  
<!-- End Page Container -->
</div>
</body>
</html>