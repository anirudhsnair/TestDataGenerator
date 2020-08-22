<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="logo.jpg" type="image/icon type">
<title style="color:#003d99">Test Data Generator</title>

</head>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />
&ensp; <input type="button" value="Home"
				onClick="location.href='index.jsp'">
			
<body  >
	<center>
 
		<form method="post" style="color:#003d99" action="dbTofile">

			&ensp;<label for="uname"><b>Database Username:</b></label>
			 <input type="text" id="uname" name="username"><br> <br> 
			 <label for="pass"><b>Database Password:</b></label> 
			 <input type="password" id="pass" name="password"><br> <br> 
			 &ensp;&ensp;&ensp;&ensp;<label for="query"><b>Database Query:</b></label> 
			 <input type="text" id="query" name="query"><br> <br> 
			 &ensp;&ensp;&ensp;&ensp;&ensp;<label for="url"><b>Database URL:</b></label> 
			 <input type="text" id="url" name="url"><br> <br>
			 &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="type"><b>File Type:</b></label> 			 
		     <input name="type" id="type" type="text" value="<%=request.getParameter("option")%>"readonly/><br><br>
		     &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="loc"><b>Save File To:</b></label> 
			<input type="text" id="loc" name="loc" /> <br><br>	
			&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="fileName"><b>Save File As:</b></label> 
			<input type="text" id="fileName" name="fileName" /> <br><br>				 
			<input type="submit" value="Generate Test-Data " size="20"
				color="blue" />
				
		</form>
	</center>
	<footer>
  <img src="gtf.PNG" style="float: right;" width="95" height="22" />
</footer>
</body>
</html>