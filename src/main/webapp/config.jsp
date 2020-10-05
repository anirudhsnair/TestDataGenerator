<html>
<head>
<link rel="icon" href="logo.jpg" type="image/icon type">
<title style="color:#003d99">Test Data Generator</title>
<img src="allianz_logo.png" width="80" height="20" style="float: left;" />
<img src="logOut.png" width="80" height="20" style="float: right; " onClick="logOut()" />

<script>
	function openPage() {
		var l = document.getElementById("files");
		var selectedPage = parseInt(l.options[l.selectedIndex].value);
		var type;
		if (selectedPage == 5) {
			type = "DB to Excel";
		} else if (selectedPage == 4) {
			type = "JSON to XML";
		} else if (selectedPage == 3) {
			type = "XML to JSON";
		} else if (selectedPage == 2) {
			type = "Excel to JSON";
		} else if (selectedPage == 1) {
			type = "JSON to Excel";
		} else if (selectedPage == 6) {
			type = "DB to CSV";
		} else if (selectedPage == 7) {
			type = "DB to JSON";
		}
		if (selectedPage < 5) {
			window.location.replace("fileTofile.jsp?option=" + type);

		} else {
			window.location.replace("dbTofile.jsp?option=" + type);

		}
	}
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
		
		<form method="post" style="color:#003d99" action="fileTofile" enctype="multipart/form-data">
			<label for="fileTypes"><b>Select the type of convertion:</b></label> <select
				name="files" id="files">
				<option value="1">JSON to Excel</option>
				<option value="2">Excel to JSON</option>
				<option value="3">XML to JSON</option>
				<option value="4">JSON to XML</option>
				<option value="5">DB to Excel</option>
				<option value="6">DB to CSV</option>
				<option value="7">DB to JSON</option>
			</select> <br> <br> <input type="button" value="Submit"
				onClick="openPage()">
		</form>

	</center>
	<footer>
		<img src="gtf.PNG" style="float: right;"width="95" height="22" />
	</footer>
</body>
</html>
