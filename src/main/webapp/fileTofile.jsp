<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="logo.jpg" type="image/icon type">
<title>Test-Data Generator</title>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />
&ensp; <input type="button" value="Home"
				onClick="location.href='index.jsp'">
</head>
<body>

	<center>
      <br>
		<form method="post" action="uploadFile" enctype="multipart/form-data">
            <label for="file">Select File:</label> 
			<input type="file" id="file" name="uploadFile" "/> <br /> <br />
			<label for="type">File Type:</label> 
		     <input name="type" id="type" type="text" value="<%=request.getParameter("option")%>"/>	
			<input type="submit" value="Generate Test-Data" />
		</form>
	</center>
	
	
	<footer>
  <img src="gtf.PNG" style="float: right;" width="95" height="22" />
</footer>

</body>

</html>