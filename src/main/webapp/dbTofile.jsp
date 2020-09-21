<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="logo.jpg" type="image/icon type">
<title style="color:#003d99">Test Data Generator</title>
</head>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />
<img src="logOut.png" width="80" height="20" style="float: right; " onClick="logOut()" />
			
<body  >
		<script type="text/javascript">
			
				function dbTofileformValidation()
				{
						var uname = document.getElementById("uname");	
           				var pwd = document.getElementById("pass");						
           				var query = document.getElementById("query");
           				var url = document.getElementById("url");		
           				var loc = document.getElementById("loc");	
           				var fileName = document.getElementById("fileName");						
           									
        if(uname.value=="") 
        { 
            alert("Please enter username:"); 
        return false;

        }  
        else if(pwd.value=="") 
        { 
            alert("Please enter password:"); 
        return false;

        }
        else if(query.value=="") 
        { 
            alert("Please enter the query:"); 
        return false;

        }
       
        else if(url.value=="") 
        { 
            alert("Please enter the url:"); 
        return false;

        }
        else if(loc.value=="") 
        {
        alert("Please input the location to be saved");
        return false;

        }
        else if(fileName.value=="") 
        { 
            alert("Please input the name of the file");
        return false;

        }
        
				return true;
				}
				
				function logOut()
				{
window.location.replace("index.jsp");
session.invalidate();
				            
				}
				
				function queryValidation()
				{
					var input;
					var fileNames = document.getElementById("fileName");
       				var query = document.getElementById("query").value;
			        var qry = query.split(";");
		            var i;	
		            var chk=document.getElementById("check").disabled;
		            if(chk==false)
		            	{
						for (i=1; i < qry.length ;i++)
							
						{	
						    input = document.createElement('input');
						    input.id="fileName"+i;
						    input.name="fileName"+i;
						    input.setAttribute('type', 'text');
						    fileNames.appendChild(input);
						}
				}
	    	
				}
				
				function checkBoxValidation(obj)
				{
					var query = obj.value;
		            var qry = query.split(";");
		            if(qry.length>1)
                       {
		            	document.getElementById("check").disabled = false;
		            	}
				}
				</script>
<center>
			<h1 style="color:#003d99">Test Data generator Utility </h1>
		</center>
	<center>
 
		<form method="post" style="color:#003d99" action="dbTofile" onsubmit="return dbTofileformValidation()">
		     <label for="uname"><b>Database Username:</b></label>
			 <input type="text" id="uname" name="username"><br> <br> 
			 <label for="pass"><b>Database Password:</b></label> 
			 <input type="password" id="pass" name="password"><br> <br> 
			 &ensp;&ensp;&ensp;&ensp;<label for="query"><b>Database Query:</b></label> 
			 <input type="text" id="query" name="query" value="<%=request.getSession().getAttribute("Query")%>" onchange="checkBoxValidation(this)"/> <br> <br> 
			 &ensp;&ensp;&ensp;&ensp;<label for="url"><b>Database URL:</b></label> 
			 <input type="text" id="url" name="url"><br> <br>
			 &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="type"><b>File Type:</b></label> 			 
		     <input name="type" id="type" type="text" value="<%=request.getParameter("option")%>"readonly/><br><br>
		     &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="loc"><b>Save File To:</b></label> 
			<input type="text" id="loc" name="loc" /> <br><br>	
			 <label for="check"><b>Multiple Workbooks:</b></label>
			 <input type="checkbox" id="check" name="check"  onchange="queryValidation()" disabled/>&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<br> <br> 
			 <div  id="fileName" name="fileName">
             &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;<label for="fileName"><b>Save File As:</b></label> 
             <input type="text" id="fileName0" name="fileName0" /> <br><br>	
             &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;	
             </div>
             <br>
			<input type="submit" value="Generate Test-Data " size="20"
				color="blue" />
				
		</form>
	</center>
	<footer>
  <img src="gtf.PNG" style="float: right;" width="95" height="22" />
</footer>
</body>
</html>