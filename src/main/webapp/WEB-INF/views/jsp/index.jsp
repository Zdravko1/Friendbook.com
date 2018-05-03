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
  <a  href="reloadPosts" class="w3-bar-item w3-button w3-padding-large w3-theme-d4"><i class="fa fa-home w3-margin-right" title="Home"></i>Home</a>
  <a href="reloadFeed" class="w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white" title="Feed"><i class="fa fa-globe"></i></a>
  <a href="logout" class="w3-bar-item w3-button w3-padding-large w3-right w3-theme-d4">Log Out</a>
  
  <form action="search" method="post">
  	<button type="submit" class="w3-bar-item w3-button w3-padding-large w3-right w3-theme-d4">Search</button>
    <input type="text" id="search" name="user" class="w3-bar-item w3-button w3-padding-large w3-right w3-theme-d4" required>
  </form>
    
 </div>
</div>

<!-- Navbar on small screens -->
<div id="navDemo" class="w3-bar-block w3-theme-d2 w3-hide w3-hide-large w3-hide-medium w3-large">
  <a href="#" class="w3-bar-item w3-button w3-padding-large">Link 1</a>
  <a href="#" class="w3-bar-item w3-button w3-padding-large">Link 2</a>
  <a href="#" class="w3-bar-item w3-button w3-padding-large">Link 3</a>
  <a href="#" class="w3-bar-item w3-button w3-padding-large">Profile</a>
</div>

<!-- Page Container -->
<div class="w3-container w3-content" style="max-width:1400px;margin-top:80px">    
  <!-- The Grid -->
  <div class="w3-row">
    <!-- Left Column -->
    <div class="w3-col m3">
      <!-- Profile -->
      <div class="w3-card w3-round w3-white">
        <div class="w3-container">
        <h4 class="w3-center">${ !visit ? "MyProfile" : "Profile" }
        <h4 class="w3-center">${ !visit ? sessionScope.user : visitedUser }
        <br>
         <c:if test="${ visit }">
         	<button id="follow" name="followedId" onclick="follow()" value="${ visitedUser.getId() }" class="w3-button w3-theme">${ visitedUser.isFollowed() ? "Followed" : "Follow" }</button>
         </c:if>
        </div>
      </div>
      <br>

    <!-- End Left Column -->
    </div>
    
    <!-- Middle Column -->
    <div id="middleColumnId" class="w3-col m7">
    
      <div class="w3-row-padding">
        <div class="w3-col m12">
          <c:if test="${ !(visit || onFeed) }">
	          <div class="w3-card w3-round w3-white" >
	            <div class="w3-container w3-padding">
	              <h6 class="w3-opacity">Post something</h6>
	              <form action="post" method="post" enctype="multipart/form-data">
	              	<input id="post" contenteditable="true" class="w3-border w3-padding" name="text" required>
					<input id="fileId" type="file" name="file" size="50" />
					<br>
	              	 <br>
	              	 <br>
	              	 <input onclick="addPost()" class="w3-button w3-theme" type="button" value="Post">
	              </form>
	            </div>
	          </div>
          </c:if>
          <form method="post" action="order">
	          <select name="order">
	          	<option value="likes">Likes</option>
	          	<option value="date">Date</option>
	          </select>
	          <input type="hidden" name="feed" value="${ feed }">
	          <input type="hidden" name="visit" value="${ visit }">
	          <input type="hidden" name="visitedUserId" value="${ visitedUser.getId() }">
	          <button type="submit">Order</button>
          </form>
        </div>
      </div>
      <c:forEach var="post" items="${ posts }">
	      <div id="postId${ post.getId() }" class="w3-container w3-card w3-white w3-round w3-margin"><br>
	        <h4>${ post.getUser() }</h4><br>
	        <!-- -=============POST IMAGE================- -->
	        <img src="getPic?postId=${ post.getId() }" class="w3-left w3-margin-right" height="50%" width="50%" alt=""> 
	        <hr class="w3-clear">
	        <p>${ post.getText() }</p>
	          <div class="w3-row-padding" style="margin:0 -16px">
		      </div>
		   <form method="post" action="likePost">
		      <input type="hidden" id="like${ post.getId() }" value="${ post.getId() }">
		      <input onclick="likePosts(${ post.getId() })" type="button"class="w3-button w3-theme-d1 w3-margin-bottom" class="fa fa-thumbs-up" value="Like">
		      <p id="likeID${ post.getId() }">${ post.getLikes() }</p>
	      </form>
	      <div id="commentID">
      		  <form action="comment" method="post">
              	 <input id="postCommentID${ post.getId() }" contenteditable="true" class="w3-border w3-padding" name="text" required>
              	  <input type="hidden" name="currentPost" value="${ post.getId() }">
              	 <br>
              	 <input type="button" class="w3-button w3-theme" value="Comment" onclick="addComment('postCommentID', ${ post.getId() }, ${ post.getId() })"> 
              </form>
              </div>
          
			      <c:forEach var="comment" items="${ post.getComments() }">
				      <div id="commentID${ comment.getId() }" class="w3-container w3-card w3-white w3-round w3-margin"><br>
				        <h3>${ comment.getUser() }</h3><br>
				        
				        <p>${ comment.getText() }</p>
				      
			        <form method="post" action="likeComment">
				      <input type="hidden" id="likeComment${ comment.getId() }" value="${ comment.getId() }">
				      <input onclick="likeComments(${ comment.getId() })" type="button" class="w3-button w3-theme-d1 w3-margin-bottom" value="Like">
				       <p id="likeCommentID${ comment.getId() }">${ comment.getLikes() }</p>
			     </form>
		      		  <form action="comment" method="post">
		              	 <input id="commentCommentID${ comment.getId() }" contenteditable="true" class="w3-border w3-padding" name="text" required>
		              	  <input type="hidden" name="currentPost" value="${ post.getId() }">
		              	 <br>
		              	 <input type="button" class="w3-button w3-theme" value="Comment" onclick="addComment('commentCommentID', ${ post.getId() }, ${ comment.getId() }, ${ comment.getId() })"> 
		              </form>
		               
					    <c:forEach var="childComment" items="${ comment.getComments() }">
						      <div class="w3-container w3-card w3-white w3-round w3-margin">
						        <h4>${ childComment.getUser() }</h4>
						        <p>${ childComment.getText() }</p>
						         <form method="post" action="likeComment">
							      <input type="hidden" id="likeComment${ childComment.getId() }" value="${ childComment.getId() }">
							      <input onclick="likeComments(${ childComment.getId() })" type="button" class="w3-button w3-theme-d1 w3-margin-bottom" value="Like">
							      <p id="likeCommentID${ childComment.getId() }">${ childComment.getLikes() }</p>
						      </form>
							  </div>
						</c:forEach>
						</div>
			 	  </c:forEach>
			 	  </div>
          </c:forEach>
    <!-- End Middle Column -->
    </div>
    
  <!-- End Grid -->
  </div>
  
<!-- End Page Container -->
</div>
<br>

<!-- Footer -->
<footer class="w3-container w3-theme-d3 w3-padding-16">
  <h5>Footer</h5>
</footer>
 
<script>
//to help with asynchronous adding of elements
var likeCommentId =  -1;
var comment_id =  -1;
var like_id =  -1;
//addComment
function addComment(nameId, postId, id, currentCommentId){
	var p = document.getElementById('postId'+id);
	var commentElement = document.getElementById('commentID' + id);
	var newElement = document.createElement("div");
	newElement.setAttribute('id', 'commentID' + likeCommentId);
	newElement.setAttribute('class', 'w3-container w3-card w3-white w3-round w3-margin');
	
	var currentComment = currentCommentId;
	if (typeof currentComment === 'undefined') {
		currentComment = null;
	}
	var request = new XMLHttpRequest();
	request.open('POST', 'comment', true);
	request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	request.send("text=" + document.getElementById(nameId+id).value + "&currentPost=" + postId + "&currentComment=" + currentComment);
	request.onreadystatechange = function(){
		if(this.readyState == 4 && this.status == 200){
			var result = this.responseText;
			result = JSON.parse(result);
			
			var user = result.user.firstName + ' ' + result.user.lastName;
			var text = result.text;
			var idComment = result.id;
			var likes = result.likes;
			var commentId = likeCommentId--; 
			if(currentComment == null){
				newElement.innerHTML = " <br><span class=\"w3-right w3-opacity\">0</span><h4>"+user+"</h4><p>"+text+"</p><form method=\"post\" action=\"likeComment\"><input type=\"hidden\" id=\"likeComment"+commentId+"\" value=\""+idComment+"\"><input onclick=\"likeComments("+commentId+")\" type=\"button\" class=\"w3-button w3-theme-d1 w3-margin-bottom\" value=\"Like\"><p id=\"likeCommentID"+commentId+"\">"+likes+"</p></form><form action=\"comment\" method=\"post\"><input id=\"commentCommentID"+commentId+"\" contenteditable=\"true\" class=\"w3-border w3-padding\" name=\"text\" required><input type=\"hidden\" name=\"currentPost\" value=\""+postId+"\"><br><input type=\"button\" class=\"w3-button w3-theme\" value=\"Comment\" onclick=\"addComment('commentCommentID', "+postId+", "+commentId+", "+idComment+")\"></form>";
				p.appendChild(newElement);//, p.lastChild.previousSibling);				
			} else {
				newElement.innerHTML = " <br><span class=\"w3-right w3-opacity\">0</span><h4>"+user+"</h4><p>"+text+"</p><form method=\"post\" action=\"likeComment\"><input type=\"hidden\" id=\"likeComment"+commentId+"\" value=\""+idComment+"\"><input onclick=\"likeComments("+commentId+")\" type=\"button\" class=\"w3-button w3-theme-d1 w3-margin-bottom\" value=\"Like\"><p id=\"likeCommentID"+commentId+"\">"+likes+"</p></form>";
				commentElement.appendChild(newElement);
			}
		}
	}
}
//follow
function follow() {
	var element = document.getElementById("follow");
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
//addPost
function addPost(){
	var flag = true;
	var p = document.getElementById('middleColumnId');
    var newElement = document.createElement("div");
    newElement.setAttribute('id', 'postId'+like_id+'');
    newElement.setAttribute('class', 'w3-container w3-card w3-white w3-round w3-margin');
    
    var file = document.getElementById('fileId').files[0];
    var data = 'text=' + document.getElementById('post').value;
    
    function getBase64(file) {
    	   var reader = new FileReader();
    	   if(file != null){
    	   	reader.readAsDataURL(file);
    	   }
    	   reader.onload = function () {
    		   data += '&file=' + reader.result;
    		   send(data);
    	   };
    	   reader.onerror = function (error) {
    	     console.log('Error: ', error);
    	   };
    	}

    getBase64(file);
    if(file == null){
    	send(data);
	}
    
     function send(data){
	    var request = new XMLHttpRequest();
		request.open('POST', "post", true);
		request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
		request.send(data);
		request.onreadystatechange = function(){
			if(this.readyState == 4 && this.status == 200){
				var result = this.responseText;
				result = JSON.parse(result);
				var user = result.user.firstName + ' ' + result.user.lastName;
			    var imagePath = result.imagePath;
			    var text = result.text;
			    var postId = result.id;
			    var likes = result.likes;
			    var likeId = like_id--;
			    var commentId = comment_id--;
			    
			    var imageElement = "<img src=\"getPic?postId="+postId+"\" class=\"w3-left w3-margin-right\" height=\"50%\" width=\"50%\" alt=\"\">";			    
			    newElement.innerHTML = "<br><span class=\"w3-right w3-opacity\">0</span><h4>"+user+"</h4><br>"+imageElement+"<hr class=\"w3-clear\"><p>"+text+"</p><div class=\"w3-row-padding\" style=\"margin:0 -16px\"></div><form method=\"post\" action=\"likePost\"><input type=\"hidden\" id=\"like"+likeId+"\" value=\""+postId+"\"><input onclick=\"likePosts("+likeId+")\" type=\"button\"class=\"w3-button w3-theme-d1 w3-margin-bottom\" class=\"fa fa-thumbs-up\" value=\"Like\"><p id=\"likeID"+likeId+"\">"+likes+"</p></form></div><form action=\"comment\" method=\"post\"><input id=\"postCommentID"+likeId+"\" contenteditable=\"true\" class=\"w3-border w3-padding\" name=\"text\" required><input type=\"hidden\" name=\"currentPost\" value=\""+postId+"\"><br><input type=\"button\" class=\"w3-button w3-theme\" value=\"Comment\" onclick=\"addComment('postCommentID', "+postId+", "+likeId+")\"></form>";
			    console.log(newElement);
			    p.insertBefore(newElement, p.childNodes[2]);
			}
		}
    }
}
//likeComment
function likeComments(a){
	var like = document.getElementById("likeCommentID"+a);
	var request = new XMLHttpRequest();
	request.open('POST', "likeComment", true);
	request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	request.send("like=" + document.getElementById("likeComment"+a).value);
	request.onreadystatechange = function(){
		if(this.readyState == 4 && this.status == 200){
			var result = this.responseText;
			result = JSON.parse(result);
			like.innerHTML = result;
		}
	}
}
//likePost
function likePosts(a){
	var like = document.getElementById("likeID"+a);
	var request = new XMLHttpRequest();
	request.open('POST', "likePost", true);
	request.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	request.send("like=" + document.getElementById("like"+a).value);
	request.onreadystatechange = function(){
		if(this.readyState == 4 && this.status == 200){
			var result = this.responseText;
			result = JSON.parse(result);
			like.innerHTML = result;
		}
	}
}

//search
$(document).ready(function() {
    $(function() {
        $("#search").autocomplete({     
            source : function(request, response) {
              $.ajax({
                   url : "searchAutoComplete",
                   type : "GET",
                   data : {
                          term : request.term
                   },
                   dataType : "json",
                   success : function(data) {
                         response(data);
                   }
            });
         }
     });
  });
})
</script>

</body>
</html>