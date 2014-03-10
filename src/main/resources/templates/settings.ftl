<!--
  settings Template
-->
<html>
<head>
<title>${title}</title>
</head>
<body>
	<h1>Camera Grabber Server Settings</h1>
	<a href="/camgrab">${homeIcon}</a> <br/><h3>Select an input device:</h3>
	<table>
		<tr>
			<td><a href="/settings/device/GalaxyNote?url=http://2.0.0.75:8080/video">${galaxyNoteIcon}</a></td>
			<td><a href="/settings/device/EOS1000D?url=/camgrab/eos/preview">${eosIcon}</a></td>
			<td><a href="/settings/device/DCS930?url=http://cam2/mjpeg.cgi">${dscIcon}</a></td>
		</tr>
		<tr>
			<td align="center"><#if deviceName.equals("GalaxyNote")>${checkIcon}</#if></td>
			<td align="center"><#if deviceName.equals("EOS1000D")>${checkIcon}</#if></td>
			<td align="center"><#if deviceName.equals("DCS930")>${checkIcon}</#if></td>		
		</tr>
	</table>
</body>
</html>