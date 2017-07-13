<?php
include "creds.php";
if($USERNAME == null || $PASSWORD == null){
  echo "You have to define the username and password on the server";
  die();
}
if (isset($_POST['username']) && isset($_POST['password'])) {
    if($_POST['username'] == $USERNAME && $_POST['password'] == $PASSWORD){
        echo "true";   
    }else{
        echo "false";
    }
}else{
    echo "Please supply credentials";
}
?>
