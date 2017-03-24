<?php

date_default_timezone_set('Asia/Seoul');
 
require './PHPMailer/PHPMailerAutoload.php';
 
$mail = new PHPMailer;
$mail->SMTPSecure = 'ssl';
$mail->isSMTP();
$mail->SMTPDebug = 2;
$mail->Debugoutput = 'html';

$mail->Host = 'smtp.gmail.com';
$mail->Port = 465;
$mail->SMTPAuth = true;

$mail->Username = "ncslab1303@gmail.com";
$mail->Password = "ncslab2015";

$mail->setFrom('pyojihye95@gmail.com', 'First Last');
$mail->addAddress('ncslab1303@gmail.com', 'John Doe');

$mail->Subject = 'PHPMailer GMail SMTP test';   //mail subject
$mail->msgHTML("abcd", dirname(__FILE__));      //mail contents
$mail->AltBody = 'This is a plain-text message body';
 
if (!$mail->send()) {
    echo "Mailer Error: " . $mail->ErrorInfo;
} else {
    echo "Message sent!";
}

?>