<?
$server = "localhost";
$user = "username";
$pass = "passwort";
$db = "datenbank";
$conn = @mysql_connect($server, $user, $pass);
if (!$conn)
	{echo "errordb";}
$verbindung = @mysql_select_db($db);
if (!$verbindung)
	{echo "errordbnotthere";}

// sanitize user inputs
function sanitize($data)
{
	$data=trim($data);
	$data=htmlspecialchars($data);
	$data=mysql_real_escape_string($data);
	return $data;
};

?>