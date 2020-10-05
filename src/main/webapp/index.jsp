<html>
<head>
<link rel="icon" href="logo.jpg" type="image/icon type">
<title style="color:#003d99">Test Data Generator</title>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />

<script>
	function openPage() {
		var userId = document.getElementById("uname");
		var password = document.getElementById("pass");
		
if(userId.value=="admin" && password.value=="admin"){
            // fetch the session from request, create new session if session
            // is not present in the request
           
            window.location.replace("config.jsp");
}else{ alert("Please input a valid username and password");
}	}
function logOut()
				{
				            window.location.replace("index.jsp");
				
				}
</script>

</head>
<body>
	<br>
	<center>

		<center>
			<h1 style="color:#003d99">Test Data Generator Utility Configuration</h1>
		</center>
		
		<form method="post" style="color:#003d99"   action="login"  enctype="multipart/form-data">
			<label for="uname"><b>Username:</b></label> 
			<input type="text" id="uname" name="uname" /> <br><br>
			<label for="pass"><b>Password:</b></label> 
			<input type="password" id="pass" name="pass" /> <br><br>

			  <input type="button" value="Login"
				onClick="openPage()">
		</form>

	</center>
	<footer>
		<img src="gtf.PNG" style="float: right;"width="95" height="22" />
	</footer>
</body>
</html>
