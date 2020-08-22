<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="logo.jpg" type="image/icon type">
<title style="color:#003d99">Test Data Generator</title>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />
&ensp; <input type="button" value="Home"
				onClick="location.href='index.jsp'">
</head>
<body >

	<center>
      <br>
       
		<form method="post" style="color:#003d99" action="uploadFile" enctype="multipart/form-data">
            &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="file"><b>Select File:</b></label> 
			<input type="file" id="file" name="uploadFile" /> <br><br>
			<label for="loc"><b>Save File To:</b></label> 
			<input type="text" id="loc" name="loc" /> <br><br>
			<label for="outputfileName"><b>Save File As:</b></label> 
			<input type="text" id="outputfileName" name="outputfileName" /> <br><br>
			&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="type"><b>File Type:</b></label> 
		     <input name="type" id="type" type="text" value="<%=request.getParameter("option")%>"readonly/>	
			<input type="submit" value="Generate Test-Data" />
		</form>
		
		
	</center>
	
	
	<footer>
  <img src="gtf.PNG" style="float: right;" width="95" height="22" />
</footer>

</body>

</html>