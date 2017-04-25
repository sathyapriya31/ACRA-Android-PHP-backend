<?php
include "creds.php";

if (isset($_POST['username']) && isset($_POST['password'])) {
    if($_POST['username'] == $USERNAME && $_POST['password'] == $PASSWORD){
        $extension = ".txt";
        $files = glob("logs/*" . $extension);
        
        foreach($files as $file){ // iterate files
            if(is_file($file))
                unlink($file); // delete file
        }
    }
}
?>
