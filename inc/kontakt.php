<?php

$t_name="Name";
$t_mail="eMail";
$t_page="Webpage";
$t_los="send";
$p1="Please enter a name.";
$p2="Please enter a valid email address.";
$p3="Please enter a message.";
$p4="Thank you, your message has been sent.";



if($_SESSION["lang"]=="de"){
$t_name="Name";
$t_mail="eMail-Adresse";
$t_page="Webseite";
$t_los="abschicken";
$p1="Bitte geben Sie einen Namen an.";
$p2="Bitte geben Sie eine g&uuml;ltige Emailadresse an.";
$p3="Bitte geben Sie einen Text ein.";
$p4="'Danke, Ihre Nachricht wurde verschickt.'";
}



if($_POST["test"]==1){

	if(!$_POST["mail_author"]){$pruef=$p1;} 
	elseif(!$_POST["email"]){$pruef=$p2;} 
	elseif(!$_POST["text_mail"]){$pruef=$p3;} 
	else{

		$absender = preg_replace( "/[^a-z0-9 !?:;,.\/_\-=+@#$&\*\(\)]/im", "", $_POST['email'] );
		$absender = preg_replace( "/(content-type:|bcc:|cc:|to:|from:)/im", "", $absender );
		$absender = $_POST["mail_author"].' <'.$absender.'>';

		$txt = strip_tags(preg_replace( "/(content-type:|bcc:|cc:|to:|from:)/im", "", $_POST['text_mail'] ));
		
		$webseite=strip_tags($_POST['url']);
				
		$headers .= 'From:' .$absender. "\n";
		$headers .= "Content-type: text/plain\r\n";
		
		mail("mail@barbarahiller.de", "Nachricht via Kontaktformular von ".$_POST["mail_author"], $txt."\n\nWebseite: ".$webseite , $headers);

		$pruef=$p4;
		$ok=1;

		$_POST["mail_author"]="";
		$_POST["email"]="";
		$_POST["url"]="";
		$_POST["text_mail"]="";

	}
}

if($_SESSION["lang"]=="de"){echo'Die Inhalte des Kontakt-Formulars werden  an <strong>mail [&auml;t] barbarahiller.de</strong> geschickt. Gerne k&ouml;nnen Sie mir auch direkt eine <a href="mailto: mail@barbarahiller.de">Email</a> senden, oder mich unter der <strong>+49 (0)176 / 27 24 84 83</strong> anrufen.';}
else{echo'The content you enter here will be sent to <strong>mail [at] barbarahiller.de</strong>. You are also welcome to <a href="mailto: mail@barbarahiller.de">email</a> me directly or contact me by phone: <strong>+49 (0)176 / 27 24 84 83</strong>.';}

?><hr>

<form action="" method="post" id="commentform">

	<p>
		<input type="text" name="mail_author" id="kontakt_author" value="<?php echo $_POST["mail_author"]; ?>" size="22" maxlength="40" tabindex="1" /><span class="schild"><?php echo $t_name;?></span>
	</p>

	<p>
		<input type="text" name="email" id="kontakt_email" value="<?php echo $_POST["email"]; ?>" size="22" maxlength="40" tabindex="2" /><span class="schild"><?php echo $t_mail;?></span>
	</p>

	<p>
		<input type="text" name="url" id="kontakt_url" value="<?php echo $_POST["url"]; ?>" size="22" maxlength="40" tabindex="3" /><span class="schild"><?php echo $t_page;?></span>
	</p>

	<p>
		<textarea name="text_mail" id="kontakt_text" tabindex="4"><?php echo $_POST["text_mail"];?></textarea>
	</p>

	<p>
		<input name="submit" type="submit" id="submit" tabindex="6" value="<?php echo $t_los;?>" />
		<input type="hidden" value="1" name="test">
	</p>
</form>
<div id="kontakt_fehler" style="<?php if($pruef){ echo "opacity: 1;";} if($ok==1){ echo "background-image: url('"; echo bloginfo('template_directory'); echo "/pix/ok.png');";}?>">

	<?php
	echo $pruef;
	//echo '<br>CAPCODE: '.$_POST["thecap"];
	//echo '<br>CAPTCHA: '.$_SESSION["capcode"];
	?>
</div>