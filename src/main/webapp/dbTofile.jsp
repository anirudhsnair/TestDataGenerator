<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="logo.jpg" type="image/icon type">
<title>Test Data Convertor</title>
</head>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />
&ensp; <input type="button" value="Home"
				onClick="location.href='index.jsp'">
<body>
	<center>

		<form method="post" action="dbTofile">

			<label for="uname">Database Username:</label>
			 <input type="text" id="uname" name="username"><br> <br> 
			 <label for="pass">Database Password:</label> 
			 <input type="password" id="pass" name="password"><br> <br> 
			 <label for="query">Database Query:</label> 
			 <input type="text" id="query" name="query"><br> <br> 
			 <label for="url">Database URL:</label> 
			 <input type="text" id="url" name="url"><br> <br>
			 <label for="type">File Type:</label> 
		     <input name="type" id="type" type="text" value="<%=request.getParameter("option")%>"/>				 
			<input type="submit" value="Generate Test-Data " size="20"
				color="blue" />
		</form>
	</center>
	<footer>
  <img src="gtf.PNG" style="float: right;" width="95" height="22" />
</footer>
</body>
</html>